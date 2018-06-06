package cc.seedland.inf.pay.paying;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    private Intent data;


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
        if(method != null) {
            client = PayClientFactory.createPayClient(method.type);
        }

        presenter.callPay(order);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(client != null) {
            client.supportPay(this);
        }


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
        setResult(RESULT_OK, data);
        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_success, 0, 0);
        showV.setText(R.string.paying_success);
        actionV.setText(R.string.paying_action_check);
        actionV.setBackgroundResource(R.drawable.bg_semicircle_end_black_border);
        actionV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName(getPackageName(), "cc.seedland.inf.OrderActivity");
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void showFailed() {
        setResult(RESULT_CANCELED, data);
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

        Bundle args = data.getBundleExtra("result");
        if(args != null) {
            String msg = args.getString("msg");
            showToast(TextUtils.isEmpty(msg) ? getString(R.string.paying_result_error_unknown) : msg);
        }
    }


    @Override
    public void showWaiting() {

        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_waiting, 0, 0);
        showV.setText(R.string.paying_waiting);

        actionV.setText(R.string.paying_tip);
        actionV.setBackgroundResource(0);
        actionV.setOnClickListener(null);
    }

    @Override
    public void prepareForClose(Map<String, String> result) {
        data = new Intent();
        Bundle args = new Bundle();
        if(result != null) {
            args.putString("code", result.get("code"));
            args.putString("msg", result.get("msg"));
            data.putExtra("raw_result", result.get("raw"));
        }
        data.putExtra("result", args);
    }
}
