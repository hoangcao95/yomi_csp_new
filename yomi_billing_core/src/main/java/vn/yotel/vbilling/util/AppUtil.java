package vn.yotel.vbilling.util;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import vn.yotel.yomi.Constants;

public class AppUtil {
	
	public static SimpleDateFormat sdf_yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static Random RANDOM = new Random();
	
	public static String normalizeShortCode(String toNumber) {
		for (String shortCode : Constants.SMSC_SHORTCODES) {
			if (toNumber.contains(shortCode)) {
				return shortCode;
			}
		}
		return toNumber;
	}
	
	public synchronized static String generateTransId() {
		String val1 = sdf_yyyyMMddHHmmssSSS.format(new Date());
		String val2 = String.format("%03d", RANDOM.nextInt(999));
		return (val1 + val2);
	}
	
	public synchronized static String generateAirtimeTransId(String prefix) {
		String data = prefix + generateTransId();
		return data; 
	}
	
	public synchronized static String nvl(String input, String defaultValue) {
		if (input != null) {
			return input;
		} else {
			return defaultValue;
		}
	}
	
	public synchronized static String decryptPropertyValue(String encryptedPropertyValue) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt");
		String decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
		return decryptedPropertyValue;
	}
	
	public synchronized static String decryptPropertyValue(String encryptedPropertyValue, String password) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(password);
		String decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
		return decryptedPropertyValue;
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public synchronized static String generateFileLocation(String fileLocation, String prefix) {
		String strDate = sdf.format(new Date());
		String str[] = strDate.split("/");
		String day = str[0];
		String month = str[1];
		String year = str[2];
		String relativePath = String.format("%s/%s/%s/%s", prefix, year, month, day);
		String fileDir = String.format("%s/%s", fileLocation, relativePath);
		File f = new File(fileDir);
		boolean success = true;
		if (!f.exists()) {
			success = f.mkdirs();
		}
		if (success) {
			return relativePath;
		} else {
			return null;
		}
	}

	public static String computeHash(String clientSecret, String ...params) {
		try {
			String result = "";
			MessageDigest md = MessageDigest.getInstance("MD5");
			String strHash = "";
			for (String param : params) {
				if (strHash.length() > 0) {
					strHash = strHash + "|";
				}
				strHash = strHash + param;
			}
			strHash = strHash + "|" + clientSecret;
			md.update(strHash.getBytes());
			result = AESUtil.bytesToHex(md.digest());
			return result.toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}

	public static String md5(String input) {
		try {
			String result = "";
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			result = AESUtil.bytesToHex(md.digest());
			return result.toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDurationString(int duration) {
		String durationString = "";
		if (duration == 1)
			durationString = "ngày";
		if (duration == 7)
			durationString = "tuần";
		if (duration == 30)
			durationString = "tháng";
		return durationString;
	}

	public synchronized static String encryptPropertyValue(String encryptedPropertyValue) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt");
		String decryptedPropertyValue = encryptor.encrypt(encryptedPropertyValue);
		return decryptedPropertyValue;
	}

	public static void main(String[] args) {
        String yomi_csp_new = AppUtil.encryptPropertyValue("yomi_csp_new");
        System.out.println(yomi_csp_new);
        String value = AppUtil.decryptPropertyValue("vgz9HugBMYXDNjnl4orL0w==");
        System.out.println(value);
    }
}
