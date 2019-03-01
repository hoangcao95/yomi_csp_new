package vn.yotel.vbilling.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.XsPromotion;
import vn.yotel.vbilling.jpa.XsPromotionLog;
import vn.yotel.vbilling.repository.XsPromotionLogRepo;
import vn.yotel.vbilling.repository.XsPromotionRepo;
import vn.yotel.vbilling.service.XsPromotionLogService;
import vn.yotel.vbilling.service.XsPromotionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(value = "xsPromotionLogService")
@Transactional
public class XsPromotionLogServiceImpl implements XsPromotionLogService {
	
	@Autowired
	XsPromotionLogRepo xsPromotionLogRepo;

	@Override
	public XsPromotionLog create(XsPromotionLog xsPromotionLog) {
		return xsPromotionLogRepo.save(xsPromotionLog);
	}

	@Override
	public XsPromotionLog update(XsPromotionLog xsPromotionLog) {
		return xsPromotionLogRepo.save(xsPromotionLog);
	}

	@Override
	public void delete(XsPromotionLog xsPromotionLog) {
		xsPromotionLogRepo.delete(xsPromotionLog);
	}

	@Override
	public List<XsPromotionLog> findByTimeId(String msisdn, String timeId) {
		return xsPromotionLogRepo.findByTimeId(msisdn, timeId);
	}

	@Override
	public List<XsPromotionLog> findByDate(Date dateCheck) {
		return xsPromotionLogRepo.findByDate(dateCheck);
	}

	public List<XsPromotionLog> findAllByDate(Date fromDate, Date toDate, String phone) {
		return xsPromotionLogRepo.findAllByDate(fromDate, toDate, phone);
	}

	@Override
	public List<XsPromotionLog> findAllByNumberMaxAndDate(String numberPick, Date processDate) {
		List<XsPromotionLog> lst = xsPromotionLogRepo.findAllByNumberMaxAndDate(numberPick, processDate);

		return lst;
	}

	public List<XsPromotionLog> findNumberMaxByDate(Date toDate, String numberNewPick, String msisdnNewPick) {
		List<Object[]> lst = xsPromotionLogRepo.findNumberMaxByDate(toDate, numberNewPick, msisdnNewPick);
		List<XsPromotionLog> lstReturn = new ArrayList<>();
		for (Object[] row: lst) {
			XsPromotionLog obj = new XsPromotionLog();
			obj.setMsisdn(String.valueOf(row[1]));
			obj.setNumberPick(String.valueOf(row[3]));
			lstReturn.add(obj);
		}
		return lstReturn;
	}

	public List<XsPromotionLog> findNumberMaxByDate(Date toDate) {
		List<Object[]> lst = xsPromotionLogRepo.findNumberMaxByDate(toDate);
		List<XsPromotionLog> lstReturn = new ArrayList<>();
		for (Object[] row: lst) {
			XsPromotionLog obj = new XsPromotionLog();
			obj.setMsisdn(String.valueOf(row[1]));
			obj.setNumberPick(String.valueOf(row[3]));
			lstReturn.add(obj);
		}
		return lstReturn;
	}

	@Override
	public Page<XsPromotionLog> findAllByDate(Date fromDate, Date toDate, String phone, Pageable pageable) {
		return xsPromotionLogRepo.findAllByDate(fromDate, toDate, phone, pageable);
	}
}
