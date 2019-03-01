package vn.yotel.vbilling.web.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.yotel.admin.jpa.SysParam;
import vn.yotel.admin.service.SysParamService;
import vn.yotel.vbilling.model.MtModel;

@Controller
@RequestMapping(value = "/param")
public class SysParamController {
	
	private Logger LOG = LoggerFactory.getLogger(SysParamController.class);
	
	@Resource SysParamService sysParamService;
	final String _MT_KEY = "_MT_KEY";
	public static final Gson GSON_ALL = new GsonBuilder().serializeNulls().create();
	
	@RequestMapping(value = "/mt/update.html", method = { RequestMethod.GET})
	public String showUpdateMT(Model model) {
		LOG.info("==showUpdateMT==");
		SysParam param = sysParamService.findByKey(_MT_KEY);
		MtModel mtModel = new MtModel();
		if (param != null) {
			mtModel = GSON_ALL.fromJson(param.getValue(), MtModel.class);
		}
		model.addAttribute("mtModel", mtModel);
		return "utility/update_mt";
	}
	
	@RequestMapping(value = "/mt/update.html", method = { RequestMethod.POST })
	public String updateMT(Model model, @ModelAttribute("mtModel") MtModel mtModel) {
		LOG.info("==updateMT==");
		SysParam param = sysParamService.findByKey(_MT_KEY);
		if (param == null) {
			param = new SysParam();
			param.setType("JSON");
			param.setKey(_MT_KEY);
			param.setValue(GSON_ALL.toJson(mtModel));
			param.setCreatedDate(new Date());
			param.setStatus(1);
		} else {
			param.setValue(GSON_ALL.toJson(mtModel));
			param.setModifiedDate(new Date());
		}
		sysParamService.update(param);
		model.addAttribute("mtModel", mtModel);
		return "utility/update_mt";
	}
}
