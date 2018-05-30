package cc.seedland.inf.pay.paying;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import cc.seedland.inf.corework.mvp.BasePresenter;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/30 09:10
 * 描述 ：
 **/
public class PayingPresenter extends BasePresenter<PayingContract.View> implements PayingContract.Presenter {

    public PayingPresenter(PayingContract.View view) {
        super(view);
    }

    @Override
    public void init() {

    }

    @Override
    public void callPay(final String method, final String orderInfo) {
        if(method == null || orderInfo == null) {
            return;
        }
        if(getView() != null) {
            getView().showWaiting();
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if(method.equals("wxpay.app")) {
                        String[] paramsArray = orderInfo.split("&");
                        Map<String, String> orderParams = new HashMap<>();
                        for(String param : paramsArray) {
                            String[] paramPair = param.split("=");
                            orderParams.put(paramPair[0], paramPair[1]);
                        }
                        getView().showWXPay(orderParams);
                    }else if(method.equals("alipay.app")) {
                        getView().showAliPay(orderInfo);
                    }
                }
            });
        }
    }

    @Override
    public void handleResult(Map<String, String> result) {
        if(getView() == null) {
            return;
        }
        String method = result.get("method");
        if(method.equals("alipay.app")) {
            String code = result.get("resultStatus");
            if(code.equals("9000")) {
                getView().showSuccess();
            }else {
                getView().showFailed();
            }
        }else if(method.equals("wxpay.app")) {
            String code = result.get("code");
            if(code.equals("0")) {
                getView().showSuccess();
            }else {
                getView().showFailed();
            }
        }
    }
}
