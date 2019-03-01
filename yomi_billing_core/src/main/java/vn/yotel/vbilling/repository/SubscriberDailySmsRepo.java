package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yotel.vbilling.jpa.SubscriberDailySms;

import java.util.Date;
import java.util.List;

@Repository
public interface SubscriberDailySmsRepo extends JpaRepository<SubscriberDailySms, Integer> {
    @Query(value = " SELECT distinct subs.msisdn FROM core_subscriber subs "
            + " LEFT OUTER JOIN yomi_subscriber_dailysms_cfg cfg ON subs.msisdn = cfg.msisdn "
            + " WHERE subs.status = 1 AND subs.expired_date >= :expiredDate "
            + " AND (cfg.denied IS NULL OR cfg.denied = :denied) "
            + " AND subs.msisdn NOT IN "
            + " (SELECT msisdn FROM yomi_subscriber_dailysms dailysms "
            + " WHERE created_date BETWEEN :startDate AND :endDate AND dailysms.type = 1 ) "
            + " LIMIT 0,500 ", nativeQuery = true)
    List<String> getAllSubscriberToSendDailySms(@Param("expiredDate") Date expiredDate,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("denied") int denied);

    @Query(value = " SELECT distinct subs.msisdn FROM core_subscriber subs "
            + " LEFT OUTER JOIN yomi_subscriber_dailysms_cfg cfg ON subs.msisdn = cfg.msisdn "
            + " WHERE subs.status = 1 AND subs.expired_date >= :expiredDate AND subs.register_date >= :dateStart "
            + " AND (cfg.denied IS NULL OR cfg.denied = :denied) "
            + " AND subs.msisdn NOT IN "
            + " (SELECT msisdn FROM yomi_subscriber_dailysms dailysms "
            + " WHERE created_date BETWEEN :startDate AND :endDate AND dailysms.type = 1 )"
            + " LIMIT 0,500 ", nativeQuery = true)
    List<String> getAllSubscriberToSendDailySmsWithCondition(@Param("expiredDate") Date expiredDate,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("dateStart") Date dateStart,
                                                @Param("denied") int denied);

    @Query(value = " SELECT distinct subs.msisdn,DATEDIFF(:expiredDate, subs.register_date),subs.package_id FROM core_subscriber subs "
            + " LEFT OUTER JOIN yomi_subscriber_dailysms_cfg cfg ON subs.msisdn = cfg.msisdn  "
            + " WHERE subs.status = 1 AND subs.expired_date >= :expiredDate AND subs.register_date >= :dateStart"
            + " AND (DATEDIFF( :expiredDate,subs.register_date)%7 = 0 AND DATEDIFF(:expiredDate, subs.register_date) >0)  "
            + " AND (cfg.denied IS NULL OR cfg.denied = :denied) "
            + " AND subs.package_id IN (5,1,2,7)  AND subs.msisdn NOT IN "
            + " (SELECT msisdn FROM yomi_subscriber_dailysms dailysms "
            + " WHERE created_date BETWEEN :startDate AND :endDate AND dailysms.type = 2 ) "
            + " LIMIT 0,500 ", nativeQuery = true)
    List<Object[]> getAllSubscriberToSendDailySmsForDayPackage(@Param("expiredDate") Date expiredDate,
                                                             @Param("startDate") Date startDate,
                                                             @Param("endDate") Date endDate,
                                                             @Param("dateStart") Date dateStart,
                                                             @Param("denied") int denied);

}
