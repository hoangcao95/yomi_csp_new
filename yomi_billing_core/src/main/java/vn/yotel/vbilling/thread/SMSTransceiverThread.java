package vn.yotel.vbilling.thread;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.SmppObject;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.WrongLengthOfStringException;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.TerminatingZeroNotFoundException;

import com.google.common.util.concurrent.RateLimiter;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.Util;
import vn.yotel.thread.Constants.HAInfo.HAMode;
import vn.yotel.thread.Constants.ManageableThreadState;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.model.MORequest;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.util.AppUtil;
import vn.yotel.vbilling.util.SmppMessageUtil;
import vn.yotel.vbilling.util.charset.CharsetUtil;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;

public class SMSTransceiverThread extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(SMSTransceiverThread.class);
	private Logger MOREQ_LOG = LoggerFactory.getLogger("vn.yotel.vbilling.thread.MO_REQUEST");
	private Logger MTRES_LOG = LoggerFactory.getLogger("vn.yotel.vbilling.thread.MT_RESPONSE");

	private boolean asynchronous = true;
	private String ipAddress = "";
	private int port = 5019;
	private String systemId = "";
	private String password = "";
	private String bindMode = "";
	private String systemType = "";
	private String strAddressRange = "";
	private boolean mtUnicodeEncoding = false;

	Session session = null;
	boolean bound = false;
	SMPPAsyncPDUEventListener pduListener = null;
	AddressRange addressRange = new AddressRange();

	long receiveTimeout = Data.RECEIVE_BLOCKING;
	int enquireInterval = 60;
	long nextEnquireLink = 0;
	volatile boolean hasError = false;

	private Queue<MTRequest> mtQueueToSMSC;
	private Object mtQueueToSMSCNotifier;
	private Queue<MORequest> moQueue;
	private Object moQueueNotifier;
	// private BlacklistSubsBo blacklistSubsBo = (BlacklistSubsBo)
	// AppContext.getBean("blacklistSubsBo");
	private RateLimiter rateLimiter = null;
	private int tps;

	@SuppressWarnings("unchecked")
	@Override
	protected void loadParameters() throws AppException {
		if (this.params != null) {
			this.ipAddress = this.getParamAsString("ipAddress");
			this.port = this.getParamAsInt("port");
			this.systemId = this.getParamAsString("systemId");
			this.password = this.getParamAsString("password");
			this.bindMode = this.getParamAsString("bindMode");
			this.systemType = this.getParamAsString("systemType");
			this.receiveTimeout = this.getParamAsInt("receiveTimeout");
			this.strAddressRange = this.getParamAsString("address-range");
			String mtEncoding = this.getParamAsString("mt-encoding");
			this.mtUnicodeEncoding = "UTF16".equalsIgnoreCase(mtEncoding);
			try {
				this.addressRange.setNpi((byte) 1);
				this.addressRange.setTon((byte) 1);
				this.addressRange.setAddressRange(strAddressRange);
			} catch (WrongLengthOfStringException e) {
				LOG.error("Invalid address-range: " + this.strAddressRange);
			}
			this.tps = this.params.optInt("tps", 10);
			rateLimiter =  RateLimiter.create(tps);
			//
			mtQueueToSMSC = (Queue<MTRequest>) AppContext.getBean("mtQueueToSMSC");
			mtQueueToSMSCNotifier = AppContext.getBean("mtQueueToSMSCNotifier");
			moQueue = (Queue<MORequest>) AppContext.getBean("moQueue");
			moQueueNotifier = AppContext.getBean("moQueueNotifier");
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
	}

	@Override
	protected void initializeSession() throws AppException {
		active = HAMode.MASTER.equals(AppParams.SERVER_MODE);
	}

	@Override
	protected boolean processSession() throws AppException {
		SMSReceiver smsReceiver = null;
		active = HAMode.MASTER.equals(getManager().getServerMode());
		while (!active && !requireStop) {
			LOG.info("Waiting in BACKUP mode. Server mode: {}, active flag : {}", AppParams.SERVER_MODE, active);
			this.setState(ManageableThreadState.IDLE);
			synchronized (activeMonitorObj) {
				try {
					for (int count = 0; (count < 60) && !active && !requireStop; count++) {
						activeMonitorObj.wait(1000L);
					}
				} catch (InterruptedException e) {
				}
			}
			active = HAMode.MASTER.equals(AppParams.SERVER_MODE);
		}
		LOG.info("Process started and in MASTER mode: {}", AppParams.SERVER_MODE);
		try {
			hasError = false;
			bound = false;
			bind();
			this.setState(ManageableThreadState.NORMAL);
			smsReceiver = new SMSReceiver();
			smsReceiver.setDaemon(true);
			smsReceiver.setName("SMSTranceiver->smsReceiver");
			smsReceiver.start();
			submit();
		} catch (Exception e) {
			LOG.error("", e);
			hasError = true;
			this.setState(ManageableThreadState.ERROR);
			return false;
		} finally {
			try {
				if (smsReceiver != null && smsReceiver.isAlive()) {
					smsReceiver.stopNow();
					smsReceiver.join(10000L);
					smsReceiver = null;
				}
			} catch (InterruptedException ex1) {
			}
			unbind();
			// to sure close socket connection
			if (session.getConnection() != null) {
				try {
					session.getConnection().close();
				} catch (Exception ex) {
				}
			}
		}
		return true;
	}

	@Override
	protected void completeSession() throws AppException {
	}

	public class SMSReceiver extends Thread {

		boolean rRequireStop = false;

		public SMSReceiver() {
		}

		public void stopNow() {
			rRequireStop = true;
		}

		public void run() {
			try {
				while (!rRequireStop && !hasError) {
					receive();
				}
			} catch (Exception ex) {
				LOG.error("", ex);
				if (!bound) { // da hoac dang unbind
					LOG.error("Receiver is stopping because session already unbind!");
				} else {
					LOG.info("Error occurs when getting message: " + ex);
				}
				hasError = true;
			}
		}
	}

	/**
	 * The first method called to start communication betwen an ESME and a SMSC. A
	 * new instance of <code>TCPIPConnection</code> is created and the IP address
	 * and port obtained from user are passed to this instance. New
	 * <code>Session</code> is created which uses the created
	 * <code>TCPIPConnection</code>. All the parameters required for a bind are set
	 * to the <code>BindRequest</code> and this request is passed to the
	 * <code>Session</code>'s <code>bind</code> method. If the call is successful,
	 * the application should be bound to the SMSC.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.1 BIND Operation."
	 * 
	 * @see BindRequest
	 * @see BindResponse
	 * @see TCPIPConnection
	 * @see Session#bind(BindRequest)
	 * @see Session#bind(BindRequest,ServerPDUEventListener)
	 */
	public void bind() throws Exception {
		logMonitor("Binding with bind-mode: " + bindMode + " and sync-mode: " + asynchronous + " and system-type: "
				+ this.systemType);
		try {
			if (bound) {
				this.unbind();
			}

			BindRequest request = null;
			BindResponse response = null;

			if (bindMode.compareToIgnoreCase("T") == 0) {
				request = new BindTransmitter();
			} else if (bindMode.compareToIgnoreCase("R") == 0) {
				request = new BindReceiver();
			} else if (bindMode.compareToIgnoreCase("TR") == 0) {
				request = new BindTransciever();
			} else {
				throw new Exception("Invalid bind mode, expected Transmitter, Receiver or Transceiver, got " + bindMode
						+ ". Operation canceled.");
			}

			TCPIPConnection connection = new TCPIPConnection(ipAddress, port);
			// set thoi gian timeout khi nhan du lieu tu socket
			connection.setReceiveTimeout(receiveTimeout);
			session = new Session(connection);
			// set values
			request.setSystemId(systemId);
			request.setPassword(password);
			request.setSystemType(systemType);
			request.setInterfaceVersion((byte) 0x34);
			request.setAddressRange(addressRange);

			// send the request
			LOG.info(String.format("Bind request ip: %s, port: %s, systemId: %s, mode: %s", ipAddress,
					String.valueOf(port), systemId, String.valueOf(asynchronous)));

			if (asynchronous) {
				pduListener = new SMPPAsyncPDUEventListener(session);
				response = session.bind(request, pduListener);
			} else {
				response = session.bind(request);
			}
			logMonitor("Bind response " + response.debugString());
			if (response.getCommandStatus() == Data.ESME_ROK) {
				bound = true;
				logMonitor("Bind DONE. Going to receive & send SMS ");
				session.getReceiver().setReceiveTimeout(receiveTimeout);
			} else {
				throw new Exception("Bind response code: " + response.getCommandStatus());
			}
		} catch (Exception e) {
			LOG.error("Bind operation failed. " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Ubinds (logs out) from the SMSC and closes the connection.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.2 UNBIND Operation."
	 * 
	 * @see Session#unbind()
	 * @see Unbind
	 * @see UnbindResp
	 */
	private void unbind() {
		if (session == null) {
			return;
		}
		try {
			if (!bound) {
				logMonitor("Not bound, cannot unbind.");
				return;
			}
			// send the request
			if (session.getReceiver().isReceiver()) {
				logMonitor("It can take a while to stop the receiver.");
			}
			UnbindResp response = null;
			logMonitor("Send request to unbind...");
			synchronized (session) {
				response = session.unbind();
				bound = false;
			}
			logMonitor("Unbind response " + ((response != null) ? response.debugString() : "Not receiver response"));
		} catch (Exception e) {
			LOG.error("", e);
			LOG.warn("Unbind operation failed. " + e);
		} finally {
			bound = false;
		}
	}

	/**
	 * Receives one PDU of any type from SMSC and prints it on the screen.
	 * 
	 * @see Session#receive()
	 * @see Response
	 * @see ServerPDUEvent
	 */
	private boolean receive() throws Exception {
		/***********************************************************************************
		 * DEPQ: 2009 Lay receive tra ve tu SMSC: + Neu gui nhan theo co che bat dong bo
		 * thi lay receive tu ServerPDUEventListener, nguoc lai lay receive tu session +
		 * Neu la tin nhan do SMSC deliver-->ESME thi day vao queueReceiver va gui xac
		 * nhan da bat tin nhan + Neu la response xac nhan cua SMSC thue bao da nhan
		 * duoc tin nhan do ESME submit thi day vao queueResponse
		 ***********************************************************************************/
		PDU pdu = null;
		if (asynchronous) {
			ServerPDUEvent pduEvent = pduListener.getRequestEvent(receiveTimeout);
			if (pduEvent != null) {
				pdu = pduEvent.getPDU();
			}
		} else {
			synchronized (session) {
				if (bound) {
					pdu = session.receive(0);
				}
			}
		}
		if (pdu == null) {
			Thread.sleep(5);
			return false;
		}
		if (pdu.isRequest()) {
			Response response = ((Request) pdu).getResponse();
			// send default response
			synchronized (session) {
				if (bound) {
					session.respond(response);
				}
			}
			if (pdu instanceof DeliverSM) {
				DeliverSM deliverSM = (DeliverSM) pdu;
				if (!deliverSM.getSourceAddr().getAddress().equals("")
						&& !deliverSM.getDestAddr().getAddress().equals("")) {
					logMonitor("Received data: " + deliverSM.debugString());
					String strMessage = deliverSM.getShortMessage();
					if (strMessage == null) {
						strMessage = "";
						deliverSM.setShortMessage(strMessage);
					}
					processDeliverMessage(deliverSM);
				} else {
					logMonitor("Received unknown dest address and source address: " + deliverSM.debugString());
				}
			} else if (pdu instanceof EnquireLink) {
				// Ignore
			} else {
				LOG.debug("Received request: " + pdu.debugString());
			}
			// LOG.debug("Response notify from SMSC:" + response.debugString());
		} else if (pdu.isResponse()) {
			if (pdu instanceof SubmitSMResp) {
				SubmitSMResp submitRes = (SubmitSMResp) pdu;
				processResponseSubmit(submitRes);
			} else {
				if (pdu instanceof EnquireLinkResp) {
					LOG.debug("Received Enquire Link response: " + pdu.debugString());
				} else {
					LOG.debug("Received response: " + pdu.debugString());
				}

			}
		} else if (pdu.isGNack()) {
			LOG.warn("Received Generic Nack: " + pdu.debugString());
		} else {
			logMonitor("Received unknow: " + pdu.debugString());
		}
		return true;
	}

	private void submit() throws AppException, Exception {
		try {
			LOG.info("requireStop: {}, hasError: {}", requireStop, hasError);
			boolean passCheckRateLimit = false;
			while (!requireStop && !hasError) {
				// Send enquireLink
				enquireLink();
				MTRequest submitMessage = mtQueueToSMSC.poll();
				if (submitMessage != null) {
					//accquire tps
					passCheckRateLimit = rateLimiter.tryAcquire();
					while (!requireStop && !passCheckRateLimit) {
						passCheckRateLimit = rateLimiter.tryAcquire();
						if (!passCheckRateLimit) {
							Thread.sleep(10L);
						}
					}
					//
					String toNumber = submitMessage.getToNumber();
					toNumber = toNumber.replaceAll("^0", "");
					if (!toNumber.startsWith(Constants.COUNTRY_MOBILE_PREFIX)) {
						toNumber = Constants.COUNTRY_MOBILE_PREFIX + toNumber;
					}
					// Message
					List<SubmitSM> submitSMs = new ArrayList<SubmitSM>();
					if (this.mtUnicodeEncoding) {
						submitSMs = buildSubmitSMUnicode(submitMessage.getFromNumber(), toNumber, submitMessage.getMessage());
					} else {
						submitSMs = buildSubmitSM(submitMessage.getFromNumber(), toNumber, submitMessage.getMessage());
					}
					boolean useBrandName = submitMessage.isBrandName();
					boolean useFlashSms = submitMessage.isFlashSms();
					for (SubmitSM submitSM : submitSMs) {
						if (useBrandName) {
							submitSM.setSourceAddr((byte) 5, (byte) 0, AppParams.BRAND_NAME);
							submitSM.setDestAddr((byte) 1, (byte) 0, toNumber);
						}
						if (useFlashSms) {
//							submitSM.setDataCoding((byte) 0x18);
							submitSM.setDataCoding((byte) 0xf0);
						}
						logMonitor("Submit data " + submitSM.debugString());
						if (asynchronous) {
							synchronized (session) {
								session.submit(submitSM);
							}
						} else {
							SubmitSMResp submitResponse = null;
							synchronized (session) {
								submitResponse = session.submit(submitSM);
							}
							if (submitResponse != null) {
								processResponseSubmit(submitResponse);
							}
						}
					}
				} else {
					synchronized (mtQueueToSMSCNotifier) {
						mtQueueToSMSCNotifier.wait(100L);
					}
				}
			}
		} catch (Exception e) {
			hasError = true;
			throw e;
		}
	}

	private List<SubmitSM> buildSubmitSM(String fromNumber, String toNumber, String message) throws PDUException,
			NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, UnsupportedEncodingException {
		List<SubmitSM> result = new ArrayList<SubmitSM>();
		byte sourceTon = (byte) 0x03;
		if (fromNumber != null && fromNumber.length() > 0) {
			sourceTon = (byte) 0x05;
		}

		byte[] textBytes = CharsetUtil.encode(message, CharsetUtil.CHARSET_ISO_8859_15);
		int maximumMultipartMessageSegmentSize = 134;
		byte[] byteSingleMessage = textBytes;
		byte[][] byteMessagesArray = SmppMessageUtil.splitUnicodeMessage(byteSingleMessage,
				maximumMultipartMessageSegmentSize);

		// submit all messages
		for (int i = 0; i < byteMessagesArray.length; i++) {
			SubmitSM submit0 = new SubmitSM();
			submit0.setEsmClass((byte) Data.SM_UDH_GSM);
			submit0.setRegisteredDelivery(Data.DFLT_REG_DELIVERY);
			submit0.setSourceAddr(new Address(sourceTon, (byte) 0x00, fromNumber));
			submit0.setDestAddr(new Address((byte) 0x03, (byte) 0x00, toNumber));
			submit0.setShortMessageData(new ByteBuffer(byteMessagesArray[i]));
			result.add(submit0);
		}
		return result;
	}

	private List<SubmitSM> buildSubmitSMUnicode(String fromNumber, String toNumber, String message) throws PDUException,
			NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, UnsupportedEncodingException {
		List<SubmitSM> result = new ArrayList<SubmitSM>();
		byte sourceTon = (byte) 0x03;
		if (fromNumber != null && fromNumber.length() > 0) {
			sourceTon = (byte) 0x05;
		}
		int maximumMultipartMessageSegmentSize = 63;
		String[] splittedMsg = this.splitByWidth(message, maximumMultipartMessageSegmentSize);
		int totalSegments = splittedMsg.length;
		// submit all messages
		for (int i = 0; i < splittedMsg.length; i++) {
			SubmitSM submit0 = new SubmitSM();
			submit0.setEsmClass((byte) Data.SM_UDH_GSM);
			submit0.setDataCoding((byte) 0x08);
			submit0.setRegisteredDelivery(Data.DFLT_REG_DELIVERY);
			submit0.setSourceAddr(new Address(sourceTon, (byte) 0x00, fromNumber));
			submit0.setDestAddr(new Address((byte) 0x03, (byte) 0x00, toNumber));

			ByteBuffer ed = new ByteBuffer();
			ed.appendByte((byte) 6); // UDH Length
			ed.appendByte((byte) 0x08); // IE Identifier
			ed.appendByte((byte) 4); // IE Data Length
			ed.appendByte((byte) 00); // Reference Number 1st Octet
			ed.appendByte((byte) 00); // Reference Number 2nd Octet
			ed.appendByte((byte) totalSegments); // Number of pieces
			ed.appendByte((byte) (i + 1)); // Sequence number
			// This encoding comes in Logica Open SMPP. Refer to its docs for more detail
			ed.appendString(splittedMsg[i], Data.ENC_UTF16_BE);
			submit0.setShortMessageData(ed);
			result.add(submit0);
		}
		return result;
	}

	private String[] splitByWidth(String s, int width) {
		try {
			if (width == 0) {
				String[] ret = new String[1];
				ret[0] = s;
				return ret;
			} else {
				if (s.isEmpty())
					return new String[0];
				else {
					if (s.length() <= width) {
						String[] ret = new String[1];
						ret[0] = s;
						return ret;
					} else {
						int NumSeg = s.length() / width + 1;
						String[] ret = new String[NumSeg];
						int startPos = 0;
						for (int i = 0; i < NumSeg - 1; i++) {
							ret[i] = s.substring(startPos, ((width * (i + 1))));
							startPos = (i + 1) * width;
						}
						ret[NumSeg - 1] = s.substring(startPos, s.length());
						return ret;
					}
				}
			}
		} catch (Exception e) {
			LOG.error(String.valueOf(e.fillInStackTrace()));
			return new String[0];
		}
	}

	/**
	 * Creates a new instance of <code>EnquireSM</code> class. This PDU is used to
	 * check that application level of the other party is alive. It can be sent both
	 * by SMSC and ESME.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.11 ENQUIRE_LINK Operation."
	 * 
	 * @see Session#enquireLink(EnquireLink)
	 * @see EnquireLink
	 * @see EnquireLinkResp
	 */
	private synchronized void enquireLink() throws Exception {
		if (System.currentTimeMillis() < nextEnquireLink) {
			return;
		}
		EnquireLink request = new EnquireLink();
		EnquireLinkResp response;
		// logDebug("Queue wait response of submit size:" + hashWait.size());
		LOG.debug("Sending Enquire Link request to SMSC ....." + request.debugString());

		if (asynchronous) {
			synchronized (session) {
				session.enquireLink(request);
			}
		} else {
			synchronized (session) {
				response = session.enquireLink(request);
			}
			LOG.debug("Received Enquire Link response " + response.debugString());
		}
		nextEnquireLink = System.currentTimeMillis() + enquireInterval * 1000;
	}

	private void processResponseSubmit(SubmitSMResp submitRes) {
		LOG.debug("Response for submit message with data: {}", submitRes.debugString());
		int seqNumber = submitRes.getSequenceNumber();
		int status = submitRes.getCommandStatus();
		MTRES_LOG.info("{},{}", seqNumber, status);
		if (status > 0) {
			try {
				@SuppressWarnings("unchecked")
				Queue<Integer> maxTpsQueue = (Queue<Integer>) AppContext.getBean("maxTpsQueue");
				maxTpsQueue.offer(seqNumber);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
	}

	private void processDeliverMessage(DeliverSM deliverSM) {
		String fromNumber = deliverSM.getSourceAddr().getAddress();
		String toNumber = deliverSM.getDestAddr().getAddress();
		String message = deliverSM.getShortMessage();
		fromNumber = Util.normalizeMsIsdn(fromNumber);
		toNumber = AppUtil.normalizeShortCode(toNumber);
		MORequest req = new MORequest();
		req.setSmsId(Util.generateTransId());
		req.setFromNumber(fromNumber);
		req.setToNumber(toNumber);
		req.setMessage(message);
		req.setReceivedDate(new Date());
		this.moQueue.offer(req);
		synchronized (moQueueNotifier) {
			this.moQueueNotifier.notifyAll();
		}
		MOREQ_LOG.info(String.format("%s,%s,%s,%s", req.getSmsId(), fromNumber, toNumber, message));
	}

	/**
	 * Implements simple PDU listener which handles PDUs received from SMSC. It puts
	 * the received requests into a queue and discards all received responses.
	 * Requests then can be fetched (should be) from the queue by calling to the
	 * method <code>getRequestEvent</code>.
	 * 
	 * @see Queue
	 * @see ServerPDUEvent
	 * @see ServerPDUEventListener
	 * @see SmppObject
	 */
	private class SMPPAsyncPDUEventListener extends SmppObject implements ServerPDUEventListener {
		// DEPQ fix dung thread-safe ngay 11/10/2010-Vinaphone
		ConcurrentLinkedQueue<ServerPDUEvent> requestEvents = new ConcurrentLinkedQueue<ServerPDUEvent>();

		public SMPPAsyncPDUEventListener(Session session) {
		}

		/**
		 * DEPQ lay tu SMPPTest cua logical
		 * 
		 * @param event
		 *            ServerPDUEvent
		 */
		public void handleEvent(ServerPDUEvent event) {
			PDU pdu = event.getPDU();
			if (pdu.isRequest() || pdu.isResponse()) {
				synchronized (requestEvents) {
					requestEvents.add(event);
					requestEvents.notifyAll();
				}
			} else {
				logMonitor("pdu of unknown class (not request nor response) received, discarding " + pdu.debugString());
			}
		}

		/**
		 * Returns received pdu from the queue. If the queue is empty, the method blocks
		 * for the specified timeout.
		 */
		public ServerPDUEvent getRequestEvent(long timeout) {
			timeout = 100;
			ServerPDUEvent pduEvent = null;
			if (!requestEvents.isEmpty()) {
				return pduEvent = (ServerPDUEvent) requestEvents.poll();
			} else {
				synchronized (requestEvents) {
					if (requestEvents.isEmpty()) {
						try {
							requestEvents.wait(timeout);
						} catch (InterruptedException e) {
						}
					}
				}
			}
			if (!requestEvents.isEmpty()) {
				return pduEvent = (ServerPDUEvent) requestEvents.poll();
			}
			return pduEvent;
		}
	}

	public static void logMonitor(String str) {
		LOG.debug(str);
	}

	@Override
	public void notifyEnteringNewMode(String serverOldMode, String serverNewMode) {
		if ((serverNewMode != null) && serverNewMode.equals(serverOldMode)) {
			LOG.warn("Shoudl not call notify when there is nothing changed");
			return;
		}
		LOG.info("Process entering new mode. From {} to {}", serverOldMode, serverNewMode);
		if (HAMode.MASTER.equals(serverNewMode)) {
			active = true;
			synchronized (activeMonitorObj) {
				activeMonitorObj.notifyAll();
			}
		} else {
			active = false;
			LOG.warn("Going to stop thread smpp thread while entering BACKUP mode");
			this.hasError = true;
		}
	}
}
