package vn.yotel.vbilling.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.SubsRequest;


@Repository
public interface SubsRequestRepo extends JpaRepository<SubsRequest, Integer> {

	SubsRequest findByMsisdn(String msisdn);
	
	@Query(value = "SELECT log FROM SubsRequest log WHERE log.reqDatetime > :fromDate AND log.processStatus != :processStatus "
						+ " AND data IS NOT NULL ORDER BY log.id")
	List<SubsRequest> getFailedLogSubsRequest(@Param("fromDate") Date fromDate,
			@Param("processStatus") int processStatus);

}
