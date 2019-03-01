package vn.yotel.vbilling.service;

import java.util.List;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.SubsRequest;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.SubscriberAttribute;

public interface SubscriberService extends GenericBo<Subscriber, Integer> {

	Subscriber findByMsisdnAndPackageIdAndStatus(String msisdn, Integer packageId, int status);

	Subscriber findByMsisdnAndPackageId(String msisdn, Integer packageId);

	Subscriber findByMisdn(String msisdn);

	List<SubscriberAttribute> findAttributeByMsisdn(String msisdn);

	SubscriberAttribute findAttributeByMsisdnAndMetaKey(String msisdn, String metaKey);

	void createAttribute(SubscriberAttribute entity);

	void updateAttribute(SubscriberAttribute entity);

	int getScore(String msisdn);

	int addScore(String msisdn, int score);

	public void logSubsRequest(Subscriber subscriber, String action, String transId, int processStatus, String data, String resp);

	void updateLogStatus(SubsRequest eachElement, int parseToHttpCode);

	List<SubsRequest> getFailedLogSubsRequest(int numbefOfBackDays);

	List<Subscriber> findActiveByMsisdn(String msisdn);

	List<Subscriber> loadAllActiveSubs();

	void update(Subscriber subscriber);
}
