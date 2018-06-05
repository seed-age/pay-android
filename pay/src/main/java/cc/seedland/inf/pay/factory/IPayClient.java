package cc.seedland.inf.pay.factory;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 10:40
 * 描述 ：
 **/
public interface IPayClient {
    String RESULT_SUCCESS = "0";

    void init(AppCompatActivity activity);

    void showPay(AppCompatActivity activity, String order, IPayResultCallback callback);

    void supportPay(AppCompatActivity activity);

    boolean checkSupported();
}
