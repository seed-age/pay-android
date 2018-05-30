package cc.seedland.inf.pay.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import cc.seedland.inf.pay.R;

/**
 * 等待对话框
 * Created by xuchunlei on 2017/11/14.
 */

class LoadingDialog extends Dialog {

    LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialogStyle);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
    }

}
