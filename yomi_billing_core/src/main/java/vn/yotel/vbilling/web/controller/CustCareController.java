package vn.yotel.vbilling.web.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.yotel.admin.service.SysParamService;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.IPRangeChecker;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.RestMessage.RestMessageBuilder;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.Blacklist;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.model.HandlingResult;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtHistory;
import vn.yotel.vbilling.service.BlacklistService;
import vn.yotel.vbilling.service.CpGateService;
import vn.yotel.vbilling.service.SmsService;
import vn.yotel.vbilling.service.SubscriberService;
import vn.yotel.vbilling.service.VasPackageService;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;
import vn.yotel.yomi.Constants.CommandCode;

@Controller
@RequestMapping(value = "/custcare")
public class CustCareController {

	private Logger LOG = LoggerFactory.getLogger(CustCareController.class);

	@Resource private SubscriberService subscriberService;
	@Resource private ChargingCSPClient chargingCSPClient;
	@Resource private SmsService smsService;
	@Resource private CpGateService cpGateService;
	@Resource private SysParamService sysParamService;
	@Resource private VasPackageService vasPackageService;
	@Resource private BlacklistService blacklistService;

	private String ERR_INVALID_PRODUCT_OR_PACKAGE = "0003";
	private String ERR_EXCEPTION = "9999";
	private String ERR = "0";


	public CustCareController() {
		loadMTFromMemory();
	}

	private void loadMTFromMemory() {
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
			List<MtHistory> listMt = smsService.getMtHistory(msisdn, _fromDate, _toDate);
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(listMt);
		} catch (Exception e) {
			LOG.error("", e);
			resp = RestMessageBuilder.FAIL("001", e.getMessage());
		}
		return GSON_ALL.toJson(resp);
	}

	@RequestMapping(value = "/cancel.html", method = { RequestMethod.GET,
			RequestMethod.POST }, headers = "Accept=*/*", produces = "application/json")
	public @ResponseBody String cancelToChargingCSP(
			@RequestParam("msisdn") final String isdn,
			@RequestParam("package_code") final String packageName,
			@RequestParam("cmd_code") final String cmdCode,
			HttpServletRequest request) {
		LOG.info("msisdn " + isdn);
		LOG.info("package_code " + packageName);
		LOG.info("cmd_code " + cmdCode);
		RestMessage wapResult = RestMessageBuilder.SUCCESS();
		try {
			String msisdn = Util.normalizeMsIsdn(isdn);
			String serviceCode = AppParams.SHORT_CODE;
			String packageCode = packageName;
			String commandCode = cmdCode;
			String sourceCode = "WAP";
			String result = chargingCSPClient.receiverServiceReq(serviceCode, msisdn, commandCode, packageCode, sourceCode);
			wapResult.setData(result);
			VasPackage vasPackage = vasPackageService.findByName(packageCode);
			if (vasPackage == null) {
				LOG.warn("Could not find corresponding packageCode: {}", packageCode);
				wapResult = RestMessageBuilder.FAIL(ERR, "Khong tin thay ma goi cuoc " + packageCode);
			} else {
				Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(msisdn, vasPackage.getId());
				Date now = new Date();
				String transIdNew = Util.generateTransId();
				String channel = "CSKH";
				String originalSms = commandCode;
				if (subscriber != null) {
					subscriber.setStatus(0);
					subscriber.setModifiedDate(now);
					subscriber.setUnregisterDate(now);
					this.subscriberService.update(subscriber);
					boolean cancelDone = true;
					String command = CommandCode.CANCEL;
					HandlingResult postResult = cpGateService.notifyCancelSubs(transIdNew, isdn,
							Util.XBD_SDF.format(now), originalSms, channel, vasPackage.getName(), cancelDone, "0000", "");
					this.subscriberService.logSubsRequest(subscriber, command, transIdNew, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
				}
			}
			LOG.info("receiverServiceReq: {}", result);
		} catch (Exception ex) {
			LOG.error("", ex);
			wapResult = RestMessageBuilder.FAIL(ERR_EXCEPTION, ex.getMessage());
		}
		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(wapResult);
	}

	@RequestMapping(value = "/cancel2.html", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ccSubsUnRegister(
			@RequestParam("msisdn") final String isdn,
			@RequestParam("package_code") final String packageName,
			HttpServletRequest request) {
		loadMTFromMemory();
		RestMessage wapResult = RestMessageBuilder.SUCCESS();
		String smsMessage = "";
		try {
			String msisdn = Util.normalizeMsIsdn(isdn);
			/**
			 * 1. Check input parameters
			 * 2. Check IP in 3G IP Pool
			 */
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			VasPackage vasPackage = this.vasPackageService.findByName(packageName);
			List<Subscriber> subscribers = this.subscriberService.findActiveByMsisdn(msisdn);
			boolean cancelDone = false;
			Date dte = new Date();
			if (vasPackage == null) {
				throw new AppException(ERR_INVALID_PRODUCT_OR_PACKAGE, "Invalid package");
			}
			Subscriber selectedSubs = null;
			for (Subscriber eachSubs : subscribers) {
				if (eachSubs.getPackageId().equals(vasPackage.getId())) {
					selectedSubs = eachSubs;
					break;
				}
			}
			if (selectedSubs != null) {
				//Already subs, process to un-subscription this phonenumber
				selectedSubs.setStatus(Subscriber.Status.INACTIVE.value());
				selectedSubs.setModifiedDate(dte);
				selectedSubs.setUnregisterDate(dte);
				this.subscriberService.update(selectedSubs);
//				smsMessage = MT_CAN_SUCCESS;
				cancelDone = true;
				smsMessage = smsMessage.replaceAll("<TENGOI>", vasPackage.getDesc());
				smsMessage = smsMessage.replaceAll("<MAGOI>", vasPackage.getName());
			} else {
//				smsMessage = MT_CAN_FAILED;
			}
			if (cancelDone) {
				String transId = Util.generateTransId();
				String channel = Constants.Channel.SYS;
				HandlingResult postResult = cpGateService.notifyCancelSubs(transId, msisdn, Util.XBD_SDF.format(dte), "", channel, vasPackage.getName(), cancelDone, "0000", "");
				this.subscriberService.logSubsRequest(selectedSubs, CommandCode.CANCEL, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
				MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, msisdn, smsMessage, null, CommandCode.CANCEL);
				mtReq.setChannel(Constants.Channel.SYS);
			}
		} catch (AppException ex) {
			LOG.error("", ex);
			wapResult = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
		} catch (Exception ex) {
			LOG.error("", ex);
			wapResult = RestMessageBuilder.FAIL(ERR_EXCEPTION, ex.getMessage());
		}
		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(wapResult);
	}

	@RequestMapping(value = { "/blacklist/add.html" }, method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String addNumberToBlacklist(
			HttpServletRequest request,
			@RequestParam(value = "msisdn") String msisdn) {
		LOG.info("msisdn " + msisdn);
		RestMessage resp = null;
		try {
			msisdn = Util.normalizeMsIsdn(msisdn);
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			Blacklist blacklist = this.blacklistService.findByMsisdn(msisdn);
			if(blacklist == null) {
				blacklist = this.blacklistService.addBlacklist(msisdn);
				if (blacklist == null) {
					resp = RestMessageBuilder.FAIL("001", "Could not add this number to Blacklist");
				}
				resp = RestMessageBuilder.SUCCESS();
			} else {
				resp = RestMessageBuilder.FAIL("1", "Duplicate number");
			}
		} catch (AppException ex) {
			LOG.error("", ex);
			resp = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("9999", e.getMessage());
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
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			Blacklist blacklist = this.blacklistService.removeBlacklist(msisdn);
			if (blacklist == null) {
				resp = RestMessageBuilder.FAIL("001", "Could not remove this number to Blacklist");
			}
			resp = RestMessageBuilder.SUCCESS();
		} catch (AppException ex) {
			LOG.error("", ex);
			resp = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("9999", e.getMessage());
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
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			List<Blacklist> subs = this.blacklistService.getAllBlackListSubs();
			resp = RestMessageBuilder.SUCCESS();
			resp.setData(subs);
		} catch (AppException ex) {
			LOG.error("", ex);
			resp = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("9999", e.getMessage());
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
			IPRangeChecker.validate_IPS(request.getRemoteAddr());
			Blacklist subs = this.blacklistService.findByMsisdn(msisdn);
			if (subs != null) {
				resp = RestMessageBuilder.SUCCESS();
				resp.setData(subs);
			} else {
				resp = RestMessageBuilder.FAIL("002", "Could not find this number in blacklist: " + msisdn);
			}
		} catch (AppException ex) {
			LOG.error("", ex);
			resp = RestMessageBuilder.FAIL(ex.getCode(), ex.getMessage());
		} catch (Exception e) {
			resp = RestMessageBuilder.FAIL("9999", e.getMessage());
		}
		return GSON_ALL.toJson(resp);
    }

	@RequestMapping(value = "/ping", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ping(HttpServletRequest request) {
		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(RestMessageBuilder.SUCCESS("Thành công"));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public @ResponseBody String missingParamterHandler(Exception exception) {
		LOG.error("", exception);
		RestMessage resp = RestMessageBuilder.FAIL("001", "Missing parameters");
		return GSON_ALL.toJson(resp);
	}

}