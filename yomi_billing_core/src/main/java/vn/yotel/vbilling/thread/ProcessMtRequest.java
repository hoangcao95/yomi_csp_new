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



public class ProcessMtRequest extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessMtRequest.class);
	
	private ConcurrentLinkedQueue<MTRequest> mtQueue;
	private Object mtQueueNotifier;
	
	private ConcurrentLinkedQueue<MTRequest> mtContentQueue;
	@SuppressWarnings("unused")
	private Object mtContentQueueNotifier;
	
	private SmsService smsService;
	private ConcurrentLinkedQueue<MTRequest> mtQueueToSMSC;
	private Object mtQueueToSMSCNotifier;

	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadParameters () throws AppException {
		if (this.params != null) {
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
		mtQueue = (ConcurrentLinkedQueue<MTRequest>) getBean("mtQueue");
		mtQueueNotifier = AppContext.getBean("mtQueueNotifier");
		
		mtContentQueue = (ConcurrentLinkedQueue<MTRequest>) getBean("mtContentQueue");
		mtContentQueueNotifier = AppContext.getBean("mtContentQueueNotifier");
		
		mtQueueToSMSC = (ConcurrentLinkedQueue<MTRequest>) getBean("mtQueueToSMSC");
		mtQueueToSMSCNotifier = getBean("mtQueueToSMSCNotifier");
    }
	
	@Override
	protected void initializeSession() throws AppException {
		smsService = (SmsService) AppContext.getBean("smsService");
    }
	
	@Override
	protected boolean processSession() throws AppException {
		try {
			while (!requireStop) {
				// MtContent is highest priority
				MTRequest mtReq = mtContentQueue.poll();
				if (mtReq == null) {
					mtReq = mtQueue.poll();
				}
				if (mtReq != null) {
					LOG.debug("Process MT request: {}", mtReq.toString());
					if (!mtReq.isProcessed()) {
						if (mtReq.getGateway().equals(MtGateway.SMPP_HN.value())) {
							mtQueueToSMSC.offer(mtReq);
							synchronized (mtQueueToSMSCNotifier) {
								mtQueueToSMSCNotifier.notifyAll();
							}
						} else if (mtReq.getGateway().equals(MtGateway.SMPP_HCM.value())) {
							mtQueueToSMSC.offer(mtReq);
							synchronized (mtQueueToSMSCNotifier) {
								mtQueueToSMSCNotifier.notifyAll();
							}
						} else {
							LOG.error("Invalid MtGateway: {}", mtReq.getGateway());
						}
					}
					this.smsService.logMtRequest(mtReq);
				} else {
					synchronized (mtQueueNotifier) {
						mtQueueNotifier.wait(100L);
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
