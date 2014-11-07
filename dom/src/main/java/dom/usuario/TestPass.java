package dom.usuario;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class TestPass {

	public static void main(String[] args) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		// TODO Auto-generated method stub
		byte[] encodedBytes = Base64.encodeBase64("infoimps".getBytes());
		System.out.print("encodedBytes " + new String(encodedBytes));

		// 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
		// 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
		// e10adc3949ba59abbe56e057f20f883e

		byte[] decodedBytes = Base64.decodeBase64("aW5mb2ltcHM=");
		System.out.println("decodedBytes " + new String(decodedBytes));
		//53adcbed3766aee9a1624ccca86bbed41ceba7348f3e7807a1141fd720f2a360
		//53adcbed3766aee9a1624ccca86bbed41ceba7348f3e7807a1141fd720f2a360
//		7fb00fe7262cad3238a5b082f7d38a43109301cea5c48c99d214542ef8021674
		System.out.println("256:: " + hash256( "infoimps"));
	}

	public static String hash256(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data.getBytes());
		return bytesToHex(md.digest());
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(
					1));
		return result.toString();
	}

	public static byte[] getEncriptarsha() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String text = "123456";

		md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();
		return digest;
	}

	/**
	 * Encrypt password by using SHA-256 algorithm, encryptedPassword length is
	 * 32 bits
	 * 
	 * @param clearTextPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 *             reference
	 *             http://java.sun.com/j2se/1.4.2/docs/api/java/security
	 *             /MessageDigest.html
	 */

	public static String getEncryptedPassword(String clearTextPassword) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(clearTextPassword.getBytes());
			return new sun.misc.BASE64Encoder().encode(md.digest());
		} catch (NoSuchAlgorithmException e) {
			// _log.error("Failed to encrypt password.", e);
		}
		return "";
	}

}
