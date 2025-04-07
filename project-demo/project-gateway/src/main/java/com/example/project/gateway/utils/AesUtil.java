package com.example.project.gateway.utils;

/**
 * @author hanyangyang
 * @since 2025/4/7
 */
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import java.util.HashMap;
import java.util.Map;

public class AesUtil {
    private static final String IV = "DYgjCEIMVrj2W9xN";
    public static final String SECRET_KEY = "fyLP3Y1r2lAtAJ5J7yMXPg==";
    private static Map<String, AES> AES_MAP = new HashMap();

    public static String getSecret() {
        return Base64.encode(SecureUtil.aes().getSecretKey().getEncoded());
    }

    public static String encrypt(String secretKey, String str) {
        AES aes = getAES(secretKey);
        return aes.encryptHex(str);
    }

    public static String encryptBase64(String secretKey, String str) {
        AES aes = getAES(secretKey);
        return aes.encryptBase64(str);
    }

    public static String decrypt(String secretKey, String str) {
        AES aes = getAES(secretKey);
        return aes.decryptStr(str);
    }

    public static AES getAES(String secretKey) {
        AES aes = (AES)AES_MAP.get(secretKey);
        if (aes == null) {
            synchronized(AesUtil.class) {
                aes = (AES)AES_MAP.get(secretKey);
                if (aes == null) {
                    aes = new AES("CBC", "PKCS7Padding", secretKey.getBytes(), "DYgjCEIMVrj2W9xN".getBytes());
                    AES_MAP.put(secretKey, aes);
                }
            }
        }

        return aes;
    }
}
