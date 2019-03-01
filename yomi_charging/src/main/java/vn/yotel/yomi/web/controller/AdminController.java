package vn.yotel.yomi.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.*;
import vn.yotel.vbilling.model.*;
import vn.yotel.vbilling.service.*;
import vn.yotel.vbilling.util.TransferIsdn;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.hazelcast.core.IMap;


@Controller
@RequestMapping(value = "/yomiad")
public class AdminController {

    private Logger LOG = LoggerFactory.getLogger(AdminController.class);
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Gson GSON_ALL = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    @Autowired
    XsPromotionSumService xsPromotionSumService;

    @Autowired
    XsPromotionLogService xsPromotionLogService;

    @Autowired
    CdrLogService cdrLogService;


    @RequestMapping(value = { "/ping" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getPing(HttpServletRequest request,@RequestParam("msisdn") String msisdn) {
        RestMessage resp = null;
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2);
            String newIsdn = TransferIsdn.transferToParam(msisdn);
            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            List<Object[]> lst = xsPromotionSumService.getListAwardByWeek(cal.getTime(),newIsdn);
            List<XsPromotionSumBean> lstAward = new ArrayList<>();
            if(lst != null) {
                for (Object[] row: lst) {
                    XsPromotionSumBean item = new XsPromotionSumBean();
                    item.setMsisdn(String.valueOf(row[0]));
                    item.setNumber_pick(String.valueOf(row[1]));
                    item.setStatus(String.valueOf(row[2]));
                    item.setCreated_date(String.valueOf(row[3]));
                    item.setCreated_time(String.valueOf(row[4]));
                    item.setId(String.valueOf(row[5]));
                    item.setPromo_id(String.valueOf(row[6]));
                    item.setAward_type(String.valueOf(row[7]));
                    item.setSum_note(String.valueOf(row[8]));
                    lstAward.add(item);
                }
            }

            resp.setData(lstAward);
//            LOG.info("XXX");
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @ResponseBody
    @RequestMapping(value = { "/top10number.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public String top10Number(HttpServletRequest request,
                              @RequestParam(value = "fromDate", required = true) String fromDate,
                              @RequestParam(value = "toDate", required = true) String toDate,
                              @RequestParam(value = "msisdn", required = false) String msisdn) {
        LOG.info("BEGIN::top10Number");
        LOG.info("PARAM::fromDate=" + fromDate + ";toDate=" + toDate + ";msisdn=" + msisdn);
        RestMessage resp = null;
        try {
            Date dtFromDate = sdf.parse(fromDate);
            Date dtToDate = sdf.parse(toDate);
//            String newIsdn = TransferIsdn.transferToParam(msisdn);
            HashMap<String, Object> data = new HashMap<>();
            // Danh sach thue bao trung giai tuan
            List<Object[]> lstBean = xsPromotionSumService.getListAwardByWeek(dtFromDate,msisdn);
            List<XsPromotionSumBean> lstAward = new ArrayList<>();
            if(lstBean != null) {
                for (Object[] row: lstBean) {
                    XsPromotionSumBean item = new XsPromotionSumBean();
                    item.setMsisdn(String.valueOf(row[0]));
                    item.setNumber_pick(String.valueOf(row[1]));
                    item.setStatus(String.valueOf(row[2]));
                    item.setCreated_date(String.valueOf(row[3]));
                    item.setCreated_time(String.valueOf(row[4]));
                    item.setId(String.valueOf(row[5]));
                    item.setPromo_id(String.valueOf(row[6]));
                    item.setAward_type(String.valueOf(row[7]));
                    item.setSum_note(String.valueOf(row[8]));
                    item.setVpoint(String.valueOf(row[9]));
                    lstAward.add(item);
                }
            }
//            LOG.info(GSON_ALL.toJson(lstBean));
            data.put("lstAwardWeek", lstAward);

            List<XsPromotionLogModel> lstReturn = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dtToDate);
            cal.set(Calendar.DATE, -1);
            List<Object[]> lst = xsPromotionSumService.findSubsNumberMax(cal.getTime());

            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            if (lst != null && !lst.isEmpty()) {
                for (Object[] row: lst) {
                    XsPromotionLogModel xsPromotionLog = new XsPromotionLogModel();
                    xsPromotionLog.setId(String.valueOf(row[0]));
                    xsPromotionLog.setMsisdn(String.valueOf(row[1]));
                    xsPromotionLog.setNumberPick(String.valueOf(row[3]));
                    xsPromotionLog.setCreatedTime(String.valueOf(row[5]));
                    lstReturn.add(xsPromotionLog);
                }
            }
            data.put("lstTopNumber", lstReturn);
            LOG.info(GSON_ALL.toJson(data));
            resp.setData(data);
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        LOG.info("END::top10Number");
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = { "/numbermax" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getNumberMax(HttpServletRequest request,
                                             @RequestParam(value = "date", required = true) String processDate,
                                             @RequestParam(value = "sum", defaultValue = "0") int isSum) {
        RestMessage resp = null;
        try {
            LOG.info("BEGIN getNumberMax: {}", processDate);
            Date dtProcessDate = Calendar.getInstance().getTime();
            if (processDate != null && !"".equals(processDate)) {
                dtProcessDate = Util.BB_CC_SDF_yyyyMMdd.parse(processDate);
            }
//            if (isSum == 1) {
//                xsPromotionSumService.resumXsPromotionDaily(dtProcessDate);
//            }
//            XsPromotionSum xsPromotionSum = xsPromotionSumService.findByDate(dtProcessDate);
            Integer maxNumber = xsPromotionSumService.findNumberMaxByDate(dtProcessDate);

            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            resp.setData(maxNumber);
            LOG.info("END getNumberMax: {}", maxNumber);
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @RequestMapping(value = { "/promotion_log_numberMax.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getAllByNumberMax(HttpServletRequest request,
                                                  @RequestParam(value = "processDate", required = true) String processDate,
                                                  @RequestParam(value = "sum", defaultValue = "0") int isSum) {
        RestMessage resp = null;
        try {
            LOG.info("BEGIN promotionLogNumberMax: {}", processDate);
            Date dtProcessDate = Calendar.getInstance().getTime();
            if (processDate != null && !"".equals(processDate)) {
                dtProcessDate = Util.BB_CC_SDF_yyyyMMdd.parse(processDate);
            }
//            if (isSum == 1) {
//                xsPromotionSumService.resumXsPromotionDaily(dtProcessDate);
//            }
//            XsPromotionSum xsPromotionSum = xsPromotionSumService.findByDate(dtProcessDate);
            Integer maxNumber = xsPromotionSumService.findNumberMaxByDate(dtProcessDate);
            List<XsPromotionLog> listPromotionLog = xsPromotionLogService.findAllByNumberMaxAndDate(String.valueOf(maxNumber), dtProcessDate);

            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            resp.setData(listPromotionLog);
            LOG.info("END promotionLogNumberMax: {}", listPromotionLog);
        } catch (Exception e) {
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @ResponseBody
    @RequestMapping(value = { "/report_control_month.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public String getReportControlMonth(HttpServletRequest request,
                                        @RequestParam(value = "fromDate") String fromDate,
                                        @RequestParam(value = "toDate") String toDate){
        RestMessage resp = null;
        try {
            Date dtFromDate = sdf.parse(fromDate);
            Date dtToDate = sdf.parse(toDate);
            HashMap<String, Object> data = new HashMap<>();
            List<CdrLogModel> listControlMonth = cdrLogService.reportControlMonth(dtFromDate, dtToDate);
            data.put("listControlMonth", listControlMonth);
            List<CdrLogModel> listRevenueMonthly = cdrLogService.reportRevenueMonthly(dtFromDate, dtToDate);
            data.put("listRevenueMonthly", listRevenueMonthly);
            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            resp.setData(data);
        }catch (Exception e){
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }


    /**
     * Ham lay danh sach thue bao dat giai tuan/ thang
     * @param request
     * @param processDate
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "/report_list_award.html" }, method = {RequestMethod.GET, RequestMethod.POST})
    public String getReportListAward(HttpServletRequest request,
                                     @RequestParam(value = "processDate") String processDate,
                                     @RequestParam("msisdn") String msisdn){
        RestMessage resp = null;
        try {
            Date dtProcessDate = sdf.parse(processDate);
            HashMap<String, Object> data = new HashMap<>();
            //Lay ds giai tuan cua cac thue bao
            List<Object[]> listBeanWeek = xsPromotionSumService.getListAwardByWeek(dtProcessDate,msisdn);
            List<XsPromotionSumBean> listAwardWeek = new ArrayList<>();
            for (Object[] list: listBeanWeek) {
                XsPromotionSumBean item = new XsPromotionSumBean();
                item.setMsisdn(String.valueOf(list[0]));
                item.setNumber_pick(String.valueOf(1));
                item.setCreated_date(String.valueOf(3));
                item.setCreated_time(String.valueOf(4));
                item.setAward_type(String.valueOf(7));
                item.setSum_note(String.valueOf(list[8]));
                item.setVpoint(String.valueOf(list[9]));
                listAwardWeek.add(item);
            }
            data.put("listAwardWeek", listAwardWeek);
            //Lay ds giai thang cua cac thue bao
            List<Object[]> listBeanMonth = xsPromotionSumService.getListAwardByMonth(dtProcessDate,msisdn);
            List<XsPromotionSumBean> listAwardMonth = new ArrayList<>();
            for (Object[] list: listBeanMonth) {
                XsPromotionSumBean item = new XsPromotionSumBean();
                item.setMsisdn(String.valueOf(list[0]));
                item.setNumber_pick(String.valueOf(1));
                item.setCreated_date(String.valueOf(3));
                item.setCreated_time(String.valueOf(4));
                item.setAward_type(String.valueOf(7));
                item.setSum_note(String.valueOf(list[8]));
                item.setVpoint(String.valueOf(list[9]));
                listAwardMonth.add(item);
            }
            data.put("listAwardMonth", listAwardMonth);

            resp = RestMessage.RestMessageBuilder.SUCCESS("SUCCESS");
            resp.setData(data);
        }catch (Exception e){
            resp = RestMessage.RestMessageBuilder.FAIL("EXCEPTION", e.getMessage());
        }
        return GSON_ALL.toJson(resp);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String missingParamterHandler(Exception exception) {
        LOG.error("", exception);
        return "/400"; /* view name of your erro jsp */
    }
}
