package vn.yotel.vbilling.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.yotel.vbilling.jpa.VasPackage;

public class MORequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -805265710253671308L;
	private String smsId;
	private String fromNumber;
	private String toNumber;
	private String message;
	private String command = "";
	private String syntax = "";
	private VasPackage subsPackage;
	private Date receivedDate = new Date();
	private Map<String, Object> context = new HashMap<String, Object>();
	private boolean processed = false;
	
	public String getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}
	public String getToNumber() {
		return toNumber;
	}
	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MORequest [fromNumber=");
		builder.append(fromNumber);
		builder.append(", toNumber=");
		builder.append(toNumber);
		builder.append(", message=");
		builder.append(message);
		builder.append(", command=");
		builder.append(command);
		builder.append("]");
		return builder.toString();
	}
	public String getSmsId() {
		return smsId;
	}
	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public VasPackage getSubsPackage() {
		return subsPackage;
	}
	
	public void setSubsPackage(VasPackage subsPackage) {
		this.subsPackage = subsPackage;
	}
	
	public void put(String key, Object obj) {
		try {
			this.context.put(key, obj);
		} catch (Exception e) {
		}
	}
	
	public Object get(String key) {
		return this.context.get(key);
	}
	
	/**
	 * @return the syntax
	 */
	public String getSyntax() {
		return syntax;
	}
	/**
	 * @param syntax the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
