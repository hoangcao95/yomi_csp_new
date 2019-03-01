package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.VasPackage;

@Repository
public interface VasPackageRepo extends JpaRepository<VasPackage, Integer> {

	VasPackage findByName(String name);
	
}
