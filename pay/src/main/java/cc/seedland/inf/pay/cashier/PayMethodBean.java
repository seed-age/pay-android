package cc.seedland.inf.pay.cashier;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import cc.seedland.inf.network.BaseBean;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/25 14:15
 * 描述 ：
 **/

public class PayMethodBean extends BaseBean {
    @SerializedName("expire_time")
    public int expireTime;
    @SerializedName("support_pay_type")
    public ArrayList<MethodItemBean> methods;

    public static class MethodItemBean implements Parcelable {
        @SerializedName("pay_type")
        public String type;
        @SerializedName("pay_name")
        public String name;
        public String toast;
        public int status;
        @SerializedName("pay_ico")
        public String icon;

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
