package vn.yotel.vbilling.resource;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.MoSms;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MTRequest.MtGateway;
import vn.yotel.vbilling.model.MTRequest.MtType;
import vn.yotel.vbilling.model.ResponseData;
import vn.yotel.vbilling.service.SmsService;
import vn.yotel.vbilling.util.ChargingCSPClient;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;


@Component
@Path(value = "/")
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED })
public class SmsResource {

    private static final Logger logger = LoggerFactory.getLogger(SmsResource.class);

    @Context
    private HttpServletRequest request;

    @Context
    private HttpHeaders httpHeaders;

    @Resource private ChargingCSPClient chargingCSPClient;
	@Resource private SmsService smsService;

    private final static ObjectMapper objectMapper = new ObjectMapper();

	@POST
	@Path(value = "/mtRequest")
	public Response mtRequest(
			@Context HttpServletRequest req,
			@FormParam("msisdn") String _msisdn,
			@FormParam("content") String content,
			@DefaultValue("0") @FormParam("type") String type,
			@DefaultValue("0") @FormParam("flash_sms") Integer flashSMS) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("isdn", _msisdn);
			parameters.put("content", content);
			logger.info("mtRequest[{}]", objectMapper.writeValueAsString(parameters));
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
		ResponseData responseData = null;
		String isdn = Util.normalizeMsIsdn(_msisdn);
		try {
			@SuppressWarnings("unchecked")
			Queue<MTRequest> mtQueue = (Queue<MTRequest>) AppContext.getBean("mtQueue");
			Object mtQueueNotifier = AppContext.getBean("mtQueueNotifier");
			//
			@SuppressWarnings("unchecked")
			Queue<MTRequest> mtContentQueue = (Queue<MTRequest>) AppContext.getBean("mtContentQueue");
			Object mtContentQueueNotifier = AppContext.getBean("mtContentQueueNotifier");
			//TT
			MTRequest mtRequest = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, isdn, content, null, type);
			if (flashSMS > 0) {
				mtRequest.setFlashSms(true);
			}
			if (MtType.TT.value().equals(type)) {
				mtRequest.setGateway(MtGateway.SMPP_HN.value());
				mtQueue.offer(mtRequest);
				synchronized (mtQueueNotifier) {
					mtQueueNotifier.notifyAll();
				}
			} else {
				mtRequest.setGateway(MtGateway.SMPP_HN.value());
				mtContentQueue.offer(mtRequest);
				synchronized (mtContentQueueNotifier) {
					mtContentQueueNotifier.notifyAll();
				}
				synchronized (mtQueueNotifier) {
					mtQueueNotifier.notifyAll();
				}
			}
			responseData = ResponseData.responseData("1", "Ok");
		} catch (Exception e) {
			logger.error("", e);
			responseData = ResponseData.responseData("0", e.getMessage());
		}
		return Response.ok(responseData).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Path(value = "/getMtMo")
	public Response getMtMo(
			@Context HttpServletRequest req,
			@FormParam("from_date") String fromDate,
			@FormParam("to_date") String toDate) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("from_date", fromDate);
			parameters.put("to_date", toDate);
			logger.info("getMtMo[{}]", objectMapper.writeValueAsString(parameters));
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
		ResponseData responseData = new ResponseData();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date _fromDate = sdf.parse(fromDate);
			Date _toDate = sdf.parse(toDate);
			LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("mt", smsService.countMT(_fromDate, _toDate));
			data.put("mo", smsService.countMO(_fromDate, _toDate));
			responseData.setData(data);
		} catch (Exception e) {
			logger.error("", e);
			responseData = ResponseData.responseData("0", e.getMessage());
		}
		return Response.ok(responseData).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Path(value = "/historyMtMo")
	public Response historyMtMo(
			@Context HttpServletRequest req,
			@FormParam("from_date") String fromDate,
			@FormParam("to_date") String toDate,
			@FormParam("msisdn") String msisdn,
			@FormParam("type") String type,
			@DefaultValue(value = "0") @FormParam("page") int page,
			@DefaultValue(value = "20") @FormParam("size") int size) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("from_date", fromDate);
			parameters.put("to_date", toDate);
			parameters.put("msisdn", msisdn);
			parameters.put("type", type);
			logger.info("historyMtMo[{}]", objectMapper.writeValueAsString(parameters));
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
		ResponseData responseData = new ResponseData();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date _fromDate = sdf.parse(fromDate);
			Date _toDate = sdf.parse(toDate);

			if(msisdn.isEmpty()) {
				msisdn = null;
			}

			Sort sort = new Sort(new Sort.Order(Direction.DESC, "createdDate"));
			Pageable pageRequest = new PageRequest(page, size, sort);

			if(type.toUpperCase().equals("0")) {
				responseData.setData(smsService.findAllMo(_fromDate, _toDate, msisdn, pageRequest));
			} else {
				responseData.setData(smsService.findAllMt(_fromDate, _toDate, msisdn, pageRequest));
			}

		} catch (Exception e) {
			logger.error("", e);
			responseData = ResponseData.responseData("0", e.getMessage());
		}
		return Response.ok(responseData).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	
	@POST
	@Path(value = "/moHistory")
	public Response moHistory(
			@Context HttpServletRequest req,
			@FormParam("from_date") String fromDate,
			@FormParam("to_date") String toDate,
			@FormParam("msisdn") String msisdn,
			@FormParam("type") String type,
			@FormParam("keyword") String keyword,
			@FormParam("message") String message,
			@DefaultValue(value = "0") @FormParam("page") int page,
			@DefaultValue(value = "20") @FormParam("size") int size) {
		try {
			Map<String, String> parameters = new ConcurrentHashMap<String, String>();
			parameters.put("from_date", fromDate);
			parameters.put("to_date", toDate);
			parameters.put("msisdn", msisdn);
			parameters.put("type", type);
			parameters.put("message", message);
			logger.info("moHistory[{}]", objectMapper.writeValueAsString(parameters));
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
		ResponseData responseData = new ResponseData();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date _fromDate = sdf.parse(fromDate);
			Date _toDate = sdf.parse(toDate);

			if (Strings.isNullOrEmpty(msisdn)) {
				msisdn = null;
			}
			if (Strings.isNullOrEmpty(keyword)) {
				keyword = null;
			}
			if (Strings.isNullOrEmpty(message)) {
				message = null;
			}
			Map<String, Object> result = new HashMap<String, Object>();
			Sort sort = new Sort(new Sort.Order(Direction.DESC, "createdDate"));
			Pageable pageRequest = new PageRequest(page, size, sort);

			//type	0 - Like
			//		1 - not like
			Page<MoSms> pageData = smsService.findMo(_fromDate, _toDate, keyword, msisdn, message, pageRequest);
			result.put("content", pageData.getContent());
			result.put("count", pageData.getTotalElements());
			result.put("page", pageData.getTotalPages());
			responseData.setData(result);
		} catch (Exception e) {
			logger.error("", e);
			responseData = ResponseData.responseData("0", e.getMessage());
		}
		return Response.ok(responseData).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
