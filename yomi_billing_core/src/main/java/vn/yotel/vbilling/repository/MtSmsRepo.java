package vn.yotel.vbilling.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.MtSms;

@Repository(value = "mtSmsRepo")
public interface MtSmsRepo extends JpaRepository<MtSms, Integer> {

	@Query(value = "SELECT mt FROM MtSms mt WHERE (:fromDate IS NULL OR mt.createdDate BETWEEN :fromDate AND :toDate) AND (:msisdn IS null OR mt.msisdn = :msisdn)", nativeQuery = false)
	Page<MtSms> findByMsisdn(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate, @Param(value = "msisdn") String msisdn, Pageable page);

	@Query(value = "SELECT count(mt) FROM MtSms mt WHERE (mt.createdDate BETWEEN :fromDate AND :toDate) "
			+ " AND (:message IS NULL OR mt.message LIKE CONCAT(:message,'%'))", nativeQuery = false)
	Long countMT(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate,
			@Param(value = "message") String message);

	@Query(value = "SELECT mt.msisdn, mt.message mtMessage, mt.created_date mtDatetime, mo.message moMessage, mo.created_date moDatetime, mt.id, "
			+ " mt.channel, mt.keyword FROM core_mt_sms mt LEFT JOIN core_mo_sms mo ON mt.sms_id = mo.sms_id"
			+ " WHERE mt.created_date BETWEEN :fromDate AND :toDate AND mt.msisdn regexp :msisdn ORDER BY mtDatetime DESC", nativeQuery = true)
	List<Object[]> getMtHistory(@Param("msisdn") String msisdn, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "SELECT mt.type, count(*) FROM MtSms mt WHERE (mt.createdDate BETWEEN :fromDate AND :toDate)"
			+ " GROUP BY mt.type", nativeQuery = false)
	List<Object[]> countMT(@Param(value = "fromDate") Date fromDate, @Param(value = "toDate") Date toDate);

//	@Query(value = "SELECT mt.msisdn, mt.message mtMessage, mt.created_date mtDatetime, mo.message moMessage, mo.created_date moDatetime, mt.id, "
//			+ " mt.channel, mt.keyword FROM core_mt_sms mt LEFT JOIN core_mo_sms mo ON mt.sms_id = mo.sms_id"
//			+ " WHERE mt.created_date BETWEEN :fromDate AND :toDate AND mt.msisdn regexp :msisdn ORDER BY mtDatetime DESC", nativeQuery = true)
//	Page<Object[]> getMtHistory(@Param("msisdn") String msisdn, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable pageable);
}
