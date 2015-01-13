package crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Encryptor {
	Cipher ecipher;

	public Encryptor(SecretKey key) throws Exception {
		ecipher = Cipher.getInstance("DES");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
	}

	public String encrypt(String string) throws Exception {
		// Encode the string into bytes using UTF-8
		byte[] utf8 = string.getBytes("UTF8");

		// Encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// Encode bytes to base64 to get a string
		return new sun.misc.BASE64Encoder().encode(enc);
	}
}
