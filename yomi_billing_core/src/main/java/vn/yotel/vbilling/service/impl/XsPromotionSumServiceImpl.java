package vn.yotel.vbilling.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.XsPromotion;
import vn.yotel.vbilling.jpa.XsPromotionSum;
import vn.yotel.vbilling.model.XsPromotionSumBean;
import vn.yotel.vbilling.repository.XsPromotionRepo;
import vn.yotel.vbilling.repository.XsPromotionSumRepo;
import vn.yotel.vbilling.service.XsPromotionService;
import vn.yotel.vbilling.service.XsPromotionSumService;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

@Service(value = "xsPromotionSumService")
@Transactional
public class XsPromotionSumServiceImpl implements XsPromotionSumService {
	
	@Autowired
	XsPromotionSumRepo xsPromotionSumRepoRepo;

	@PersistenceContext
	EntityManager em;

	@Override
	public XsPromotionSum create(XsPromotionSum xsPromotionSum) {
		return xsPromotionSumRepoRepo.save(xsPromotionSum);
	}

	@Override
	public XsPromotionSum update(XsPromotionSum xsPromotionSum) {
		return xsPromotionSumRepoRepo.save(xsPromotionSum);
	}

	@Override
	public void delete(XsPromotionSum xsPromotionSum) {
		xsPromotionSumRepoRepo.delete(xsPromotionSum);
	}

	@Override
	public List<XsPromotionSum> findAllByDate(Date fromDate, Date toDate) {
		return xsPromotionSumRepoRepo.findAllByDate(fromDate,toDate);
	}

	@Override
    public Integer findNumberMaxByDate(Date processDate) {
	    return xsPromotionSumRepoRepo.findNumberMaxByDate(processDate);
    }

	public List<Object[]> findSubsNumberMax(Date toDate) {
		return xsPromotionSumRepoRepo.findSubsNumberMax(toDate);
	}

//	public void resumXsPromotionDaily(Date processDate) {
//		List<XsPromotionSum> lst = xsPromotionSumRepoRepo.resumXsPromotionDaily(processDate);
//	}

    public XsPromotionSum findByDate(Date processDate) {
        return xsPromotionSumRepoRepo.findByDate(processDate);
    }

	public List<Object[]> getListAwardByWeek(Date processDate, String msisdn) {
//		return xsPromotionSumRepoRepo.getListAwardByWeek(processDate);
		StoredProcedureQuery spq = em.createNamedStoredProcedureQuery("sp_week_award");
		spq.setParameter("process_date", processDate);
		spq.setParameter("_msisdn", msisdn);
		List<Object[]> lst = spq.getResultList();
		return lst;
	}

	public List<Object[]> getListAwardByMonth(Date processDate, String msisdn) {
//		return xsPromotionSumRepoRepo.getListAwardByWeek(processDate);
		StoredProcedureQuery spq = em.createNamedStoredProcedureQuery("sp_month_award");
		spq.setParameter("process_date", processDate);
		spq.setParameter("_msisdn", msisdn);
		List<Object[]> lst = spq.getResultList();
		return lst;
	}

	public Page<XsPromotionSum> findAllByDate(Date fromDate, Date toDate, Pageable pageable) {
		return xsPromotionSumRepoRepo.findAllByDate(fromDate,toDate, pageable);
	}
}
