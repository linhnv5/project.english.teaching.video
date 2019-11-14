package topica.linhnv5.video.teaching.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Hash util, contain function to encode and decode hash string
 * 
 * @author ljnk975
 */
public class HashUtil {

	public static String getHash265(String mss) throws Exception {
		return DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-256").digest(mss.getBytes("UTF-8"))).toLowerCase();
	}

	public static String getHMAC512(String mss, String key) throws Exception {
		final String HMAC_SHA512 = "HmacSHA512";

		Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);      
		sha512_HMAC.init(new SecretKeySpec(key.getBytes("UTF-8"), HMAC_SHA512));

        return DatatypeConverter.printHexBinary(sha512_HMAC.doFinal(mss.getBytes("UTF-8"))).toLowerCase();
	}

	public static String getMD5(String mss) throws Exception {
		return DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(mss.getBytes("UTF-8"))).toLowerCase();
	}

	public static String decodeRC4(String mss, String key) throws Exception {
		final String RC4 = "RC4";

		// Create Cipher instance and initialize it to encrytion mode
        Cipher cipher = Cipher.getInstance(RC4);  // Transformation of the algorithm

        // Reinitialize the Cipher to decryption mode
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), RC4), cipher.getParameters());

        return new String(cipher.doFinal(DatatypeConverter.parseHexBinary(mss)), "UTF-8");
	}

}
