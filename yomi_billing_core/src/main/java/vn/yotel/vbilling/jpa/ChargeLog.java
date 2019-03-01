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
@Table(name="core_charge_log")
public class ChargeLog implements Serializable {
	
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
	
	@Column(name="type")
	private String type;
	
	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="amount")
	private float amount;
	
	@Column(name="call_status")
	private boolean callStatus;
	
	@Column(name="result_status")
	private boolean resultStatus;
	
	@Column(name = "req_data", columnDefinition = "TEXT")
	private String reqData;
	
	@Column(name="resp_data", columnDefinition = "TEXT")
	private String respData;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(boolean callStatus) {
		this.callStatus = callStatus;
	}

	public boolean getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(boolean resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getReqData() {
		return reqData;
	}

	public void setReqData(String reqData) {
		this.reqData = reqData;
	}

	public String getRespData() {
		return respData;
	}

	public void setRespData(String respData) {
		this.respData = respData;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
	
}
