package vn.yotel.yomi;





public final class ConstantsEnum {
	
	/**
	 * Cờ on/off chức năng thu hồi sub khi vượt quá khoảng thời gian chờ cho phép
	 */
	public static final  boolean SUB_COLLECTION_ENABLE  = true;
	
	/**
	 * Khoảng thời gian chờ cho phép(MINUTE). nếu SUB_COLLECTION_ENABLE = true thì sau khoảng SUB_COLLECTION_INTERVAL những sub 
	 * mà không có owner(chưa được mapping tới partner) nào thì sẽ được collection 
	 * và mapping về partner defualt ( affiliate_id = 11111 & app_code = 11111) 
	 * note : MINUTE 
	 */
	public static final  int SUB_COLLECTION_INTERVAL  = 15*24*60; // 15 days
	
	
	/**
	 * 
	 */
	public static final String DEFAULT_UNKONW_OWNER_CODE = "00000";
	public static final String DEFAULT_UNKONW_APP_CODE_EXTEND = "00001";
	public static final String DEFAULT_UNKONW_APP_CODE_ADS = "00002";
	public static final String DEFAULT_UNKONW_OWNER_NAME = "N/A";
//	public static final  String DEFAULT_UNOWNER_APP_CODE = "00000";
	
	public static final  String DEFAULT_OWNER_CODE= "11111";
	
	/**
	 * 
	 * @author loind
	 * Trạng thái yêu cầu
	 */
	public static enum ReqStatus {

		SUCCESSFUL(1),
		UNSUCCESSFUL(0);

		private final int value;

		private ReqStatus(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		public static ReqStatus valueOf(int status) {
			for (ReqStatus subStatus : values()) {
				if (subStatus.value == status) {
					return subStatus;
				}
			}
			throw new IllegalArgumentException("No matching constant for [" + status + "]");
		}
	}
	
	
	public static enum Subscriber{
		NEW_SUB(1),
		RE_SUB(0);

		private final int value;

		private Subscriber(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		public static Subscriber valueOf(int status) {
			for (Subscriber subStatus : values()) {
				if (subStatus.value == status) {
					return subStatus;
				}
			}
			throw new IllegalArgumentException("No matching constant for [" + status + "]");
		}
	}
	
	
	/**
	 * 
	 * @author loind
	 * Các kênh khách hàng thực hiện
	 */
	public static enum SubChannel {
		SMS("SMS"),
//		USSD("USSD"),
		VASGATE("VASGATE"),
		APP("APP"),
		SYS("SYS"),
		WAP("WAP"),
		WEB("WEB");

		private final String value;

		private SubChannel(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
		
		public static boolean isValid(String channel) {
			boolean valid = false;
			 for(SubChannel subChannel : SubChannel.values()){
				 if(subChannel.value().equalsIgnoreCase(channel)){
					 valid = true;
					 break;
				 }
			 }
			 return valid;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	/**
	 * 
	 * @author loind
	 * Các kênh khách hàng thực hiện
	 */
//	public static enum ChargeChannel {
//		SMS("SMS"),
////		USSD("USSD"),
//		APP("APP"),
//		SYS("SYS"),
//		WAP("WAP"),
//		WEB("WEB");
//
//
//		private final String value;
//
//		private ChargeChannel(String value) {
//			this.value = value;
//		}
//
//		public String value() {
//			return this.value;
//		}
//		
//		public static boolean isValid(String channel) {
//			boolean valid = false;
//			 for(ChargeChannel chargeChannel : ChargeChannel.values()){
//				 if(chargeChannel.value().equalsIgnoreCase(channel)){
//					 valid = true;
//					 break;
//				 }
//			 }
//			 return valid;
//		}
//		
//		@Override
//		public String toString() {
//			return value;
//		}
//	}
	
	/**
	 * 
	 * @author loind
	 *
	 */
	public static enum ChargeStatus {

		SUCCESSFUL(1),
		UNSUCCESSFUL(0);

		private final int value;

		private ChargeStatus(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		public static ChargeStatus valueOf(int status) {
//			int seriesCode = status / 100;
			for (ChargeStatus subStatus : values()) {
				if (subStatus.value == status) {
					return subStatus;
				}
			}
			throw new IllegalArgumentException("No matching constant for [" + status + "]");
		}
	}
	/**
	 * 
	 * @author loind
	 *	Trạng thái sử dụng dịch vụ của thuê bao
	 */
	public static enum SubscriberVasPackageStatus{

		ACTIVE("ACTIVE"),
		INACTIVE("INACTIVE"),
		CANCELED("CANCELED"),
		SUSPEND("SUSPEND"),
		BLOCK("BLOCK");

		private final String value;

		private SubscriberVasPackageStatus(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		@Override
		public String toString() {
			return value;
		}
	}


	/**
	 * 
	 * @author loind
	 * Các tác động cho phép lên thuê bao
	 */
	public static enum ReqAction {

		REGISTER("REGISTER")
//		DEACTIVE("DEACTIVE"),
//		ACTIVE("ACTIVE"),
		,EXTEND("EXTEND")
		,CANCEL("CANCEL");

		private final String value;

		private ReqAction(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
	
	/**
	 * 
	 * @author loind
	 * Đối tượng thực hiện các reqAction lên thuê bao.
	 */
	public static enum ReqMsg {

		SUBSCRIBER("SUBSCRIBER"),
		SYS("SYS");

		private final String value;

		private ReqMsg(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
		
		public static boolean isValid(String channel) {
			boolean valid = false;
			 for(ReqMsg reqMsg : ReqMsg.values()){
				 if(reqMsg.value().equalsIgnoreCase(channel)){
					 valid = true;
					 break;
				 }
			 }
			 return valid;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}


	 
	/**
	 * 
	 * @author loind
	 * các loại giao dịch phát sinh trừ cước
	 */
	public static enum ChargeType {

		REGISTER("REGISTER"),
//		CANCEL("CANCEL"),
		EXTEND("EXTEND"),
//		PURCHASE("PURCHASE")
		;

		private final String value;

		private ChargeType(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static enum ErrorCode{
		SUCCESS("0000"), FAILURE("0001"), REG_AGAIN("5000"), DOUBLE_PKG("5001"), INVALID_BALANCE("1001"),;

		private final String value;

		private ErrorCode(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
	
}
