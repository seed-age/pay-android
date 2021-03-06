package cc.seedland.inf.pay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cc.seedland.inf.network.JsonCallback;
import cc.seedland.inf.network.Networkit;
import cc.seedland.inf.pay.cashier.CashierActivity;
import cc.seedland.inf.pay.cashier.PayMethodBean;
import cc.seedland.inf.pay.utils.DialogUtil;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/23 15:29
 * 描述 ：
 **/
public class PayHome {

    public static final int REQUEST_CODE_PAY = 8888;

    private static final PayHome INSTANCE = new PayHome();
    private static Application APP;
    private static String CHANNEL_ID;

    private SharedPreferences prefs;

    private PayHome() {

    }

    public static void init(Application app) {
        if(APP == null) {
            APP = app;
            String channel = app.getString(R.string.channel);
            String key = app.getString(R.string.key);
            Networkit.init(app, channel, key);
            CHANNEL_ID = app.getString(R.string.channel_id);
        }
    }

    public static PayHome getInstance() {
        return INSTANCE;
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }

    public final void openCashier(final TreeMap<String, String> trade, final Context context) {

        DialogUtil.showLoading(context);
        OkGo.<JSONObject>get(Networkit.generateFullUrl("/unipay/rest/1.0/pay/supports"))
                .params("channel_id", CHANNEL_ID)
                .params("merchant_id", trade.get("merchant_id"))
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(Response<JSONObject> response) {
                        Intent i = new Intent(context, CashierActivity.class);
                        i.putParcelableArrayListExtra(CashierActivity.EXTRA_KEY_PAY_METHODS, PayMethodBean.fromJson(response.body()).methods);
                        trade.put("channel_id", CHANNEL_ID);
                        trade.put("client_type", String.valueOf(0));
                        i.putExtra(CashierActivity.EXTRA_KEY_TRADE, trade);
                        if(context instanceof Activity) {
                            ((Activity)context).startActivityForResult(i, REQUEST_CODE_PAY);
                        }else {
                            context.startActivity(i);
                        }
                    }
                    @Override
                    public void onError(Response<JSONObject> response) {
                        super.onError(response);

                        String msg = "unknown error";
                        if(response != null && response.getException() != null) {
                            msg = response.getException().getMessage();
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        DialogUtil.hideLoading();
                    }
                });
    }

    private void openSandbox(boolean sandbox) {
        EnvUtils.setEnv(sandbox ? EnvUtils.EnvEnum.SANDBOX : EnvUtils.EnvEnum.ONLINE);
    }

    public SharedPreferences getPayConfigs() {
        if(prefs == null && APP != null) {
            String name = APP.getPackageName();
            prefs = APP.getSharedPreferences(name, Context.MODE_PRIVATE);
        }

        return prefs;
    }

}
