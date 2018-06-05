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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;

import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.utils.TipDialog;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 10:47
 * 描述 ：
 **/
public class AliPayClient extends BaseClient {
    private static final String TAG = "AliPayClient";
    private static final String RESULT_SUCCESS_ALI = "9000";

    private boolean isInstalled = true;

    public AliPayClient() {

    }

    public AliPayClient(String publicKey) {
        super(publicKey);
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
                String code = result.get("resultStatus");
                result.remove("resultStatus");
                result.put("code", code);
                if(RESULT_SUCCESS_ALI.equalsIgnoreCase(code)) { // 验签;同步无需验签
//                    if(checkSign(result.get("result"))){
                        result.put("code", RESULT_SUCCESS);
//                    }
                }

                callback.onResultReceived(result);
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

    @Override
    public boolean checkSign(String response) {

        try {
            JSONObject jsonResult = new JSONObject(response);
            String data = jsonResult.getString("alipay_trade_app_pay_response");
            String sign = jsonResult.getString("sign");
            String signType = jsonResult.getString("sign_type");
            String charSet = jsonResult.getString("charset");
            Signature signer = null;
            if("RSA".equalsIgnoreCase(signType)) {
                signer = Signature.getInstance("SHA1WithRSA");
            }else if("RSA2".equalsIgnoreCase(signType)) {
                signer = Signature.getInstance("SHA256WithRSA");
            }
            if(signer != null) {
                signer.initVerify(key);
                signer.update(TextUtils.isEmpty(charSet) ? data.getBytes() : data.getBytes(charSet));
                return signer.verify(TextUtils.isEmpty(charSet) ? sign.getBytes() : sign.getBytes(charSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
}
