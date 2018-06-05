package cc.seedland.inf.pay.factory;

import java.util.Map;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 11:15
 * 描述 ：
 **/
public interface IPayResultCallback {

    void onResultReceived(Map<String, String> result);
}
