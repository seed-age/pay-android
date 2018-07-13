package cc.seedland.inf.pay.cashier;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.seedland.inf.network.BaseBean;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/25 14:15
 * 描述 ：
 **/

public class PayMethodBean extends BaseBean {

    public int expireTime;
    public ArrayList<MethodItemBean> methods;

    public static PayMethodBean fromJson(JSONObject json) {
        PayMethodBean bean = new PayMethodBean();
        bean.expireTime = json.optInt("expire_time");
        JSONArray array = json.optJSONArray("support_pay_type");
        bean.methods = new ArrayList<>();
        for(int i = 0;i < array.length();i++) {
            bean.methods.add(MethodItemBean.fromJson(array.optJSONObject(i)));
        }
        return bean;
    }

    public static class MethodItemBean extends BaseBean implements Parcelable {
        public String type;
        public String name;
        public String toast;
        public int status;
        public String icon;

        public MethodItemBean(JSONObject json) {
            type = json.optString("pay_type");
            name = json.optString("pay_name");
            toast = json.optString("toast");
            status = json.optInt("status");
            icon = json.optString("pay_ico");
        }

        private static MethodItemBean fromJson(JSONObject json) {
            return new MethodItemBean(json);
        }

        protected MethodItemBean(Parcel in) {
            type = in.readString();
            name = in.readString();
            toast = in.readString();
            status = in.readInt();
            icon = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(type);
            dest.writeString(name);
            dest.writeString(toast);
            dest.writeInt(status);
            dest.writeString(icon);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MethodItemBean> CREATOR = new Creator<MethodItemBean>() {
            @Override
            public MethodItemBean createFromParcel(Parcel in) {
                return new MethodItemBean(in);
            }

            @Override
            public MethodItemBean[] newArray(int size) {
                return new MethodItemBean[size];
            }
        };
    }
}
