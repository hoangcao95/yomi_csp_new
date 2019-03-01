package vn.yotel.vbilling.resource;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import vn.yotel.admin.jpa.SysParam;
import vn.yotel.admin.service.SysParamService;
import vn.yotel.commons.util.StringUtils;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.ChargeLog;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.jpa.XsPromotion;
import vn.yotel.vbilling.model.*;
import vn.yotel.vbilling.service.*;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;
import vn.yotel.yomi.Constants.CommandCode;


@Component
@Path(value = "/")
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED })
public class ChargingResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChargingResource.class);
	private static final Gson GSON_ALL = new GsonBuilder().serializeNulls().create();
    private static final SimpleDateFormat sdf_YYYYMMDDHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static final SimpleDateFormat sdf_DDMMYYYY = new SimpleDateFormat("ddMMyyyy");

    @Context private HttpServletRequest request;

    @Context private HttpHeaders httpHeaders;

	@Resource private CpGateService cpGateService;
	@Resource private SubscriberService subscriberService;
	@Resource private VasPackageService vasPackageService;
	@Resource private SmsService smsService;
	@Resource private ChargeLogService chargeLogService;
	@Resource private SysParamService sysParamService;
	@Resource private XsPromotionService xsPromotionService;

	@Resource private ConcurrentLinkedQueue<MORequest> moQueue;
	@Resource private Object moQueueNotifier;
	@Resource private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
	@Resource private Object mtQueueToCSPNotifier;

	private final String MSISDN_TEST_KEY = "MSISDN_TEST";
	private final String AS_DKQT_KEY = "AS_DKQT";
	private final String AS_PACKAGE_CODE_KEY = "AS_PACKAGE_CODE";
	private String AS_PACKAGE_CODE_VALUE = null;
	private Date dtCurrent = null;

    private final static ObjectMapper objectMapper = new ObjectMapper();
	@Resource private ChargingCSPClient chargingCSPClient;

    /**
     * Request Body Parameters[{isdn=[934530254], serviceCode=[9755], commandCode=[HUY TKMN],
     * channel=[SMS], charge_price=[], staDatetime=[07/06/2017 16:36:44],
     * regDatetime=[07/06/2017 16:36:44], expireDatetime=[08/06/2017 16:36:43],
     * packageCode=[TKMN_GOINGAY], message_send=[(DK) Quy khach da dang ky thanh cong Goi ngay Ket qua xo so Mien Bac dich vu iLoto do HaThanh cung cap. Cuoc dich vu 1000d/ngay, gia han hang ngay. Vui long truy cap http://iloto.vn de cap nhat thong tin xo so moi nhat. De huy dich vu, soan HUY XSMB gui 9755. Chi tiet LH 19000105 (1000d/phut). Tran trong cam on!],
     * endDatetime=[08/06/2017 14:22:21], groupCode=[ILOTO], status=[3]}]
     */
	@POST
	@Path(value = "/updatePackage")
	public ResponseData updatePackage(
			@Context HttpServletRequest req,
			@FormParam("isdn") String isdn,
			@FormParam("serviceCode") String serviceCode,
			@FormParam("commandCode") String commandCode,
			@FormParam("org_request") String orgRequest,
			@FormParam("channel") String channel,
			@FormParam("status") String status,
			@FormParam("groupCode") String groupCode,
			@FormParam("packageCode") String packageCode,
			@FormParam("charge_price") String chargePrice,
			@FormParam("regDatetime") String regDatetime,
			@FormParam("staDatetime") String staDatetime,
			@FormParam("endDatetime") String endDatetime,
			@FormParam("expireDatetime") String expireDatetime,
			@FormParam("message_send") String messageSend) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("isdn", isdn);
			parameters.put("serviceCode", StringUtils.nvl(serviceCode, ""));
			parameters.put("commandCode", StringUtils.nvl(commandCode, ""));
			parameters.put("org_request", StringUtils.nvl(orgRequest, ""));
			parameters.put("channel", StringUtils.nvl(channel, ""));
			parameters.put("status", StringUtils.nvl(status, ""));
			parameters.put("groupCode", StringUtils.nvl(groupCode, ""));
			parameters.put("packageCode", StringUtils.nvl(packageCode, ""));
			parameters.put("charge_price", StringUtils.nvl(chargePrice, ""));
			parameters.put("regDatetime", StringUtils.nvl(regDatetime, ""));
			parameters.put("staDatetime", StringUtils.nvl(staDatetime, ""));
			parameters.put("endDatetime", StringUtils.nvl(endDatetime, ""));
			parameters.put("expireDatetime", StringUtils.nvl(expireDatetime, ""));
			LOG.info("updatePackage[{}]", objectMapper.writeValueAsString(parameters));
		} catch (Exception e) {
			LOG.error("", e);
		}

//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.YEAR, 2019);
//		cal.set(Calendar.MONTH, 2);
//		cal.set(Calendar.DATE, 12);
//		cal.set(Calendar.HOUR, 23);
//		cal.set(Calendar.MINUTE, 59);
//		cal.set(Calendar.SECOND, 59);
//
//		Calendar calCurrent = Calendar.getInstance();

		isdn = Util.normalizeMsIsdn(isdn);
//		Map<String, Object> result = new HashMap<String, Object>();
		Date now = new Date();
		String mpin = Util.generateMPIN();
		String transId = Util.generateTransId();
		String providerCode = "";
		String refCode = "";
		String errorCode = "0000";
		String errorDesc = "";
		int intChannel;
		switch (channel) {
			case "WAP":
				intChannel = 1;
				break;
			case "APP":
				intChannel = 2;
				break;
			case "WEB":
				intChannel = 3;
				break;
			case "VASGATE":
				intChannel = 4;
				break;
			case "BIGTET2016":
				intChannel = 5;
				break;
			case "EURO2016":
				intChannel = 6;
				break;
			case "D10EU":
				intChannel = 7;
			case "BIGPRODATA2016_FREE":
				intChannel = 8;
				break;
			case "BIGPRODATA2016_PAID":
				intChannel = 9;
				break;
			case "PRO_COMBO":
				intChannel = 10;
				break;
			default:
				intChannel = 0;
				break;
		}

		boolean regNew = true;
		boolean regDone = true;
		//Bien kiem tra thong tin thue bao la DK hay tai DK
		int reRegNew = 0;
		//Luu thong tin ngay dang ky cua thue bao DK lai
		Date regDateSubs = null;

		String originalSms = orgRequest;
		int amount = Strings.isNullOrEmpty(chargePrice) ? 0 : Integer.valueOf(chargePrice);

		SysParam sysParamPkgCode = sysParamService.findByKey(AS_PACKAGE_CODE_KEY);
		if (sysParamPkgCode != null && !"".equals(sysParamPkgCode.getValue())) {
			AS_PACKAGE_CODE_VALUE = sysParamPkgCode.getValue();
		}
		SysParam sysParam = sysParamService.findByKey(AS_DKQT_KEY);
		dtCurrent = Calendar.getInstance().getTime();

		MORequest moRequest = null;
		MTRequest mtRequest = null;
		MTRequest mtRequest2 = null;
		ChargeLog chargeLog =  new ChargeLog();
		try {
			//process insert MO-MT
			if (!Strings.isNullOrEmpty(orgRequest)) {
				moRequest = new MORequest();
				moRequest.setSmsId(Util.generateTransId());
				moRequest.setProcessed(true);
				moRequest.setCommand(CommandCode.DEFAULT);
				moRequest.setFromNumber(isdn);
				moRequest.setToNumber(AppParams.SHORT_CODE);
				moRequest.setMessage(orgRequest);
				moRequest.setReceivedDate(new Date());
			}
			VasPackage vasPackage = vasPackageService.findByName(packageCode);
			if (vasPackage == null) {
				LOG.warn("Could not find corresponding packageCode: {}", packageCode);
				return ResponseData.responseData("0", "Khong tin thay ma goi cuoc " + packageCode);
			} else {
				Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(isdn, vasPackage.getId());
				// status 0: Gia hạn, 1: Đăng kí, 3: Hủy, 2: Chờ confirm
				if(!"2".equals(status)) {
					if (Strings.isNullOrEmpty(endDatetime)) {
						//Gia dich dang ky hoac gia han
						String command = "";
						if (subscriber == null) {
							reRegNew = 1;

							subscriber = new Subscriber();
							subscriber.setMsisdn(isdn);
							subscriber.setMpin(mpin);
							subscriber.setChannel(intChannel);
							subscriber.setCreatedDate(now);
							subscriber.setModifiedDate(now);
							subscriber.setRegisterDate(Util.SDF_DDMMYYYYHHMMSS.parse(regDatetime));
							subscriber.setExpiredDate(Util.SDF_DDMMYYYYHHMMSS.parse(expireDatetime));
							subscriber.setLastChargedDate(null);
							subscriber.setProductId(vasPackage.getProductId());
							subscriber.setPackageId(vasPackage.getId());
							subscriber.setRegNew(1);
							subscriber.setStatus(1);
							subscriberService.create(subscriber);

							command = CommandCode.REGISTER;
							HandlingResult postResult = cpGateService.notifyRegSubs(transId, isdn, mpin, amount, regNew, Util.XBD_SDF.format(now),
									Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, channel, packageCode, regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.REGISTER_SUBS_PACKAGE);
							this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
							chargeLog.setAmount(amount);
							chargeLog.setMsisdn(isdn);
							chargeLog.setReqData(postResult.parseObjData());
							chargeLog.setRespData(postResult.parseResp());
							chargeLog.setResultStatus(postResult.parseToHttpCode() == 200 ? true : false);
							chargeLog.setTransDate(now);
							chargeLog.setType(CommandCode.REGISTER);
							chargeLog.setCallStatus(true);
							chargeLogService.create(chargeLog);

							//Neu dung cu phap DK QT thi tao thong tin CTKM Kham pha an so
							//Neu trong khoang thoi gian CTKM thi gui MT
//							if (calCurrent.getTime().after(cal.getTime())) {
								if (AS_PACKAGE_CODE_VALUE != null && Arrays.asList(AS_PACKAGE_CODE_VALUE.split(";")).contains(commandCode.replaceAll("\\s+", ""))) {
									if (sysParam != null) {
										//Tao thong tin ban ghi KPAS
										XsPromotion xsPromotion = new XsPromotion();
										xsPromotion.setNumber(500);//Dang ky moi dc 500 diem
										xsPromotion.setMsisdn(isdn);
										xsPromotion.setStatus("ACTIVE");
										xsPromotion.setVasPackageCode(packageCode);
										xsPromotion.setNumberSms(5);
										xsPromotion.setCreatedDate(now);
										xsPromotion.setCreatedTime(new Timestamp(now.getTime()));
										xsPromotion.setTimeId(sdf_YYYYMMDDHHmmssSSS.format(dtCurrent));
										xsPromotionService.create(xsPromotion);
									}
								}
//							}
						} else {
							if (amount == 0) {
								regDone = false;
							}
							if (subscriber.getStatus() == 1) {
								subscriber.setRegNew(0);
								subscriber.setModifiedDate(now);
								subscriber.setRegisterDate(Util.SDF_DDMMYYYYHHMMSS.parse(regDatetime));


								subscriber.setExpiredDate(Util.SDF_DDMMYYYYHHMMSS.parse(expireDatetime));
								subscriberService.update(subscriber);
								command = CommandCode.RENEW_DAY;
	//							HandlingResult postResult = cpGateService.notifyRechargeSubs(transId, isdn, Util.XBD_SDF.format(subscriber.getRegisterDate()), Util.XBD_SDF.format(subscriber.getExpiredDate()), amount, channel, vasPackage.getName(), regDone, errorCode, errorDesc);
								HandlingResult postResult = cpGateService.notifyRegSubs(transId, isdn, mpin, amount, regNew, Util.XBD_SDF.format(now),
										Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, channel, packageCode, regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.EXTEND_SUBS_PACKAGE);
								this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
								chargeLog.setAmount(amount);
								chargeLog.setMsisdn(isdn);
								chargeLog.setReqData(postResult.parseObjData());
								chargeLog.setRespData(postResult.parseResp());
								chargeLog.setResultStatus(postResult.parseToHttpCode() == 200 ? true : false);
								chargeLog.setTransDate(now);
								chargeLog.setType(CommandCode.RENEW_DAY);
								chargeLog.setCallStatus(true);
								chargeLogService.create(chargeLog);
//								LOG.debug("updatePackage.RENEW_DAY {}", isdn);

								//Neu KH gia han thi se cong lai luot gui tin mien phi cho KH
								//Neu trong khoang thoi gian CTKM thi gui MT
//								if (calCurrent.getTime().after(cal.getTime())) {
									if (orgRequest != null && "GH_YOMI_NGAY QT".equals(orgRequest.toUpperCase()) && (packageCode.equals("QT") || packageCode.equals("DK QT"))) {
										List<XsPromotion> lstPromotion = xsPromotionService.findPromotion(isdn, "ACTIVE");
										if (lstPromotion != null && lstPromotion.size() > 0) {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = lstPromotion.get(0);
											xsPromotion.setNumber(1000);//Gia han thi cong 1000 diem/ ngay
//										xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setNumberSms(5);
											xsPromotion.setModifyTime(new Timestamp(now.getTime()));
											xsPromotionService.update(xsPromotion);
										} else {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = new XsPromotion();
											xsPromotion.setNumber(1000);//Gia han thi cong 1000 diem/ ngay
											xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setMsisdn(isdn);
											xsPromotion.setStatus("ACTIVE");
											xsPromotion.setNumberSms(5);
											xsPromotion.setCreatedDate(now);
											xsPromotion.setCreatedTime(new Timestamp(now.getTime()));
											xsPromotion.setTimeId(sdf_YYYYMMDDHHmmssSSS.format(dtCurrent));
											xsPromotionService.create(xsPromotion);
										}
									}
//								}
							} else if (subscriber.getStatus() == 0) {
								//Thue bao tai dang ky lai
								reRegNew = 2;
								regDateSubs = subscriber.getRegisterDate();

								subscriber.setStatus(1);
								subscriber.setRegNew(0);
								subscriber.setModifiedDate(now);
								subscriber.setRegisterDate(Util.SDF_DDMMYYYYHHMMSS.parse(regDatetime));
								subscriber.setExpiredDate(Util.SDF_DDMMYYYYHHMMSS.parse(expireDatetime));
								subscriberService.update(subscriber);
								command = CommandCode.REGISTER;
								HandlingResult postResult = cpGateService.notifyRegSubs(transId, isdn, mpin, amount, regNew, Util.XBD_SDF.format(now),
										Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, channel, packageCode, regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.RE_REGISTER_SUB);
								this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
								chargeLog.setAmount(amount);
								chargeLog.setMsisdn(isdn);
								chargeLog.setReqData(postResult.parseObjData());
								chargeLog.setRespData(postResult.parseResp());
								chargeLog.setResultStatus(postResult.parseToHttpCode() == 200 ? true : false);
								chargeLog.setTransDate(now);
								chargeLog.setType(CommandCode.REGISTER);
								chargeLog.setCallStatus(true);
								chargeLogService.create(chargeLog);

								//Neu KH gia han thi se cong lai luot gui tin mien phi cho KH
								//Neu trong khoang thoi gian CTKM thi gui MT
//								if (calCurrent.getTime().after(cal.getTime())) {
									if (orgRequest != null && "GH_YOMI_NGAY QT".equals(orgRequest.toUpperCase()) && (packageCode.equals("QT") || packageCode.equals("DK QT"))) {
										List<XsPromotion> lstPromotion = xsPromotionService.findPromotion(isdn, "ACTIVE");
										if (lstPromotion != null && lstPromotion.size() > 0) {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = lstPromotion.get(0);
											xsPromotion.setNumber(1000);//Gia han thi cong 1000 diem/ ngay
//										xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setNumberSms(5);
											xsPromotion.setModifyTime(new Timestamp(now.getTime()));
											xsPromotionService.update(xsPromotion);

										} else {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = new XsPromotion();
											xsPromotion.setNumber(1000);//Gia han thi cong 1000 diem/ ngay
											xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setMsisdn(isdn);
											xsPromotion.setStatus("ACTIVE");
											xsPromotion.setNumberSms(5);
											xsPromotion.setCreatedDate(now);
											xsPromotion.setCreatedTime(new Timestamp(now.getTime()));
											xsPromotion.setTimeId(sdf_YYYYMMDDHHmmssSSS.format(dtCurrent));
											xsPromotionService.create(xsPromotion);
										}
									}
//								}
							}

							//Cac thue bao dang ky roi se duoc cong 500 diem vao TK tham gia CTKM Kham pha an so
							//Neu trong khoang thoi gian CTKM thi gui MT
//							if (calCurrent.getTime().after(cal.getTime())) {
								if (AS_PACKAGE_CODE_VALUE != null && Arrays.asList(AS_PACKAGE_CODE_VALUE.split(";")).contains(commandCode.replaceAll("\\s+", ""))) {
									if (sysParam != null) {
										List<XsPromotion> lstPromotion = xsPromotionService.findPromotion(isdn, "ACTIVE");
										Date dtRegDateTime = Util.SDF_DDMMYYYYHHMMSS.parse(regDatetime);
										if (lstPromotion != null && lstPromotion.size() > 0) {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = lstPromotion.get(0);
											if (reRegNew == 1) {
												xsPromotion.setNumber(500);//Dang ky moi dc 500 diem
											} else if (reRegNew == 2) {
												//Kiem tra xem thue bao DK lai co phai khac ngay dang ky hien tai ko
												String strRegDateSub = sdf_DDMMYYYY.format(regDateSubs);
												String strRegDateTimeRequest = sdf_DDMMYYYY.format(dtRegDateTime);

												//Neu ngay dang ky cua thue bao khi nhan tin len khac voi ngay dang ky ma thue bao hien tai dang luu thi cong diem
												//Dang ky lai chi cong diem 1 lan duy nhat trong 1 ngay
												if (regDateSubs != null && strRegDateSub != null && !strRegDateSub.equals(strRegDateTimeRequest)) {
													xsPromotion.setNumber(1000);//Dang ky moi dc 500 diem
												}
											}
											xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setNumberSms(5);
											xsPromotion.setModifyTime(new Timestamp(now.getTime()));
											xsPromotionService.update(xsPromotion);
										} else {
											//Tao thong tin ban ghi KPAS
											XsPromotion xsPromotion = new XsPromotion();
											if (reRegNew == 1) {
												xsPromotion.setNumber(500);//Dang ky moi dc 500 diem
											} else if (reRegNew == 2) {
												//Kiem tra xem thue bao DK lai co phai khac ngay dang ky hien tai ko
												String strRegDateSub = sdf_DDMMYYYY.format(regDateSubs);
												String strRegDateTimeRequest = sdf_DDMMYYYY.format(dtRegDateTime);

												//Neu ngay dang ky cua thue bao khi nhan tin len khac voi ngay dang ky ma thue bao hien tai dang luu thi cong diem
												//Dang ky lai chi cong diem 1 lan duy nhat trong 1 ngay
												if (regDateSubs != null && strRegDateSub != null && !strRegDateSub.equals(strRegDateTimeRequest)) {
													xsPromotion.setNumber(1000);//Dang ky moi dc 500 diem
												}
											}
											xsPromotion.setVasPackageCode(packageCode);
											xsPromotion.setMsisdn(isdn);
											xsPromotion.setStatus("ACTIVE");
											xsPromotion.setNumberSms(5);
											xsPromotion.setCreatedDate(now);
											xsPromotion.setCreatedTime(new Timestamp(now.getTime()));
											xsPromotion.setTimeId(sdf_YYYYMMDDHHmmssSSS.format(dtCurrent));
											xsPromotionService.create(xsPromotion);
										}
									}
								}
//							}
						}

						//Gui MT2 KPAS theo CTKM
						if (AS_PACKAGE_CODE_VALUE != null && Arrays.asList(AS_PACKAGE_CODE_VALUE.split(";")).contains(commandCode.replaceAll("\\s+",""))){
//							LOG.debug(calCurrent.getTime() +";" + cal.getTime());
							//Neu trong khoang thoi gian CTKM thi gui MT
//							if (calCurrent.getTime().after(cal.getTime())) {
								if (sysParam != null) {
									SmsModel smsModel = GSON_ALL.fromJson(sysParam.getValue(), SmsModel.class);
//									String msg = smsModel.getMtContent2().replaceAll("<NGAY>", Util.SDF_dd_MM_yyyy.format(dtCurrent));

									//Neu thue bao la thue bao dang ky moi
									String msg = "";
									if (reRegNew == 1) {
										msg = smsModel.getMtContent2().replaceAll("<NGAY>", Util.SDF_dd_MM_yyyy.format(dtCurrent));
									} else {
										//Neu thue bao la thue bao tai dang ky
										msg = smsModel.getMtContent3().replaceAll("<NGAY>", Util.SDF_dd_MM_yyyy.format(dtCurrent));
									}

									moRequest.setCommand(command);
									MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, msg, moRequest, moRequest.getCommand());

									this.moQueue.offer(moRequest);
									synchronized (moQueueNotifier) {
										this.moQueueNotifier.notifyAll();
									}

									this.mtQueueToCSP.offer(mtReq);
									synchronized (mtQueueToCSPNotifier) {
										this.mtQueueToCSPNotifier.notifyAll();
									}
//								LOG.debug("updatePackage.xsPromotion.MT2.message: {}", msg);
									LOG.info("xsPromotion.SEND_MT2: {}", msg);
									//Luu log MT CCSP gui
                                    if (!Strings.isNullOrEmpty(messageSend)) {
                                        MTRequest mtRequest1 = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, messageSend, moRequest, moRequest.getCommand());
                                        mtRequest1.setProcessed(true);
                                        this.mtQueueToCSP.offer(mtRequest1);
                                        synchronized (mtQueueToCSPNotifier) {
                                            this.mtQueueToCSPNotifier.notifyAll();
                                        }
                                    }
								}
//							} else {
//								String msg = smsService.mtModel().getMtInvalid();
//								mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, msg, moRequest, moRequest.getCommand());
//								this.mtQueueToCSP.offer(mtRequest);
//								synchronized (mtQueueToCSPNotifier) {
//									this.mtQueueToCSPNotifier.notifyAll();
//								}
//							}
						} else {
							//MT2
							String msg = smsService.mtModel().getMtMK();
							msg = msg.replaceAll("<MATKHAU>", mpin);
							if (!Strings.isNullOrEmpty(messageSend)) {
								if (moRequest != null) {
									moRequest.setCommand(command);
									mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, messageSend, moRequest, moRequest.getCommand());
									mtRequest2 = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, msg, moRequest, moRequest.getCommand());


									this.moQueue.offer(moRequest);
									synchronized (moQueueNotifier) {
										this.moQueueNotifier.notifyAll();
									}
								} else {
									mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, messageSend, null, command);
									mtRequest2 = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, msg, null, command);
								}
								mtRequest.setProcessed(true);
								this.mtQueueToCSP.offer(mtRequest);
								this.mtQueueToCSP.offer(mtRequest2);
								synchronized (mtQueueToCSPNotifier) {
									this.mtQueueToCSPNotifier.notifyAll();
								}
								LOG.info("SEND_MT2: {}", msg);
							}
						}

						return ResponseData.responseData("1", "OK");
					} else {
						//Giao dich huy
						SysParam sysParamHuy = sysParamService.findByKey("AS_HUY_QT");

						if (subscriber != null) {
							subscriber.setStatus(0);
							subscriber.setModifiedDate(now);
							subscriber.setUnregisterDate(Util.SDF_DDMMYYYYHHMMSS.parse(endDatetime));
							this.subscriberService.update(subscriber);
							boolean cancelDone = true;
							String command = CommandCode.CANCEL;
							HandlingResult postResult = cpGateService.notifyCancelSubs(transId, isdn,
									Util.XBD_SDF.format(now), originalSms, channel, vasPackage.getName(), cancelDone, "0000", "");
							this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
							if (!Strings.isNullOrEmpty(messageSend)) {
								if (moRequest != null) {
									moRequest.setCommand(command);
									mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, messageSend, moRequest, moRequest.getCommand());
									this.moQueue.offer(moRequest);
									synchronized (moQueueNotifier) {
										this.moQueueNotifier.notifyAll();
									}
								} else {
									mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, messageSend, null, command);
								}
								mtRequest.setProcessed(true);
								this.mtQueueToCSP.offer(mtRequest);
								synchronized (mtQueueToCSPNotifier) {
									this.mtQueueToCSPNotifier.notifyAll();
								}
							}

							//Gui MT Huy KPAS
							if (AS_PACKAGE_CODE_VALUE != null && Arrays.asList(AS_PACKAGE_CODE_VALUE.split(";")).contains(commandCode.replaceAll("\\s+",""))) {
								if (sysParamHuy != null && !"".equals(sysParamHuy.getValue())) {
									List<XsPromotion> lstPromotion = xsPromotionService.findPromotion(isdn, "ACTIVE");
									SmsModel smsModel = GSON_ALL.fromJson(sysParamHuy.getValue(), SmsModel.class);
									String msg = null;
									if (lstPromotion != null && lstPromotion.size() > 0) {
										msg = smsModel.getMtContent1();
										XsPromotion xsPromotion = lstPromotion.get(0);
										xsPromotion.setStatus("CANCELED");
										xsPromotion.setModifyTime(new Timestamp(now.getTime()));
										xsPromotionService.update(xsPromotion);
									} else {
										msg = smsModel.getMtContent2();
									}
								}
							}
							return ResponseData.responseData("1", "OK");
						} else {
							return ResponseData.responseData("0", "Thue bao chua dang ky goi");
						}
					}
				}
			}
			return ResponseData.responseData("1", "OK");
		} catch (Exception e) {
			LOG.error("", e);
			return ResponseData.responseData("0", e.getMessage());
		}
    }

	@POST
	@Path(value = "/forwardMessage")
	public ResponseData forwardMessage(
			@Context HttpServletRequest req,
			@FormParam("isdn") String isdn,
			@FormParam("content") String content,
			@DefaultValue("") @FormParam("request_id") String requestId) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("isdn", isdn);
			parameters.put("content", content);
			parameters.put("request_id", requestId);
			LOG.info("forwardMessage[{}]", objectMapper.writeValueAsString(parameters));
		} catch (Exception e) {
			LOG.error("", e);
		}
		isdn = Util.normalizeMsIsdn(isdn);
		MORequest moRequest = new MORequest();
		if (Strings.isNullOrEmpty(requestId)) {
			moRequest.setSmsId(Util.generateTransId());
		} else {
			moRequest.setSmsId(requestId);
		}
		moRequest.setFromNumber(isdn);
		moRequest.setToNumber(AppParams.SHORT_CODE);
		moRequest.setMessage(content);
		moRequest.setReceivedDate(new Date());
		this.moQueue.offer(moRequest);
		synchronized (moQueueNotifier) {
			this.moQueueNotifier.notifyAll();
		}
		return ResponseData.responseData("1", "OK");
    }

	@POST
	@Path(value = "/minusMoneyCheckMO")
	public String minusMoneyCheckMO(
			@Context HttpServletRequest req,
			@FormParam("ServiceCode") String serviceCode,
			@FormParam("ISDN") String msisdn,
            @FormParam("RequestId") String requestId,
			@FormParam("PackageCode") String packageCode,
			@FormParam("PackageName") String packageName,
			@FormParam("SP_ID") String spId,
			@FormParam("CP_ID") String cpId,
			@FormParam("Content_ID") String contentId,
			@FormParam("Category_ID") String categoryId,
			@FormParam("Amount") String amount,
            @FormParam("UserName") String user,
            @FormParam("Password") String pass) {
		String resultCode = "";
		try {
			resultCode = chargingCSPClient.minusMoneyCheckMORest(serviceCode, msisdn, requestId, packageCode, packageName, spId, cpId, contentId, categoryId, amount, user, pass);
		} catch (Exception e) {
			LOG.error("", e);
		}
		return resultCode;
	}
}
