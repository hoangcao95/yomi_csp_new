package vn.yotel.yomi.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.Blacklist;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.jpa.XsPromotionLog;
import vn.yotel.vbilling.jpa.XsPromotionSum;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtHistory;
import vn.yotel.vbilling.repository.MtSmsRepo;
import vn.yotel.vbilling.service.*;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.TransferIsdn;
import vn.yotel.yomi.AppParams;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

//import com.hazelcast.core.IMap;


@Controller
@RequestMapping(value = "/custcare/kpbt")
public class CustCareYomiController {

    private Logger LOG = LoggerFactory.getLogger(CustCareYomiController.class);

    private List<VasPackage> allVasPackages;

    private String MT_REG_SUCCESS1 = "";
    private String MT_REG_SUCCESS2 = "";
    private String MT_REG_AGAIN = "";
    private String MT_REG_INVALID_BALANCE = "";
    private String MT_CAN_SUCCESS = "";
    private String MT_CAN_FAILED = "";

    private String ERR_INVALID_BALANCE = "0001";
    private String ERR_REG_AGAIN = "0002";
    private String ERR_INVALID_PRODUCT_OR_PACKAGE = "0003";
    private String ERR_INVALID_ISDN = "0006";
    private String ERR_EXCEPTION = "9999";

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    VasPackageService vasPackageService;

    @Autowired
    CpGateService cpGateService;

    @Autowired
    MtSmsRepo mtSmsRepo;

    @Autowired
    BlacklistService blacklistService;

    @Autowired
    SmsService smsService;

    @Autowired
    XsPromotionSumService xsPromotionSumService;

    @Autowired
    XsPromotionLogService xsPromotionLogService;

    @Resource private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
    @Resource private Object mtQueueToCSPNotifier;
    @Resource private ChargingCSPClient chargingCSPClient;

    /*@RequestMapping(value = "/register.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String ccSubsRegister(
            @RequestParam("request_id") final String requestId,
            @RequestParam("msisdn") final String regMsisdn,
            @RequestParam("product_id") final int productId,
            @RequestParam("package_code") final String packageName,
            @RequestParam(required = false, value = "pro_code", defaultValue = "") final String providerCode,
            @RequestParam(required = false, value = "ref_code", defaultValue = "") final String refCode,
            HttpServletRequest request) {
        RestMessage wapResult = RestMessage.RestMessageBuilder.SUCCESS();
        String smsMessage = "";
        String msisdn = Util.normalizeMsIsdn(regMsisdn);
        boolean regDone = false;
        try {
            *//**
             * 1. Check input parameters
             * 2. Check IP in 3G IP Pool
             *//*
            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            if (!IPRangeChecker.isValiadReceiveMsIsdn(msisdn)) {
                throw new AppException(ERR_INVALID_ISDN, "Invalid Msisdn");
            }
            if (this.blacklistSubsBo.isForbidden(msisdn)) {
                throw new AppException(ERR_EXCEPTION, "This number " + msisdn + " is in blacklist");
            }
            VasPackage vasPackage = this.checkProductAndPackage(productId, packageName);

            Subscriber subscriber = this.subscriberBo.findByMsisdnAndProduct(msisdn, vasPackage.getProductId());
            boolean regFree = true;
            int amount = 0;
            boolean regNew = true;
            String errorCode = "0000";
            String errorDesc = "";
            Date regDate = new Date();
            String mpin = Util.generateMPIN();
            String oldMpin = "";
            if ((subscriber != null) && (subscriber.getStatus() == Subscriber.Status.ACTIVE.value())) {
                regDone = false;
                errorCode = "5000";
                errorDesc = String.format("Subscriber %s is already registered!", msisdn);
                smsMessage = MT_REG_AGAIN;//Already subs
                VasPackage vasPackageOld = this.subscriberBo.loadVasPackage(subscriber);
                smsMessage = smsMessage.replaceAll("<TENGOI>", vasPackageOld.getDesc());
                smsMessage = smsMessage.replaceAll("<MAGOI>", vasPackageOld.getName());
                wapResult = RestMessageBuilder.FAIL(ERR_REG_AGAIN, "Already registered");
            } else {
                DateTime regDateTime = new DateTime(regDate);
                if (subscriber == null) {
                    //Register new
                    DateTime expiredDate = regDateTime.plusDays(vasPackage.getFreeDuration());
                    smsMessage = MT_REG_SUCCESS1;
                    regFree = true;
                    regNew = true;
                    subscriber = new Subscriber();
                    subscriber.setChannel(SUBS_CHANNEL.WEB);
                    subscriber.setCreatedDate(regDate);
                    subscriber.setModifiedDate(regDate);
                    subscriber.setRegisterDate(regDate);
                    subscriber.setExpiredDate(expiredDate.toDate());
                    subscriber.setProductId(vasPackage.getProductId());
                    subscriber.setPackageId(vasPackage.getId());
                    subscriber.setMsisdn(msisdn);
                    subscriber.setMpin(mpin);
                    subscriber.setRegNew(regNew);
                    subscriber.setStatus(SUBS_STATUS.ACTIVE);
                    subscriber.setSub(true);
                    this.subscriberBo.create(subscriber);
                } else {
                    //Re-register
                    regNew = false;
                    regFree = false;
                    smsMessage = MT_REG_SUCCESS2;
//					if ((subscriber.getRegNew() == false) && Util.isOnSameCycle(subscriber.getExpiredDate(), regDate)) {
//						regFree = true;
//					}
                    if (!subscriber.isSub()) {
                        //First time register subscription
                        regFree = true;
                    }
                    if (!regFree) {
                        DateTime expiredDate = regDateTime.plusDays(vasPackage.getDuration());
                        subscriber.setExpiredDate(expiredDate.toDate());
                    } else {
                        DateTime expiredDate = regDateTime.plusDays(vasPackage.getFreeDuration());
                        subscriber.setExpiredDate(expiredDate.toDate());
                    }
                    oldMpin = subscriber.getMpin();
                    subscriber.setStatus(SUBS_STATUS.ACTIVE);
                    subscriber.setChannel(SUBS_CHANNEL.WEB);
                    subscriber.setRegNew(regNew);
                    subscriber.setRegisterDate(regDate);
                    subscriber.setModifiedDate(regDate);
                    subscriber.setProductId(vasPackage.getProductId());
                    subscriber.setPackageId(vasPackage.getId());
                    subscriber.setMpin(mpin);
                    subscriber.setSub(true);
                    this.subscriberBo.update(subscriber);
                }
                smsMessage = smsMessage.replaceAll("<MATKHAU>", mpin);
                smsMessage = smsMessage.replaceAll("<TENGOI>", vasPackage.getDesc());
                smsMessage = smsMessage.replaceAll("<MAGOI>", vasPackage.getName());
                smsMessage = smsMessage.replaceAll("<SONGAY-MP>", vasPackage.getFreeDuration() + "");
                smsMessage = smsMessage.replaceAll("xxxd/yy", vasPackage.getPriceFormatted() + "d/" + vasPackage.getDuration());
                regDone = true;
                JSONObject option = new JSONObject();
                option.put(MapKey.Option.Channel, VasGate.Channel.WEB);
                option.put(MapKey.Option.MT_SMS, smsMessage);
                if (regFree) {
                    //Free
                    LOG.debug("Register subscription for free");
//					HandlingResult result = this.yosportChargingBo.regNewService(msisdn, 0, ChargingCategory.SUBS, ChargingContent.SUBS_REG, option);
//					LOG.debug("Call prc result: {}", result.isSuccess());
                } else {
                    LOG.debug("Register subscription with fee: {}", vasPackage.getPrice());
                    HandlingResult result = this.yosportChargingBo.regNewService(msisdn, (int) vasPackage.getPrice(), ChargingCategory.SUBS, ChargingContent.SUBS_REG, option);
                    if (result.isSuccess()) {
                        amount = (int) vasPackage.getPrice();
                        subscriber.setChargedCount(subscriber.getChargedCount() + 1);
                    } else {
                        errorCode = result.getStatus();
                        errorDesc = result.getMessage();
                        subscriber.setStatus(SUBS_STATUS.INACTIVE);
                        smsMessage = MT_REG_INVALID_BALANCE;
                        regDone = false;
                        wapResult = RestMessageBuilder.FAIL(ERR_INVALID_BALANCE, "Not enough money");
                        if (oldMpin.length() > 0) {
                            subscriber.setMpin(oldMpin);
                        }
                    }
                    try {
                        this.subscriberBo.update(subscriber);
                    } catch (Exception e) {
                        LOG.error("", e);
                    }
                }
            }
            //Notify Bibibook
            String transId = Util.generateTransId();
            HandlingResult postResult = bibibookBo.notifyAppWapSubs(transId, msisdn, mpin, amount, regNew, Util.BIBIBOOK_SDF.format(regDate), Util.BIBIBOOK_SDF.format(subscriber.getExpiredDate()), regDone, errorCode, errorDesc, vasPackage.getName(), providerCode, refCode, SUBS_CHANNEL.WEB);
            this.subscriberBo.logSubsRequest(subscriber, SubsCommand.REGISTER, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
            if (regDone) {
                //Generate Vasgate record
                String vgRequestId = Util.generateRequestId(subscriber.getMsisdn());
                VasGateRecord record = MessageBuilder.buildVasGateMessage(
                        vgRequestId, subscriber.getMsisdn(),
                        VasGate.RequestMessage.FROM_SYSTEM,
                        VasGate.SDF.format(regDate), VasGate.Status.SUBS_REG,
                        VasGate.SDF.format(subscriber.getExpiredDate()), amount + "", vasPackage.getName(),
                        VasGate.Channel.WEB,VasGate.SDF.format(subscriber.getRegisterDate()), "");
                vasGateReqQueue.offer(record);
                synchronized (vasGateReqQueueNotifier) {
                    vasGateReqQueueNotifier.notifyAll();
                }
                MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, msisdn, smsMessage, null, SubsCommand.REGISTER);
                mtReq.setChannel(MtChannel.SYSTEM);
                mtReq.setBrandName(true);
                mtReq.setSendViaPRC(false);
                this.sendSMS(mtReq);
            }
        } catch (AppException ex) {
            LOG.warn(ex.toString());
            wapResult = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            LOG.error("", ex);
            wapResult = RestMessageBuilder.FAIL(ERR_EXCEPTION, ex.getMessage());
        }
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(wapResult);
    }*/

    @RequestMapping(value = "/cancel.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String ccSubsUnRegister(
            @RequestParam("request_id") final String requestId,
            @RequestParam("msisdn") final String isdn,
            @RequestParam("product_id") final int productId,
            @RequestParam("package_code") final String packageName,
            HttpServletRequest request) {
        RestMessage wapResult = RestMessage.RestMessageBuilder.SUCCESS();
        String smsMessage = "";
        try {
            String msisdn = Util.normalizeMsIsdn(isdn);
            String serviceCode = AppParams.SHORT_CODE;
            String packageCode = packageName;
            String commandCode = "HUY";
            String sourceCode = "CP";
            String result = chargingCSPClient.receiverServiceReq(serviceCode, msisdn, commandCode, packageCode,
                    sourceCode);
            LOG.info("serviceCode {}", serviceCode);
            LOG.info("msisdn {}", msisdn);
            LOG.info("commandCode {}", commandCode);
            LOG.info("packageCode {}", packageCode);
            LOG.info("sourceCode {}", sourceCode);
            LOG.info("receiverServiceReq: {}", result);
            /**
             * 1. Check input parameters
             * 2. Check IP in 3G IP Pool
             *//*
//            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            VasPackage vasPackage = this.vasPackageService.findByName(packageName);
            Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(msisdn, vasPackage.getId());
            boolean cancelDone = false;
            Date dte = new Date();
            if ((subscriber != null) && (subscriber.getStatus() == 1)) {
                //Already subs, process to un-subscription this phonenumber
                subscriber.setStatus(Subscriber.Status.INACTIVE.value());
                subscriber.setModifiedDate(dte);
                subscriberService.update(subscriber);
                smsMessage = MT_CAN_SUCCESS;
                cancelDone = true;
                smsMessage = smsMessage.replaceAll("<TENGOI>", vasPackage.getDesc());
                smsMessage = smsMessage.replaceAll("<MAGOI>", vasPackage.getName());
            } else {
                smsMessage = MT_CAN_FAILED;
            }
            if (cancelDone) {
                String transId = Util.generateTransId();
//                HandlingResult postResult = bibibookBo.notifyCancelSubsBySystem(transId, msisdn, Util.BIBIBOOK_SDF.format(dte), "", vasPackage.getName(), cancelDone);
//                this.subscriberBo.logSubsRequest(subscriber, SubsCommand.CANCEL, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
                HandlingResult postResult = cpGateService.notifyCancelSubs(transId, msisdn,
                        Util.XBD_SDF.format(dte), "", Constants.Channel.SYS, vasPackage.getName(), cancelDone, "0000", "");
                this.subscriberService.logSubsRequest(subscriber, Constants.CommandCode.CANCEL, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
                MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, msisdn, smsMessage, null, Constants.CommandCode.CANCEL);
                mtReq.setChannel(Constants.Channel.SYS);
                this.sendSMS(mtReq);
            }*/
        } catch (Exception ex) {
            LOG.error("", ex);
            wapResult = RestMessage.RestMessageBuilder.FAIL(ERR_EXCEPTION, ex.getMessage());
        }
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(wapResult);
    }

    /*@SuppressWarnings("unchecked")
    private VasPackage checkProductAndPackage(int productId, String packageName) throws AppException {
        VasPackage result = null;
        if ((allVasPackages == null) || (allVasPackages.size() == 0)) {
            AppBootstrapBo appBootstrapBo = (AppBootstrapBo) AppContext.getBean("appBootstrapBo");
            appBootstrapBo.initializeApp();
            allVasPackages = (List<VasPackage>) distributedMap.get(MapKey.ALL_VAS_PACKAGE);
        }
        for (VasPackage onePackage : allVasPackages) {
            if (onePackage.getName().equalsIgnoreCase(packageName) && (onePackage.getProductId() == productId)) {
                result = onePackage;
                break;
            }
        }
        if (result == null) {
            throw new AppException(ERR_INVALID_PRODUCT_OR_PACKAGE, "Invalid product or package");
        }
        return result;
    }*/

    /**
     *
     * @param mtReq
     */
    private void sendSMS(MTRequest mtReq) {
        mtQueueToCSP.offer(mtReq);
        synchronized (mtQueueToCSPNotifier) {
            mtQueueToCSPNotifier.notifyAll();
        }
    }

    Gson GSON_ALL = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    @RequestMapping(value = "/mt_history.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String getMtHistory(
            @RequestParam("msisdn") String msisdn,
            @RequestParam("startdate") String startDate,
            @RequestParam("enddate") String endDate) {
        LOG.info("from: {}, to: {}", startDate, endDate);
        RestMessage resp = null;
        try {
            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
            String newIsdn = TransferIsdn.transferToParam(msisdn);
            List<MtHistory> listMt = smsService.getMtHistory(newIsdn, _fromDate, _toDate);
            LOG.info(listMt.toString());
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            resp.setData(listMt);
        } catch (Exception e) {
            LOG.error("", e);
            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    /*@SuppressWarnings("unchecked")
    @RequestMapping(value = { "/resend_mt.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String smsSenderPost(HttpServletRequest request, @RequestParam(value = "mtId") int mtId) {
        RestMessage resp = null;
        try {
            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            MtSms mtSms = this.mtSmsBo.read(mtId);
            if (mtSms == null) {
                resp = RestMessageBuilder.FAIL("003", "Invalid message Id. Please check your request data.");
            } else {
                MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, mtSms.getMsisdn(), mtSms.getMessage(), null, mtSms.getKeyword());
                mtQueue = (ConcurrentLinkedQueue<MTRequest>) AppContext.getBean("mtQueue");
                mtQueue.offer(mtReq);
                resp = RestMessage.RestMessageBuilder.SUCCESS();
                resp.setData("Enque successfully!");
            }
        } catch (AppException ex) {
            LOG.error("", ex);
            resp = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            resp = RestMessageBuilder.FAIL("9999", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }*/

    @RequestMapping(value = { "/blacklist/add.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String addNumberToBlacklist(
            HttpServletRequest request,
            @RequestParam(value = "msisdn") String msisdn) {
        RestMessage resp = null;
        try {
            msisdn = Util.normalizeMsIsdn(msisdn);
//            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            Blacklist result = blacklistService.addBlacklist(msisdn);
            if (result != null) {
                resp = RestMessage.RestMessageBuilder.FAIL("001", "Could not add this number to Blacklist");
            }
            resp = RestMessage.RestMessageBuilder.SUCCESS();
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("9999", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = { "/blacklist/remove.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String removeNumberFromBlacklist(
            HttpServletRequest request,
            @RequestParam(value = "msisdn") String msisdn) {
        RestMessage resp = null;
        try {
            msisdn = Util.normalizeMsIsdn(msisdn);
//            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            Blacklist result = blacklistService.removeBlacklist(msisdn);
            if (result != null) {
                resp = RestMessage.RestMessageBuilder.FAIL("001", "Could not remove this number to Blacklist");
            }
            resp = RestMessage.RestMessageBuilder.SUCCESS();
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("9999", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = { "/blacklist/list.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getBlacklist(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10000") int pageSize) {
        RestMessage resp = null;
        try {
//            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            List<Blacklist> subs = blacklistService.getAllBlackListSubs();
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            resp.setData(subs);
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("9999", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = { "/blacklist/search.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getSearchBlacklist(
            HttpServletRequest request,
            @RequestParam(value = "msisdn", required = true, defaultValue = "") String msisdn) {
        RestMessage resp = null;
        try {
            msisdn = Util.normalizeMsIsdn(msisdn);
            String temp = TransferIsdn.transferToNativeSQL(msisdn);
//            IPRangeChecker.validateBB_IPS(request.getRemoteAddr());
            Blacklist subs = blacklistService.findByMsisdn(temp);
            if (subs != null && subs.getStatus()) {
                resp = RestMessage.RestMessageBuilder.SUCCESS();
                resp.setData(subs);
            } else {
                resp = RestMessage.RestMessageBuilder.FAIL("002", "Could not find this number in blacklist: " + msisdn);
            }
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("9999", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String missingParamterHandler(Exception exception) {
        LOG.error("", exception);
        return "/400"; /* view name of your erro jsp */
    }

    @RequestMapping(value = "/promotion_sum.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String getPromotionSum(
            @RequestParam("startdate") String startDate,
            @RequestParam("enddate") String endDate) {
        LOG.info("from: {}, to: {}", startDate, endDate);
        RestMessage resp = null;
        try {
            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
            List<XsPromotionSum> listPromotionSum = null;
            try {
                listPromotionSum = xsPromotionSumService.findAllByDate(_fromDate, _toDate);
            }catch (Exception ex){
                LOG.error("",ex);
            }
            LOG.info(listPromotionSum.toString());
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            resp.setData(listPromotionSum);
        } catch (Exception e) {
            LOG.error("", e);
            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = "/promotion_log.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String getPromotionLog(
            @RequestParam("startdate") String startDate,
            @RequestParam("enddate") String endDate,
            @RequestParam("msisdn") String msisdn){
        LOG.info("from: {}, to: {}", startDate, endDate);
        RestMessage resp = null;
        try {
            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
            List<XsPromotionLog> listPromotionLog = null;
            try {
                 listPromotionLog = xsPromotionLogService.findAllByDate(_fromDate, _toDate, msisdn);
            }catch (Exception ex){
                LOG.error("",ex);
            }
//            LOG.info(listPromotionLog.toString());
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            resp.setData(listPromotionLog);
        } catch (Exception e) {
            LOG.error("", e);
            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

}
