package vn.yotel.vbilling.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.XsPromotionLog;
import vn.yotel.vbilling.jpa.XsPromotionSum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

public class ReportCommonRepoImpl implements ReportCommonRepo{
    @PersistenceContext
    EntityManager em;

    @Override
    public List<Object[]> reportControlMonth(Date fromDate, Date toDate) {
        StoredProcedureQuery spd = em.createNamedStoredProcedureQuery("sp_report_control_monthly");
        spd.setParameter("fromDate", fromDate);
        spd.setParameter("toDate", toDate);
        List<Object[]> listObj = spd.getResultList();
        return listObj;
    }

    @Override
    public List<Object[]> reportRevenueMonthly(Date fromDate, Date toDate) {
        StoredProcedureQuery spd = em.createNamedStoredProcedureQuery("sp_report_revenue_monthly");
        spd.setParameter("fromDate", fromDate);
        spd.setParameter("toDate", toDate);
        List<Object[]> listObj = spd.getResultList();
        return listObj;
    }

    @Override
    public List<XsPromotionSum> findAll() {
        return null;
    }

    @Override
    public List<XsPromotionSum> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<XsPromotionSum> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<XsPromotionSum> findAll(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(XsPromotionSum xsPromotionSum) {

    }

    @Override
    public void delete(Iterable<? extends XsPromotionSum> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends XsPromotionSum> S save(S s) {
        return null;
    }

    @Override
    public <S extends XsPromotionSum> List<S> save(Iterable<S> iterable) {
        return null;
    }

    @Override
    public XsPromotionSum findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends XsPromotionSum> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<XsPromotionSum> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public XsPromotionSum getOne(Long aLong) {
        return null;
    }
}
