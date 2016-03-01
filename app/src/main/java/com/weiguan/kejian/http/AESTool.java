package com.weiguan.kejian.http;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2016/2/3 0003.
 */
public class AESTool {
    private static final String PWD = "bbA3H18lkVbQDfak";
    private static final String IV = "0123456789012345";

    public static SecretKeySpec makeKey() throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(PWD.getBytes("UTF-8")));
        SecretKey key = kgen.generateKey();
//        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

         SecretKeySpec keySpec = new SecretKeySpec(PWD.getBytes("UTF-8"), "AES");
        return keySpec;
    }

    public static IvParameterSpec makeIv() throws UnsupportedEncodingException {
        return new IvParameterSpec(IV.getBytes("UTF-8"));
    }

    /**
     * 加密
     *
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return
     */
    public static byte[] encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
            return cipher.doFinal(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密
     *
     * @param content
     *            待解密内容
     * @param password
     *            解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

