package com.haokan.screen.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean_old.DataResponse;

import retrofit2.Response;

public class HttpStatusManager {
    /**检查网络连接状态
     * */
    public static boolean checkNetWorkConnect(Context context) {
        boolean result;
        if (context!=null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            result = netinfo != null && netinfo.isConnected();
            return result;
        }else{
            return false;
        }
    }

    /**
     * 当前是不是wifi链接
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected() && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取网络类型
     *
     * @param mContext
     * @return 0没网 1Wi-Fi 2mobile
     */
    public static int getNetworkType(Context mContext) {
        int type = 0;
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                type = 1;
            } else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                type = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * 检查接口返回的值是否成功
     */
    public static <T> boolean checkResponseSuccess(Response<DataResponse<T>> response) {
        if (response.isSuccessful() && response.body().getCode() == 200) {
            return true;
        } else {
            if (response.body() != null) {
                String resName = "servercode_" + response.body().getCode();
                String message;
                Context context = App.getAppContext();
                int identifier = context.getResources().getIdentifier(resName, "string", context.getPackageName());
                if (identifier != 0) {
                    message = context.getResources().getString(identifier);
                } else {
                    message = context.getResources().getString(R.string.servercode_unknown) + "" + response.body().getCode();
                }
                response.body().setMessage(message);
            }
            return false;
        }
    }

    /**
     * 检查接口返回的值是否成功
     */
    public static <T> DataResponse<T> checkResponseSuccess(DataResponse response) {
        if (response == null) {
            response = new DataResponse<>();
            response.setCode(-100); //代表response为null的错误码
        }
        try {
            if (response.getCode() != 200) {
                String resName = "servercode_" + response.getCode();
                String message;
                Context context = App.getAppContext();
                int identifier = context.getResources().getIdentifier(resName, "string", context.getPackageName());
                if (identifier != 0) {
                    message = context.getResources().getString(identifier);
                } else {
                    message = context.getResources().getString(R.string.servercode_unknown) + "" + response.getCode();
                }
                response.setMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
