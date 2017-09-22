package com.haokan.screen.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;

public class ToastManager {
	public static void showShort(Context c, String t) {
        if (c == null) {
            return;
        }
        Toast.makeText(c, t, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context c, int id) {
        if (c == null) {
            return;
        }
        Toast.makeText(c, id, Toast.LENGTH_SHORT).show();
    }

    public static void showCenter(Context c, String str) {
        if (c == null) {
            return;
        }
        Toast toast = Toast.makeText(c, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public static void showFollowToast(Context c, String toastStr) {
        if (c == null) {
            return;
        }
        View layout = LayoutInflater.from(c).inflate(R.layout.layout_toast_subtag,null);
        TextView title = (TextView) layout.findViewById(R.id.tv_title);
        title.setText(toastStr);
        Toast toast = new Toast(c);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showFollowToast(Context c, int strId) {
        if (c == null) {
            return;
        }
        View layout = LayoutInflater.from(c).inflate(R.layout.layout_toast_subtag,null);
        TextView title = (TextView) layout.findViewById(R.id.tv_title);
        title.setText(strId);
        Toast toast = new Toast(c);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showCenterToastForLockScreen(Context c, View rootView, int strId) {
        c=c.getApplicationContext();
        View layout = LayoutInflater.from(c).inflate(R.layout.layout_toast_subtag,null);
        TextView title = (TextView) layout.findViewById(R.id.tv_title);
        title.setText(strId);
//        final PopupWindow window = new PopupWindow(layout, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
//        window.showAtLocation(rootView, Gravity.CENTER, 0, 0);
//        App.mMainHanlder.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                window.dismiss();
//            }
//        }, 1200);
        Toast.makeText(c,""+title.getText(),Toast.LENGTH_SHORT).show();
    }

    public static void showCenterToastForLockScreen(Context c, View rootView, String msg) {
        c=c.getApplicationContext();
        View layout = LayoutInflater.from(c).inflate(R.layout.layout_toast_subtag,null);
        TextView title = (TextView) layout.findViewById(R.id.tv_title);
        title.setText(msg);
//        final PopupWindow window = new PopupWindow(layout, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
//        window.showAtLocation(rootView, Gravity.CENTER, 0, 0);
//        App.mMainHanlder.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                window.dismiss();
//            }
//        }, 1200);
      Toast.makeText(c,""+msg,Toast.LENGTH_SHORT).show();
    }
}
