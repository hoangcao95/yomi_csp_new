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
@Table(name="core_mo_sms")
@NamedQuery(name="MoSms.findAll", query="SELECT a FROM MoSms a")

public class MoSms implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1677721350450714110L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="short_code")
	private String shortCode;
	
	@Column(name="message")
	private String message;
	
	@Column(name="service_code")
	private String serviceCode;
	
	@Column(name="keyword")
	private String keyword;
	
	@Column(name="sms_id")
	private String smsId;
	
	@Column(name="created_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(name="process_status")
	private int processStatus;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmscId(String smsId) {
		this.smsId = smsId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}
	
}
