package vn.yotel.vbilling.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name="xs_promotion")
@NamedQuery(name = "XsPromotion.findAll", query = "SELECT a FROM XsPromotion a")
public class XsPromotion implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true, nullable=false)
	private Long id;
	
	@Column(nullable=false, length=15)
	private String msisdn;
	
	@Column(name="vas_package_code",nullable=false, length=255)
	private String vasPackageCode;
	
	private String status;
	
	private int number;

	@Column(name="number_sms")
	private int numberSms;
	
	@Column(name="created_date",nullable=false)
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Column(name="created_time",nullable=false)
	private Timestamp createdTime;

	@Column(name="modify_time",nullable=true)
	private Timestamp modifyTime;

	@Column(name="arr_number_pick",nullable=true)
	private String arrNumberPick;

    @Column(name="send_notification")
    private int sendNotification;

	@Column(name="time_id")
    private String timeId;
	
	@PrePersist
	public void prePersist(){ 
		if(this.createdTime == null){
			this.createdTime = new Timestamp(Calendar.getInstance().getTimeInMillis());	
		}
		this.createdDate = this.createdTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getVasPackageCode() {
		return vasPackageCode;
	}

	public void setVasPackageCode(String vasPackageCode) {
		this.vasPackageCode = vasPackageCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public int getNumberSms() {
		return numberSms;
	}

	public void setNumberSms(int numberSms) {
		this.numberSms = numberSms;
	}

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getArrNumberPick() {
		return arrNumberPick;
	}

	public void setArrNumberPick(String arrNumberPick) {
		this.arrNumberPick = arrNumberPick;
	}

	public String getTimeId() {
		return timeId;
	}

	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}

	public int getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(int sendNotification) {
        this.sendNotification = sendNotification;
    }

    @Override
	public String toString() {
		return "XsPromotion [id=" + id + ", msisdn=" + msisdn + ", vasPackageCode=" + vasPackageCode + ", status="
				+ status + ", number=" + number + ", numberSms=" + numberSms + ", arrNumberPick=" + arrNumberPick + ", createdTime=" + createdTime + "]";
	}
}
