package cc.seedland.inf.pay.paying;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import cc.seedland.inf.corework.mvp.BaseActivity;
import cc.seedland.inf.pay.R;
import cc.seedland.inf.pay.utils.DialogUtil;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/30 08:58
 * 描述 ：
 **/
public class PayingActivity extends BaseActivity<PayingContract.View, PayingPresenter> implements PayingContract.View, IWXAPIEventHandler{

    public static final String EXTRA_KEY_METHOD = "method";
    public static final String EXTRA_KEY_ORDER = "order";

    private IWXAPI api;

    // view
    private TextView showV;
    private TextView actionV;

    private String method;
    private String order;

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Map<String, String> result = new HashMap<>();
        result.put("method", "wxpay.app");
        result.put("code", String.valueOf(resp.errCode));
        result.put("msg", resp.errStr);
        presenter.handleResult(result);
    }


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

        method = getIntent().getStringExtra(EXTRA_KEY_METHOD);
        order = getIntent().getStringExtra(EXTRA_KEY_ORDER);
        presenter.callPay(method, order);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // todo 需要区分支付宝
        api.handleIntent(intent, this);
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
    public void showWXPay(Map<String, String> orderInfo) {

        String appId = orderInfo.get("appid");
        api = WXAPIFactory.createWXAPI(this, appId, false);
        api.registerApp(appId);
        api.handleIntent(getIntent(), this);

        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = decode(orderInfo.get("partnerid"));
        request.prepayId= decode(orderInfo.get("prepayid"));
        request.packageValue = decode(orderInfo.get("package"));
        request.nonceStr= decode(orderInfo.get("noncestr"));
        request.timeStamp= decode(orderInfo.get("timestamp"));
        request.sign= decode(orderInfo.get("sign"));
        api.sendReq(request);
    }

    @Override
    public void showAliPay(String orderInfo) {
        PayTask alipay = new PayTask(this);
        final Map<String, String> result = alipay.payV2(orderInfo,true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.put("method", "alipay.app");
                presenter.handleResult(result);
            }
        });
    }

    @Override
    public void showSuccess() {
        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_success, 0, 0);
        showV.setText(R.string.paying_success);
        actionV.setText(R.string.paying_action_check);
        actionV.setBackgroundResource(R.drawable.bg_semicircle_end_black_border);
        actionV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("cc.seedland.inf.PAY");
                setResult(RESULT_OK);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void showFailed() {
        showV.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paying_failed, 0, 0);
        showV.setText(R.string.paying_failed);
        actionV.setText(R.string.paying_action_consume);
        actionV.setBackgroundResource(R.drawable.bg_semicircle_end_black_border);
        actionV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                presenter.callPay(method, order);
                Intent i = new Intent("cc.seedland.inf.PAY");
                setResult(RESULT_OK);
                startActivity(i);
                finish();
            }
        });
    }

    private String decode(String value) {
        return Uri.decode(value);
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
