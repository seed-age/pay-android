package cc.seedland.inf.pay.cashier;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/29 17:46
 * 描述 ：业务子订单
 **/
public class TradeItemBuilder {
    private List<Map<String, String>> params = new ArrayList<>();
    private Map<String, String> currentParam;

    public TradeItemBuilder begin() {
        if(currentParam != null) {
            throw new IllegalStateException("a trade item has begun, finish it first");
        }
        currentParam = new HashMap<>();
        return this;
    }

    public TradeItemBuilder unitTradeNo(String value) {
        currentParam.put("unit_trade_no", value);
        return this;
    }

    public TradeItemBuilder productName(String value) {
        currentParam.put("product_name", value);
        return this;
    }

    public TradeItemBuilder unitPrice(int value) {
        currentParam.put("unit_price", String.valueOf(value));
        return this;
    }

    public TradeItemBuilder productAmount(int value) {
        currentParam.put("product_amount", String.valueOf(value));
        return this;
    }

    public TradeItemBuilder subTotal(int value) {
        currentParam.put("subtotal", String.valueOf(value));
        return this;
    }

    public TradeItemBuilder discountDetail(String value) {
        currentParam.put("discount_detail", value);
        return this;
    }

    public TradeItemBuilder discountableAmount(String value) {
        currentParam.put("discountable_amount", value);
        return this;
    }

    public TradeItemBuilder end() {
        params.add(currentParam);
        currentParam = null;
        return this;
    }

    public String build() {
        return new JSONArray(params).toString();
    }
}
