package cc.seedland.inf.pay.paying;

import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.Map;

import cc.seedland.inf.corework.mvp.IBaseView;
import cc.seedland.inf.pay.cashier.PayMethodBean;
import cc.seedland.inf.pay.factory.IPayResultCallback;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/30 09:00
 * 描述 ：
 **/
public interface PayingContract {
    interface View extends IBaseView {
        void showPay(String order);
        void showSuccess();
        void showFailed();
        void showWaiting();
    }

    interface Presenter {
        void callPay(String orderInfo);
        void handleResult(Map<String, String> result);
    }
}
