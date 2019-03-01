package vn.yotel.vbilling.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.yotel.vbilling.jpa.XsPromotionLog;

import java.util.Date;
import java.util.List;

public interface XsPromotionLogRepo extends JpaRepository<XsPromotionLog, Long> {

	@Query(value="SELECT e FROM XsPromotionLog e WHERE e.msisdn = :msisdn AND e.timeId = :timeId ",nativeQuery=false)
	List<XsPromotionLog> findByTimeId(@Param("msisdn") String msisdn, @Param("timeId") String timeId);

    @Query(value="SELECT e FROM XsPromotionLog e WHERE e.createdDate = :dateCheck ",nativeQuery=false)
    List<XsPromotionLog> findByDate(@Param("dateCheck") Date dateCheck);

    @Query(value="SELECT e FROM XsPromotionLog e WHERE e.createdDate >= :fromDate AND e.createdDate <= :toDate " +
            "AND (e.msisdn = :msisdn OR :msisdn IS NULL OR :msisdn = '') ORDER BY e.createdDate DESC",nativeQuery=false)
    List<XsPromotionLog> findAllByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("msisdn") String msisdn);

    @Query(value = "SELECT xs FROM XsPromotionLog xs WHERE xs.numberPick = :numberPick AND xs.createdDate = :processDate", nativeQuery = false)
    List<XsPromotionLog> findAllByNumberMaxAndDate(@Param("numberPick") String numberPick, @Param("processDate") Date processDate);

    @Query(value=" " +
            "SELECT * FROM xs_promotion_log WHERE id IN ( " +
            "    SELECT id FROM ( " +
            "        SELECT COUNT(1) AS num, MAX(id) id, CONVERT(number_pick, UNSIGNED INTEGER) AS number_pick " +
            "        FROM xs_promotion_log " +
            "        WHERE created_date = :toDate " +
            "        AND ((number_pick = :numberNewPick AND msisdn = :msisdnNewPick) OR :msisdnNewPick = '') " +
            "        GROUP BY CONVERT(number_pick, UNSIGNED INTEGER) " +
            "        HAVING COUNT(1) = 1 " +
            "    ) a " +
            ") ORDER BY DATE(created_date) DESC, CONVERT(number_pick, UNSIGNED INTEGER) DESC " +
            "LIMIT 1 ", nativeQuery=true)
    List<Object[]> findNumberMaxByDate(@Param("toDate") Date toDate,
                                       @Param("numberNewPick") String numberNewPick,
                                       @Param("msisdnNewPick") String msisdnNewPick);

    @Query(value=" " +
            "SELECT * FROM xs_promotion_log WHERE id IN ( " +
            "    SELECT id FROM ( " +
            "        SELECT COUNT(1) AS num, MAX(id) id, CONVERT(number_pick, UNSIGNED INTEGER) AS number_pick " +
            "        FROM xs_promotion_log " +
            "        WHERE created_date = DATE(:toDate) " +
            "        GROUP BY CONVERT(number_pick, UNSIGNED INTEGER) " +
            "        HAVING COUNT(1) = 1 " +
            "    ) a " +
            ") ORDER BY DATE(created_date) DESC, CONVERT(number_pick, UNSIGNED INTEGER) DESC " +
            "LIMIT 1 ", nativeQuery=true)
    List<Object[]> findNumberMaxByDate(@Param("toDate") Date toDate);

    @Query(value="SELECT e FROM XsPromotionLog e WHERE e.createdDate >= :fromDate AND e.createdDate <= :toDate " +
            "AND (e.msisdn = :msisdn OR :msisdn IS NULL OR :msisdn = '') ORDER BY e.createdDate DESC",nativeQuery=false)
    Page<XsPromotionLog> findAllByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("msisdn") String msisdn, Pageable pageable);
}
