package cc.seedland.inf.pay;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Map;
import java.util.TreeMap;

import cc.seedland.inf.network.Networkit;
import cc.seedland.inf.network.SeedCallback;
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

    private static final PayHome INSTANCE = new PayHome();
    private static Application APP;
    private static String CHANNEL_ID;
    private static String HOST;

    private SharedPreferences prefs;
    private PreparePayCallback callback;

    private PayHome() {

    }

    public static void init(Application app) {
        if(APP == null) {
            APP = app;
            String channel = app.getString(R.string.channel);
            String key = app.getString(R.string.key);
            Networkit.init(app, channel, key);
            CHANNEL_ID = app.getString(R.string.channel_id);
            HOST = app.getString(R.string.http_host);
        }
    }

    public static PayHome getInstance() {
        return INSTANCE;
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }

    public final void openCashier(final TreeMap<String, String> trade, final Context context, PreparePayCallback callback) {

        this.callback = callback;
        DialogUtil.showLoading(context);
        OkGo.<PayMethodBean>get(getFullUrl("unipay/rest/1.0/pay/supports"))
                .params("channel_id", CHANNEL_ID)
                .params("merchant_id", trade.get("merchant_id"))
                .tag("seedland")
                .execute(new SeedCallback<PayMethodBean>(PayMethodBean.class) {
                    @Override
                    public void onSuccess(Response<PayMethodBean> response) {
                        super.onSuccess(response);
                        Intent i = new Intent(context, CashierActivity.class);
                        i.putParcelableArrayListExtra(CashierActivity.EXTRA_KEY_PAY_METHODS, response.body().methods);
                        trade.put("channel_id", CHANNEL_ID);
                        trade.put("client_type", String.valueOf(0));
                        i.putExtra(CashierActivity.EXTRA_KEY_TRADE, trade);
                        context.startActivity(i);
                    }
                    @Override
                    public void onError(Response<PayMethodBean> response) {
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

    public SharedPreferences getPayConfigs() {
        if(prefs == null && APP != null) {
            String name = APP.getPackageName();
            prefs = APP.getSharedPreferences(name, Context.MODE_PRIVATE);
        }

        return prefs;
    }

    public PreparePayCallback getCallback() {
        return callback;
    }

    public static String getFullUrl(String path) {
        return HOST.concat(path);
    }


//    public final void startAliPay(final Activity activity) {
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(activity);
//                Map<String, String> result = alipay.payV2(
//                        "_input_charset=\"UTF-8\"" +
//                                "&body=\"商品名称：哈奇礼物\"" +
//                                "&it_b_pay=\"1h\"" +
//                                "&notify_url=\"https://test-open.seedland.cc/unipay/rest/1.0/pay/notify//279e9c823b009a7e9ddca2ff38560952\"" +
//                                "&out_trade_no=\"1101201805240847370456663\"" +
//                                "&partner=\"2088921759575141\"" +
//                                "&pay_method=\"directPay\"" +
//                                "&payment_type=\"1\"" +
//                                "&seller_id=\"2088921759575141\"" +
//                                "&service=\"mobile.securitypay.pay\"" +
//                                "&subject=\"商品名称：哈奇礼物\"" +
//                                "&total_fee=\"999999\"" +
//                                "&sign_type=\"RSA\"" +
//                                "&sign=\"bHXvsCUVYRlSWgy1dfEEQO1EzfBGP%2Fd1%2FBtedBHj8v9fUrZzzFxkF%2FiTIj%2FMSaKuQuOud13RYEu1N2%2BUVlI5Imtzevw2QSv%2B9WnBe8vQ6ubYR7JiYwQyz46QKTYX%2FwJFok7%2BwRFYzBXkcfdBIGl5aIQ3jcaSjKhjPLy3Iw3wq24NLOdZ79mSzMFazJLgzuwnzesgpPDfWLTXrz8ybubPL35xs4iAbL0dQV9jhkN7CmRCfWzhvcXGof6RZQhxEGKqYWq7OLIHOpQ%2B0%2Bec5ln0EsysqOvJbYjRNS7iyWcyVh0Fdxdmqmu2yya%2BTFCXPS1UEJsOeyrj15RhWbOeF9kxsA%3D%3D\""
//                        ,true);
//                Log.e("xuchunlei", result.toString());
//            }
//        };
//        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }
}
