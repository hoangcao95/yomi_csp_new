package vn.yotel.vbilling.thread;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.Util;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.model.HandlingResult;
import vn.yotel.vbilling.model.MORequest;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtModel;
import vn.yotel.vbilling.service.CpGateService;
import vn.yotel.vbilling.service.SmsService;
import vn.yotel.vbilling.service.SubscriberService;
import vn.yotel.vbilling.service.VasPackageService;
import vn.yotel.vbilling.util.AppUtil;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants.CommandCode;


public class ProcessContentRequest extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessContentRequest.class);
	
	private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
	private Object mtQueueToCSPNotifier;
	private ConcurrentLinkedQueue<MORequest> moProcessQueue;
	private Object moProcessQueueNotifier;
	
	private SubscriberService subscriberService;
	private VasPackageService vasPackageService;
	private CpGateService cpGateService;
	private ChargingCSPClient chargingCSPClient;
	private SmsService smsService;
	private MtModel mtModel = new MtModel();
	private SimpleDateFormat sdfFull = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadParameters () throws AppException {
		if (this.params != null) {
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
		mtQueueToCSP = (ConcurrentLinkedQueue<MTRequest>) getBean("mtQueueToCSP");
		mtQueueToCSPNotifier = getBean("mtQueueToCSPNotifier");
		
		moProcessQueue = (ConcurrentLinkedQueue<MORequest>) getBean("moProcessQueue");
		moProcessQueueNotifier = getBean("moProcessQueueNotifier");
    }
	
	@Override
	protected void initializeSession() throws AppException {
		subscriberService = (SubscriberService) getBean("subscriberService");
		vasPackageService = (VasPackageService) getBean("vasPackageService");
		cpGateService = (CpGateService) getBean("cpGateService");
		chargingCSPClient = (ChargingCSPClient) getBean("chargingCSPClient");
		smsService = (SmsService) getBean("smsService");
		mtModel = smsService.mtModel();
    }
	
	@Override
	protected boolean processSession() throws AppException {
		try {
			while (!requireStop) {
				MORequest moReq = moProcessQueue.poll();
				if (moReq != null) {
					LOG.debug("Process MO request: {}", moReq.toString());
					MTRequest mtReq = null;
					String fromNumber = moReq.getFromNumber();
					String toNumber = moReq.getToNumber();
					if (CommandCode.GUIDE_XS.equalsIgnoreCase(moReq.getCommand())) {
						moReq.setCommand(CommandCode.GUIDE_XS);
						mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, mtModel.getMtHDXS(), moReq, moReq.getCommand());
					} else if (CommandCode.GUIDE_DT.equalsIgnoreCase(moReq.getCommand())) {
						moReq.setCommand(CommandCode.GUIDE_DT);
						mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, mtModel.getMtHDDT(), moReq, moReq.getCommand());
					} else if (CommandCode.CHECK.equalsIgnoreCase(moReq.getCommand())) {
						processCheckSubs(fromNumber, moReq);
					} else if (CommandCode.RESET_PWD.equalsIgnoreCase(moReq.getCommand())) {
						String mpin = Util.generateMPIN();
						processPasswordSubs(fromNumber, mpin, true, moReq);
					} else if (CommandCode.BUY.equalsIgnoreCase(moReq.getCommand())) {
//						processBuyPackage(fromNumber, moReq);
					}
					if (mtReq != null) {
						sendMT(mtReq);
					}
				} else {
					synchronized (moProcessQueueNotifier) {
						moProcessQueueNotifier.wait(1000L);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return true;
	}
	
//	private void processBuyPackage(String fromNumber, MORequest moReq) {
//		LOG.info("--- buy -----");
//		try {
//			String message = moReq.getMessage().toUpperCase();
//			String content_ID = "";
//			String category_ID = "";
//			if (message.contains("XS ") || message.contains("TK ") || message.contains("VIP ") || message.contains("MEGA") || message.contains("MAX") || message.contains("LOT")) {
//				LOG.info("--- dk taile ----- " + message);
//				if (message.contains("XS ")) {
//					content_ID = "0000000004";
//					category_ID = "000002";
//				} else if (message.contains("TK ")) {
//					content_ID = "0000000005";
//					category_ID = "000002";
//				} else if (message.contains("VIP ")) {
//					content_ID = "0000000006";
//					category_ID = "000002";
//				} else if (message.contains("MEGA")) {
//					content_ID = "0000000007";
//					category_ID = "000003";
//				} else if(message.contains("MAX")) {
//					content_ID = "0000000008";
//					category_ID = "000004";
//				} else if(message.contains("LOT")) {
//					content_ID = "0000000009";
//					category_ID = "000005";
//				}
//			}
//			String amount = "";
//			String transId = Util.generateTransId();
//			amount = String.valueOf(moReq.getSubsPackage().getPriceAsInt());
//			LOG.info("amount " + amount);
//			String result = chargingCSPClient.minusMoneyCheckMO(AppParams.SHORT_CODE, moReq.getFromNumber(), moReq.getSmsId(),
//					moReq.getSubsPackage().getName(), moReq.getSubsPackage().getName(), "001",
//					"001", content_ID, category_ID, amount);
//			LOG.info("-- transId -- " + transId);
//			VasPackage vasPackage = moReq.getSubsPackage();
//			//TC là 1
//			if(result.equals("1")) {
//				Date now = new Date();
//				String mpin = Util.generateMPIN();
//				String transIdNew = Util.generateTransId();
//				String providerCode = "";
//				String refCode = "";
//				String errorCode = "0000";
//				String errorDesc = "";
//				int channel = 0;
//				String originalSms = moReq.getMessage();
//				int _amount = moReq.getSubsPackage().getPriceAsInt();
//				boolean regNew = true;
//				boolean regDone = true;
//				String command = CommandCode.REGISTER;
//				LOG.info("--- from number --- " + moReq.getFromNumber());
//				LOG.info("--- vasPackage id --- " + vasPackage.getId());
//				Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(moReq.getFromNumber(), vasPackage.getId());
//
//				if (subscriber == null) {
//					subscriber = new Subscriber();
//					subscriber.setMsisdn(moReq.getFromNumber());
//					subscriber.setMpin(mpin);
//					subscriber.setChannel(channel);
//					subscriber.setCreatedDate(now);
//					subscriber.setModifiedDate(now);
//					Calendar cal = Calendar.getInstance();
//					subscriber.setRegisterDate(cal.getTime());
//					cal.add(Calendar.DATE, 1);
//					subscriber.setExpiredDate(cal.getTime());
//					subscriber.setLastChargedDate(null);
//					subscriber.setProductId(vasPackage.getProductId());
//					subscriber.setPackageId(vasPackage.getId());
//					subscriber.setRegNew(1);
//					subscriber.setStatus(1);
//					subscriberService.create(subscriber);
//				} else {
//					subscriber.setRegNew(0);
//					subscriber.setModifiedDate(now);
//					Calendar cal = Calendar.getInstance();
//					subscriber.setRegisterDate(cal.getTime());
//					cal.add(Calendar.DATE, 1);
//					subscriber.setExpiredDate(cal.getTime());
//					subscriberService.update(subscriber);
//				}
//
//				HandlingResult postResult = cpGateService.notifyBuyPackage(transIdNew, moReq.getFromNumber(), mpin, _amount, regNew, Util.XBD_SDF.format(now),
//						Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, channel, moReq.getSubsPackage().getName(), regDone, providerCode, refCode, errorCode, errorDesc);
//				this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
//			} else {
//				//Send MT
//				String sendMessage = mtModel.getMtSystemError();
//				MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, fromNumber, sendMessage, moReq, moReq.getCommand());
//				mtReq.setBrandName(false);
//				sendMT(mtReq);
//			}
//		} catch (Exception e) {
//			LOG.error("", e);
//			//Send MT
//			String sendMessage = mtModel.getMtSystemError();
//			MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, fromNumber, sendMessage, moReq, moReq.getCommand());
//			mtReq.setBrandName(false);
//			sendMT(mtReq);
//		}
//	}

	@Override
	protected void completeSession() throws AppException {
	}
	
	private void processCheckSubs(String fromNumber, MORequest moReq) {
		LOG.debug("{} check subscription: {}", fromNumber, moReq.getMessage());
		String sendMessage = "";
		try {
			List<Subscriber> lstSubs = subscriberService.findActiveByMsisdn(fromNumber);
			if (!lstSubs.isEmpty()) {
				sendMessage = mtModel.getMtKT();
				String tenGoi = "";
				for (Subscriber eachSubs : lstSubs) {
					VasPackage vasPackage = this.vasPackageService.findOne(eachSubs.getPackageId());
					if (Strings.isNullOrEmpty(tenGoi)) {
						tenGoi = vasPackage.getDesc();
					} else {
						tenGoi = tenGoi + "; " + vasPackage.getDesc();
					}
					String giaGoi = String.valueOf(vasPackage.getPriceFormattedDot()) + "đ/"
							+ AppUtil.getDurationString(vasPackage.getDuration());

					sendMessage = sendMessage.replaceAll("<START>", sdfFull.format(eachSubs.getRegisterDate()));
					sendMessage = sendMessage.replaceAll("<END>", sdfFull.format(eachSubs.getExpiredDate()));
					sendMessage = sendMessage.replaceAll("<MAGOI>", vasPackage.getName());
					sendMessage = sendMessage.replaceAll("<GIAGOI>", giaGoi);
				}
				sendMessage = sendMessage.replaceAll("<TENGOI>", tenGoi);

			} else {
				sendMessage = mtModel.getMtNonSubsKT();
			}
		} catch (Exception e) {
			LOG.error("", e);
			sendMessage = mtModel.getMtSystemError();
		}
		//Send MT
		MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, fromNumber, sendMessage, moReq, moReq.getCommand());
		mtReq.setBrandName(false);
		sendMT(mtReq);
	}
	
	private void processPasswordSubs(String fromNumber, String mpin, boolean reset, MORequest moReq) {
		/**
		 * 1. Check subscription exists
		 * 3. Reset password
		 */
		String msg = "";
		try {
			boolean setDone = false;
			String action = "";
			Date dte = new Date();
			List<Subscriber> lstSubs = subscriberService.findActiveByMsisdn(fromNumber);
			if (!lstSubs.isEmpty()) {
				if (reset) {
					msg = mtModel.getMtMK();
					action = CommandCode.RESET_PWD;
				} else {
					msg = mtModel.getMtMK();
					action = CommandCode.SET_PWD;
				}
				setDone = true;
			} else {
				msg = mtModel.getMtNonSubsMK();
			}
			msg = msg.replaceAll("<MATKHAU>", mpin);
			if (setDone) {
				HandlingResult postResult = cpGateService.notifyResetPassword(moReq.getSmsId(), fromNumber, mpin, Util.BIBIBOOK_SDF.format(dte), moReq.getMessage());
				this.subscriberService.logSubsRequest(lstSubs.get(0), action, moReq.getSmsId(),
						postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
			}
		} catch (Exception e) {
			LOG.error("", e);
			msg = mtModel.getMtSystemError();
		}
		//Send MT
		MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, fromNumber, msg, moReq, moReq.getCommand());
		sendMT(mtReq);
	}
	
	private void sendMT(MTRequest mtReq) {
		mtQueueToCSP.offer(mtReq);
		synchronized (mtQueueToCSPNotifier) {
			mtQueueToCSPNotifier.notifyAll();
		}
	}
	
}
