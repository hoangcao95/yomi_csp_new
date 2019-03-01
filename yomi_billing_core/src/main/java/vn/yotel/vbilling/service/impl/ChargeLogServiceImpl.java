package vn.yotel.vbilling.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.vbilling.jpa.ChargeLog;
import vn.yotel.vbilling.repository.ChargeLogRepo;
import vn.yotel.vbilling.service.ChargeLogService;

/**
 *
 */
@Service(value = "chargeLogService")
@Transactional
public class ChargeLogServiceImpl extends GenericBoImpl<ChargeLog, Long> implements ChargeLogService {

    @Resource private ChargeLogRepo chargeLogRepo;

    public ChargeLogServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChargeLogRepo getDAO() {
        return this.chargeLogRepo;
    }

}
