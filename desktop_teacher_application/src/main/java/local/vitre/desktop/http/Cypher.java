package local.vitre.desktop.http;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import local.vitre.desktop.Log;
import local.vitre.desktop.util.Utils;

public class Cypher {

	private static int CIPHER_KEY_LEN = 16; // 128 bits

	public static String payload(String data) {
		int delim = data.indexOf('.');
		String header = data.substring(0, delim);
		char key = header.charAt(header.length() - 1);
		String body = data.substring(delim, data.length());
		String shakeKey = truncate(Character.toString(key));

		String shake = decrypt(header, shakeKey);
		String load = decrypt(body, truncate(shake));
		Log.fine("HTTP", "Decrypted payload.");
		return load;
	}

	public static String decrypt(String input, String passphrase) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(passphrase.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decodeBase64(input));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(output);
	}

	public static String pseudoEncrypt(String body) {
		String handshakeKey = Utils.createUniqueHash(8);
		String headerKey = RandomStringUtils.randomAlphanumeric(1);
		String headerEncrKey = Cypher.encrypt(Cypher.truncate(handshakeKey), headerKey);

		if (headerEncrKey.endsWith("="))
			headerEncrKey = headerEncrKey.replaceAll("=", "");

		String headerData = headerEncrKey + headerKey + ".";
		String bodyData = Cypher.encrypt(body, handshakeKey);

		return headerData.concat(bodyData);
	}

	public static String encrypt(String input, String passphrase) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(truncate(passphrase).getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(crypted));
	}

	public static String sha256(String data) {
		return DigestUtils.sha256Hex(data);
	}

	/**
	 * Truncate passphrases into 16 bytes.
	 * 
	 * @param key
	 * @return
	 */
	public static String truncate(String key) {
		if (key.length() < CIPHER_KEY_LEN) {
			int numPad = CIPHER_KEY_LEN - key.length();

			for (int i = 0; i < numPad; i++) {
				key += "0"; // 0 pad to len 16 bytes
			}
			return key;
		}

		if (key.length() > CIPHER_KEY_LEN) {
			return key.substring(0, CIPHER_KEY_LEN); // truncate to 16 bytes
		}

		return key;
	}
}
