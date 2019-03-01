package vn.yotel.iloto.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;

public class JasyptTest {

	@Test
	public void test() {
//		fail("Not yet implemented");
	}

	@Test
	public void testEncrypt() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt");
		String username = encryptor.encrypt("yomi");
		String password = encryptor.encrypt("yomi!@#45");
		System.out.println(username);
		System.out.println(password);

		System.out.println(encryptor.decrypt("YIEJYnLpjkzB2lLBtvYBSg=="));
		System.out.println(encryptor.decrypt("MEVgjBtC2w5iUaAmwJKMwpp6Xn90HxRg"));
	}

}
