package com.yonyou.iuap.demo.contract;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterDigestUtils {
    private static final String ENCODE_KEY_PREFIX = "APPROVE_CENTER_4YrSiIrpY2baQcY2_";
    private static final Logger log = LoggerFactory.getLogger(RegisterDigestUtils.class);

    /**
     * 获取SecretKey
     */
    private static SecretKey getKey(String tenant) throws NoSuchAlgorithmException {
        String finalKey= ENCODE_KEY_PREFIX + tenant;
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(finalKey.getBytes());
        keygen.init(128, random);
        SecretKey original_key = keygen.generateKey();
        byte[] raw = original_key.getEncoded();
        return new SecretKeySpec(raw, "AES");
    }

    /**
     * 加密
     * 1.构造密钥生成器
     * 2.根据ecnodeRules规则初始化密钥生成器
     * 3.产生密钥
     * 4.创建和初始化密码器
     * 5.内容加密
     * 6.返回字符串
     */
    public static String encode(String content,String tenant) {
        try {
            SecretKey key = getKey(tenant);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] byte_encode = content.getBytes(StandardCharsets.UTF_8);
            byte[] byte_AES = cipher.doFinal(byte_encode);
            return Base64.getEncoder().encodeToString(byte_AES);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            log.error("fail to encode content: " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串还原成byte[]数组
     * 3.将加密内容解密
     */
    public static String decode(String content,String tenant){
        try {
            SecretKey key = getKey(tenant);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] byte_content = Base64.getDecoder().decode(content);
            byte[] byte_decode = cipher.doFinal(byte_content);
            return new String(byte_decode, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            log.error("fail to decode content: " + e.getLocalizedMessage(),e);
        }
        return null;
    }

//    public static void main(String[] args) {
//        //String content = "{\"appName\":\"baidu\",\"authType\":\"SDK\",\"ctime\":123545,\"fromTenant\":\"yonyou\",\"id\":3,\"mUrl\":\"https://baidu.com\",\"mtime\":122222,\"appId\":\"126466\",\"openType\":\"DIRECT\",\"source\":\"approve\",\"sysType\":\"YS\",\"typeButton\":\"101111\",\"typeStatus\":\"110000\",\"webUrl\":\"https://baidu.com\"}";
//        String content = "1";
//        System.out.println(content);
//        String aes = encode(content,"yonyou");
//        System.out.println(aes);
//        System.out.println(decode(aes,"yonyou"));
//    }
}
