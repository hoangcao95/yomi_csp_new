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
@Table(name="core_detect_number_log")
@NamedQuery(name="DetectNumberLog.findAll", query="SELECT a FROM DetectNumberLog a")

public class DetectNumberLog implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6706490613034950554L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="trans_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date transDate;
	
	@Column(name="caller_ip")
	private String callerIp;
	
	@Column(name="source_ip")
	private String sourceIp;
	
	
	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="result_status")
	private boolean resultStatus;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public String getCallerIp() {
		return callerIp;
	}

	public void setCallerIp(String callerIp) {
		this.callerIp = callerIp;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public boolean getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(boolean resultStatus) {
		this.resultStatus = resultStatus;
	}
	
}
