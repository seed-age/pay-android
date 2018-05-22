package cc.seedland.inf.paydemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.security.PrivateKey;
import java.security.PublicKey;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cc.seedland.inf.paydemo.rsa.SignUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences prefs;

    private TextView encryptV;
    private TextView decryptV;
    private EditText editV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.key_maker).setOnClickListener(this);
        findViewById(R.id.encrypt).setOnClickListener(this);
        findViewById(R.id.decrypt).setOnClickListener(this);

        encryptV = findViewById(R.id.encrypt_content);
        decryptV = findViewById(R.id.decrypt_content);
        editV = findViewById(R.id.sample);

        prefs = getSharedPreferences("seedpay_demo", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.key_maker:
                Intent i = new Intent(this, SecretKeyActivity.class);
                startActivity(i);
                break;
            case R.id.encrypt:
                if(!TextUtils.isEmpty(editV.getText())) {
                    String sample = editV.getText().toString();
                    try {

                        PrivateKey key = SignUtil.loadPrivateKey(prefs.getString("private", null));
                        byte[] result = SignUtil.encryptData(sample.getBytes(), key);
                        encryptV.setText(Base64.encodeToString(result, Base64.DEFAULT));
                        Log.e("seedpay", "encrype---->" + encryptV.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.decrypt:
                String sample = encryptV.getText().toString();
                if(!TextUtils.isEmpty(sample)) {
                    try {
                        PublicKey key = SignUtil.loadPublicKey(prefs.getString("public", null));
                        byte[] result = SignUtil.decryptData(Base64.decode(sample, Base64.DEFAULT), key);
                        decryptV.setText(new String(result));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }



//    private static byte[] encryptByPublicKey(byte[] data, String key)
//            throws Exception {
//        // 对公钥解密
////        byte[] keyBytes = decryptBASE64(key);
//        byte[] keyBytes = Base64.getDecoder().decode(key);
//
//        // 取得公钥
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        Key publicKey = keyFactory.generatePublic(x509KeySpec);
//
//        // 对数据加密
////        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());//java上使用
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//Android上使用
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        return cipher.doFinal(data);
//    }

//    private static byte[] decryptByPrivateKey(byte[] data, String key) {
//        try {
//            // 对公钥解密
////        byte[] keyBytes = decryptBASE64(key);
//            byte[] keyBytes = Base64.getDecoder().decode(key);
//
//            // 取得私钥
//            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//
//
//
//            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            return cipher.doFinal(data);
//        } catch (Exception e)
//        {
//            return null;
//        }
//
//    }
//
//
//    private static String toHex(byte[] buf) {
//        final String HEX = "0123456789ABCDEF";
//        if (buf == null)
//            return "";
//        StringBuffer result = new StringBuffer(2 * buf.length);
//        for (int i = 0; i < buf.length; i++) {
//            result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(
//                    HEX.charAt(buf[i] & 0x0f));
//        }
//        return result.toString();
//    }

//
//    //解码返回byte
//    public static byte[] decryptBASE64(String key) throws Exception {
//
//        return (new BASE64Decoder()).decodeBuffer(key);
//    }

}