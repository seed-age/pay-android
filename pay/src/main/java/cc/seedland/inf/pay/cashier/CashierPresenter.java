package cc.seedland.inf.pay.cashier;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cc.seedland.inf.corework.mvp.BasePresenter;
import cc.seedland.inf.network.JsonCallback;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:26
 * 描述 ：
 **/
class CashierPresenter extends BasePresenter<CashierContract.View> implements CashierContract.Presenter {

    private CashierModel model = new CashierModel();

    public CashierPresenter(CashierContract.View view) {
        super(view);
    }

    @Override
    public void pay(PayMethodBean.MethodItemBean payItem, Map<String, String> tradeParams) {

        if(getView() == null) {
            return;
        }

        if(getView() != null) {
            getView().showLoading();
        }
        if(payItem != null && tradeParams != null && !tradeParams.isEmpty()) {
            final String method = payItem.type;
            model.preparePay(payItem.type, tradeParams, new JsonCallback(){
                @Override
                public void onSuccess(Response<JSONObject> response) {
                    final String orderInfo = response.body().optString("params");
                    if(getView() != null) {
                        getView().showPay(method, orderInfo);
                    }
                }

                @Override
                public void onError(Response<JSONObject> response) {
                    super.onError(response);
                    String msg = "unknown error";
                    if(response != null && response.getException() != null) {
                        msg = response.getException().getMessage();

                    }
                    if(getView() != null) {
                        getView().showToast(msg);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if(getView() != null) {
                        getView().hideLoading();
                    }
                }
            });
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

    @Override
    public void loadPayMethods(List<PayMethodBean.MethodItemBean> methods) {
        if(methods != null && getView() != null) {
            int id = 1;
            for(PayMethodBean.MethodItemBean method : methods) {
                boolean result = getView().addPayMethod(id, method);
                if(result) {
                    id++;
                }
            }
            if(id == 1) { // 所有支付方式都不支持
                getView().showError(CashierModel.ERROR_CODE_METHODS_NONE, null);
            }
        }
    }


}
