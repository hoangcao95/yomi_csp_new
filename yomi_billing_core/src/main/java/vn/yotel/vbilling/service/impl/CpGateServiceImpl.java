package vn.yotel.vbilling.service.impl;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import vn.yotel.vbilling.model.HandlingResult;
import vn.yotel.vbilling.model.HandlingResult.HandlingResultBuilder;
import vn.yotel.vbilling.service.CpGateService;
import vn.yotel.vbilling.util.AppUtil;
import vn.yotel.yomi.Constants;

@Service(value = "cpGateService")
@Transactional
public class CpGateServiceImpl implements CpGateService {

	private Logger LOG = LoggerFactory.getLogger(CpGateServiceImpl.class);
	
	private String regSubsApiURL = "/subscription/sms_subs";
	private String buyPackageApiURL = "/notifications/sub_once";
	private String chargeApiURL = "/notifications/charge";
	private String cancelSubsApiURL = "/notifications/unsub";
	private String changePasswordApiURL = "/notifications/changepwd";
	
	@Resource String cpGatewayUrl;
	@Resource String authorizationKey;

	@Override
	public HandlingResult notifyRegSubs(String transId, String msisdn, String mpin, int amount, boolean regNew, String regDate,
			String expiredDate, String originalSms, String channel, String subsPackageCode, boolean status, String providerCode, String refCode, String errorCode, String errorDesc, String action) {
		HandlingResult result = null;
		JSONObject apiData = new JSONObject();
		try {
			String strAmount = (int) amount + "";
			apiData.put("transId", transId)
					.put("msisdn", msisdn)
					.put("mpin", mpin)
					.put("amount", amount)
					.put("regNew", regNew)
					.put("regDate", regDate)
					.put("expiredDate", expiredDate)
					.put("originalSms", originalSms)
					.put("channel", channel)
					.put("subsPackageCode", subsPackageCode)
					.put("status", status)
					.put("providerCode", providerCode)
					.put("refCode", refCode)
					.put("errorCode", errorCode)
					.put("errorDesc", errorDesc);
			String hash = AppUtil.computeHash(Constants.CommandCode.HASH_PRIVATE_KEY, transId, msisdn, action, subsPackageCode, originalSms, amount + "");
			HttpResponse<JsonNode> resp = Unirest.post(cpGatewayUrl + regSubsApiURL)
					.header("Authorization", authorizationKey)
					.field("trans_id", transId)
					.field("hash", hash)
					.field("msisdn", msisdn)
					.field("action", action)
					.field("subs_package_code", subsPackageCode)
					.field("origin_sms", originalSms)
					.field("amount", strAmount)
					.field("charge_time", regDate)
					.field("register_time", regDate)
					.field("expired_time", expiredDate)
					.field("success", status ? 1 : 0)
					.field("password", mpin)
					.field("error", errorDesc)
					.field("error_code", errorCode)
					.asJson();
			if (resp.getStatus() == 200) {
				LOG.debug("Call API successfully");
				JSONObject jsonData = resp.getBody().getObject();
				int returnCode = jsonData.getInt("status");
				if (returnCode == 200) {
					result = HandlingResultBuilder.SUCCESS();
				} else {
					result = HandlingResultBuilder.FAIL(returnCode + "", jsonData.optString("message" , "null"));
				}
				result.setOption(jsonData);
			} else {
				LOG.warn("Could not process -> status: {}, data: {}", resp.getStatusText(), "");
				result = HandlingResultBuilder.FAIL(String.valueOf(resp.getStatus()), "Call API failed");
			}
		} catch (Exception e) {
			LOG.error("", e);
			result = HandlingResultBuilder.FAIL("9999", "Exception: " + e.getMessage());
		}
		result.setData(apiData);
		return result;
	}
	
//	@Override
//	public HandlingResult notifyBuyPackage(String transId, String msisdn, String mpin, int amount, boolean regNew, String regDate,
//			String expiredDate, String originalSms, int channel, String subsPackageCode, boolean status, String providerCode, String refCode, String errorCode, String errorDesc) {
//		HandlingResult result = null;
//		JSONObject apiData = new JSONObject();
//		try {
//			String strAmount = (int) amount + "";
//			apiData.put("transId", transId)
//					.put("msisdn", msisdn)
//					.put("mpin", mpin)
//					.put("amount", amount)
//					.put("regNew", regNew)
//					.put("regDate", regDate)
//					.put("expiredDate", expiredDate)
//					.put("originalSms", originalSms)
//					.put("channel", channel)
//					.put("subsPackageCode", subsPackageCode)
//					.put("status", status)
//					.put("providerCode", providerCode)
//					.put("refCode", refCode)
//					.put("errorCode", errorCode)
//					.put("errorDesc", errorDesc);
//
//			HttpResponse<JsonNode> resp = Unirest.post(cpGatewayUrl + buyPackageApiURL)
//					.header("Authorization", authorizationKey)
//					.field("transactionNo", transId)
//					.field("transactionTime", regDate)
//					.field("expiredTime", expiredDate)
//					.field("sms", originalSms)
//					.field("msisdn", msisdn)
//					.field("packageCode", subsPackageCode)
//					.field("chargeAmount", strAmount)
//					.field("channel", channel)
//					.field("newSub", regNew ? 1 : 0)
//					.field("password", mpin)
//					.field("status", status ? 1 : 0)
//					.field("affiliateId", providerCode)
//					.field("appCode", refCode)
//					.field("errorCode", errorCode)
//					.field("errorDescription", errorDesc)
//					.asJson();
//			if (resp.getStatus() == 200) {
//				LOG.debug("Call API successfully");
//				JSONObject jsonData = resp.getBody().getObject();
//				int returnCode = jsonData.getInt("status");
//				if (returnCode == 200) {
//					result = HandlingResultBuilder.SUCCESS();
//				} else {
//					result = HandlingResultBuilder.FAIL(returnCode + "", jsonData.optString("message" , "null"));
//				}
//				result.setOption(jsonData);
//			} else {
//				LOG.warn("Could not process -> status: {}, data: {}", resp.getStatusText(), "");
//				result = HandlingResultBuilder.FAIL(String.valueOf(resp.getStatus()), "Call API failed");
//			}
//		} catch (Exception e) {
//			LOG.error("", e);
//			result = HandlingResultBuilder.FAIL("9999", "Exception: " + e.getMessage());
//		}
//		result.setData(apiData);
//		return result;
//	}

	@Override
	public HandlingResult notifyRechargeSubs(String transId, String msisdn, String chargeDate, String expiredDate, int amount,
			String channel, String subsPackageCode, boolean status, String errorCode, String errorDesc) {
		HandlingResult result = null;
		JSONObject apiData = new JSONObject();
		try {
			String strAmount = (int) amount + "";
			apiData.put("transId", transId)
					.put("msisdn", msisdn)
					.put("chargeDate", chargeDate)
					.put("expiredDate", expiredDate)
					.put("amount", amount)
					.put("channel", channel)
					.put("subsPackageCode", subsPackageCode)
					.put("status", status)
					.put("errorCode", errorCode)
					.put("errorDesc", errorDesc);
			
			HttpResponse<JsonNode> resp = Unirest.post(cpGatewayUrl + chargeApiURL)
					.header("Authorization", authorizationKey)
					.field("transactionNo", transId)
					.field("transactionTime", chargeDate)
					.field("expiredTime", expiredDate)
					.field("msisdn", msisdn)
					.field("packageCode", subsPackageCode)
					.field("chargeAmount", strAmount)
					.field("chargeStatus", status ? 1 : 0)
					.field("errorCode", errorCode)
					.field("errorDescription", errorDesc)
					.asJson();
			if (resp.getStatus() == 200) {
				LOG.debug("Call API successfully");
				JSONObject jsonData = resp.getBody().getObject();
				int returnCode = jsonData.getInt("status");
				if (returnCode == 200) {
					result = HandlingResultBuilder.SUCCESS();
				} else {
					result = HandlingResultBuilder.FAIL(returnCode + "", jsonData.optString("message" , "null"));
				}
				result.setOption(jsonData);
			} else {
				LOG.warn("Could not process -> status: {}, data: {}", resp.getStatusText(), "");
				result = HandlingResultBuilder.FAIL(String.valueOf(resp.getStatus()), "Call API failed");
			}
		} catch (Exception e) {
			LOG.error("", e);
			result = HandlingResultBuilder.FAIL("9999", "Exception: " + e.getMessage());
		}
		result.setData(apiData);
		return result;
	}

	@Override
	public HandlingResult notifyCancelSubs(String transId, String msisdn, String cancelDate, String originalSms, String channel,
			String subsPackageCode, boolean status, String errorCode, String errorDesc) {
		HandlingResult result = null;
		JSONObject apiData = new JSONObject();
		try {
			apiData.put("trans_id", transId)
					.put("fromNumber", msisdn)
					.put("subsPackageCode", subsPackageCode)
					.put("originalSms", originalSms)
					.put("status", status)
					.put("datetime", cancelDate);

			String action = Constants.CommandCode.CANCEL_SUBS_PACKAGE;
			String amount = "0";
			String hash = AppUtil.computeHash(Constants.CommandCode.HASH_PRIVATE_KEY, transId, msisdn, action, subsPackageCode, originalSms, amount);
			HttpResponse<JsonNode> resp = Unirest.post(cpGatewayUrl + regSubsApiURL)
					.field("trans_id", transId)
					.field("msisdn", msisdn)
					.field("action", action)
					.field("subs_package_code", subsPackageCode)
					.field("origin_sms", originalSms)
					.field("amount", amount)
					.field("charge_time", cancelDate)
					.field("success", "1")
					.field("error_code", "0000")
					.field("error", "")
					.field("hash", hash)
					.asJson();
			if (resp.getStatus() == 200) {
				LOG.debug("Call API successfully");
				JSONObject jsonData = resp.getBody().getObject();
				int returnCode = jsonData.getInt("status");
				if (returnCode == 200) {
					result = HandlingResultBuilder.SUCCESS();
				} else {
					result = HandlingResultBuilder.FAIL(returnCode + "", jsonData.optString("message" , "null"));
				}
				result.setOption(jsonData);
			} else {
				LOG.warn("Could not process -> status: {}, data: {}", resp.getStatusText(), "");
				result = HandlingResultBuilder.FAIL(String.valueOf(resp.getStatus()), "Call API failed");
			}
		} catch (Exception e) {
			LOG.error("", e);
			result = HandlingResultBuilder.FAIL("9999", "Exception: " + e.getMessage());
		}
		result.setData(apiData);
		return result;
	}
	
	@Override
	public HandlingResult notifyResetPassword(String transId, String fromNumber, String mpin, String datetime, String originalSms) {
		HandlingResult result = null;
		JSONObject apiData = new JSONObject();
		try {
			String action = "RESET_PASSWORD";
			String subsPackageCode = "";
			String amount = "0";
			apiData.put("trans_id", transId)
					.put("fromNumber", fromNumber)
					.put("mpin", mpin)
					.put("originalSms", originalSms)
					.put("datetime", datetime);

			String hash = AppUtil.computeHash(Constants.CommandCode.HASH_PRIVATE_KEY, transId, fromNumber, action, subsPackageCode, originalSms, amount);
			HttpResponse<JsonNode> resp = Unirest.post(cpGatewayUrl + "/subscription/sms_subs")
					.field("trans_id", transId)
					.field("msisdn", fromNumber)
					.field("action", action)
					.field("subs_package_code", subsPackageCode)
					.field("origin_sms", originalSms)
					.field("amount", amount)
					.field("charge_time", datetime)
					.field("success", "1")
					.field("error_code", "0000")
					.field("error", "")
					.field("password", mpin)
					.field("hash", hash)
					.asJson();
			if (resp.getStatus() == 200) {
				LOG.debug("Call API successfully");
				JSONObject jsonData = resp.getBody().getObject();
				String returnCode = jsonData.getString("status");
				if ("0".equals(returnCode)) {
					result = HandlingResultBuilder.SUCCESS();
				} else {
					result = HandlingResultBuilder.FAIL(returnCode, jsonData.optString("message" , ""));
				}
			} else {
				LOG.warn("Could not process -> status: {}, data: {}", resp.getStatusText(), "");
				result = HandlingResultBuilder.FAIL(String.valueOf(resp.getStatus()), "Call API failed");
			}
		} catch (Exception e) {
			LOG.error("", e);
			result = HandlingResultBuilder.FAIL("9999", "Exception: " + e.getMessage());
		}
		result.setData(apiData);
		return result;
	}
}
