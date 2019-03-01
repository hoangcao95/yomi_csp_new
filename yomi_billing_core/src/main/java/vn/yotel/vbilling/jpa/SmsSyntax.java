package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="core_package_sms_syntax")
@NamedQuery(name="SmsSyntax.findAll", query="SELECT a FROM SmsSyntax a")

public class SmsSyntax implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1677721350450714110L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="operator")
	private String operator;

	@Column(name="short_code")
	private String shortCode;

	@Column(name="syntax")
	private String syntax;

	@Column(name="regex")
	private String regex;

	@Column(name="command")
	private String command;

	@Column(name="description")
	private String description;

	@Column(name="created_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name="status", columnDefinition = "BIT")
	private int status;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "package_id")
	private VasPackage vasPackage;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the vasPackage
	 */
	public VasPackage getVasPackage() {
		return vasPackage;
	}

	/**
	 * @param vasPackage the vasPackage to set
	 */
	public void setVasPackage(VasPackage vasPackage) {
		this.vasPackage = vasPackage;
	}

}
