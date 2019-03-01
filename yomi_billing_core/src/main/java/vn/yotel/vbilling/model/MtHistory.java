package vn.yotel.vbilling.model;

import java.util.Date;

public class MtHistory {
	
	private String msisdn;
	private String message;
	private Date sentDate;
	private String strMtDate;
	private String moMessage;
	private Date moDate;
	private String strMoDate;
	private String type;
	private String channel;
	private int id;
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getSentDate() {
		return sentDate;
	}
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	public String getMoMessage() {
		return moMessage;
	}
	public void setMoMessage(String moMessage) {
		this.moMessage = moMessage;
	}
	public Date getMoDate() {
		return moDate;
	}
	public void setMoDate(Date moDate) {
		this.moDate = moDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStrMtDate() {
		return strMtDate;
	}
	public void setStrMtDate(String strMtDate) {
		this.strMtDate = strMtDate;
	}
	public String getStrMoDate() {
		return strMoDate;
	}
	public void setStrMoDate(String strMoDate) {
		this.strMoDate = strMoDate;
	}
	
}
