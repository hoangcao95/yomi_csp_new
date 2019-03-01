package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.yotel.vbilling.jpa.CdrLog;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public interface CdrLogRepo extends JpaRepository<CdrLog, Long> {
	@Query(value = "SELECT amount, count(1), sum(amount) FROM cdr_log " +
			"WHERE 1 and time1 >= :fromDate " +
			"and time1 < :toDate and status = 1 " +
			"group by amount", nativeQuery = true)
	List<Object[]> reportControlMonth(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "SELECT date_format(time1,'%Y/%m/%d') date1, sum(amount) FROM cdr_log " +
			"WHERE 1 and time1 >= :fromDate " +
			"and time1 < :toDate group by date_format(time1,'%Y/%m/%d') ", nativeQuery = true)
	List<Object[]> reportTotalMoneyByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
