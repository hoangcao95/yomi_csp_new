package vn.yotel.vbilling.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name="core_vas_product")
@NamedQuery(name = "VasProduct.findAll", query = "SELECT a FROM VasProduct a")

public class VasProduct implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1677721350450714110L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="code")
	private String code;
	
	@Column(name="name")
	private String name;
	
	@Column(name="status", columnDefinition = "BIT")
	private int status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
