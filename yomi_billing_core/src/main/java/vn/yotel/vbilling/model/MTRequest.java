package vn.yotel.vbilling.model;

import java.io.Serializable;

public class MTRequest implements Serializable {
	private String fromNumber;
	private String toNumber;
	private String message;
	private String serviceCode;
	private String mtType;
	private MORequest moReq;
	private String channel;
	private boolean brandName = false;
	private boolean processed = false;
	private boolean flashSms = false; 
	private String gateway = MtGateway.CHARGING_CSP.value();
	
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
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public MORequest getMoReq() {
		return moReq;
	}
	public void setMoReq(MORequest moReq) {
		this.moReq = moReq;
		if (moReq != null) {
			this.setMtType(moReq.getCommand());
		}
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public boolean isBrandName() {
		return brandName;
	}
	public void setBrandName(boolean brandName) {
		this.brandName = brandName;
	}
	
	public String getMtType() {
		if (((mtType == null) || (mtType.length() == 0)) && (moReq != null)) {
			return moReq.getCommand();
		} else {
			return mtType;
		}
	}
	public void setMtType(String mtType) {
		this.mtType = mtType;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public boolean isFlashSms() {
		return flashSms;
	}
	public void setFlashSms(boolean flashSms) {
		this.flashSms = flashSms;
	}

	public static enum MtGateway {

		CHARGING_CSP("CHARGING_CSP"), SMPP_HN("SMPP_HN"), SMPP_HCM("SMPP_HCM");

		private final String value;

		private MtGateway(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}

	@Override
	public String toString() {
		return "MTRequest{" +
				"fromNumber='" + fromNumber + '\'' +
				", toNumber='" + toNumber + '\'' +
				", message='" + message + '\'' +
				", serviceCode='" + serviceCode + '\'' +
				", mtType='" + mtType + '\'' +
				", moReq=" + moReq +
				", channel='" + channel + '\'' +
				", brandName=" + brandName +
				", processed=" + processed +
				", flashSms=" + flashSms +
				", gateway='" + gateway + '\'' +
				'}';
	}

	public static enum MtType {

		TT("1"), Content("0");

		private final String value;

		private MtType(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
}
