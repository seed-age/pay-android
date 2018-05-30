package cc.seedland.inf.pay.cashier;

import android.text.TextUtils;

import java.util.Map;
import java.util.TreeMap;

import cc.seedland.inf.pay.PayHome;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/29 09:00
 * 描述 ：
 **/
public final class TradeParamsBuilder {

    private Map<String, String> params = new TreeMap<>();

    public TradeParamsBuilder() {
        params.put("client_type", String.valueOf(0)); // 使用手机请求时固定为0，表示APP类型
        params.put("channel_id", PayHome.getChannelId());
        params.put("currency", "CNY");  // 默认币种为人民币
    }

    public TradeParamsBuilder merchantId(String id) {
        params.put("merchant_id", id);
        return this;
    }

    public TradeParamsBuilder tradeNo(String no) {
        params.put("trade_no", no);
        return this;
    }

    public TradeParamsBuilder productName(String name) {
        params.put("product_name", name);
        return this;
    }

    public TradeParamsBuilder currency(String currency) {
        params.put("currency", TextUtils.isEmpty(currency) ? "CNY" : currency);
        return this;
    }

    public TradeParamsBuilder orderAmount(long amount) {
        params.put("order_amount", String.valueOf(amount));
        return this;
    }

    public TradeParamsBuilder buyerName(String buyer) {
        params.put("buyer_name", buyer);
        return this;
    }

    public TradeParamsBuilder mobile(String mobile) {
        params.put("mobile", mobile);
        return this;
    }

    public Map<String, String> build() {
        return params;
    }
}
