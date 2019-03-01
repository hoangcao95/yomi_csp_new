package vn.yotel.vbilling.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.vbilling.jpa.CdrLog;
import vn.yotel.vbilling.model.CdrLogModel;
import vn.yotel.vbilling.repository.ReportCommonRepo;
import vn.yotel.vbilling.service.CdrLogService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(value = "cdrLogService")
@Transactional
public class CdrLogServiceImpl implements CdrLogService {
    @Autowired
    ReportCommonRepo reportCommonRepo;

    private Logger LOG = LoggerFactory.getLogger(CdrLogServiceImpl.class);

    @Override
    public List<CdrLogModel> reportControlMonth(Date fromDate, Date toDate) {
        List<Object[]> listObj = reportCommonRepo.reportControlMonth(fromDate, toDate);
        List<CdrLogModel> listCdrLog = new ArrayList<>();
        for (Object[] list : listObj) {
            CdrLogModel cdr = new CdrLogModel();
            cdr.setAmount(String.valueOf(list[0]));
            cdr.setNumSub(String.valueOf(list[1]));
            cdr.setTotalAmount(String.valueOf(list[2]));
            listCdrLog.add(cdr);
        }
        return listCdrLog;
    }

    @Override
    public List<CdrLogModel> reportRevenueMonthly(Date fromDate, Date toDate) {
        List<CdrLogModel> listCdrLog = new ArrayList<>();
        try {
            List<Object[]> listObj = reportCommonRepo.reportRevenueMonthly(fromDate, toDate);
            for (Object[] list : listObj) {
                CdrLogModel cdr = new CdrLogModel();
                cdr.setDate1(String.valueOf(list[0]));
                cdr.setTotalAmount(String.valueOf(list[1]));
                listCdrLog.add(cdr);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return listCdrLog;
    }
}
