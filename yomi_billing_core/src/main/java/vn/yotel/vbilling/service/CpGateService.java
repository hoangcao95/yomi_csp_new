package vn.yotel.vbilling.service;

import vn.yotel.vbilling.model.HandlingResult;

/**
 *
 */
public interface CpGateService {
	
	public HandlingResult notifyRegSubs(String transId, String msisdn, String mpin, int amount, boolean regNew, String regDate, String expiredDate,
			String originalSms, String channel, String vasPackageCode, boolean status, String providerCode, String refCode, String errorCode, String errorDesc, String action);
	
//	public HandlingResult notifyBuyPackage(String transId, String msisdn, String mpin, int amount, boolean regNew, String regDate, String expiredDate,
//			String originalSms, String channel, String vasPackageCode, boolean status, String providerCode, String refCode, String errorCode, String errorDesc);
	
	public HandlingResult notifyRechargeSubs(String transId, String msisdn, String chargeDate, String expiredDate, int amount,
			String channel, String vasPackageCode, boolean status, String errorCode, String errorDesc);

	public HandlingResult notifyCancelSubs(String transId, String msisdn, String cancelDate, String originalSms, String channel,
			String vasPackageCode, boolean status, String errorCode, String errorDesc);

	public HandlingResult notifyResetPassword(String transId, String fromNumber, String mpin, String datetime, String originalSms);

	
}
