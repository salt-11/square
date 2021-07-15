/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package cn.hawy.quick.partner.core.util;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

/**
 * 
 * @author simon.xxm
 * @version $Id: RSA.java, v 0.1 2016年1月25日 上午10:34:58 simon.xxm Exp $
 */
public class RSA {
    /** 指定key的大小 */
    private static int          KEYSIZE        = 2048;
    private static final String encoding       = "UTF-8";
    private static final String RSA_ALGORITHM  = "RSA";
    /** 公钥 RSA */
    public static final String RSA_publicKey  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn+AxK8naUxacQFWTLY0+X/eFcS+oOqdVflCTCDHavu0QIyuQdUthErOk6V3FY38uVQRNSkPYit+Wokwxb2YXTPTol0T/gQ8/i3DMCxg1Is2VgrQ8xdazDEQJ+dAd1SS5+xFm5LLUUYXAA3EyEW0VB3AKRybQ58jm2y/xgB7kDjCJzW52XiZvEX71/BznXUTZCSiv6pXnNgy2oy4niFWFzTF81nq76wfip1mI9twbqcJ+U/QmTUf10nk451vXhHzbz1TwECKBrLJyIuhz/tYNS6OG0VRAY8EkFxMPV1kD8j4p1U/VvbVDJDfEvW1ecNmTkn2kWj887krFeN2fDqQu5QIDAQAB";
    /** 私钥 RSA */
    public static final String RSA_privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCf4DErydpTFpxAVZMtjT5f94VxL6g6p1V+UJMIMdq+7RAjK5B1S2ESs6TpXcVjfy5VBE1KQ9iK35aiTDFvZhdM9OiXRP+BDz+LcMwLGDUizZWCtDzF1rMMRAn50B3VJLn7EWbkstRRhcADcTIRbRUHcApHJtDnyObbL/GAHuQOMInNbnZeJm8RfvX8HOddRNkJKK/qlec2DLajLieIVYXNMXzWervrB+KnWYj23Bupwn5T9CZNR/XSeTjnW9eEfNvPVPAQIoGssnIi6HP+1g1Lo4bRVEBjwSQXEw9XWQPyPinVT9W9tUMkN8S9bV5w2ZOSfaRaPzzuSsV43Z8OpC7lAgMBAAECggEALgBLBadx22BxGw3EaGHwpZpJ2hR1WlMVhV/Xvo7huvfpIcWCLk2/I6tu8vrZNdS9VxcCyjCVWPgWlqJHwx7j62+4kPX4coEZrpyJCVQwzc/Lii0kI2SwZgIZwLiCLPcBUF1kdr+B2ecbJaJnXp49Y8N4IZrolxi2XDECUvQQeIhIVhKY1ANPSdC3byiDT6hBP1hb6afZymjSpg6HnzgVAzbqD25SE+05imu5u8SKU0t2btYpx4mevOaFNPugiG4T/w1HMCIvttl18Lni1qhu8hfd8P0XKr5etLT4wL9MOp2FTnokiqIJb5Cjg1VLPgaCRtrpuwOL/DlJfNJ3q8zPrQKBgQDQMFx38IRejHvQFGmSftJwdX2Og2t1fcWFl+q1kICbHU7vPUt2X533jENqwGUbjxOGVB04HOPkP90nsgCh2K5sV7jugueCbHAXPeRZw1cuyfDISx0CNTNQYJgcNnR4brOcPdXlEq6+/tPanwvXPZHxO7pltFPVNEeVdkyV6JtrGwKBgQDEl3Nm6dK62q0IcnlC3XK8UXNl6BXbbnisv7Yce/uvrq1+s3Li2D++039NO5s7rWaSh4I1bPYBYhl+Pi7PQQmbV7dKUA1zvrdJa4A4CaC0oawCmHmSi2RcN5BdvlhLB5jWAWAh1E1CUnYNAk8HrYwiQmH6s7H+LWrJ8fo6d05t/wKBgHtSOmQdx/tN0kW24baSg0t0fVR/CpeFUgzJqZG1sEix+sECmQHcr/EkWAcTQpEplpI6nzhO/LuHDRTnfzJf7SpmaDh4uHRskZVi5zI44RrpKkUrIVrecrAJF/GWXDW+tYw2oM5Z28NtpcbZ8aSlZx7zl32JbSNdHY4ujj06lV3DAoGBAI/AdyA/xlnf4TsWCdl8+JL/tDRGOwVTiGY5YT7+4hGa1z6YlV+O5T3yhVnCRkajCygGco4CePiV9TI84hmm4xF2WI68brm+MWQ8eWuAjsTGm3Lib7Navi44axZBQ2rJyVZjdxoFleDzT2M/1u40Vw4OIHsjvvmv2DCHAoSSDQWdAoGAQNYWqI0Mq+0pzGXEOCVJhTJlHrRBVuBmUpqY5aDMLmVHNjLAX5623RRKi1VTMNBu+J8wB0QHPDocbxkYZLjgJO9RK+HWkenFSzZR9qrwP8/B3oF8n653CRxHuRC5wfDh3OljnwlrOHliBhOf0kw0+H9TtE1+gaz+FPB69gbfcno=";

    public static void main(String[] args) throws Exception {
//        Map<String, String> map = generateKeyPair();
//        System.out.println(map.get("publicKey"));
//        System.out.println(map.get("privateKey"));
        String sign = RSA.sign("123", RSA_privateKey);
        System.out.println(sign);
        //System.out.println(RSA.checkSign("MerchantId=1037221300972519426&PartnerId=1001", sign, RSA_publicKey));
    }

    /**
     * 生成密钥对
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(KEYSIZE, sr);
        /** 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
        /** 得到公钥 */
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String pub = new String(Base64.encodeBase64(publicKeyBytes), encoding);
        /** 得到私钥 */
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = new String(Base64.encodeBase64(privateKeyBytes), encoding);

        Map<String, String> map = new HashMap<String, String>();
        map.put("publicKey", pub);
        map.put("privateKey", pri);
        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.encodeBase64(b);
        String retValue = new String(deBase64Value);
        map.put("modulus", retValue);
        return map;
    }

    /**
     * 加密方法 source： 源数据
     */
    public static String encrypt(String source, String publicKey) throws Exception {
        Key key = getPublicKey(publicKey);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return new String(Base64.encodeBase64(b1), encoding);
    }

    /**
     * 解密算法 cryptograph:密文
     */
    public static String decrypt(String cryptograph, String privateKey) throws Exception {
        Key key = getPrivateKey(privateKey);
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b1 = Base64.decodeBase64(cryptograph.getBytes());
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 得到公钥
     * 
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     * 
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String sign(String content, String privateKey) {
        String charset = encoding;
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey
                .getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance("SHA256WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {

        }
        return null;
    }

    public static boolean checkSign(String content, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance("SHA256WithRSA");

            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            boolean bverify = signature.verify(Base64.decodeBase64(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
