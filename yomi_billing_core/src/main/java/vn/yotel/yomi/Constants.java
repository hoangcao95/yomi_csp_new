package vn.yotel.yomi;

import java.text.SimpleDateFormat;
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
		public String REGISTER_SUBS_PACKAGE = "REGISTER_SUBS_PACKAGE";
		public String RE_REGISTER_SUB = "RE_REGISTER_SUB";
		public String EXTEND_SUBS_PACKAGE = "EXTEND_SUBS_PACKAGE";
		public String CANCEL_SUBS_PACKAGE = "CANCEL_SUBS_PACKAGE";
		public String HASH_PRIVATE_KEY = "QWERTY!@#$5678/,.<>";


		// TTXS
		public String SMS_DAILY_STATS = "SMS_DAILY_STATS";

		//KPAS - Kham pha an so
		public String CMD_HDAS = "AS_HD";
	}

	public interface Channel {
		public String SMS = "SMS";
		public String WAP = "WAP";
		public String SYS = "SYSTEM";
	}

	public interface Xembongda {
		public interface ErrorCode {
			public String SUCCESS = "0000";
			public String REG_AGAIN = "5000";
			public String DOUBLE_PACKAGE = "5001";
			public String BLACK_LIST = "5002";
			public String EXCEPTION = "9999";
		}
	}

    public interface Option {
        public String Channel = "_Channel_$";
        public String MT_SMS = "_MtSms_$";
        public String ACCOUNT_TYPE = "_AcctType_$";
        public String CONTENT_NAME = "_ContentName_$";
    }

	public interface CDR {
		public static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

		public interface Status {
			public String FAIL = "0";
			public String SUCCESS = "1";
		}

		public String LAST_EXPORT_DATE_KEY = "_LAST_EXPORT_DATE_KEY_";
		public String CURR_FILE_INDEX_KEY = "_CURR_FILE_INDEX_";
		public String CURR_FILE_SIZE_KEY = "_CURR_FILE_SIZE_";
		//BigPromos
		public String LAST_EXPORT_BIGPROMOS_DATE_KEY = "_LAST_EXPORT_BIGPROMOS_DATE_KEY_";

		public interface AccountTypes {
			public String MAIN = "MAIN";
			public String KM3 = "KM3";
			public String KM2 = "KM2";
		}
	}
}
