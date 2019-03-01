package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.yotel.vbilling.jpa.XsPromotion;

import java.util.Date;
import java.util.List;

public interface XsPromotionRepo extends JpaRepository<XsPromotion, Long> {
	XsPromotion findByMsisdnAndVasPackageCodeAndStatus(String msisdn, String vasPakageCode, String status);
	
	@Query(value="SELECT number FROM xs_promotion where number != 0",nativeQuery=true)
	List<String> findAllNumbers();
	
	@Query(value="SELECT e FROM XsPromotion e where e.msisdn = :msisdn AND e.status = :status ",nativeQuery=false)
	List<XsPromotion> findPromotion(@Param("msisdn") String msisdn, @Param("status") String status);

	@Query(value="SELECT e FROM XsPromotion e WHERE e.status = :status AND e.sendNotification = 0 ",nativeQuery=false)
	List<XsPromotion> findAllByStatus(@Param("status") String status);

	@Query(value="SELECT e FROM XsPromotion e WHERE e.status = :status AND e.modifyTime >= :fromDate AND e.modifyTime < :toDate ",nativeQuery=false)
	List<XsPromotion> findAllByModifyTime(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("status") String status);
}
