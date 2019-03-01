package vn.yotel.vbilling.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.yotel.commons.bo.impl.GenericBoImpl;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.repository.VasPackageRepo;
import vn.yotel.vbilling.service.VasPackageService;

/**
 *
 */
@Service(value = "vasPackageService")
@Transactional
public class VasPackageServiceImpl extends GenericBoImpl<VasPackage, Integer> implements VasPackageService {

    @Resource private VasPackageRepo vasPackageRepo;

    public VasPackageServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public VasPackageRepo getDAO() {
        return this.vasPackageRepo;
    }
    
	@Override
	public VasPackage findByName(String name) {
		return vasPackageRepo.findByName(name);
	}
}
