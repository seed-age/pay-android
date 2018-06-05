package cc.seedland.inf.pay.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import cc.seedland.inf.pay.R;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/06/04 12:39
 * 描述 ：
 **/
public class TipDialog extends AlertDialog {


    protected TipDialog(@NonNull Context context) {
        this(context, 0);
    }

    protected TipDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TipDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static void show(final Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(context instanceof AppCompatActivity) {
                            ((AppCompatActivity)context).finish();
                        }
                    }
                })
                .setCancelable(false)
                .create()
                .show();

    }

}
