package cc.seedland.inf.pay.factory;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import cc.seedland.inf.network.GsonHolder;
import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.utils.TipDialog;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 10:47
 * 描述 ：
 **/
public class AliPayClient implements IPayClient {
    private static final String TAG = "AliPayClient";
    private static final String RESULT_SUCCESS_ALI = "9000";

    private boolean isInstalled = true;

    public AliPayClient() {

    }

    @Override
    public void init(AppCompatActivity activity) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
        isInstalled = componentName != null;
    }

    @Override
    public void showPay(final AppCompatActivity activity, String order, final IPayResultCallback callback) {
        PayTask alipay = new PayTask(activity);
        final Map<String, String> result = alipay.payV2(order,true);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> ret = new HashMap<>();
                String code = result.get("resultStatus");
                ret.put("code", RESULT_SUCCESS_ALI.equalsIgnoreCase(code) ? RESULT_SUCCESS : code);
                try {
                    JSONObject response = new JSONObject(result.get("result"));
                    ret.put("msg", response.optJSONObject("alipay_trade_app_pay_response").optString("msg"));

                } catch (Exception e) {
                    e.printStackTrace();
                    ret.put("msg", result.get("memo"));
                }
                ret.put("raw", GsonHolder.getInstance().toJson(result));
                callback.onResultReceived(ret);
            }
        });
    }

    @Override
    public void supportPay(AppCompatActivity activity) {
        Log.i("seedpay", "AliPay has supported itself");
    }

    @Override
    public boolean checkSupported() {
        return isInstalled;
    }
}
