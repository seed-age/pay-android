package cc.seedland.inf.pay.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/25 15:11
 * 描述 ：
 **/
public class IconLoader {

    private IconLoader() {

    }

    public static void loadIcon(final ImageView imv, String url) {

        OkGo.<Bitmap>get(url)
                .tag("external")
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        Log.e("xuchunlei", imv.toString() + ":response--->" + response.getRawCall().toString());
                        imv.setImageBitmap(response.body());
                    }
                });
    }

}
