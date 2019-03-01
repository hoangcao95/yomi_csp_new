package vn.yotel.vbilling.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.MoSms;
import vn.yotel.vbilling.jpa.MtSms;
import vn.yotel.vbilling.jpa.SmsSyntax;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.MtHistory;
import vn.yotel.vbilling.model.MtModel;

/**
 *
 */
public interface SmsService extends GenericBo<MoSms, Integer> {

	void createMt(MtSms record);

	List<SmsSyntax> findAllSmsSyntax();

	MoSms findBySmsId(String smsId);
	
	Page<MoSms> findMo(Date fromDate, Date toDate, String keyword, String msisdn, String message, Pageable page);

	Page<MoSms> findAllMo(Date fromDate, Date toDate,String msisdn, Pageable page);

	Page<MtSms> findAllMt(Date fromDate, Date toDate, String msisdn, Pageable page);

	List<Object[]> countMO(Date fromDate, Date toDate);

	Long countMT(Date fromDate, Date toDate, String messagePrefix);

	public List<Object[]> countMT(Date fromDate, Date toDate);

	void logMtRequest(MTRequest mtReq);

	List<MtHistory> getMtHistory(String msisdn, Date fromDate, Date toDate);

	MtModel mtModel();

}
