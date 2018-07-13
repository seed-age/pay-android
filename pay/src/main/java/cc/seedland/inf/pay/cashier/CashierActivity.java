package cc.seedland.inf.pay.cashier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import cc.seedland.inf.corework.permission.OnPermissionCallback;
import cc.seedland.inf.corework.permission.PermissionAgent;
import cc.seedland.inf.pay.PayHome;
import cc.seedland.inf.pay.cashier.PayMethodBean.MethodItemBean;

import cc.seedland.inf.corework.mvp.BaseActivity;
import cc.seedland.inf.pay.R;
import cc.seedland.inf.pay.factory.IPayClient;
import cc.seedland.inf.pay.factory.PayClientFactory;
import cc.seedland.inf.pay.paying.PayingActivity;
import cc.seedland.inf.pay.utils.DialogUtil;
import cc.seedland.inf.pay.utils.IconLoader;
import cc.seedland.inf.pay.utils.TipDialog;
import cc.seedland.inf.pay.widget.RadioGroupEx;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/24 14:09
 * 描述 ：
 **/
public class CashierActivity extends BaseActivity<CashierPresenter> implements CashierContract.View,
        RadioGroupEx.OnCheckedChangeListener, OnPermissionCallback, View.OnClickListener {

    public static final String EXTRA_KEY_PAY_METHODS = "methods";
    public static final String EXTRA_KEY_TRADE = "trade";

    private static final int REQUEST_CODE_PAYING = 8108;

    private RadioGroupEx methodContainer;
    private Button confirmV;
    private LayoutInflater inflater;
    private MethodItemBean payItem;         // 当前选择的支付方式
    private @ColorInt int colorDisable;

    // 权限
    private PermissionAgent agent;
    private static final int REQUEST_CODE_PERMISSION = 0x00099;
    private static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cachier;
    }

    @Override
    protected CashierPresenter createPresenter() {
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
        confirmV = findViewById(R.id.cashier_btn_confirm);
        colorDisable = getResources().getColor(R.color.gray);
        confirmV.getBackground().setColorFilter(colorDisable, PorterDuff.Mode.SRC_ATOP);
        confirmV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.pay(payItem, trade);
            }
        });

        // 初始化金额
        TextView moneyV = findViewById(R.id.cashier_txv_money);
        moneyV.setText(formatMoney(trade));

        // 初始化支付方式
        inflater = LayoutInflater.from(this);
        methodContainer = findViewById(R.id.cashier_container_method);
        methodContainer.setOnCheckedChangeListener(this);
        ArrayList<MethodItemBean> methods = getIntent().getParcelableArrayListExtra(EXTRA_KEY_PAY_METHODS);
        presenter.loadPayMethods(methods);
        presenter.getDefaultPay();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        agent = new PermissionAgent(this, REQUEST_CODE_PERMISSION);
        agent.setCallback(this);
        agent.requestPermission(REQUEST_PERMISSIONS);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            agent.verifyPermissions(grantResults);
        }
    }

    @Override
    public void showLoading() {
        DialogUtil.showLoading(this);
    }

    @Override
    public void hideLoading() {
        DialogUtil.hideLoading();
    }

    @Override
    public void showError(int code, String msg) {
        TipDialog.show(this, getString(code));

    }

    @Override
    public void onCheckedChanged(RadioGroupEx group, int checkedId) {
        View child = group.findViewById(checkedId);
        if(child != null) {
            View item = (View) child.getParent();
            payItem = (MethodItemBean)item.getTag();
        }
        presenter.setDefaultPay(checkedId);
        if(!confirmV.isEnabled()) {
            confirmV.setEnabled(true);
            confirmV.getBackground().clearColorFilter();
        }

    }

    @Override
    public void initDefaultPay(int methodId) {
        methodContainer.check(methodId);
    }

    @Override
    public void showPay(final String method, final String orderInfo) {
        Intent i = new Intent(CashierActivity.this, PayingActivity.class);
        i.putExtra(PayingActivity.EXTRA_KEY_METHOD, payItem);
        i.putExtra(PayingActivity.EXTRA_KEY_ORDER, orderInfo);
        startActivityForResult(i, REQUEST_CODE_PAYING);
    }

    @Override
    public boolean addPayMethod(int id, MethodItemBean method) {
        if(method != null) {
            IPayClient client = PayClientFactory.createPayClient(method.type);
            if(client == null) {
                showToast(getString(R.string.paying_method_not_suppored, method.name));
            }else {
                client.init(this);
                if(client.checkSupported()) {
                    View item = inflater.inflate(R.layout.item_pay_method, methodContainer, false);
                    item.setOnClickListener(this);
                    TextView nameV = item.findViewById(R.id.method_txv_name);
                    nameV.setText(method.name);
                    TextView tipV = item.findViewById(R.id.method_txv_tip);
                    tipV.setText(method.toast);

                    boolean enabled = method.status == 0;
                    RadioButton rdb = item.findViewWithTag("rdb");
                    rdb.setId(id);
                    rdb.setVisibility(enabled ? View.VISIBLE : View.GONE);

                    ImageView imv = item.findViewById(R.id.method_imv);
                    if(!enabled) {
                        imv.setColorFilter(colorDisable);
                    }

                    IconLoader.loadIcon(imv, method.icon);
                    item.setTag(method);
                    methodContainer.addView(item);

                    item.setEnabled(enabled);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_PAYING:
                setResult(resultCode, data);
                if(resultCode == RESULT_OK) {
                    finish();
                }

                break;
        }
    }

    @Override
    public void permissionSuccess(int i) {

    }

    @Override
    public void permissionFail(int i) {

    }

    @Override
    public void onClick(View v) {
        RadioButton rdb = v.findViewWithTag("rdb");
        rdb.performClick();
    }

    private String formatMoney(Map<String, String> trade) {
        BigDecimal moneyCent = new BigDecimal(trade.get("order_amount"));
        BigDecimal scaleYuan = new BigDecimal(100);
        String currency = trade.get("currency");
        String money = moneyCent.divide(scaleYuan, 2, RoundingMode.HALF_UP).toString();
        if("CNY".equals(currency)) {
            money = "¥ " + money;
        }
        return money;
    }
}
