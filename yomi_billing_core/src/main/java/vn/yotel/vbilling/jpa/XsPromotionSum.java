package vn.yotel.vbilling.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name="xs_promotion_sum")
@NamedQuery(name = "XsPromotionSum.findAll", query = "SELECT a FROM XsPromotionSum a")
@NamedStoredProcedureQueries({
            @NamedStoredProcedureQuery(
				name = "sp_week_award",
				procedureName = "sp_week_award",
				parameters = {
						@StoredProcedureParameter( name = "process_date", type = Date.class, mode = ParameterMode.IN),
						@StoredProcedureParameter( name = "_msisdn", type = String.class, mode = ParameterMode.IN)
				}
			),
			@NamedStoredProcedureQuery(
				name = "sp_month_award",
				procedureName = "sp_month_award",
				parameters = {
						@StoredProcedureParameter( name = "process_date", type = Date.class, mode = ParameterMode.IN),
						@StoredProcedureParameter( name = "_msisdn", type = String.class, mode = ParameterMode.IN)
				}
			)
})
public class XsPromotionSum implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true, nullable=false)
	private Long id;
	
	@Column(nullable=false, length=15)
	private String msisdn;
	
	@Column(name="number_pick")
	private int numberPick;

	@Column(name="status")
	private int status;

	@Column(name="created_date")
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Column(name="created_time")
	private Timestamp createdTime;

	@Column(name="log_id")
	private Long logId;

	@Column(name="promo_id")
	private int promoId;

    @Column(name="award_type")
    private String awardType;

	@Column(name="sms_status")
	private int smsStatus;

	@Column(name="process_date")
	private Timestamp processDate;

	@Column(name = "sum_note")
	private String sumNote;

	@Column(name ="vpoint")
	private int vpoint;

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

	public int getNumberPick() {
		return numberPick;
	}

	public void setNumberPick(int numberPick) {
		this.numberPick = numberPick;
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

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public int getPromoId() {
		return promoId;
	}

	public void setPromoId(int promoId) {
		this.promoId = promoId;
	}

	public String getAwardType() {
		return awardType;
	}

	public void setAwardType(String awardType) {
		this.awardType = awardType;
	}

	public int getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(int smsStatus) {
		this.smsStatus = smsStatus;
	}

	public Timestamp getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Timestamp processDate) {
		this.processDate = processDate;
	}

	public String getSumNote() {
		return sumNote;
	}

	public void setSumNote(String sumNote) {
		this.sumNote = sumNote;
	}

	public int getVpoint() {
		return vpoint;
	}

	public void setVpoint(int vpoint) {
		this.vpoint = vpoint;
	}
}
