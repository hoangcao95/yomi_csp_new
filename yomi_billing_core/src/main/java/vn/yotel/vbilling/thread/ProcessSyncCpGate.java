package vn.yotel.vbilling.thread;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.Util;
import vn.yotel.thread.Constants.HAInfo.HAMode;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.jpa.SubsRequest;
import vn.yotel.vbilling.model.HandlingResult;
import vn.yotel.vbilling.service.CpGateService;
import vn.yotel.vbilling.service.SubscriberService;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants.Channel;
import vn.yotel.yomi.Constants.CommandCode;

public class ProcessSyncCpGate extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessSyncCpGate.class);
	
	private CpGateService cpGateService;
	private SubscriberService subscriberService;
	
	private int numbefOfBackDays = 1;
	@Override
	protected void loadParameters () throws AppException {
		if (this.params != null) {
			numbefOfBackDays = this.getParamAsInt("back-days");
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
		cpGateService = (CpGateService) AppContext.getBean("cpGateService");
		subscriberService = (SubscriberService) AppContext.getBean("subscriberService");
    }
	
	@Override
	protected void initializeSession() throws AppException {
		active = HAMode.MASTER.equals(AppParams.SERVER_MODE);
    }
	
	@Override
	protected boolean processSession() throws AppException {
		while (!active && !requireStop) {
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
		try {
			List<SubsRequest> failedRequests = subscriberService.getFailedLogSubsRequest(numbefOfBackDays);
			LOG.debug("Process sync for: {} request(s)", failedRequests.size());
			while ((failedRequests.size() > 0) && !requireStop) {
				SubsRequest eachElement = failedRequests.remove(0);
				try {
					HandlingResult postResult = null;
					if (eachElement.getCommand().equals(CommandCode.REGISTER)) {
						JSONObject data = new JSONObject(eachElement.getData());
						String mpin = data.getString("mpin");
						int amount = data.getInt("amount");
						boolean regNew = data.getBoolean("regNew");
						String regDate = data.getString("regDate");
						String expiredDate = data.getString("expiredDate");
						String subsPackageCode = data.getString("subsPackageCode");
						boolean regDone = data.getBoolean("status");
						String channel = data.getString("channel");
						String transId = data.optString("transId", Util.generateTransId());
						String errorCode = data.getString("errorCode");
						String errorDesc = data.getString("errorDesc");
						if (Channel.SMS.equals(channel)) {
							String originalSms = data.optString("originalSms", "");
							postResult = cpGateService.notifyRegSubs(transId, eachElement.getMsisdn(), mpin, amount, regNew, regDate, expiredDate, originalSms, channel, subsPackageCode, regDone, "", "", errorCode, errorDesc, CommandCode.REGISTER_SUBS_PACKAGE);
						} else {
							String providerCode = data.optString("providerCode", "");
							String refCode = data.optString("refCode", "");
							String originalSms = "";
							postResult = cpGateService.notifyRegSubs(transId, eachElement.getMsisdn(), mpin, amount, regNew, regDate, expiredDate, originalSms, channel, subsPackageCode, regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.REGISTER_SUBS_PACKAGE);
						}
					} else if (eachElement.getCommand().equals(CommandCode.RENEW_DAY)) {
						JSONObject data = new JSONObject(eachElement.getData());
						int amount =  data.getInt("amount");
						String providerCode = data.getString("providerCode");
						String errorCode = data.getString("errorCode");
						String errorDesc = data.getString("errorDesc");
						String transId = data.optString("transId", Util.generateTransId());
						String expiredDate = data.getString("expiredDate");
						String channel = data.getString("channel");
						String chargeDate = data.getString("chargeDate");
						String originalSms = data.getString("originalSms");
						String subsPackageCode = data.getString("subsPackageCode");
						String mpin = data.getString("mpin");
						String refCode = data.getString("refCode");
						boolean regNew = data.getBoolean("regNew");
						boolean status = data.getBoolean("status");
						boolean regDone = data.getBoolean("status");
						postResult = cpGateService.notifyRegSubs(transId, eachElement.getMsisdn(), mpin, amount, regNew, chargeDate,
								expiredDate, originalSms, channel, subsPackageCode, regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.EXTEND_SUBS_PACKAGE);
					} else if (eachElement.getCommand().equals(CommandCode.CANCEL)) {
						JSONObject data = new JSONObject(eachElement.getData());
						String originalSms = data.getString("originalSms");
						String subsPackageCode = data.getString("subsPackageCode");
						String cancelDate = data.getString("cancelDate");
						String channel = data.getString("channel");
						boolean cancelDone = data.getBoolean("status");
						String errorCode = data.getString("errorCode");
						String errorDesc = data.getString("errorDesc");
						String transId = data.optString("transId", Util.generateTransId());
						postResult = cpGateService.notifyCancelSubs(transId, eachElement.getMsisdn(), cancelDate, originalSms, channel, subsPackageCode, cancelDone, errorCode, errorDesc);
					} else if (eachElement.getCommand().equals(CommandCode.RESET_PWD)) {
						JSONObject data = new JSONObject(eachElement.getData());
						String originalSms = data.getString("originalSms");
						String changeDate = data.getString("changeDate");
						String mpin = data.getString("mpin");
						String transId = data.optString("transId", Util.generateTransId());
						postResult = cpGateService.notifyResetPassword(transId, eachElement.getMsisdn(), mpin, changeDate, originalSms);
					}
					if (postResult != null) {
						this.subscriberService.updateLogStatus(eachElement, postResult.parseToHttpCode());							
					}
				} catch (Exception e) {
					LOG.error("", e);
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
	
	@Override
	public void notifyEnteringNewMode(String serverOldMode, String serverNewMode) {
		if ((serverNewMode != null) && serverNewMode.equals(serverOldMode)) {
			LOG.warn("Should not call notify when there is nothing changed");
			return;
		}
		if (HAMode.MASTER.equals(serverNewMode)) {
			active = true;
			synchronized (activeMonitorObj) {
				activeMonitorObj.notifyAll();
			}
		} else {
			active = false;
			LOG.warn("Going to stop BB synchronized thread while entering BACKUP mode");
		}
	}
}
