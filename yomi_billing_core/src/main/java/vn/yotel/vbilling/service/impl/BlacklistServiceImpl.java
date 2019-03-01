package vn.yotel.vbilling.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.vbilling.jpa.Blacklist;
import vn.yotel.vbilling.repository.BlacklistRepo;
import vn.yotel.vbilling.service.BlacklistService;

/**
 *
 */
@Service(value = "blacklistService")
@Transactional
public class BlacklistServiceImpl extends GenericBoImpl<Blacklist, Long> implements BlacklistService {

    @Resource private BlacklistRepo blacklistRepo;

    public BlacklistServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlacklistRepo getDAO() {
        return this.blacklistRepo;
    }

	@Override
	public Blacklist addBlacklist(String msisdn) {
		Blacklist blacklist = new Blacklist();
		blacklist.setCreatedDate(new Date());
		blacklist.setModifiedDate(new Date());
		blacklist.setMsisdn(msisdn);
		blacklist.setStatus(true);

		blacklist = blacklistRepo.save(blacklist);

		return blacklist;
	}

	@Override
	public Blacklist removeBlacklist(String msisdn) {
		Blacklist blacklist = blacklistRepo.findByMsisdn(msisdn);
		blacklist.setModifiedDate(new Date());
		blacklist.setMsisdn(msisdn);
		blacklist.setStatus(false);

		blacklist = blacklistRepo.save(blacklist);

		return blacklist;
	}

	@Override
	public List<Blacklist> getAllBlackListSubs() {
		return blacklistRepo.findAll();
	}

	@Override
	public Blacklist findByMsisdn(String msisdn) {
		return blacklistRepo.findByMsisdn(msisdn);
	}
}
