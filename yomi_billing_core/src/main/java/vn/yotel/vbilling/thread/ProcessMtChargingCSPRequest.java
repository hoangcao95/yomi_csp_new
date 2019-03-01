package vn.yotel.vbilling.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MTRequest.MtGateway;
import vn.yotel.vbilling.service.SmsService;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.yomi.AppParams;

public class ProcessMtChargingCSPRequest extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessMtChargingCSPRequest.class);

	private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
	private Object mtQueueToCSPNotifier;
	private SmsService smsService;
	private ChargingCSPClient chargingCSPClient;
	private String shortCode;

	@SuppressWarnings("unchecked")
	@Override
	protected void loadParameters() throws AppException {
		if (this.params != null) {
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
		mtQueueToCSP = (ConcurrentLinkedQueue<MTRequest>) getBean("mtQueueToCSP");
		mtQueueToCSPNotifier = getBean("mtQueueToCSPNotifier");
		shortCode = getParamAsString("short-code");
	}

	@Override
	protected void initializeSession() throws AppException {
		smsService = (SmsService) AppContext.getBean("smsService");
		chargingCSPClient = (ChargingCSPClient) getBean("chargingCSPClient");
	}

	@Override
	protected boolean processSession() throws AppException {
		try {
			while (!requireStop) {
				MTRequest mtReq = mtQueueToCSP.poll();
				if (mtReq != null) {
					LOG.info("Process mtQueueToCSP request: {}", mtReq.toString());
					if (!mtReq.isProcessed()) {
						if (mtReq.getGateway().equals(MtGateway.CHARGING_CSP.value())) {
							chargingCSPClient.sendMessage(shortCode, mtReq.getToNumber(), mtReq.getMessage());
						} else {
							LOG.error("Invalid MtGateway: {}", mtReq.getGateway());
						}
					}
					this.smsService.logMtRequest(mtReq);
				} else {
					synchronized (mtQueueToCSPNotifier) {
						mtQueueToCSPNotifier.wait(100L);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return true;
	}

	@Override
	protected void completeSession() throws AppException {
	}

}
