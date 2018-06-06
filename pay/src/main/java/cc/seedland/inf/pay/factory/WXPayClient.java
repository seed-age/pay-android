package cc.seedland.inf.pay.factory;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import cc.seedland.inf.network.GsonHolder;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/31 10:48
 * 描述 ：
 **/
public class WXPayClient implements IPayClient, IWXAPIEventHandler {
    private IWXAPI api;
    private IPayResultCallback callback;

    private boolean isInstalled = true;
    private Intent intent;

    @Override
    public void init(AppCompatActivity activity) {
        api = WXAPIFactory.createWXAPI(activity, null);
        isInstalled = api.isWXAppInstalled() && api.isWXAppSupportAPI();
        intent = activity.getIntent();
        api.handleIntent(intent, this);
    }

    @Override
    public void showPay(AppCompatActivity activity, String order, IPayResultCallback callback) {
        this.callback = callback;
        String[] paramsArray = order.split("&");
        Map<String, String> orderParams = new HashMap<>();
        for(String param : paramsArray) {
            String[] paramPair = param.split("=");
            orderParams.put(paramPair[0], paramPair[1]);
        }

        String appId = orderParams.get("appid");
//        api = WXAPIFactory.createWXAPI(activity, appId, false);
        api.registerApp(appId);
//        api.handleIntent(intent, this);

        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = orderParams.get("partnerid");
        request.prepayId= Uri.decode(orderParams.get("prepayid"));
        request.packageValue = orderParams.get("package");
        request.nonceStr= orderParams.get("noncestr");
        request.timeStamp= orderParams.get("timestamp");
        request.sign= orderParams.get("sign");
        api.sendReq(request);
    }

    @Override
    public void supportPay(AppCompatActivity activity) {
        api.handleIntent(activity.getIntent(), this);
    }

    @Override
    public boolean checkSupported() {
        return isInstalled;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Map<String, String> result = new HashMap<>();
        result.put("code", String.valueOf(resp.errCode));
        result.put("msg", resp.errStr);
        result.put("raw", GsonHolder.getInstance().toJson(resp));
        callback.onResultReceived(result);
    }
}
