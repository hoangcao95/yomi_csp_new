package vn.yotel.vbilling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.Subscriber;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, Integer> {

	Subscriber findByMsisdn(String msisdn);

	@Query(value = "SELECT subs FROM Subscriber subs WHERE subs.msisdn = :msisdn AND subs.status = 1")
	List<Subscriber> findActiveByMsisdn(@Param("msisdn") String msisdn);

	Subscriber findByMsisdnAndPackageIdAndStatus(String msisdn, Integer packageId, int status);

	Subscriber findByMsisdnAndPackageId(String msisdn, Integer packageId);
}
