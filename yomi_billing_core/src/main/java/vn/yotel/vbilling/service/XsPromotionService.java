package vn.yotel.vbilling.service;

import vn.yotel.vbilling.jpa.XsPromotion;

import java.util.Date;
import java.util.List;

public interface XsPromotionService {
	XsPromotion create(XsPromotion xsPromotion);
	XsPromotion update(XsPromotion xsPromotion);
	void delete(XsPromotion xsPromotion);
	XsPromotion findByMsisdnAndVasPackageCodeAndStatus(String msisdn, String vasPakageCode, String status);
	List<String> findAllNumber();
	List<XsPromotion> findPromotion(String msisdn, String status);
    List<XsPromotion> findAllByStatus(String status);
	List<XsPromotion> findAllByModifyTime(Date fromDate, Date toDate, String status);
}
