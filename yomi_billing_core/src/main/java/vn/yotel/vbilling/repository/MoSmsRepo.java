package vn.yotel.vbilling.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.MoSms;


@Repository(value = "moSmsRepo")
public interface MoSmsRepo extends JpaRepository<MoSms, Integer> {
	
	MoSms findBySmsId(String smsId);

	@Query(value = "SELECT mo FROM MoSms mo WHERE (:fromDate IS NULL OR mo.createdDate BETWEEN :fromDate AND :toDate) AND (:msisdn IS null OR mo.msisdn = :msisdn)", nativeQuery = false)
	Page<MoSms> findByMsisdn(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate, @Param(value = "msisdn") String msisdn, Pageable page);
	
	@Query(value = "SELECT mo.message, count(*) FROM MoSms mo WHERE (mo.createdDate BETWEEN :fromDate AND :toDate)"
			+ " GROUP BY mo.message", nativeQuery = false)
	List<Object[]> countMO(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate);

	@Query(value = "SELECT mo FROM MoSms mo WHERE (mo.createdDate BETWEEN :fromDate AND :toDate)"
			+ " AND (:msisdn IS null OR mo.msisdn = :msisdn)"
			+ " AND (:keyword IS null OR mo.keyword = :keyword)"
			+ " AND (:message IS null OR mo.message LIKE CONCAT('%', :message, '%'))"
			, nativeQuery = false)
	Page<MoSms> findMo(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate,
			@Param(value = "keyword") String keyword, @Param(value = "msisdn") String msisdn,
			@Param(value = "message") String message, Pageable page);
}
