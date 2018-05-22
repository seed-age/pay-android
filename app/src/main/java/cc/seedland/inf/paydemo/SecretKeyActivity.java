package cc.seedland.inf.paydemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/*
 * <pre>
 *     作者: xuchunlei
 *     联系方式: xuchunlei@seedland.cc / QQ:22003950
 *     时间: 2018/05/21
 *     描述:
 * </pre>
 */
public class SecretKeyActivity extends AppCompatActivity {


    private SharedPreferences prefs;
    private String publicKey;
    private String privateKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secret_key);
        prefs = getSharedPreferences("seedpay_demo", Context.MODE_PRIVATE);
        refreshKeys();

        if(TextUtils.isEmpty(publicKey) || TextUtils.isEmpty(privateKey)) {
            try {
                initKey();
                refreshKeys();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final TextView publicV = findViewById(R.id.public_key);
        publicV.setText(publicKey);

        final TextView privateV = findViewById(R.id.private_key);
        privateV.setText(privateKey);

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().clear().commit();
                refreshKeys();
                privateV.setText(privateKey);
                publicV.setText(publicKey);
            }
        });
    }

    private void refreshKeys() {
        publicKey = prefs.getString("public", null);
        privateKey = prefs.getString("private", null);
        Log.e("seedpay", publicKey);
    }

    public void initKey() throws Exception {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 保存公私钥
        SharedPreferences.Editor e = prefs.edit();

        e.putString("public", Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
        e.putString("private", Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));
        e.commit();
    }
}
