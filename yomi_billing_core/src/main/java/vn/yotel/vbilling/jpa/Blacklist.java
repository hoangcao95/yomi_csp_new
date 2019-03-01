package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="core_subs_blacklist")
@NamedQuery(name="Blacklist.findAll", query="SELECT a FROM Blacklist a")

public class Blacklist implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1677721350450714110L;
	
	public Blacklist() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="type")
	private boolean type;

	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="regex")
	private String regex;
	
	@Column(name="added_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(name="modified_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name="status")
	private boolean status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
