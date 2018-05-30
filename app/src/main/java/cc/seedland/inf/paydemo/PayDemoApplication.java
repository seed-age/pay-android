package cc.seedland.inf.paydemo;

import android.app.Application;

import cc.seedland.inf.pay.PayHome;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:35
 * 描述 ：
 **/
public class PayDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PayHome.init(this);
    }
}
