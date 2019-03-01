package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
@Table(name="core_vas_package")
@NamedQuery(name="VasPackage.findAll", query="SELECT a FROM VasPackage a")

public class VasPackage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1677721350450714110L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="product_id")
	private Integer productId;

	@Column(name = "name", unique = true)
	private String name;
	
	@Column(name="desc")
	private String desc;
	
	@Column(name="type", columnDefinition = "BIT")
	private int type;
	
	@Column(name="price")
	private float price;
	
	@Column(name="duration")
	private int duration;
	
	@Column(name="free_duration")
	private int freeDuration;
	
	@Column(name="created_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(name="modified_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name="status", columnDefinition = "BIT")
	private int status;
	
	@Column(name="reg_command")
	private String regCommand;
	
	@Column(name="can_command")
	private String canCommand;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getPrice() {
		return price;
	}
	
	public int getPriceAsInt() {
		return (int) price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getFreeDuration() {
		return freeDuration;
	}

	public void setFreeDuration(int freeDuration) {
		this.freeDuration = freeDuration;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	public String getPriceFormatted() {
		DecimalFormat moneyFormatter = new DecimalFormat("###,###");
		return moneyFormatter.format(price);
	}
	
	public String getPriceFormattedDot() {
//		DecimalFormat moneyFormatter = new DecimalFormat("###.###");
		DecimalFormatSymbols fS = new DecimalFormatSymbols();
		fS.setGroupingSeparator(".".toCharArray()[0]);
		DecimalFormat moneyFormatter = new DecimalFormat("###,###", fS);
		return moneyFormatter.format(price);
	}

	public String getRegCommand() {
		return regCommand;
	}

	public void setRegCommand(String regCommand) {
		this.regCommand = regCommand;
	}

	public String getCanCommand() {
		return canCommand;
	}

	public void setCanCommand(String canCommand) {
		this.canCommand = canCommand;
	}
}
