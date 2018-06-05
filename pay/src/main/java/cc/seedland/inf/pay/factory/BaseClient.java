package cc.seedland.inf.pay.factory;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/06/01 09:52
 * 描述 ：
 **/
public abstract class BaseClient implements IPayClient {

    protected PublicKey key;

    public BaseClient() {

    }

    public BaseClient(String publicKey) {
        if(publicKey != null && !publicKey.isEmpty()) {
            try {
                byte[] buffer = Base64.decode(publicKey, Base64.DEFAULT);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
                key = keyFactory.generatePublic(keySpec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkSign(String data) {
        return true;
    }

    @Override
    public boolean checkSupported() {
        return true;
    }
}
