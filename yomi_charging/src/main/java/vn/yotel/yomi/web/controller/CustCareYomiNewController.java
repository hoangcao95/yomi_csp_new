package vn.yotel.yomi.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.*;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtHistory;
import vn.yotel.vbilling.repository.MtSmsRepo;
import vn.yotel.vbilling.service.*;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.TransferIsdn;
import vn.yotel.yomi.AppParams;

import javax.annotation.Resource;
import javax.management.StringValueExp;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

//import com.hazelcast.core.IMap;


@Controller
@RequestMapping(value = "/custcare/kpbt/new")
public class CustCareYomiNewController {

    private Logger LOG = LoggerFactory.getLogger(CustCareYomiNewController.class);

    Gson GSON_ALL = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

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
    BlacklistService blacklistService;

    @Autowired
    SmsService smsService;

    @Autowired
    XsPromotionSumService xsPromotionSumService;

    @Autowired
    XsPromotionLogService xsPromotionLogService;

    @Autowired
    SubscriberLogService subscriberLogService;
//
//    @RequestMapping(value = "/mt_history.html", method = { RequestMethod.GET, RequestMethod.POST })
//    public @ResponseBody String getMtHistory(
//            @RequestParam("msisdn") String msisdn,
//            @RequestParam("startdate") String startDate,
//            @RequestParam("enddate") String endDate,
//            @RequestParam("numPage") String numPage) {
//        LOG.info("getMtHistory::BEGIN");
//        RestMessage resp = null;
//        String result = null;
//        try {
//            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
//            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
//            String newIsdn = TransferIsdn.transferToParam(msisdn);
//
//            int _numPage = Integer.parseInt(numPage);
//            Pageable _pageable = new PageRequest(_numPage,10);
//            Page<MtHistory> listMt = smsService.getMtHistory(newIsdn, _fromDate, _toDate, _pageable);
//            LOG.info(listMt.toString());
//            resp = RestMessage.RestMessageBuilder.SUCCESS();
//            resp.setData(listMt);
//        } catch (Exception e) {
//            LOG.error("", e);
//            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
//        }
//        LOG.info("getMtHistory::END");
//        return GSON_ALL.toJson(resp);
//    }

    @RequestMapping(value = "/promotion_sum.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String getPromotionSum(
           @RequestParam("startdate") String startDate,
           @RequestParam("enddate") String endDate,
           @RequestParam("numPage") String numPage) {
        LOG.info("getPromotionSum::BEGIN");
        RestMessage resp = null;
        String result = null;
        try {
            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
            Page<XsPromotionSum> pagePromotionSum  = null;
            try {
                int _numPage = Integer.parseInt(numPage);
                Pageable _pageable = new PageRequest(_numPage,10);
                pagePromotionSum = xsPromotionSumService.findAllByDate(_fromDate, _toDate, _pageable);
            }catch (Exception ex){
                LOG.error("",ex);
            }
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            if(pagePromotionSum.hasContent()) {
                resp.setData(pagePromotionSum.getContent());
                resp.setMessage(String.valueOf(pagePromotionSum.getTotalElements()));
            }
            result = GSON_ALL.toJson(resp);
        } catch (Exception e) {
            LOG.error("", e);
            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
        }
        LOG.info("getPromotionSum::END");
        return result;
    }

    @RequestMapping(value = "/promotion_log.html", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String getPromotionLog(
           @RequestParam("startdate") String startDate,
           @RequestParam("enddate") String endDate,
           @RequestParam("msisdn") String msisdn,
           @RequestParam("numPage") String numPage){
        LOG.info("getPromotionLog::BEGIN");
        RestMessage resp = null;
        String result = null;
        try {
            Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
            Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
            Page<XsPromotionLog> pagePromotionLog = null;
            try {
                int _numPage = Integer.parseInt(numPage);
                Pageable _pageable = new PageRequest(_numPage, 10);
                pagePromotionLog = xsPromotionLogService.findAllByDate(_fromDate, _toDate, msisdn, _pageable);
            } catch (Exception ex) {
                LOG.error("",ex);
            }
            resp = RestMessage.RestMessageBuilder.SUCCESS();
            if (pagePromotionLog.hasContent()) {
                resp.setData(pagePromotionLog.getContent());
                resp.setMessage(String.valueOf(pagePromotionLog.getTotalElements()));
            }
            result = GSON_ALL.toJson(resp);
        } catch (Exception e) {
            LOG.error("", e);
            resp = RestMessage.RestMessageBuilder.FAIL("001", e.getMessage());
        }
        LOG.info("getPromotionLog::END");
        return result;
    }

    @RequestMapping(value = "/charge_history.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String getChartHistory(Model model, HttpServletRequest request,
                                  @RequestParam(value = "from_date", defaultValue = "") String fromDate,
                                  @RequestParam(value = "to_date", defaultValue = "") String toDate,
                                  @RequestParam(value = "phone", defaultValue = "") String phone,
                                  @RequestParam(value = "status", defaultValue = "all") String status,
                                  @RequestParam(value = "action", defaultValue = "all") String action) {
        LOG.info("getChartHistory::BEGIN");
        RestMessage resp = null;
        try {
            if (phone.equalsIgnoreCase("")) {
                if (request.getSession().getAttribute("phone") != null) {
                    phone = (String) request.getSession().getAttribute("phone");
                }
            } else {
                request.getSession().setAttribute("phone", phone);
            }
            Date _fromDate = new Date();
            Date _toDate = new Date();
            List<SubscriberLog> listSubscriberLog = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                _fromDate = sdf.parse(fromDate);
                _toDate = sdf.parse(toDate);
            } catch (Exception e) {
                Calendar calendar = Calendar.getInstance();
                _toDate = new Date();
                calendar.setTime(_toDate);
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                _fromDate = calendar.getTime();
                fromDate = sdf.format(_fromDate);
                toDate = sdf.format(_toDate);
            }

                //HienHV_BEGIN: Sua so isdn truyen vao param de query ca 10 va 11 so
                String newIsdn = TransferIsdn.transferToParam(phone);
                listSubscriberLog = subscriberLogService.findCharge(_fromDate, _toDate, newIsdn, action, status);
                //HienHV_END: Sua so isdn truyen vao param de query ca 10 va 11 so
                resp = RestMessage.RestMessageBuilder.SUCCESS();
                resp.setData(listSubscriberLog);
        }catch (Exception ex) {
            LOG.info("", ex);
            resp = RestMessage.RestMessageBuilder.FAIL("001", ex.getMessage());
        }
        LOG.info("searchSms::END");
        return GSON_ALL.toJson(resp);
    }

}
