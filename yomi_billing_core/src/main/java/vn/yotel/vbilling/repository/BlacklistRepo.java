package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.Blacklist;


@Repository
public interface BlacklistRepo extends JpaRepository<Blacklist, Integer> {

	@Query(value = "SELECT b FROM Blacklist b WHERE b.msisdn IN ( substring(:msisdn,1, locate('|',:msisdn) - 1 ), substring(:msisdn,locate('|',:msisdn) + 1, length(:msisdn))) " +
			" and b.status = 1", nativeQuery = false)
	Blacklist findByMsisdn(@Param("msisdn") String msisdn);
}
