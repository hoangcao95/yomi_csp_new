package vn.yotel.vbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.yotel.vbilling.jpa.SmsSyntax;

@Repository(value = "smsSyntaxRepo")
public interface SmsSyntaxRepo extends JpaRepository<SmsSyntax, Integer> {
}
