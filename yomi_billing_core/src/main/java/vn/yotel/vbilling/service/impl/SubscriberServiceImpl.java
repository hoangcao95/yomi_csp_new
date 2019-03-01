package vn.yotel.vbilling.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.vbilling.jpa.SubsRequest;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.SubscriberAttribute;
import vn.yotel.vbilling.repository.SubsRequestRepo;
import vn.yotel.vbilling.repository.SubscriberAttributeRepo;
import vn.yotel.vbilling.repository.SubscriberRepo;
import vn.yotel.vbilling.service.SubscriberService;

/**
 *
 */
@Service(value = "subscriberService")
@Transactional
public class SubscriberServiceImpl extends GenericBoImpl<Subscriber, Integer> implements SubscriberService {
	
	private static Logger LOG = LoggerFactory.getLogger(SubscriberServiceImpl.class);
	
    @Resource
    private SubscriberRepo subscriberRepo;

    @Resource
    private SubsRequestRepo subsRequestRepo;

    @Resource
    private SubscriberAttributeRepo subscriberAttributeRepo;
    
    @PersistenceContext(unitName = "entityManagerFactory") private EntityManager entityManager;

    public SubscriberServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public SubscriberRepo getDAO() {
        return this.subscriberRepo;
    }

	@Override
	public Subscriber findByMisdn(String msisdn) {
		return subscriberRepo.findByMsisdn(msisdn);
	}

	@Override
	public List<Subscriber> findActiveByMsisdn(String msisdn) {
		return subscriberRepo.findActiveByMsisdn(msisdn);
	}

	@Override
	public Subscriber findByMsisdnAndPackageIdAndStatus(String msisdn, Integer packageId, int status) {
		return subscriberRepo.findByMsisdnAndPackageIdAndStatus(msisdn, packageId, status);
	}

	// Attribute
	@Override
	public void createAttribute(SubscriberAttribute entity) {
		subscriberAttributeRepo.save(entity);
	}

	@Override
	public void updateAttribute(SubscriberAttribute entity) {
		subscriberAttributeRepo.save(entity);
	}

	@Override
	public List<SubscriberAttribute> findAttributeByMsisdn(String msisdn) {
		return subscriberAttributeRepo.findByMsisdn(msisdn);
	}

	@Override
	public SubscriberAttribute findAttributeByMsisdnAndMetaKey(String msisdn, String metaKey) {
		return subscriberAttributeRepo.findByMsisdnAndMetaKey(msisdn, metaKey);
	}

	@Override
	public int getScore(String msisdn) {
		String metaKey = Subscriber.MetaKey.SCORE.value();
		SubscriberAttribute attribute = subscriberAttributeRepo.findByMsisdnAndMetaKey(msisdn, metaKey);
		int score = 0;
		if (attribute == null) {
			score = 0;
		} else {
			score = Integer.parseInt(attribute.getMetaValue());
		}
		return score;
	}

	@Override
	public int addScore(String msisdn, int score) {
		String metaKey = Subscriber.MetaKey.SCORE.value();
		SubscriberAttribute attribute = subscriberAttributeRepo.findByMsisdnAndMetaKey(msisdn, metaKey);
		int finalScore = 0;
		if (attribute == null) {
			attribute = new SubscriberAttribute();
			attribute.setMetaKey(metaKey);
			attribute.setMetaValue(String.valueOf(score));
			attribute.setMsisdn(msisdn);
			subscriberAttributeRepo.save(attribute);
			finalScore = score;
		} else {
			int _score = Integer.parseInt(attribute.getMetaValue());
			attribute.setMetaValue(String.valueOf(score + _score));
			subscriberAttributeRepo.save(attribute);
			finalScore = score + _score;
		}
		return finalScore;
	}

	@Override
	public void logSubsRequest(Subscriber subscriber, String command, String transId, int processStatus, String data, String resp) {
		SubsRequest subsReq = new SubsRequest();
		subsReq.setTransId(transId);
		subsReq.setMsisdn(subscriber.getMsisdn());
		subsReq.setSubsId(subscriber.getId());
		subsReq.setCommand(command);
		subsReq.setReqDatetime(new Date());
		subsReq.setProcessStatus(0);
		subsReq.setProcessStatus(processStatus);
		subsReq.setData(data);
		subsReq.setDataResp(resp);
		this.subsRequestRepo.save(subsReq);
	}

	@Override
	public void updateLogStatus(SubsRequest eachElement, int parseToHttpCode) {
		eachElement.setProcessStatus(parseToHttpCode);
		this.subsRequestRepo.save(eachElement);
	}

	@Override
	public List<SubsRequest> getFailedLogSubsRequest(int numbefOfBackDays) {
		Date today = new Date();
		Date fromDate = new DateTime(today).minusDays(numbefOfBackDays).toDate();
		int processStatus = 200;
		return subsRequestRepo.getFailedLogSubsRequest(fromDate, processStatus);
	}

	@Override
	public Subscriber findByMsisdnAndPackageId(String msisdn, Integer packageId) {
		return subscriberRepo.findByMsisdnAndPackageId(msisdn, packageId);
	}
	
	@Override
	public List<Subscriber> loadAllActiveSubs() {
		List<Subscriber> lstAllActiveSubs = new ArrayList<Subscriber>();
		int pageSize = 10000;
		int pageIndex = 0;
		boolean shouldBreak = false;
		long lstart = System.currentTimeMillis();
		StatelessSession session = ((Session) entityManager.getDelegate()).getSessionFactory().openStatelessSession();
		try {
			while (!shouldBreak) {
				Transaction tx = session.beginTransaction();
				ScrollableResults listSubs = session.createQuery("SELECT a FROM Subscriber a WHERE a.status = 1 ORDER BY a.id ASC")
//						.setCacheMode(CacheMode.IGNORE)
						.setCacheable(false)
						.setReadOnly(true)
						.setFirstResult(pageIndex * pageSize)
						.setMaxResults(pageSize)
						.scroll(ScrollMode.FORWARD_ONLY);
				boolean hasValue = false;
				while (listSubs.next()) {
					hasValue = true;
					Subscriber eachSubs = (Subscriber) listSubs.get(0);
					lstAllActiveSubs.add(eachSubs);
				}
				listSubs.close();
				tx.commit();
				pageIndex++;
				if (!hasValue) {
					shouldBreak = true;
				}
			}			
		} finally {
			session.close();
		}
		long lend = System.currentTimeMillis();
		LOG.info("Time to load all active subs[{}]: {} miniseconds", lstAllActiveSubs.size(), (lend - lstart));
		return lstAllActiveSubs;
	}

	@Override
	public void update(Subscriber subscriber) {
		subscriberRepo.save(subscriber);
	}
}
