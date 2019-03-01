package vn.yotel.vbilling.service;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.SubscriberDailySms;

import java.util.Date;
import java.util.List;

public interface SubscriberDailySmsService extends GenericBo<SubscriberDailySms, Integer> {
    List<String> getAllSubscriberToSendDailySms(Date today);
    List<String> getAllSubscriberToSendDailySmsWithCondition(Date today,Date dateStart);
    List<Object[]> getAllSubscriberToSendDailySmsForDayPackage(Date today, List<Integer> packageIds,Date dateStart);

}
