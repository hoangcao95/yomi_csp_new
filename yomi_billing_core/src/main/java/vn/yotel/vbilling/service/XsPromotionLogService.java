package vn.yotel.vbilling.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yotel.vbilling.jpa.XsPromotionLog;

import java.util.Date;
import java.util.List;

public interface XsPromotionLogService {
	XsPromotionLog create(XsPromotionLog xsPromotionLog);
	XsPromotionLog update(XsPromotionLog xsPromotionLog);
	void delete(XsPromotionLog xsPromotionLog);
	List<XsPromotionLog> findByTimeId(String msisdn, String timeId);
    List<XsPromotionLog> findByDate(Date dateCheck);
    List<XsPromotionLog> findAllByDate(Date fromDate, Date toDate, String phone);
    List<XsPromotionLog> findAllByNumberMaxAndDate(String numberPick, Date processDate);
    List<XsPromotionLog> findNumberMaxByDate(Date toDate, String numberNewPick, String msisdnNewPick);
    List<XsPromotionLog> findNumberMaxByDate(Date toDate);
    Page<XsPromotionLog> findAllByDate(Date fromDate, Date toDate, String phone, Pageable pageable);
}
