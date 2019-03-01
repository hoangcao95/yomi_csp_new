package vn.yotel.vbilling.thread;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.admin.jpa.SysParam;
import vn.yotel.admin.service.SysParamService;
import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.Util;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.jpa.*;
import vn.yotel.vbilling.model.*;
import vn.yotel.vbilling.service.*;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;

import javax.annotation.Resource;

public class ProcessMoRequest extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessMoRequest.class);
    private static final Gson GSON_ALL = new GsonBuilder().serializeNulls().create();
	private static final SimpleDateFormat sdf_DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat sdf_DDMMYYYYHHmmss = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private ConcurrentLinkedQueue<MORequest> moQueue;
	private Object moQueueNotifier;
	private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
	private Object mtQueueToCSPNotifier;
	private ConcurrentLinkedQueue<MORequest> moProcessQueue;
	private Object moProcessQueueNotifier;

	private List<SmsSyntax> allSyntaxs = null;
	private MtModel mtModel = new MtModel();
	private SmsService smsService;
	private SysParamService sysParamService;
	private XsPromotionService xsPromotionService;
	private XsPromotionLogService xsPromotionLogService;
    private ChargingCSPClient chargingCSPClient;
    private ChargeLogService chargeLogService;

	private final String AS_PACKAGE_CODE_KEY = "AS_PACKAGE_CODE";
	private final String AS_TIME_START_KEY = "AS_TIME_START";
	private final String AS_TIME_END_KEY = "AS_TIME_END";
	private final String AS_NOT_TIME_PROMOTION_KEY = "AS_NOT_TIME_PROMOTION";
    private final String AS_NOT_ENOUGH_MONEY_KEY = "AS_NOT_ENOUGH_MONEY";
	private final String AS_MIN_NUMBER_KEY = "AS_MIN_NUMBER";
	private final String AS_MAX_NUMBER_KEY = "AS_MAX_NUMBER";
	private final String AS_NOTIFICATION_MESSAGE_KEY = "AS_NOTIFICATION_MESSAGE";

	private String AS_PACKAGE_CODE_VALUE = "";
	private String AS_TIME_START_VALUE = "";
	private String AS_TIME_END_VALUE = "";
	private Date dtCurrentTime = null;
	private int iMinNumber = 1;
	private int iMaxNumber = 50000;
	private String strPrivateNumber = "934538590";

	private SubscriberService subscriberService;
	private CpGateService cpGateService;

//	private HashMap<String, Boolean> data = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	protected void loadParameters() throws AppException {
		if (this.params != null) {
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}
		moQueue = (ConcurrentLinkedQueue<MORequest>) AppContext.getBean("moQueue");
		moQueueNotifier = AppContext.getBean("moQueueNotifier");

		mtQueueToCSP = (ConcurrentLinkedQueue<MTRequest>) AppContext.getBean("mtQueueToCSP");
		mtQueueToCSPNotifier = AppContext.getBean("mtQueueToCSPNotifier");

		moProcessQueue = (ConcurrentLinkedQueue<MORequest>) AppContext.getBean("moProcessQueue");
		moProcessQueueNotifier = AppContext.getBean("moProcessQueueNotifier");
		this.allSyntaxs = null;
	}

	@Override
	protected void initializeSession() throws AppException {
		smsService = (SmsService) AppContext.getBean("smsService");
		sysParamService = (SysParamService) AppContext.getBean("sysParamService");
		xsPromotionService = (XsPromotionService) AppContext.getBean("xsPromotionService");
		xsPromotionLogService = (XsPromotionLogService) AppContext.getBean("xsPromotionLogService");
        chargeLogService = (ChargeLogService) AppContext.getBean("chargeLogService");
        chargingCSPClient = (ChargingCSPClient) getBean("chargingCSPClient");

		subscriberService = (SubscriberService) AppContext.getBean("subscriberService");
		cpGateService = (CpGateService) AppContext.getBean("cpGateService");

		SysParam sysParam = sysParamService.findByKey(AS_PACKAGE_CODE_KEY);
		if (sysParam != null) {
			AS_PACKAGE_CODE_VALUE = sysParam.getValue();
		}
		sysParam = sysParamService.findByKey(AS_TIME_START_KEY);
		if (sysParam != null) {
			AS_TIME_START_VALUE = sysParam.getValue();
		} else {
			AS_TIME_START_VALUE = "08:00:00";
		}
		sysParam = sysParamService.findByKey(AS_TIME_END_KEY);
		if (sysParam != null) {
			AS_TIME_END_VALUE = sysParam.getValue();
		} else {
			AS_TIME_END_VALUE = "21:59:59";
		}

        LOG.debug("AS_PACKAGE_CODE_VALUE: {}", AS_PACKAGE_CODE_VALUE);
        LOG.debug("AS_TIME_START_VALUE: {}", AS_TIME_START_VALUE);
        LOG.debug("AS_TIME_END_VALUE: {}", AS_TIME_END_VALUE);

        try {
			sysParam = sysParamService.findByKey(AS_MIN_NUMBER_KEY);
			if (sysParam != null) {
				iMinNumber = Integer.parseInt(sysParam.getValue());
			}
			sysParam = sysParamService.findByKey(AS_MAX_NUMBER_KEY);
			if (sysParam != null) {
				iMaxNumber = Integer.parseInt(sysParam.getValue());
			}
		} catch (Exception ex){
        	LOG.error("ERROR_PARSE_NUMBER:", ex);
		}

		mtModel = smsService.mtModel();
	}

	@Override
	protected boolean processSession() throws AppException {
		try {
			while (!requireStop) {
				MORequest moReq = moQueue.poll();
				if (moReq != null) {
					LOG.info("Process MO request: {}", moReq.toString());
					LOG.info("Processed : ", moReq.isProcessed());
					if (!moReq.isProcessed()) {
						MTRequest mtReq = null;
						boolean isValid = true;
						String fromNumber = moReq.getFromNumber();
						String toNumber = moReq.getToNumber();
						VasPackage vasPackage = this.preProcessMO(moReq);
						isValid = this.validateShortCode(toNumber);

						LOG.info("isValid " + (isValid ? "true" : "false"));
						LOG.info(vasPackage != null ? "vasPackage:" + (vasPackage.getName()) : "vasPackage: IS NULL");

						if (isValid) {
							String message = moReq.getMessage().toUpperCase();
							message = message.trim();
							boolean processed = processMO(moReq);
							LOG.debug("msisdn=" + fromNumber + ";moCommand=" + moReq.getCommand() + ";processed=" + processed);
							if (!processed) {
								if (Arrays.asList(AS_PACKAGE_CODE_VALUE.split(";")).contains(moReq.getCommand())){
									mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, buildKPASMessage(moReq), moReq, moReq.getCommand());
								} else if (Constants.CommandCode.GUIDE.equals(moReq.getCommand())) {
									moReq.setCommand(Constants.CommandCode.GUIDE);
									mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, mtModel.getMtHD(), moReq, moReq.getCommand());
								} else if ("CHANGE_PASS".equals(moReq.getCommand())){
									moReq.setCommand("CHANGE_PASS");
									mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, buildFotGotMessage(moReq), moReq, moReq.getCommand());
								}else if ("CHANGE_PASS_ACCEPT".equals(moReq.getCommand())){
									moReq.setCommand("CHANGE_PASS_ACCEPT");
									mtReq = MessageBuilder.buildMTRequest(toNumber, fromNumber, buildFotGotMessage(moReq), moReq, moReq.getCommand());
								} else {
									mtReq = processDefaultMoRequest(moReq);
								}
							}
						}
						if (mtReq != null) {
							mtQueueToCSP.offer(mtReq);
							synchronized (mtQueueToCSPNotifier) {
								mtQueueToCSPNotifier.notifyAll();
							}
						}
					}
					this.logMoRequest(moReq);
				} else {
					synchronized (moQueueNotifier) {
						moQueueNotifier.wait(100L);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return true;
	}

	/**
	 * Should override in each application
	 *
	 * @param moReq
	 * @return
	 */
	protected boolean processMO(MORequest moReq) {
		List<String> processList = getProcessList();
		if (processList.contains(moReq.getCommand())) {
			moProcessQueue.offer(moReq);
			synchronized (moProcessQueueNotifier) {
				moProcessQueueNotifier.notifyAll();
			}
			return true;
		} else {
			return false;
		}
	}

	protected MTRequest processDefaultMoRequest(MORequest moReq) {
		moReq.setCommand(Constants.CommandCode.DEFAULT);
		MTRequest mtReq = MessageBuilder.buildMTRequest(moReq.getToNumber(), moReq.getFromNumber(),
				mtModel.getMtInvalid(), moReq, moReq.getCommand());
		return mtReq;
	}

	protected List<String> getProcessList() {
		List<String> processList = Arrays.asList(Constants.CommandCode.RESET_PWD, Constants.CommandCode.SET_PWD, Constants.CommandCode.BUY,
				Constants.CommandCode.CHECK, Constants.CommandCode.GUIDE_DT, Constants.CommandCode.GUIDE_XS);
		return processList;
	}

	private void logMoRequest(MORequest moReq) {
		MoSms record = new MoSms();
		record.setShortCode(moReq.getToNumber());
		record.setMsisdn(moReq.getFromNumber());
		record.setMessage(moReq.getMessage().toUpperCase());
		record.setSmscId(moReq.getSmsId());
		record.setServiceCode(AppParams.PRODUCT_NAME);
		record.setKeyword(moReq.getCommand());
		record.setCreatedDate(moReq.getReceivedDate());
		smsService.create(record);
	}

	private boolean validateShortCode(String toNumber) {
		String regex = "\\d{0,9}" + AppParams.SHORT_CODE + "$";
		return toNumber.matches(regex);
	}

	/**
	 * Base on shortcode & shortmessage to find out service and package
	 * subscriber wants to use
	 *
	 * @param moReq
	 */
	protected VasPackage preProcessMO(MORequest moReq) {
		String message = moReq.getMessage().toUpperCase();
		message = message.trim();
		VasPackage result = null;
		for (SmsSyntax packageSmsSyntax : getAllSyntaxs()) {
			if (message.matches(packageSmsSyntax.getRegex())) {
				LOG.info("{} is matched: {}", message, packageSmsSyntax.getRegex());
				result = packageSmsSyntax.getVasPackage();
				moReq.setCommand(packageSmsSyntax.getCommand());
				moReq.setSubsPackage(result);
				break;
			} else {
				// LOG.debug("{} is not matched: {}", message,
				// packageSmsSyntax.getRegex());
			}
		}
		return result;
	}

	@Override
	protected void completeSession() throws AppException {
	}

	protected List<SmsSyntax> getAllSyntaxs() {
		if (this.allSyntaxs == null || allSyntaxs.isEmpty()) {
			allSyntaxs = smsService.findAllSmsSyntax();
			for (SmsSyntax packageSmsSyntax : allSyntaxs) {
				// Load because of lazy load setting
				packageSmsSyntax.getVasPackage().getId();
			}
		}
		return this.allSyntaxs;
	}

	protected String buildKPASMessage(MORequest moReq) {
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.YEAR, 2019);
//		cal.set(Calendar.MONTH, 2);
//		cal.set(Calendar.DATE, 12);
//		cal.set(Calendar.HOUR, 23);
//		cal.set(Calendar.MINUTE, 59);
//		cal.set(Calendar.SECOND, 59);
//
//		Calendar calCurrent = Calendar.getInstance();
//
//		//Neu thoi gian hien tai lon hon thoi gian hieu luc CTKM thi gui MT sai cu phap
//		if (calCurrent.getTime().after(cal.getTime())) {
//			return mtModel.getMtInvalid();
//		}

		dtCurrentTime = Calendar.getInstance().getTime();
		StringBuilder sb = new StringBuilder();
		SmsModel smsModel = new SmsModel();
		Date dtStartTime = null;
		Date dtEndTime = null;
		try {
            String strCurrentTime = sdf_DDMMYYYY.format(dtCurrentTime);
            dtStartTime = sdf_DDMMYYYYHHmmss.parse(strCurrentTime + " " + AS_TIME_START_VALUE);
            dtEndTime = sdf_DDMMYYYYHHmmss.parse(strCurrentTime + " " + AS_TIME_END_VALUE);
            SysParam sysParamSMS = sysParamService.findByKey(moReq.getCommand());
            if (sysParamSMS != null) {
                smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
                List<XsPromotion> lstPromotion = xsPromotionService.findPromotion(moReq.getFromNumber(), "ACTIVE");

                if ("AS_HD".equals(moReq.getCommand())) {
                    sb.append(smsModel.getMtContent1());
                } else if ("AS_CHECK_MT".equals(moReq.getCommand())) {
                    if (lstPromotion != null && lstPromotion.size() > 0) {
                        sb.append(smsModel.getMtContent1().replace("<SO_TIN_MP>", String.valueOf(lstPromotion.get(0).getNumberSms())));
                    } else {
                        sb.append(smsModel.getMtContent2());
                    }
                } else if ("AS_MS_POINT".equals(moReq.getCommand())) {
                    if (lstPromotion != null && lstPromotion.size() > 0) {
                        sb.append(smsModel.getMtContent1().replaceAll("<NGAY>", strCurrentTime));
                    } else {
                        sb.append(smsModel.getMtContent2());
                    }
                } else if ("AS_CHECK_POINT".equals(moReq.getCommand())) {
                    if (lstPromotion != null && lstPromotion.size() > 0) {
                        sb.append(smsModel.getMtContent1().replace("<TONG_DIEM_TB>", String.valueOf(lstPromotion.get(0).getNumber())));
                    } else {
                        sb.append(smsModel.getMtContent2());
                    }
                } else if ("AS_PICK_NUMBER_1".equals(moReq.getCommand())) {
                    if (dtCurrentTime.getTime() >= dtStartTime.getTime() && dtCurrentTime.getTime() <= dtEndTime.getTime()) {
                        if (lstPromotion != null && lstPromotion.size() > 0) {
                            XsPromotion xsPromotion = lstPromotion.get(0);
                            try {
                            	String strNumPick = moReq.getMessage().toUpperCase().replaceFirst("AS\\s+", "");
//                            	LOG.debug("NUMBER_AS==" + strNumPick + "|");
                                int iNumPick = Integer.valueOf(strNumPick).intValue();

								if (iNumPick < iMinNumber || iNumPick > iMaxNumber) {
									sysParamSMS = sysParamService.findByKey("AS_MIN_MAX_MESSAGE");
									if (sysParamSMS != null) {
										smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
										if(xsPromotion.getNumberSms() > 0) {
											sb.append(smsModel.getMtContent1());
										} else {
											sb.append(smsModel.getMtContent2());
										}
									}
								} else if (xsPromotion.getArrNumberPick() != null && !"".equals(xsPromotion.getArrNumberPick())
										&& Arrays.asList(xsPromotion.getArrNumberPick().split(";")).contains(String.valueOf(iNumPick))){
                                    sb.append(smsModel.getMtContent3().replace("<SO_NGUYEN_DUONG>", String.valueOf(iNumPick)));

                                    xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));

                                    //Tru di so luot dat mien phi
                                    xsPromotion.setNumberSms(xsPromotion.getNumberSms() - 1);
                                    xsPromotionService.update(xsPromotion);

									//Luu log pick number
//									XsPromotionLog xsPromotionLog = new XsPromotionLog();
//									xsPromotionLog.setCreatedDate(dtCurrentTime);
//									xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
//									xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
//									xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
//									xsPromotionLog.setStatus(1);
//									xsPromotionLog.setTimeId(xsPromotion.getTimeId());
//									xsPromotionLogService.create(xsPromotionLog);
                                } else {
                                    if (xsPromotion.getNumberSms() > 1) {
                                        sb.append(smsModel.getMtContent1().replace("<SO_NGUYEN_DUONG>", String.valueOf(iNumPick)));
                                        if (xsPromotion.getArrNumberPick() == null || "".equals(xsPromotion.getArrNumberPick())) {
                                            xsPromotion.setArrNumberPick(";" + iNumPick);
                                        } else {
                                            xsPromotion.setArrNumberPick(xsPromotion.getArrNumberPick() + ";" + iNumPick);
                                        }

                                        xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));

                                        //Tru di so luot dat mien phi
                                        xsPromotion.setNumberSms(xsPromotion.getNumberSms() - 1);
                                        xsPromotionService.update(xsPromotion);

//										List<XsPromotionLog> lstPromotionLog = xsPromotionLogService.findByDate(dtCurrentTime);
										List<XsPromotionLog> lstPromotionLog_Old =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);
										LOG.info("lstPromotionLog_Old.1: " + GSON_ALL.toJson(lstPromotionLog_Old));

                                        //Luu log pick number
                                        XsPromotionLog xsPromotionLog = new XsPromotionLog();
                                        xsPromotionLog.setCreatedDate(dtCurrentTime);
                                        xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
                                        xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
                                        xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
                                        xsPromotionLog.setStatus(1);
                                        xsPromotionLog.setTimeId(xsPromotion.getTimeId());
										xsPromotionLogService.create(xsPromotionLog);

										checkNumber(lstPromotionLog_Old, xsPromotionLog);

                                    } else if (xsPromotion.getNumberSms() == 1) {
                                        sb.append(smsModel.getMtContent2().replace("<SO_NGUYEN_DUONG>", String.valueOf(iNumPick)));
                                        if (xsPromotion.getArrNumberPick() == null || "".equals(xsPromotion.getArrNumberPick())) {
                                            xsPromotion.setArrNumberPick(";" + iNumPick);
                                        } else {
                                            xsPromotion.setArrNumberPick(xsPromotion.getArrNumberPick() + ";" + iNumPick);
                                        }

                                        xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));

                                        //Tru di so luot dat mien phi
                                        xsPromotion.setNumberSms(xsPromotion.getNumberSms() - 1);
                                        xsPromotionService.update(xsPromotion);

//										List<XsPromotionLog> lstPromotionLog = xsPromotionLogService.findByDate(dtCurrentTime);
										List<XsPromotionLog> lstPromotionLog_Old =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);
										LOG.info("lstPromotionLog_Old.1: " + GSON_ALL.toJson(lstPromotionLog_Old));

										//Luu log pick number
										XsPromotionLog xsPromotionLog = new XsPromotionLog();
										xsPromotionLog.setCreatedDate(dtCurrentTime);
										xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
										xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
										xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
										xsPromotionLog.setStatus(1);
										xsPromotionLog.setTimeId(xsPromotion.getTimeId());
										xsPromotionLogService.create(xsPromotionLog);

										checkNumber(lstPromotionLog_Old, xsPromotionLog);
                                    } else {
                                        //Goi len CCSP tru tien khi thue bao het luot mien phi
                                        String result = buyPackage(moReq);
                                        if ("1".equals(result) || "OK".equals(result)) {
                                            sb.append(smsModel.getMtContent2().replace("<SO_NGUYEN_DUONG>", String.valueOf(iNumPick)));
                                            if (xsPromotion.getArrNumberPick() == null || "".equals(xsPromotion.getArrNumberPick())) {
                                                xsPromotion.setArrNumberPick(";" + iNumPick);
                                            } else {
                                                xsPromotion.setArrNumberPick(xsPromotion.getArrNumberPick() + ";" + iNumPick);
                                            }

                                            xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));

                                            xsPromotionService.update(xsPromotion);

//											List<XsPromotionLog> lstPromotionLog = xsPromotionLogService.findByDate(dtCurrentTime);
											List<XsPromotionLog> lstPromotionLog_Old =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);
											LOG.info("lstPromotionLog_Old.1: " + GSON_ALL.toJson(lstPromotionLog_Old));

											XsPromotionLog xsPromotionLog = new XsPromotionLog();
											xsPromotionLog.setCreatedDate(dtCurrentTime);
											xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
											xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
											xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
											xsPromotionLog.setStatus(1);
											xsPromotionLog.setTimeId(xsPromotion.getTimeId());
											xsPromotionLog.setPrice(1000);
											xsPromotionLogService.create(xsPromotionLog);

											checkNumber(lstPromotionLog_Old, xsPromotionLog);
                                        } else {
                                            sysParamSMS = sysParamService.findByKey(AS_NOT_ENOUGH_MONEY_KEY);
                                            if (sysParamSMS != null) {
                                                smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
                                                sb.append(smsModel.getMtContent1());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex1) {
                            	LOG.error("ERROR_PARSE_NUMBER", ex1);
                                sb.append(smsModel.getMtContent5());
                            }
                        } else {
                            sb.append(smsModel.getMtContent4());
                        }
                    } else {
                        sysParamSMS = sysParamService.findByKey(AS_NOT_TIME_PROMOTION_KEY);
                        if (sysParamSMS != null) {
                            smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
                            sb.append(smsModel.getMtContent1().replaceAll("<NGAY>", strCurrentTime));
                        }
                    }
                } else if ("AS_PICK_NUMBER_2".equals(moReq.getCommand())) {
                    if (dtCurrentTime.getTime() >= dtStartTime.getTime() && dtCurrentTime.getTime() <= dtEndTime.getTime()) {
                        if (lstPromotion != null && lstPromotion.size() > 0) {
                            XsPromotion xsPromotion = lstPromotion.get(0);
                            try {
								String strNumPick = moReq.getMessage().toUpperCase().replaceFirst("DIEM\\s+", "");
//								LOG.debug("NUMBER_AS2==" + strNumPick + "|");
								int iNumPick = Integer.valueOf(strNumPick).intValue();
								if (iNumPick < iMinNumber || iNumPick > iMaxNumber) {
									sysParamSMS = sysParamService.findByKey("AS_MIN_MAX_MESSAGE");
									if (sysParamSMS != null) {
										smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
										if(xsPromotion.getNumberSms() > 0) {
											sb.append(smsModel.getMtContent1());
										} else {
											sb.append(smsModel.getMtContent2());
										}
									}
								} else if (xsPromotion.getArrNumberPick() != null && !"".equals(xsPromotion.getArrNumberPick())
										&& Arrays.asList(xsPromotion.getArrNumberPick().split(";")).contains(String.valueOf(iNumPick))){
                                    if (xsPromotion.getNumber() >= 1000) {
                                        sb.append(smsModel.getMtContent3().replace("<SO_NGUYEN_DUONG>", String.valueOf(iNumPick)));

                                        xsPromotion.setNumber(xsPromotion.getNumber() - 1000);
                                        xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));
                                        xsPromotionService.update(xsPromotion);

//										List<XsPromotionLog> lstPromotionLog = xsPromotionLogService.findByDate(dtCurrentTime);
										List<XsPromotionLog> lstPromotionLog_Old =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);
										LOG.info("lstPromotionLog_Old.1: " + GSON_ALL.toJson(lstPromotionLog_Old));

										XsPromotionLog xsPromotionLog = new XsPromotionLog();
										xsPromotionLog.setCreatedDate(dtCurrentTime);
										xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
										xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
										xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
										xsPromotionLog.setStatus(1);
										xsPromotionLog.setTimeId(xsPromotion.getTimeId());
										xsPromotionLogService.create(xsPromotionLog);

										checkNumber(lstPromotionLog_Old, xsPromotionLog);
                                    } else {
                                        sb.append(smsModel.getMtContent2());
                                    }
                                } else {
                                    if (xsPromotion.getNumber() >= 1000) {
                                        xsPromotion.setNumber(xsPromotion.getNumber() - 1000);
                                        sb.append(smsModel.getMtContent1().replace("<TONG_DIEM_TB>", String.valueOf(xsPromotion.getNumber())));

                                        if (xsPromotion.getArrNumberPick() == null || "".equals(xsPromotion.getArrNumberPick())) {
                                            xsPromotion.setArrNumberPick(";" + iNumPick);
                                        } else {
                                            xsPromotion.setArrNumberPick(xsPromotion.getArrNumberPick() + ";" + iNumPick);
                                        }

                                        xsPromotion.setModifyTime(new Timestamp(dtCurrentTime.getTime()));
                                        xsPromotionService.update(xsPromotion);

//										List<XsPromotionLog> lstPromotionLog = xsPromotionLogService.findByDate(dtCurrentTime);
										List<XsPromotionLog> lstPromotionLog_Old =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);
										LOG.info("lstPromotionLog_Old.1: " + GSON_ALL.toJson(lstPromotionLog_Old));

										XsPromotionLog xsPromotionLog = new XsPromotionLog();
										xsPromotionLog.setCreatedDate(dtCurrentTime);
										xsPromotionLog.setCreatedTime(new Timestamp(dtCurrentTime.getTime()));
										xsPromotionLog.setMsisdn(xsPromotion.getMsisdn());
										xsPromotionLog.setNumberPick(String.valueOf(iNumPick));
										xsPromotionLog.setStatus(1);
										xsPromotionLog.setTimeId(xsPromotion.getTimeId());
										xsPromotionLogService.create(xsPromotionLog);

										checkNumber(lstPromotionLog_Old, xsPromotionLog);
                                    } else {
                                        sb.append(smsModel.getMtContent2());
                                    }
                                }
                            } catch (Exception ex1) {
								LOG.error("ERROR_PARSE_NUMBER", ex1);
                                sb.append(smsModel.getMtContent5());
                            }
                        } else {
                            sb.append(smsModel.getMtContent4());
                        }
                    } else {
                        sysParamSMS = sysParamService.findByKey(AS_NOT_TIME_PROMOTION_KEY);
                        if (sysParamSMS != null) {
                            smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
                            sb.append(smsModel.getMtContent1().replaceAll("<NGAY>", strCurrentTime));
                        }
                    }
                }
            }
		} catch (Exception ex) {
            LOG.error("ERROR_buildKPASMessage", ex);
			//GUI MT thong bao he thong loi
			MTRequest mtReqPrivate = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, strPrivateNumber,
					"ERR_MESSAGE_KPAS " + ex.getMessage() , null, "ERR_MESSAGE_KPAS");
			if (mtReqPrivate != null) {
				mtQueueToCSP.offer(mtReqPrivate);
				synchronized (mtQueueToCSPNotifier) {
					mtQueueToCSPNotifier.notifyAll();
				}
			}
		}
		return sb.toString();
	}

    private String buyPackage(MORequest moReq) {
        LOG.info("processBuyPackage: BEGIN -----");
        String result = "0";
        ChargeLog chargeLog =  new ChargeLog();
        try {
            String content_ID = "0000000001";
            String category_ID = "000001";
            String spId = "001";
            String cpId = "001";
            result = chargingCSPClient.minusMoneyCheckMO(AppParams.SHORT_CODE,
                    moReq.getFromNumber(), moReq.getSmsId(),
                    "QT", "QT",
                    spId, cpId, content_ID, category_ID, "1000");
			LOG.error("processBuyPackage.result=", result);
            //Ghi log charge tai le cua KH
            chargeLog.setAmount(1000);
            chargeLog.setMsisdn(moReq.getFromNumber());
            chargeLog.setResultStatus("!".equals(result) || "OK".equals(result) ? true : false);
            chargeLog.setType("AS_BUY");
            chargeLogService.create(chargeLog);
        } catch (Exception ex) {
            LOG.error("processBuyPackage: ERROR", ex);
			//GUI MT thong bao he thong loi
			MTRequest mtReqPrivate = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, strPrivateNumber,
					"ERR_BUY_PACKAGE " + ex.getMessage() , null, "ERR_BUY_PACKAGE");
			if (mtReqPrivate != null) {
				mtQueueToCSP.offer(mtReqPrivate);
				synchronized (mtQueueToCSPNotifier) {
					mtQueueToCSPNotifier.notifyAll();
				}
			}
        }
        LOG.info("processBuyPackage: END -----");
        return result;
    }

	/**
	 * Thuc hien so sanh LOG cu va LOG moi
	 * @param lstPromotionLogOld
	 * @param newPromotionLog
	 */
	private void checkNumber(List<XsPromotionLog> lstPromotionLogOld, XsPromotionLog newPromotionLog) {
		try {
			LOG.info("checkNumber::BEGIN--------------------------------------------------------");
		    LOG.info("checkNumber|newPromotionLog=" + GSON_ALL.toJson(newPromotionLog));
			SysParam sysParam = sysParamService.findByKey(AS_NOTIFICATION_MESSAGE_KEY);
			SmsModel smsModel = new SmsModel();
			MTRequest mtReq = null;


			List<XsPromotionLog> lstNumberMax_LogOld =  lstPromotionLogOld;
			LOG.info("lstNumberMax_LogOld: " + GSON_ALL.toJson(lstNumberMax_LogOld));
			//Neu LOG MAX khi bo log moi nhat vua dat
			if(!lstNumberMax_LogOld.isEmpty()) {
				XsPromotionLog xsNumberMaxOld = lstNumberMax_LogOld.get(0);
				//Gui MT khach hang bi mat vi tri cao nhat
				//Neu so cao nhat o LOG cu giong voi so moi dat thi gui tin mat uu the
				if (Integer.valueOf(xsNumberMaxOld.getNumberPick()).intValue() <= Integer.valueOf(newPromotionLog.getNumberPick()).intValue()) {
					if (sysParam != null) {
						LOG.info("checkNumber|SEND MT MISS MAX");
						smsModel = GSON_ALL.fromJson(sysParam.getValue(), SmsModel.class);
						String msg = smsModel.getMtContent3()
								.replaceAll("<SO_DA_DAT>", xsNumberMaxOld.getNumberPick());

						mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, xsNumberMaxOld.getMsisdn(), msg, null, "AS_SMS_NUMBER_OUT");
						LOG.info("SUBSCRIBER {}:MISS MAX {}", xsNumberMaxOld.getMsisdn(), xsNumberMaxOld.getNumberPick());

						if (mtReq != null) {
							mtQueueToCSP.offer(mtReq);
							synchronized (mtQueueToCSPNotifier) {
								mtQueueToCSPNotifier.notifyAll();
							}
						}
					}
				}
			}

			//Lay ra so thue bao dang duoc CAO NHAT va DUY NHAT
			List<XsPromotionLog> lstNumberMaxNew =  xsPromotionLogService.findNumberMaxByDate(dtCurrentTime);;
			LOG.info("lstNumberMaxNew: " + GSON_ALL.toJson(lstNumberMaxNew));
			if (!lstNumberMaxNew.isEmpty()) {
				XsPromotionLog xsNumberMax = lstNumberMaxNew.get(0);
				LOG.info("xsNumberMax" + GSON_ALL.toJson(xsNumberMax));
				//Neu ma so cao nhat LOG moi = so cao nhat thue bao vua dat thi gui tin ban la ng dat so cao nhat
				if (xsNumberMax.getMsisdn().equals(newPromotionLog.getMsisdn()) && xsNumberMax.getNumberPick().equals(newPromotionLog.getNumberPick())) {
					//Gui MT khach hang vua dat vi tri cao nhat
					if (sysParam != null) {
						LOG.info("checkNumber|SEND MT MAX");
						smsModel = GSON_ALL.fromJson(sysParam.getValue(), SmsModel.class);
						String strDate = sdf_DDMMYYYY.format(newPromotionLog.getCreatedTime());
						String strTime = sdf_HHmm.format(newPromotionLog.getCreatedTime());
						String msg = smsModel.getMtContent4()
								.replaceAll("<SO_DA_DAT>",newPromotionLog.getNumberPick())
								.replaceAll("<NGAY>", strDate)
								.replaceAll("<GIO_DAT>", strTime);

						mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, newPromotionLog.getMsisdn(), msg, null, "AS_SMS_NUMBER_IN");
						LOG.info("SUBSCRIBER {}:MAX {}", newPromotionLog.getMsisdn(), newPromotionLog.getNumberPick());

						if (mtReq != null) {
							mtQueueToCSP.offer(mtReq);
							synchronized (mtQueueToCSPNotifier) {
								mtQueueToCSPNotifier.notifyAll();
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			LOG.error("", ex);

			//GUI MT thong bao he thong loi
			MTRequest mtReqPrivate = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, strPrivateNumber,
					"ERR_CHECK_MAX " + ex.getMessage() , null, "ERR_CHECK_MAX");
			if (mtReqPrivate != null) {
				mtQueueToCSP.offer(mtReqPrivate);
				synchronized (mtQueueToCSPNotifier) {
					mtQueueToCSPNotifier.notifyAll();
				}
			}

		}
        LOG.info("checkNumber::END--------------------------------------------------------");
	}

	protected String buildFotGotMessage(MORequest moReq) {
		dtCurrentTime = Calendar.getInstance().getTime();
		StringBuilder sb = new StringBuilder();
		MtModel mtModel = new MtModel();
		String mpin = Util.generateMPIN();
		try {
			String strCurrentTime = sdf_DDMMYYYY.format(dtCurrentTime);
			SysParam sysParamSMS = sysParamService.findByKey("_MT_KEY");
			List<Subscriber> listSub = subscriberService.findActiveByMsisdn(moReq.getFromNumber());
			if (sysParamSMS != null) {
				mtModel = GSON_ALL.fromJson(sysParamSMS.getValue(), MtModel.class);
				if("CHANGE_PASS_ACCEPT".equals(moReq.getCommand())){
					String message = mtModel.getMtMK().replaceFirst("<MATKHAU>", mpin);
					if (listSub != null) {
						for (Subscriber list : listSub) {
							//thay doi ma pin
							list.setMpin(mpin);
							subscriberService.update(list);
						}
						//Update ma pin len webapp
						HandlingResult postResult = cpGateService.notifyResetPassword(moReq.getSmsId(), moReq.getFromNumber(), mpin, strCurrentTime, message);

						//GUI MT thong bao he thong mat dong bo MAT KHAU thue bao
						if (postResult.parseToHttpCode() != 200) {
							MTRequest mtReqPrivate = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, strPrivateNumber,
									"ERR_SYNC_MPIN_SUBS " + moReq.getFromNumber(), null, "ERR_SYNC_MPIN_SUBS");
							if (mtReqPrivate != null) {
								mtQueueToCSP.offer(mtReqPrivate);
								synchronized (mtQueueToCSPNotifier) {
									mtQueueToCSPNotifier.notifyAll();
								}
							}
						}
						sb.append(message);
					}
//					}
				}else if ("CHANGE_PASS".equals(moReq.getCommand())) {
					sb.append(mtModel.getMtSmsFogotMessage().replaceFirst("<ISDN_FOGOT>", moReq.getFromNumber()));
				}
			}
		} catch (Exception ex) {
			LOG.error("ERROR_buildKPASMessage", ex);
		}
		return sb.toString();
	}
}
