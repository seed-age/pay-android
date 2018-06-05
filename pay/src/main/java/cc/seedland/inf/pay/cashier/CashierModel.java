package cc.seedland.inf.pay.cashier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Map;

import cc.seedland.inf.network.GsonHolder;
import cc.seedland.inf.network.SeedCallback;
import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.R;
import cc.seedland.inf.pay.factory.IPayClient;
import cc.seedland.inf.pay.factory.PayClientFactory;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/28 16:53
 * 描述 ：
 **/
public class CashierModel {

    public static final int ERROR_CODE_METHODS_NONE = R.string.cashier_error_no_methods_supported;

    public CashierModel() {

    }

    public void preparePay(String method, Map<String, String> params, SeedCallback<PayCallBean> callback) {

        OkGo.<PayCallBean>post(PayHome.getFullUrl("unipay/rest/1.0/pay?pay_type=").concat(method))
                .tag("seedland")
                .upJson(GsonHolder.getInstance().toJson(params))
                .execute(callback);
    }

    public void setDefaultPayMethod(int payMethodId) {
        PayHome.getInstance().getPayConfigs().edit().putInt("default_pay", payMethodId).commit();
    }

    public int getDefaultPayMethod() {
        return PayHome.getInstance().getPayConfigs().getInt("default_pay", 0);
    }

}
