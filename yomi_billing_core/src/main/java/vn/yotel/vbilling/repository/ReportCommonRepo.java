package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.yotel.vbilling.jpa.XsPromotionSum;

import java.util.Date;
import java.util.List;

public interface ReportCommonRepo extends JpaRepository<XsPromotionSum, Long> {
	List<Object[]> reportControlMonth(Date fromDate,Date toDate);
	List<Object[]> reportRevenueMonthly(Date fromDate,Date toDate);
}
