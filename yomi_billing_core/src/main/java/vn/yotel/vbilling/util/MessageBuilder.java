package vn.yotel.vbilling.util;

import java.util.Date;

import vn.yotel.vbilling.jpa.ChargeLog;
import vn.yotel.vbilling.jpa.DetectNumberLog;
import vn.yotel.vbilling.model.MORequest;
import vn.yotel.vbilling.model.MTRequest;

public class MessageBuilder {

	/**
	 * 
	 * @param datetime
	 * @param msisdn
	 * @param bNumber
	 * @param categoryId
	 * @param spId
	 * @param cpId
	 * @param contentId
	 * @param status
	 * @param cost
	 * @param channelType
	 * @param otherInfo
	 * @return
	 */
//	public static CDRRecord buildCDRRecord(String datetime, String msisdn,
//			String bNumber, String categoryId, String spId, String cpId,
//			String contentId, String status, String cost, String channelType,
//			String otherInfo, String costKM2, String costKM3) {
//		CDRRecord record = new CDRRecord();
//		record.setDatetime(datetime);
//		record.setMsisdn(msisdn);
//		record.setbNumber(bNumber);
//		record.setCategoryId(categoryId);
//		record.setSpcpId(spId + cpId);
//		record.setContentId(contentId);
//		record.setStatus(status);
//		record.setCost(cost);
//		record.setChannelType(normalizeChannel(channelType));
//		record.setInfomation(otherInfo);
//		record.setCostKM2(costKM2);
//		record.setCostKM3(costKM3);
//		return record;
//	}
	
	/**
	 * 
	 * @param transDate
	 * @param type
	 * @param callStatus
	 * @param resultStatus
	 * @param reqData
	 * @param respData
	 * @return
	 */
	public static ChargeLog buildChargeLog(String msisdn, float amount, Date transDate, String type, boolean callStatus, int resultStatus, String reqData, String respData) {
		ChargeLog chargeLog = new ChargeLog();
		chargeLog.setMsisdn(msisdn);
		chargeLog.setAmount(amount);
		chargeLog.setTransDate(transDate);
		chargeLog.setType(type);
		chargeLog.setCallStatus(callStatus);
		chargeLog.setResultStatus(resultStatus == 200 ? true : false);
		chargeLog.setReqData(reqData);
		chargeLog.setRespData(respData);
		return chargeLog;
	}
	
	/**
	 * 
	 * @param shortCode
	 * @param toNumber
	 * @param message
	 * @param moReq
	 * @return
	 */
	public static MTRequest buildMTRequest(String shortCode, String toNumber, String message, MORequest moReq, String mtType) {
		MTRequest mtReq = new MTRequest();
		mtReq.setFromNumber(shortCode);
		mtReq.setToNumber(toNumber);
		mtReq.setMessage(message);
		mtReq.setMoReq(moReq);
		if (moReq != null) {
			mtReq.setChannel("SMS");
		} else {
			mtReq.setChannel("SYSTEM");
		}
		mtReq.setMtType(mtType);
		return mtReq;
	}
	
	/**
	 * 
	 * @param callerIp
	 * @param sourceIp
	 * @param msisdn
	 * @param detected
	 * @return
	 */
	public static DetectNumberLog buildDetectNumberLog(Date transDate, String callerIp, String sourceIp, String msisdn, boolean detected) {
		DetectNumberLog log = new DetectNumberLog();
		log.setTransDate(transDate);
		log.setCallerIp(callerIp);
		log.setSourceIp(sourceIp);
		log.setMsisdn(msisdn);
		log.setResultStatus(detected);
		return log;
	}
	
	/**
	 * 
	 * @param msisdn
	 * @param packageCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
//	public static DailyVasReportRecord buildDailyVasReport(String msisdn, String packageCode, Date startDate, Date endDate) {
//		DailyVasReportRecord record = new DailyVasReportRecord();
//		record.setMsisdn(msisdn);
//		record.setPackageCode(packageCode);
//		record.setStartDate(VasGate.SDF.format(startDate));
//		record.setEndDate(VasGate.SDF.format(endDate));
//		return record;
//	}

	/**
	 * 
	 * @param msisdn
	 * @param packageCode
	 * @param price
	 * @return
	 */
//	public static BigPromosCDRRecord buildBigPromosCDRRecord(String msisdn, String packageCode, Float cost) {
//		BigPromosCDRRecord record = new BigPromosCDRRecord();
//		record.setMsisdn(msisdn);
//		record.setPackageCode(packageCode);
//		record.setCost(cost);
//		return record;
//	}
}