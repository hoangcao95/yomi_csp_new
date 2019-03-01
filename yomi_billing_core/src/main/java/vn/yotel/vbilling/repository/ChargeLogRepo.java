package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.ChargeLog;


@Repository
public interface ChargeLogRepo extends JpaRepository<ChargeLog, Integer> {
}
