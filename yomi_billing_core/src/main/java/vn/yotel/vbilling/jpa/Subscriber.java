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
@Table(name="core_subscriber")
@NamedQueries({
		@NamedQuery(name = "Subscriber.findAll", query = "SELECT a FROM Subscriber a"),
		@NamedQuery(name = "Subscriber.findAllActive", query = "SELECT a FROM Subscriber a WHERE a.status = 1") })

public class Subscriber implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1677721350450714110L;
	
	public Subscriber() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="msisdn")
	private String msisdn;
	
	@Column(name="mpin")
	private String mpin;

	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="package_id")
	private Integer packageId;
	
	@Column(name="created_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(name="modified_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name="register_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date registerDate;
	
	@Column(name="unregister_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date unregisterDate;
	
	@Column(name="last_renew")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastRenew;
	
	@Column(name="last_retry")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastRetry;
	
	@Column(name="expired_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date expiredDate;
	
	@Column(name="last_charged")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastChargedDate;
	
	@Column(name = "channel", columnDefinition = "BIT")
	private int channel;

	@Column(name = "status", columnDefinition = "BIT")
	private int status;

	@Column(name = "reg_new", columnDefinition = "BIT")
	private int regNew;
	
	@Column(name="charged_count")
	private int chargedCount;
	
	@Column(name="charge_failed_count")
	private int chargeFailedCount;
	
	@Column(name="charge_count_inday")
	private int chargeCountInDay;

	@Column(name="is_sub")
	private boolean isSub;
	
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

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getRegNew() {
		return regNew;
	}

	public void setRegNew(int regNew) {
		this.regNew = regNew;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public int getChargedCount() {
		return chargedCount;
	}

	public void setChargedCount(int chargedCount) {
		this.chargedCount = chargedCount;
	}

	public String getMpin() {
		return mpin;
	}

	public void setMpin(String mpin) {
		this.mpin = mpin;
	}

	public Date getLastChargedDate() {
		return lastChargedDate;
	}

	public void setLastChargedDate(Date lastChargedDate) {
		this.lastChargedDate = lastChargedDate;
	}

	public int getChargeFailedCount() {
		return chargeFailedCount;
	}

	public void setChargeFailedCount(int chargFailedCount) {
		this.chargeFailedCount = chargFailedCount;
	}

	/**
	 * @return the unregisterDate
	 */
	public Date getUnregisterDate() {
		return unregisterDate;
	}

	/**
	 * @param unregisterDate the unregisterDate to set
	 */
	public void setUnregisterDate(Date unregisterDate) {
		this.unregisterDate = unregisterDate;
	}

	/**
	 * @return the lastRenew
	 */
	public Date getLastRenew() {
		return lastRenew;
	}

	/**
	 * @param lastRenew the lastRenew to set
	 */
	public void setLastRenew(Date lastRenew) {
		this.lastRenew = lastRenew;
	}

	/**
	 * @return the lastRetry
	 */
	public Date getLastRetry() {
		return lastRetry;
	}

	/**
	 * @param lastRetry the lastRetry to set
	 */
	public void setLastRetry(Date lastRetry) {
		this.lastRetry = lastRetry;
	}

	/**
	 * @return the chargeCountInDay
	 */
	public int getChargeCountInDay() {
		return chargeCountInDay;
	}

	/**
	 * @param chargeCountInDay the chargeCountInDay to set
	 */
	public void setChargeCountInDay(int chargeCountInDay) {
		this.chargeCountInDay = chargeCountInDay;
	}

	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}


	/**
	 * @return the packageId
	 */
	public Integer getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}


	public static enum Status {

		INACTIVE(0), ACTIVE(1);

		private final int value;

		private Status(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}
	
	
	public static enum MetaKey {

		SCORE("SCORE");

		private final String value;

		private MetaKey(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}

	public boolean isSub() {
		return isSub;
	}

	public void setSub(boolean isSub) {
		this.isSub = isSub;
	}
}
