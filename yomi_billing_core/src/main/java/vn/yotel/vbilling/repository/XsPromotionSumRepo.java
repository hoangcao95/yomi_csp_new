package vn.yotel.vbilling.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import vn.yotel.vbilling.jpa.XsPromotionSum;
import vn.yotel.vbilling.model.XsPromotionSumBean;

import java.util.Date;
import java.util.List;

public interface XsPromotionSumRepo extends JpaRepository<XsPromotionSum, Long> {

	@Query(value="SELECT e FROM XsPromotionSum e WHERE e.status = 1 And e.createdDate >= :fromDate AND e.createdDate <= :toDate ORDER BY e.createdDate DESC", nativeQuery=false)
    List<XsPromotionSum> findAllByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value="SELECT MAX(number_pick) max_number_pick " +
            "       FROM ( " +
            "           SELECT COUNT(1) AS num, CONVERT(number_pick, UNSIGNED INTEGER) AS number_pick " +
            "             FROM xs_promotion_log " +
            "            WHERE created_date = :processDate " +
            "         GROUP BY CONVERT(number_pick, UNSIGNED INTEGER) " +
            "           HAVING COUNT(1) = 1 " +
            "       ) xs", nativeQuery=true)
	Integer findNumberMaxByDate(@Param("processDate") Date processDate);

    @Query(value=" " +
            "SELECT * FROM xs_promotion_log WHERE id IN ( " +
            "    SELECT id FROM ( " +
            "        SELECT COUNT(1) AS num, MAX(id) id, CONVERT(number_pick, UNSIGNED INTEGER) AS number_pick " +
            "        FROM xs_promotion_log " +
            "        WHERE created_date = :toDate " +
            "        GROUP BY CONVERT(number_pick, UNSIGNED INTEGER) " +
            "        HAVING COUNT(1) = 1 " +
            "    ) a " +
            ") ORDER BY DATE(created_date) DESC, CONVERT(number_pick, UNSIGNED INTEGER) DESC " +
            "LIMIT 0, 10 ", nativeQuery=true)
    List<Object[]> findSubsNumberMax(@Param("toDate") Date toDate);

//    @Query(value = "CALL sp_xs_promotion_sum_daily(:processDate)", nativeQuery = true)
//    @Procedure(name = "sp_xs_promotion_sum_daily")
//    List<XsPromotionSum> resumXsPromotionDaily(@Param("processDate") Date processDate);

    @Query(value = "SELECT xs FROM XsPromotionSum xs WHERE xs.createdDate = :processDate AND xs.status = 1")
    XsPromotionSum findByDate(@Param("processDate") Date processDate);

//    @Procedure(name = "sp_diff_date")
//    List<XsPromotionSum> getListAwardByWeek(@Param("processDate") Date processDate);

    @Query(value="SELECT e FROM XsPromotionSum e WHERE e.status = 1 And e.createdDate >= :fromDate AND e.createdDate <= :toDate ORDER BY e.createdDate DESC", nativeQuery=false)
    Page<XsPromotionSum> findAllByDate(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable pageable);
}
