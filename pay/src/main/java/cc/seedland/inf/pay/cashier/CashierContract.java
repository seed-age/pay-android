package cc.seedland.inf.pay.cashier;

import java.util.Map;

import cc.seedland.inf.corework.mvp.IBaseView;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:24
 * 描述 ：
 **/
interface CashierContract {

    interface View extends IBaseView {
        void initDefaultPay(int methodId);

        /**
         * 显示支付过程
         * @param method 支付方式
         * @param orderInfo 订单信息
         */
        void showPay(String method, String orderInfo);
    }

    interface Presenter {
        void pay(PayMethodBean.MethodItemBean payItem, Map<String, String> tradeParams);

        void getDefaultPay();

        void setDefaultPay(int methodId);
    }
}
