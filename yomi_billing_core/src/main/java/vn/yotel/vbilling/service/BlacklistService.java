package vn.yotel.vbilling.service;

import java.util.List;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.Blacklist;

public interface BlacklistService extends GenericBo<Blacklist, Long> {

	Blacklist addBlacklist(String msisdn);

	Blacklist removeBlacklist(String msisdn);

	List<Blacklist> getAllBlackListSubs();

	Blacklist findByMsisdn(String msisdn);
}
