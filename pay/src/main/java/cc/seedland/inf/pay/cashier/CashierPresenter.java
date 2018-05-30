package cc.seedland.inf.pay.cashier;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.model.Response;

import java.util.Map;
import java.util.concurrent.Executors;

import cc.seedland.inf.corework.mvp.BasePresenter;
import cc.seedland.inf.network.SeedCallback;
import cc.seedland.inf.pay.PayHome;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:26
 * 描述 ：
 **/
public class CashierPresenter extends BasePresenter<CashierContract.View> implements CashierContract.Presenter {

    private CashierModel model = new CashierModel();

    public CashierPresenter(CashierContract.View view) {
        super(view);
    }

    @Override
    public void init() {

    }

    @Override
    public void pay(PayMethodBean.MethodItemBean payItem, Map<String, String> tradeParams) {
        if(payItem != null && tradeParams != null && !tradeParams.isEmpty()) {
            final String method = payItem.type;
            model.preparePay(payItem.type, tradeParams, new SeedCallback<PayCallBean>(PayCallBean.class){
                @Override
                public void onSuccess(Response<PayCallBean> response) {
                    super.onSuccess(response);
                    final String orderInfo = response.body().params;
                    if(getView() != null) {
                        getView().showPay(method, orderInfo);
                    }
                }

                @Override
                public void onError(Response<PayCallBean> response) {
                    super.onError(response);
                    String msg = "unknown error";
                    if(response != null && response.getException() != null) {
                        msg = response.getException().getMessage();

                    }
                    if(getView() != null) {
                        getView().showToast(msg);
                    }
                }
            });

//            final String orderInfo = payItem.;   // 订单信息
//
//            Runnable payRunnable = new Runnable() {
//
//                @Override
//                public void run() {
//                    PayTask alipay = new PayTask(DemoActivity.this);
//                    String result = alipay.payV2(orderInfo,true);
//
//                    Message msg = new Message();
//                    msg.what = SDK_PAY_FLAG;
//                    msg.obj = result;
//                    mHandler.sendMessage(msg);
//                }
//            };
//            // 必须异步调用
//            Thread payThread = new Thread(payRunnable);
//            payThread.start();
        }
    }

    @Override
    public void getDefaultPay() {
        if(getView() != null) {
            int payId = model.getDefaultPayMethod();
            if(payId > 0) {
                getView().initDefaultPay(model.getDefaultPayMethod());
            }

        }
    }

    @Override
    public void setDefaultPay(int methodId) {
        model.setDefaultPayMethod(methodId);
    }


}
