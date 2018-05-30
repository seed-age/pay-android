package cc.seedland.inf.pay.cashier;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.cashier.PayMethodBean.MethodItemBean;

import cc.seedland.inf.corework.mvp.BaseActivity;
import cc.seedland.inf.pay.R;
import cc.seedland.inf.pay.paying.PayingActivity;
import cc.seedland.inf.pay.utils.IconLoader;
import cc.seedland.inf.pay.widget.RadioGroupEx;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:09
 * 描述 ：
 **/
public class CashierActivity extends BaseActivity<CashierContract.View, CashierPresenter> implements CashierContract.View, RadioGroupEx.OnCheckedChangeListener {

    public static final String EXTRA_KEY_PAY_METHODS = "methods";
    public static final String EXTRA_KEY_TRADE = "trade";

    private static final int REQUEST_CODE_PAYING = 8108;

    private RadioGroupEx methodContainer;
    public MethodItemBean payItem;         // 当前选择的支付方式

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cachier;
    }

    @Override
    protected CashierPresenter createPresenter(CashierContract.View view) {
        return new CashierPresenter(this);
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

        final TreeMap<String, String> trade = new TreeMap<> ((Map<String, String>)getIntent().getExtras().get(EXTRA_KEY_TRADE));
        findViewById(R.id.cashier_btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.pay(payItem, trade);
            }
        });

        // 初始化支付方式
        methodContainer = findViewById(R.id.cashier_container_method);
        loadMethods(methodContainer, getIntent().<MethodItemBean>getParcelableArrayListExtra(EXTRA_KEY_PAY_METHODS));
        presenter.getDefaultPay();

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

    public void loadMethods(RadioGroupEx container, ArrayList<MethodItemBean> methods) {
        if(methods != null) {
            container.setOnCheckedChangeListener(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            int id = 1;
            for(MethodItemBean method : methods) {
                View item = inflater.inflate(R.layout.item_pay_method, container, false);
                TextView nameV = item.findViewById(R.id.method_txv_name);
                nameV.setText(method.name);
                TextView tipV = item.findViewById(R.id.method_txv_tip);
                tipV.setText(method.toast);
                RadioButton rdb = item.findViewWithTag("rdb");
                rdb.setId(id++);
                ImageView imv = item.findViewById(R.id.method_imv);
                IconLoader.loadIcon(imv, method.icon);
                item.setTag(method);
                container.addView(item);
            }
        }
    }

    @Override
    public void showError(int code, String msg) {

    }

    @Override
    public void onCheckedChanged(RadioGroupEx group, int checkedId) {
        View child = group.findViewById(checkedId);
        if(child != null) {
            View item = (View) child.getParent();
            payItem = (MethodItemBean)item.getTag();
        }
        presenter.setDefaultPay(checkedId);
    }

    @Override
    public void initDefaultPay(int methodId) {
        methodContainer.check(methodId);
    }

    @Override
    public void showPay(final String method, final String orderInfo) {
        Intent i = new Intent(CashierActivity.this, PayingActivity.class);
        i.putExtra(PayingActivity.EXTRA_KEY_METHOD, method);
        i.putExtra(PayingActivity.EXTRA_KEY_ORDER, orderInfo);
        startActivityForResult(i, REQUEST_CODE_PAYING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_PAYING:
                if(resultCode == Activity.RESULT_OK) {
                    finish();
                }
                break;
        }
    }
}
