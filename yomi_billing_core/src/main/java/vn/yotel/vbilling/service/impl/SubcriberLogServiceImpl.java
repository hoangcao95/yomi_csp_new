package vn.yotel.vbilling.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.SubscriberLog;
import vn.yotel.vbilling.service.SubscriberLogService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

@Service(value = "subcriberLogService")
@Transactional
public class SubcriberLogServiceImpl implements SubscriberLogService {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<SubscriberLog> findCharge(Date fromDate, Date toDate, String phone, String action, String status) {
        StoredProcedureQuery sqp = em.createNamedStoredProcedureQuery("sp_charge_history");
        sqp.setParameter("_fromDate", fromDate);
        sqp.setParameter("_toDate", toDate);
        sqp.setParameter("_phone", phone);
        sqp.setParameter("_action", action);
        sqp.setParameter("_status", status);
        List<SubscriberLog> list = sqp.getResultList();
        return list;
    }
}
