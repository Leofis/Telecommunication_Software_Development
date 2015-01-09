package com.leofis.network.crypto;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Decryptor {
    Cipher dcipher;

    public Decryptor(SecretKey key) throws Exception {
        dcipher = Cipher.getInstance("DES");
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public String decrypt(String string) throws Exception {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        byte[] decrypted = dcipher.doFinal(data);
        return new String(decrypted, "UTF-8");
    }
}