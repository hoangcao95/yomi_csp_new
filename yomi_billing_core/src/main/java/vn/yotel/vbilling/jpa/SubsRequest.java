package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="core_subs_request")

@NamedQueries({
		@NamedQuery(name = "SubsRequest.findAll", query = "SELECT a FROM SubsRequest a"),
		@NamedQuery(name = "SubsRequest.findRequestByCommand", query = "SELECT a FROM SubsRequest a WHERE (a.reqDatetime BETWEEN :fromDate AND :toDate) AND command = :command"),
		@NamedQuery(name = "SubsRequest.findRequestByCommands", query = "SELECT a FROM SubsRequest a WHERE (a.reqDatetime BETWEEN :fromDate AND :toDate) AND command in (:commands)"),
		@NamedQuery(name = "SubsRequest.findRequestGrpByCommandStatus", query = "SELECT a.command,a.reqStatus,count(id) FROM SubsRequest a WHERE (a.reqDatetime BETWEEN :fromDate AND :toDate) AND a.command in (:commands) GROUP BY a.command, a.reqStatus"),
		@NamedQuery(name = "SubsRequest.findRequestCancelByDate", query = "SELECT a.command,a.reqStatus,count(a.id) FROM SubsRequest a WHERE (a.reqDatetime BETWEEN :fromDate AND :toDate) AND a.command in (:commands)"
				+ " AND a.msisdn in (SELECT b.msisdn FROM Subscriber b WHERE b.status = 1 AND b.expiredDate >= :toDate) GROUP BY a.command, a.reqStatus")})

public class SubsRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6706490613034950554L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="trans_id")
	private String transId;
	
	@Column(name="subs_id")
	private Integer subsId;
	
	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="req_datetime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reqDatetime;
	
	@Column(name="req_status")
	private boolean reqStatus;
	
	@Column(name="amount")
	private Float amount;
	
	@Column(name="command")
	private String command;
	
	@Column(name="process_status")
	private int processStatus;
	
	@Column(name = "data", columnDefinition = "TEXT")
	private String data;
	
	@Column(name="data_resp", columnDefinition = "TEXT")
	private String dataResp;

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

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

	public boolean getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(boolean reqStatus) {
		this.reqStatus = reqStatus;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Integer getSubsId() {
		return subsId;
	}

	public void setSubsId(Integer subsId) {
		this.subsId = subsId;
	}

	public Date getReqDatetime() {
		return reqDatetime;
	}

	public void setReqDatetime(Date reqDatetime) {
		this.reqDatetime = reqDatetime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDataResp() {
		return dataResp;
	}

	public void setDataResp(String dataResp) {
		this.dataResp = dataResp;
	}
}
