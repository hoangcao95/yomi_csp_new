package vn.yotel.vbilling.service;

import vn.yotel.vbilling.jpa.SubscriberLog;

import java.util.Date;
import java.util.List;

public interface SubscriberLogService {
    public List<SubscriberLog> findCharge(Date fromDate, Date toDate, String phone, String action, String status);
}
