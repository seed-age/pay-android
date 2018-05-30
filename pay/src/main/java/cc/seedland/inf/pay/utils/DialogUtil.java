package cc.seedland.inf.pay.utils;

import android.content.Context;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/25 13:53
 * 描述 ：
 **/
public class DialogUtil {

    private static LoadingDialog sLoadingDialog;

    private DialogUtil() {

    }

    public static void showLoading(Context context) {
        if(context == null) {
            return;
        }
        hideLoading();
        sLoadingDialog = new LoadingDialog(context);
        sLoadingDialog.show();
    }

    public static void hideLoading() {
        if(sLoadingDialog != null && sLoadingDialog.isShowing()) {
            sLoadingDialog.dismiss();
            sLoadingDialog = null;
        }
    }
}
