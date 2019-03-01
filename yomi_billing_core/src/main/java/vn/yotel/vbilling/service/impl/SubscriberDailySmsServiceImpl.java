package vn.yotel.vbilling.service.impl;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.SubscriberDailySms;
import vn.yotel.vbilling.repository.ChargeLogRepo;
import vn.yotel.vbilling.repository.SubscriberDailySmsRepo;
import vn.yotel.vbilling.service.SubscriberDailySmsService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service(value = "subscriberDailySmsService")
@Transactional
public class SubscriberDailySmsServiceImpl extends GenericBoImpl<SubscriberDailySms, Integer> implements SubscriberDailySmsService {

    @Resource
    private SubscriberDailySmsRepo subscriberDailySmsRepo;

    public SubscriberDailySmsServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public SubscriberDailySmsRepo getDAO() {
        return this.subscriberDailySmsRepo;
    }

    @Override
    public List<String> getAllSubscriberToSendDailySms(Date today) {
        DateTime dateTime = new DateTime(today);
        return subscriberDailySmsRepo.getAllSubscriberToSendDailySms(dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.plusDays(1).minusMinutes(1).toDate(),
                Subscriber.Status.INACTIVE.value());
    }

    @Override
    public List<String> getAllSubscriberToSendDailySmsWithCondition(Date today, Date dateStart) {
        DateTime dateTime = new DateTime(today);
        return subscriberDailySmsRepo.getAllSubscriberToSendDailySmsWithCondition(dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.plusDays(1).minusMinutes(1).toDate(),
                dateStart,
                Subscriber.Status.INACTIVE.value());
    }

    @Override
    public List<Object[]> getAllSubscriberToSendDailySmsForDayPackage(Date today, List<Integer> packageIds, Date dateStart) {
        DateTime dateTime = new DateTime(today);
        return subscriberDailySmsRepo.getAllSubscriberToSendDailySmsForDayPackage(dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.withTimeAtStartOfDay().toDate(),
                dateTime.plusDays(1).minusMinutes(1).toDate(),
                dateStart,
                Subscriber.Status.INACTIVE.value());
    }
}
