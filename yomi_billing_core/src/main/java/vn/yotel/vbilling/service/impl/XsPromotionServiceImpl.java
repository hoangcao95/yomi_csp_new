package vn.yotel.vbilling.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.XsPromotion;
import vn.yotel.vbilling.repository.XsPromotionRepo;
import vn.yotel.vbilling.service.XsPromotionService;

import java.util.Date;
import java.util.List;

@Service(value = "xsPromotionService")
@Transactional
public class XsPromotionServiceImpl implements XsPromotionService {
	
	@Autowired
	XsPromotionRepo xsPromotionRepo;

	@Override
	public XsPromotion create(XsPromotion xsPromotion) {
		return xsPromotionRepo.save(xsPromotion);
	}

	@Override
	public XsPromotion update(XsPromotion xsPromotion) {
		return xsPromotionRepo.save(xsPromotion);
	}

	@Override
	public void delete(XsPromotion xsPromotion) {
		xsPromotionRepo.delete(xsPromotion);
	}

	@Override
	public XsPromotion findByMsisdnAndVasPackageCodeAndStatus(String msisdn, String vasPakageCode, String status) {
		return xsPromotionRepo.findByMsisdnAndVasPackageCodeAndStatus(msisdn, vasPakageCode, status);
	}

	@Override
	public List<String> findAllNumber() {
		return xsPromotionRepo.findAllNumbers();
	}

	@Override
	public List<XsPromotion> findPromotion(String msisdn, String status) {
		return xsPromotionRepo.findPromotion(msisdn, status);
	}

	@Override
	public List<XsPromotion> findAllByStatus(String status) {
		return xsPromotionRepo.findAllByStatus(status);
	}

	@Override
	public List<XsPromotion> findAllByModifyTime(Date fromDate, Date toDate, String status) {
		return xsPromotionRepo.findAllByModifyTime(fromDate, toDate, status);
	}
}
