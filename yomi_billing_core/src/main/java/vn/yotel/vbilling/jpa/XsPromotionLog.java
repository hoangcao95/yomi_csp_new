package vn.yotel.vbilling.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name="xs_promotion_log")
@NamedQuery(name = "XsPromotionLog.findAll", query = "SELECT a FROM XsPromotionLog a")
public class XsPromotionLog implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true, nullable=false)
	private Long id;
	
	@Column(nullable=false, length=15)
	private String msisdn;

	private int status;

	@Column(name="number_pick")
	private String numberPick;
	
	@Column(name="created_date",nullable=false)
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Column(name="created_time",nullable=false)
	private Timestamp createdTime;

	@Column(name="time_id")
    private String timeId;

	private int price;
	
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getNumberPick() {
		return numberPick;
	}

	public void setNumberPick(String numberPick) {
		this.numberPick = numberPick;
	}

	public String getTimeId() {
		return timeId;
	}

	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "XsPromotion [id=" + id + ", msisdn=" + msisdn + ", status="
				+ status + ", number_pick=" + numberPick + ", createdTime=" + createdTime + "]";
	}
}
