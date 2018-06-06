package cc.seedland.inf.pay.paying;

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
    public void callPay(final String orderInfo) {
        if(orderInfo == null) {
            return;
        }
        if(getView() != null) {
            getView().showWaiting();
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    getView().showPay(orderInfo);
                }
            });
        }
    }

    @Override
    public void handleResult(Map<String, String> result) {
        if(getView() == null) {
            return;
        }
        getView().prepareForClose(result);
        if(result.get("code").equals("0")) {
            getView().showSuccess();
        }else {
            getView().showFailed();
        }
    }
}
