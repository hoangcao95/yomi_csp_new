package vn.yotel.vbilling.service;

import java.util.Date;
import java.util.List;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.DetectNumberLog;
import vn.yotel.vbilling.jpa.KpiDaily;
import vn.yotel.vbilling.model.ErrorCode;

/**
 *
 */
public interface KPIReportService extends GenericBo<KpiDaily, Long> {

	void createDetectNumberLog(DetectNumberLog detectLog);

	List<KpiDaily> getDetectNumberKpiDaily(Date _fromDate, Date _toDate);

	List<ErrorCode> getErrorStatisticDaily(Date _fromDate, Date _toDate);

	List<KpiDaily> getChargeKpiDaily(Date _fromDate, Date _toDate);

	List<KpiDaily> getMtKpiDaily(Date _fromDate, Date _toDate);
}
