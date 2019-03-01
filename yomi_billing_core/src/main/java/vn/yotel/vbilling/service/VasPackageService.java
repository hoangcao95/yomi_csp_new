package vn.yotel.vbilling.service;

import vn.yotel.commons.bo.GenericBo;
import vn.yotel.vbilling.jpa.VasPackage;

public interface VasPackageService extends GenericBo<VasPackage, Integer> {
	
	VasPackage findByName(String name);
	
}
