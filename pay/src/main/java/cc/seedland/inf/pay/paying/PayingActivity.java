package cc.seedland.inf.pay.paying;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import cc.seedland.inf.corework.mvp.BaseActivity;
import cc.seedland.inf.pay.R;
import cc.seedland.inf.pay.cashier.PayMethodBean.MethodItemBean;
import cc.seedland.inf.pay.factory.IPayClient;
import cc.seedland.inf.pay.factory.IPayResultCallback;
import cc.seedland.inf.pay.factory.PayClientFactory;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/30 08:58
 * 描述 ：
 **/
public class PayingActivity extends BaseActivity<PayingContract.View, PayingPresenter> implements PayingContract.View {

    public static final String EXTRA_KEY_METHOD = "method";
    public static final String EXTRA_KEY_ORDER = "order";

    // view
    private TextView showV;
    private TextView actionV;

    private MethodItemBean method;
    private String order;
    private IPayClient client;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_paying;
    }

    @Override
    protected PayingPresenter createPresenter(PayingContract.View view) {
        return new PayingPresenter(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ic_back);

        TextView titleV = findViewById(R.id.txv_title);
        titleV.setText(R.string.cashier_title);

        showV = findViewById(R.id.paying_txv_show);
        actionV = findViewById(R.id.paying_txv_action);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        method = getIntent().getParcelableExtra(EXTRA_KEY_METHOD);
        order = getIntent().getStringExtra(EXTRA_KEY_ORDER);
        client = PayClientFactory.createPayClient(method);
        presenter.callPay(order);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        client.supportPay(this);

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
    public void showError(int i, String s) {

    }

    @Override
    public void showPay(String order) {
        if(client != null) {
            client.showPay(this, order, new IPayResultCallback() {
                @Override
                public void onResultReceived(Map<String, String> result) {
                    presenter.handleResult(result);
                }
            });
        }else { // 服务端配置了不支持的支付方式，会提示该信息
            showToast(getString(R.string.paying_method_not_suppored, method.name));
        }
    }

    @Override
    public void showSuccess() {
        setResult(RESULT_OK);
        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_success, 0, 0);
        showV.setText(R.string.paying_success);
        actionV.setText(R.string.paying_action_check);
        actionV.setBackgroundResource(R.drawable.bg_semicircle_end_black_border);
        actionV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("cc.seedland.inf.PAY");
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void showFailed() {
        setResult(RESULT_CANCELED);
        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_failed, 0, 0);
        showV.setText(R.string.paying_failed);
        actionV.setText(R.string.paying_action_consume);
        actionV.setBackgroundResource(R.drawable.bg_semicircle_end_black_border);
        actionV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callPay(order);
            }
        });
    }


    @Override
    public void showWaiting() {

        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_waiting, 0, 0);
        showV.setText(R.string.paying_waiting);

        actionV.setText(R.string.paying_tip);
        actionV.setBackgroundResource(0);
        actionV.setOnClickListener(null);
    }
}
