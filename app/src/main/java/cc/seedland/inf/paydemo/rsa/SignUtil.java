package cc.seedland.inf.paydemo.rsa;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

/*
 * <pre>
 *     作者: xuchunlei
 *     联系方式: xuchunlei@seedland.cc / QQ:22003950
 *     时间: 2018/05/21
 *     描述:
 * </pre>
 */
public class SignUtil {

    private SignUtil() {

    }


   public static TreeMap<String, String> signPrivate(String keyContent, Map<String, String> params) {
       TreeMap<String, String> result = new TreeMap<>();
        result.putAll(params);
        try {
            PrivateKey key = loadPrivateKey(keyContent);
            Signature signer = Signature.getInstance("SHA1WithRSA");
            signer.initSign(key);

            String signQuery = generateQueryString(result, false);
            Log.d("SeedPay", "before sign ----> " + signQuery);
            signer.update(signQuery.getBytes());
            result.put("sign", Base64.encodeToString(signer.sign(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("sign failed");
        }
        return result;

    }

    /**
     * 生成请求参数地址
     * @param params
     * @return
     */
    private static String generateQueryString(Map<String, String> params, boolean encodeFlag) {
        StringBuilder paramSb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(TextUtils.isEmpty(value)) { // 去除值为空的参数
                continue;
            }
            paramSb.append(encodeFlag ? encode(key) : key);
            paramSb.append("=");
            paramSb.append(encodeFlag ? encode(value) : value);
            paramSb.append("&");
        }
        if(paramSb.length() > 0) {
            paramSb.deleteCharAt(paramSb.length() - 1);
        }

        return paramSb.toString();

    }

    private static String encode(String value) {
        return Uri.encode(value, "UTF-8");
    }


    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception
    {
        try {
            byte[] buffer = Base64.decode(privateKeyStr, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception
    {
        try {
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用公钥加密 <br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data
     *            需加密数据的byte数据
     * @param key
     *            密钥
     * @return 加密后的byte型数据
     */
    public static byte[] encryptData(byte[] data, PrivateKey key)
    {
//        try {
//            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
//            // 编码前设定编码方式及密钥
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            // 传入编码数据并返回编码结果
//            return cipher.doFinal(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }

        try {
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initSign(key);
            sign.update(data);
            return sign.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param data
     *            经过encryptedData()加密返回的byte数据
     * @param key
     *            密钥
     * @return
     */
    public static byte[] decryptData(byte[] data, PublicKey key)
    {
//        try {
//            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            return cipher.doFinal(encryptedData);
//        } catch (Exception e) {
//            return null;
//        }
        try {
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initVerify(key);
            byte[] result = "seedland".getBytes();
            sign.update(result);
            if(sign.verify(data)) {
                return result;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
