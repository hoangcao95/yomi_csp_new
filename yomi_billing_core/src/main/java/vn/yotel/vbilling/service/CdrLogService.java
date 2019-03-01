package vn.yotel.vbilling.service;

import vn.yotel.vbilling.jpa.CdrLog;
import vn.yotel.vbilling.model.CdrLogModel;

import java.util.Date;
import java.util.List;

public interface CdrLogService {
	List<CdrLogModel> reportControlMonth(Date fromDate, Date toDate);
	List<CdrLogModel> reportRevenueMonthly(Date fromDate, Date toDate);
}
