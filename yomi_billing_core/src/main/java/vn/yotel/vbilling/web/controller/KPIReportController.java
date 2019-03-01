package vn.yotel.vbilling.web.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.IPRangeChecker;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.RestMessage.RestMessageBuilder;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.DetectNumberLog;
import vn.yotel.vbilling.jpa.KpiDaily;
import vn.yotel.vbilling.model.ErrorCode;
import vn.yotel.vbilling.service.KPIReportService;
import vn.yotel.vbilling.util.MessageBuilder;

@Controller
@RequestMapping(value = "/kpi")
public class KPIReportController {
	
	private Logger LOG = LoggerFactory.getLogger(KPIReportController.class);
	private Logger LOG_DETECT_MSISDN = LoggerFactory.getLogger(KPIReportController.class);
	
	private KPIReportService kpiReportService;
	
	@RequestMapping(value = "/mt/daily", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RestMessage kpiMTDaily(@RequestParam("startdate") String startDate, @RequestParam("enddate") String endDate) {
		LOG.info("from: {}, to: {}", startDate, startDate);
		RestMessage resp = null;
		try {
			Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
			Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
			List<KpiDaily> list = kpiReportService.getMtKpiDaily(_fromDate, _toDate);
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(list);
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("001", e.getMessage());
		}
		return resp;
	}
	
	@RequestMapping(value = "/charge/daily", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RestMessage kpiChargeDaily(
			@RequestParam("startdate") String startDate,
			@RequestParam("enddate") String endDate) {
		LOG.info("from: {}, to: {}", startDate, startDate);
		RestMessage resp = null;
		try {
			Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
			Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
			List<KpiDaily> list = kpiReportService.getChargeKpiDaily(_fromDate, _toDate);
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(list);
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("001", e.getMessage());
		}
		return resp;
	}
	
	@RequestMapping(value = "/charge/error-statistics", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RestMessage kpiErrorStatisticDaily(
			@RequestParam("startdate") String startDate,
			@RequestParam("enddate") String endDate) {
		LOG.info("from: {}, to: {}", startDate, startDate);
		RestMessage resp = null;
		try {
			Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
			Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
			List<ErrorCode> list = kpiReportService.getErrorStatisticDaily(_fromDate, _toDate);
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(list);
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("001", e.getMessage());
		}
		return resp;
	}
	
	@RequestMapping(value = "/detectnumber/daily", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RestMessage kpiDetectNumberDaily(
			@RequestParam("startdate") String startDate,
			@RequestParam("enddate") String endDate) {
		LOG.info("from: {}, to: {}", startDate, startDate);
		RestMessage resp = null;
		try {
			Date _fromDate = Util.BB_CC_SDF_yyyyMMdd.parse(startDate);
			Date _toDate = Util.BB_CC_SDF_yyyyMMdd.parse(endDate);
			List<KpiDaily> list = kpiReportService.getDetectNumberKpiDaily(_fromDate, _toDate);
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(list);
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("001", e.getMessage());
		}
		return resp;
	}
	
	@RequestMapping(value = "/msisdn/logDetectNumber.html", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String logDetectNumber(
			@RequestParam(value = "client_ip", required = true, defaultValue = "") String clientIp,
			@RequestParam(value = "msisdn", required = true, defaultValue = "") String msisdn,
			HttpServletRequest request) {
		LOG.info("from: {}, msisdn: {}", clientIp, msisdn);
		String detectLine = String.format("%s|%s", clientIp, msisdn);
		RestMessage resp = null;
		try {
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			if ((IPRangeChecker.listIpRange == null) || (IPRangeChecker.listIpRange.size() == 0)) {
				//TODO
			}
			if (IPRangeChecker.isInRange(IPRangeChecker.listIpRange, clientIp)) {
				detectLine += "|1";
				msisdn = Util.normalizeMsIsdn(msisdn);
				boolean val = IPRangeChecker.isValiadReceiveMsIsdn(msisdn);
				detectLine += "|" + (val ? "1" : "0");
				DetectNumberLog detectLog = MessageBuilder.buildDetectNumberLog(new Date(), request.getRemoteAddr(), clientIp, msisdn, val);
				kpiReportService.createDetectNumberLog(detectLog);
				resp = RestMessageBuilder.SUCCESS();
			} else {
				detectLine += "|0";
				resp = RestMessageBuilder.FAIL("100", "Client IP not in 3G ip pool");
			}
		} catch (AppException e) {
			resp = RestMessageBuilder.FAIL(e.getCode(), e.getMessage());
		} catch (Exception e) {
			LOG.error("", e);
			resp = RestMessageBuilder.FAIL("9999", e.getMessage());
		} finally {
			LOG_DETECT_MSISDN.info(detectLine);
		}
		return Util.GSON_ALL.toJson(resp);
	}
}
