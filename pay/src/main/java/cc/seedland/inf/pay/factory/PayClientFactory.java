package cc.seedland.inf.pay.factory;

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cc.seedland.inf.pay.cashier.PayMethodBean.MethodItemBean;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 10:40
 * 描述 ：
 **/
public class PayClientFactory {
    private static final String TAG = "PayClientFactory";

    public static final String CLIENT_TYPE_ALIPAY = "alipay.app";
    public static final String CLIENT_TYPE_WXPAY = "wxpay.app";

    private static final Map<String, IPayClient> CLIENT_STORE = new HashMap<>();

    public static IPayClient createPayClient(MethodItemBean method) {
        IPayClient client = CLIENT_STORE.get(method.type);

        if(client == null) {
            if(CLIENT_TYPE_ALIPAY.equalsIgnoreCase(method.type)) {
                client = new AliPayClient(method.publicKey);
            }else if(CLIENT_TYPE_WXPAY.equalsIgnoreCase(method.type)) {
                client = new WXPayClient();
            }
            if(client != null) {
                CLIENT_STORE.put(method.type, client);
            }
        }

        return client;
    }
}
