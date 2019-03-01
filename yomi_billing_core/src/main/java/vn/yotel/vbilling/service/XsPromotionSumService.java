package vn.yotel.vbilling.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yotel.vbilling.jpa.XsPromotionSum;
import vn.yotel.vbilling.model.XsPromotionSumBean;

import java.util.Date;
import java.util.List;

public interface XsPromotionSumService {
	XsPromotionSum create(XsPromotionSum xsPromotionSum);
	XsPromotionSum update(XsPromotionSum xsPromotionSum);
	void delete(XsPromotionSum xsPromotionSum);

	List<XsPromotionSum> findAllByDate(Date fromDate, Date toDate);

	Integer findNumberMaxByDate(Date processDate);

	List<Object[]> findSubsNumberMax(Date toDate);

//    void resumXsPromotionDaily(Date processDate);

    XsPromotionSum findByDate(Date processDate);

	List<Object[]> getListAwardByWeek(Date processDate, String msisdn);

	List<Object[]> getListAwardByMonth(Date processDate, String msisdn);

	Page<XsPromotionSum> findAllByDate(Date fromDate, Date toDate, Pageable pageable);
}
