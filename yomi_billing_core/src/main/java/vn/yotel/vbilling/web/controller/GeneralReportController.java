package vn.yotel.vbilling.web.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.util.RestMessage;
import vn.yotel.commons.util.RestMessage.RestMessageBuilder;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.StatsModel;

@Controller
@RequestMapping(value = "/report")
public class GeneralReportController {
	
	private Logger LOG = LoggerFactory.getLogger(CustCareController.class);
	Gson gson = new GsonBuilder().serializeNulls().create();

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/queues.html" }, method = { RequestMethod.GET })
	public String checkQueues(Model model) {
		LOG.info("Checking queues: {}", "");
		NumberFormat formatter = new DecimalFormat("#,##0");
		Queue<MTRequest> mtQueueSMSC = (Queue<MTRequest>) AppContext.getBean("mtQueueToSMSC");
		StatsModel mtQueueSMSCModel = new StatsModel("Mt Queue (SMSC)", formatter.format(mtQueueSMSC.size()));
		//
		Queue<MTRequest> mtQueue = (Queue<MTRequest>) AppContext.getBean("mtQueue");
		StatsModel mtQueueModel = new StatsModel("Mt Queue", formatter.format(mtQueue.size()));
		//
		Queue<MTRequest> mtContentQueue = (Queue<MTRequest>) AppContext.getBean("mtContentQueue");
		StatsModel mtContentQueueModel = new StatsModel("Mt Content Queue", formatter.format(mtContentQueue.size()));
		
		//
		Queue<Integer> maxTpsQueue = (Queue<Integer>) AppContext.getBean("maxTpsQueue");
		StatsModel maxTpsQueueModel = new StatsModel("Max Tps Queue", formatter.format(maxTpsQueue.size()));
		//
		model.addAttribute("mtQueueSMSC", mtQueueSMSCModel);
		model.addAttribute("mtQueue", mtQueueModel);
		model.addAttribute("mtContentQueue", mtContentQueueModel);
		model.addAttribute("maxTpsQueue", maxTpsQueueModel);
		return "report/queue";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/clearQueues.html" }, method = { RequestMethod.POST })
	public @ResponseBody String clearQueues(Model model) {
		LOG.info("clearQueues: {}", "");
		Map<String, Object> result = new HashMap<String, Object>();
		Queue<Integer> maxTpsQueue = (Queue<Integer>) AppContext.getBean("maxTpsQueue");
		result.put("maxTpsQueue-size", maxTpsQueue.size());
		maxTpsQueue.clear();
		result.put("topupQueue-size-after", maxTpsQueue.size());
		RestMessage resp = RestMessageBuilder.SUCCESS();
		resp.setData(result);
		return gson.toJson(resp);
	}
}
