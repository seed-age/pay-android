package cc.seedland.inf.pay.cashier;

import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.Map;

import cc.seedland.inf.network.JsonCallback;
import cc.seedland.inf.network.Networkit;
import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.R;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/28 16:53
 * 描述 ：
 **/
class CashierModel {

    public static final int ERROR_CODE_METHODS_NONE = R.string.cashier_error_no_methods_supported;

    public CashierModel() {

    }

    public void preparePay(String method, Map<String, String> params, JsonCallback callback) {
        OkGo.<JSONObject>post(Networkit.generateFullUrl("/unipay/rest/1.0/pay?pay_type=").concat(method))
                .tag("seedland")
                .upJson(new JSONObject(params).toString())
                .execute(callback);
    }

    public void setDefaultPayMethod(int payMethodId) {
        PayHome.getInstance().getPayConfigs().edit().putInt("default_pay", payMethodId).commit();
    }

    public int getDefaultPayMethod() {
        return PayHome.getInstance().getPayConfigs().getInt("default_pay", 0);
    }

}
