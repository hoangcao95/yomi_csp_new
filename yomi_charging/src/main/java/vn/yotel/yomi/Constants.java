package vn.yotel.yomi;

import java.util.Arrays;
import java.util.List;

public interface Constants {

	public String serviceId = "FootballGame";
	public String SHORT_CODE = "5899";
	
	public List<String> SMSC_SHORTCODES = Arrays.asList("5899");
	
	public String COUNTRY_MOBILE_PREFIX = "84";

	public interface CommandCode {
		public String DEFAULT = "DEFAULT";
		public String GUIDE = "GUIDE";
		public String GUIDE_XS = "GUIDE_XS";
		public String GUIDE_DT = "GUIDE_DT";
		public String SCORE = "SCORE";
		public String ANSWER = "ANSWER";
		public String PLAY = "PLAY";
		public String PLAY_DAILY = "PLAY_DAILY";
		public String REJECT = "REJECT";

		public String REGISTER = "REG";
		public String CONFIRM_REGISTER = "CONFIRM_REG";
		public String CANCEL = "CAN";
		public String RENEW_DAY = "RENEW_DAY";
		public String RESET_PWD = "RESET_PWD";
		public String SET_PWD = "SET_PWD";
		public String BUY = "BUY";
		public String GIFT = "GIFT";
		public String CHECK = "CHECK";
		public String CAN_SMS_DAILY = "CAN_SMS_DAILY";
		public String REG_SMS_DAILY = "REG_SMS_DAILY";
		public String SMS_DAILY = "SMS_DAILY";
		public String SMS_DAILY_PRG = "SMS_DAILY_PRG";
		public String CHECK_POINT = "CHECK_POINT";
		public String FORWARD_MO = "FORWARD_MO";
		public String CHECK_PRICE = "CHECK_PRICE";
		public String PROMOTION = "PROMOTION";
		public String EURO2016 = "EURO2016";
		public String REG_REQ = "REG_REQ";
		public String REG_CONFIRM = "REG_CONFIRM";
		public String PACKAGE_CONFIRM = "PACKAGE_CONFIRM";

		// TTXS
		public String SMS_DAILY_STATS = "SMS_DAILY_STATS";
	}

	public interface Channel {
		public String SMS = "SMS";
		public String WAP = "WAP";
		public String SYS = "SYSTEM";
	}

	public interface Option {
		public String Channel = "_Channel_$";
		public String MT_SMS = "_MtSms_$";
		public String ACCOUNT_TYPE = "_AcctType_$";
		public String CONTENT_NAME = "_ContentName_$";
	}
}
