package vn.yotel.vbilling.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.yotel.admin.jpa.SysParam;
import vn.yotel.admin.service.SysParamService;
import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.commons.util.Util;
import vn.yotel.vbilling.jpa.MoSms;
import vn.yotel.vbilling.jpa.MtSms;
import vn.yotel.vbilling.jpa.SmsSyntax;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtHistory;
import vn.yotel.vbilling.model.MtModel;
import vn.yotel.vbilling.repository.MoSmsRepo;
import vn.yotel.vbilling.repository.MtSmsRepo;
import vn.yotel.vbilling.repository.SmsSyntaxRepo;
import vn.yotel.vbilling.service.SmsService;
import vn.yotel.yomi.AppParams;

/**
 *
 */
@Service(value = "smsService")
@Transactional
public class SmsServiceImpl extends GenericBoImpl<MoSms, Integer> implements SmsService {

	@Resource private MoSmsRepo moSmsRepo;
	@Resource private MtSmsRepo mtSmsRepo;
	@Resource private SmsSyntaxRepo smsSyntaxRepo;
	@Resource private SysParamService sysParamService;

	public static final Gson GSON_ALL = new GsonBuilder().serializeNulls().create();

	private final String _MT_KEY = "_MT_KEY";

	public SmsServiceImpl() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public MoSmsRepo getDAO() {
		return this.moSmsRepo;
	}

	@Override
	public void createMt(MtSms record) {
		this.mtSmsRepo.save(record);
	}

	@Override
	public List<SmsSyntax> findAllSmsSyntax() {
		return smsSyntaxRepo.findAll();
	}

	@Override
	public MoSms findBySmsId(String smsId) {
		return moSmsRepo.findBySmsId(smsId);
	}
	@Override
	public Page<MoSms> findMo(Date fromDate, Date toDate, String keyword, String msisdn, String message, Pageable page) {
		return moSmsRepo.findMo(fromDate, toDate, keyword, msisdn, message, page);
	}

	@Override
	public Page<MoSms> findAllMo(Date fromDate, Date toDate, String msisdn, Pageable page) {
		return moSmsRepo.findByMsisdn(fromDate, toDate, msisdn, page);
	}

	@Override
	public Page<MtSms> findAllMt(Date fromDate, Date toDate, String msisdn, Pageable page) {
		return mtSmsRepo.findByMsisdn(fromDate, toDate, msisdn, page);
	}

	@Override
	public List<Object[]> countMO(Date fromDate, Date toDate) {
		return moSmsRepo.countMO(fromDate, toDate);
	}

	@Override
	public Long countMT(Date fromDate, Date toDate, String messagePrefix) {
		return mtSmsRepo.countMT(fromDate, toDate, messagePrefix);
	}

	@Override
	public List<Object[]> countMT(Date fromDate, Date toDate) {
		return mtSmsRepo.countMT(fromDate, toDate);
	}

	@Override
	public void logMtRequest(MTRequest mtReq) {
		MtSms record = new MtSms();
		String smsId = (mtReq.getMoReq() != null) ? mtReq.getMoReq().getSmsId() : "";
		record.setShortCode(mtReq.getFromNumber());
		record.setMsisdn(mtReq.getToNumber());
		record.setMessage(mtReq.getMessage());
		record.setSmsId(smsId);
		record.setServiceCode(AppParams.PRODUCT_NAME);
		record.setKeyword(mtReq.getMtType());
		record.setCreatedDate(new Date());
		record.setChannel(mtReq.getChannel());
		record.setSentStatus(true);
		record.setType(mtReq.getMtType());
		mtSmsRepo.save(record);
	}

	@Override
	public List<MtHistory> getMtHistory(String msisdn, Date _fromDate, Date _toDate) {
		List<MtHistory> result = new ArrayList<MtHistory>();
		DateTime dtFromDate = new DateTime(_fromDate);
		dtFromDate = dtFromDate.withTimeAtStartOfDay();
		DateTime dtToDate = new DateTime(_toDate);
		dtToDate = dtToDate.withTime(23, 59, 59, 999);
		List<Object[]> dataReport = mtSmsRepo.getMtHistory(msisdn, dtFromDate.toDate(), dtToDate.toDate());
		for (Object[] eachRow : dataReport) {
			String isdn = (String) eachRow[0];//HienHV: Lay gia tri isdn tra ve thu query
			String mtMessage = (String) eachRow[1];
			Date mtTime = (Date) eachRow[2];
			String moMessage = (String) eachRow[3];
			Date moDate = (Date) eachRow[4];
			int mtId = ((Integer) eachRow[5]).intValue();
			String channel = (String) eachRow[6];
			String keyword = (String) eachRow[7];
			MtHistory mt = new MtHistory();
			mt.setMsisdn(isdn);
			mt.setMessage(mtMessage);
			mt.setSentDate(mtTime);
			mt.setMoMessage(moMessage);
			mt.setMoDate(moDate);
			mt.setType(keyword);
			mt.setId(mtId);
			mt.setStrMtDate(Util.BIBIBOOK_SDF.format(mtTime));
			mt.setStrMoDate((moDate != null) ? Util.BIBIBOOK_SDF.format(moDate) : null);
			mt.setChannel(channel);
			result.add(mt);
		}
		return result;
	}

	@Override
	public MtModel mtModel() {
		MtModel mtModel = new MtModel();
		SysParam param = sysParamService.findByKey(_MT_KEY);
		if (param != null) {
			mtModel = GSON_ALL.fromJson(param.getValue(), MtModel.class);
		}
		return mtModel;
	}
}
