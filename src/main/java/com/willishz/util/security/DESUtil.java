package com.willishz.util.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.SecureRandom;


public class DESUtil {
    public static final String ALGORITHM = "DES";
    public static final BASE64Decoder base64decoder = new BASE64Decoder();
    public static final BASE64Encoder base64encoder = new BASE64Encoder();

    public static void main(String[] args) {
        String password = "111111";
        // DES加密
        try {
            password = DESUtil.encryptBASE64(DESUtil.encrypt(password.getBytes(), "TEST_KEY_DES_MD5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(password);

        // DES解密
        String _password = null;
        try {
            _password = new String(DESUtil.decrypt(DESUtil.decryptBASE64(password), "TEST_KEY_DES_MD5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(_password);
    }

    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);

        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);

        return cipher.doFinal(data);
    }

    public static String initKey(String seed) throws Exception {
        SecureRandom secureRandom = null;

        if (seed != null) {
            secureRandom = new SecureRandom(decryptBASE64(seed));
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(secureRandom);

        SecretKey secretKey = kg.generateKey();

        return encryptBASE64(secretKey.getEncoded());
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return base64decoder.decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return base64encoder.encodeBuffer(key);
    }
}
