package cc.seedland.inf.paydemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.cashier.TradeItemBuilder;
import cc.seedland.inf.pay.cashier.TradeParamsBuilder;
import cc.seedland.inf.paydemo.rsa.SignUtil;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/06/01 16:47
 * 描述 ：
 **/
public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_MERCHANT = "merchant";
    public static final String EXTRA_KEY_SANDBOX_ON = "sandbox";

    private String merchantId;
    private String tradeNo;

    private boolean sandboxFlag;

    private TextView merchantV;
    private TextView tradeNoV;
    private EditText productV;
    private EditText amountV;
    private EditText buyerV;
    private EditText phoneV;

    // 子业务
    private TradeItemBuilder builder;
    private int count = 0;
    private ViewGroup container;
    private TextView subCountV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("购物车");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.cart_pay).setOnClickListener(this);
        findViewById(R.id.cart_sub_add).setOnClickListener(this);

        merchantId = getIntent().getStringExtra(EXTRA_KEY_MERCHANT);
        sandboxFlag = getIntent().getBooleanExtra(EXTRA_KEY_SANDBOX_ON, false);


        merchantV = findViewById(R.id.cart_merchant_id);
        tradeNoV = findViewById(R.id.cart_trade_no);
        productV = findViewById(R.id.cart_product_name);
        amountV = findViewById(R.id.cart_amount);
        buyerV = findViewById(R.id.cart_buyer);
        phoneV = findViewById(R.id.cart_mobile);

        merchantV.setText("商户ID:" + merchantId);
        subCountV = findViewById(R.id.cart_sub_count);
        container = (ViewGroup) merchantV.getParent();
        updateTrade();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cart_sub_add:
                if(count < 5) {
                    TextView subV = new TextView(this);
                    LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    subV.setLayoutParams(paramsV);
                    int sequence = ++count;
                    String subTrade = tradeNo + "-" + sequence;
                    String name = "子业务" + sequence;
                    builder.begin()
                            .unitTradeNo(subTrade)
                            .productName(name)
                            .unitPrice(1)
                            .productAmount(2)
                            .subTotal(2)
                            .end();
                    subV.setText("*" + subTrade + "*" + name + ":" + "单价:" + 1 + "；数量:" + 2 + "；小计:" + 2);
                    container.addView(subV, container.getChildCount() - 1);
                    subCountV.setText("子业务数：" + count);
                }else {
                    Toast.makeText(this, "测试5个子业务就够了哈", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.cart_pay:

                Map<String, String> params = new TradeParamsBuilder()
                        .merchantId(merchantId)
                        .tradeNo(tradeNo)
                        .productName(checkAndGet(productV))
                        .orderAmount(Long.valueOf(checkAndGet(amountV)))
                        .buyerName(checkAndGet(buyerV))
                        .mobile(checkAndGet(phoneV))
                        .tradeSubItem(count == 0 ? "" : builder.build())
                        .build();
                TreeMap<String, String> signParams = SignUtil.signPrivate("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBANun6k8jJy" +
                        "2huFCucfSgIrlurwyetPJcvKyUCYrSKvGT5NjVpsy+N/xm5yiqOzj8XysO2CF1A5Fzn/nk5aDBMdx9BM8vEPMsTbzlrhm3eP67IMG+YBtq" +
                        "U6WI/YX4D2guDxSF/p5lAFuqI7O8l2COJTGwYXo3qqx6E6XVuQ99B+dXAgMBAAECgYEAmIGHHxbrFrWXwPy9RfkA4vpEM2DlhPh6TuAhl+6" +
                        "/vibO1vXP74uKV4YirIs0vyYJ9V1DFkemCJDc26XfALPiJ0igzAmSPWe5dQ7mqBf6BaA1X5S/4+Zx4AvVY4rg5hsSWULctyAzANcNRulE98Wy" +
                        "OdZ3C0Ssf2OHmIXIlbu+92ECQQDvePx7gIZO9jjvp4yr3csM/DbSEvfjsjf5BjTQ6neNb9cBIUVr7AIxxeNDS4dRpOwpToRUAiC7VNlEc/4mgj" +
                        "4ZAkEA6tDOjvtFahdoMD8Qv9gc0Hqfo0QTudF+rb4i9f3yAvm04Ecvsc6XR0tzbQVQSAu+8mHAEkQ7UFgLEOL5KpEe7wJBANECV9uzIYZpgOgq5" +
                        "KxcuIxs1awkwhcJxbCjqhVtj0rzAkUKNP0sz/2BKgniMgkgWL70uKpZ8RePxtHoKzqREoECQQDVp556zLixOpELbSahWFOHgjukw3mrVqoMDngjGa" +
                        "hN+sUQWNVV1OMi9M0WwoH0u/NG+ZhZRootpZ6UA+GxUJAzAkEAoJwZrmetqnuBbWL41PhoLrD3yn2BJDcvWuwkVBUEdZl5IsPhcHPvYFm3f2DI++" +
                        "7dn6Ulfft4vMv0qaNAXwNKhw==", params);

                PayHome.getInstance().openCashier(signParams, this, sandboxFlag);
                updateTrade();
                break;
        }
    }

    private String checkAndGet(EditText e) {
        if(TextUtils.isEmpty(e.getText())) {
            throw new NullPointerException("所有参数不能为空");
        }

        return e.getText().toString();
    }

    private void updateTrade() {
        tradeNo = String.valueOf(System.currentTimeMillis());
        tradeNoV.setText("订单号:" + tradeNo);
        builder = new TradeItemBuilder();
        if(count != 0) {
            int end = container.getChildCount() - 2;
            int start = end - (count -1);
            container.removeViews( start, count);
        }
        count = 0;
        subCountV.setText("子业务数：" + count);
    }
}
