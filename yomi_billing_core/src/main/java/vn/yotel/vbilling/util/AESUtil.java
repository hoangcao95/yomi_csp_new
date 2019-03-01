package vn.yotel.vbilling.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESUtil {

    private static Logger LOG = LoggerFactory.getLogger(AESUtil.class);

    private static String algorithm = "AES";
    private static SecureRandom secureRandom = null;
    private static Object lockObj = new Object();

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Performs Encryption
    public static String encrypt(String plainText, String strKey) throws Exception {
        byte[] keyValue = hexStringToByteArray(strKey);
        Key key = generateKey(keyValue);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = chiper.doFinal(plainText.getBytes("UTF-8"));
        LOG.debug(bytesToHex(encVal));
        String encryptedValue = StringUtils.newStringUtf8(Base64.encodeBase64(encVal, false));
        return encryptedValue;
    }

    // Performs decryption
    public static String decrypt(String encryptedText, String strKey) throws Exception {
        byte[] keyValue = hexStringToByteArray(strKey);
        Key key = generateKey(keyValue);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.DECRYPT_MODE, key);
        byte[] bytesToDecrypt = Base64.decodeBase64(encryptedText);
//		byte[] bytesToDecrypt = org.bouncycastle.util.encoders.Base64.decode(encryptedText);
//		byte[] finalBytesToDecrypt = null;
//		if (bytesToDecrypt.length % 16 != 0) {
//			int newLen = (bytesToDecrypt.length / 16 + 1) * 16;
//			LOG.debug("Old: {},  new: {}", bytesToDecrypt.length, newLen);
//			finalBytesToDecrypt = new byte[newLen];
//			System.arraycopy(bytesToDecrypt, 0, finalBytesToDecrypt, 0, bytesToDecrypt.length);
//			for (int index = bytesToDecrypt.length; index < newLen; index++) {
//				finalBytesToDecrypt[index] = (byte) 0x00;
//			}
//		} else {
//			finalBytesToDecrypt = bytesToDecrypt;
//		}
//		byte[] decValue = chiper.doFinal(finalBytesToDecrypt);
        byte[] decValue = chiper.doFinal(bytesToDecrypt);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey(byte[] keyValue) throws Exception {
        Key key = new SecretKeySpec(keyValue, algorithm);
        return key;
    }

    /**
     *
     * @param len Length of the expected string
     * @return generated card or null
     * @throws NoSuchAlgorithmException
     */
    public static String generateToken(int len) {
        synchronized (lockObj) {
            if (secureRandom == null) {
                LOG.debug("Initializing SHA1PRNG ...");
                try {
                    secureRandom = SecureRandom.getInstance("SHA1PRNG");
                } catch (NoSuchAlgorithmException e) {
                    secureRandom = new SecureRandom();
                }
            }
        }
        byte[] myBytes = new byte[len / 2];
        secureRandom.nextBytes(myBytes);
        String str = bytesToHex(myBytes);
        return str.substring(0, len);
    }
}
