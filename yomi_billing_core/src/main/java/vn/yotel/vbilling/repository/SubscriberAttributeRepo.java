package vn.yotel.vbilling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.SubscriberAttribute;


@Repository
public interface SubscriberAttributeRepo extends JpaRepository<SubscriberAttribute, Integer> {

	List<SubscriberAttribute> findByMsisdn(String msisdn);

	SubscriberAttribute findByMsisdnAndMetaKey(String msisdn, String metaKey);

}
