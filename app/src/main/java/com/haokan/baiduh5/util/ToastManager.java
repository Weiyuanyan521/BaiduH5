package com.haokan.baiduh5.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastManager {
	public static void showShort(Context c, String t) {
        if (c == null) {
            return;
        }
        c = c.getApplicationContext();
        Toast.makeText(c, t, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context c, int id) {
        if (c == null) {
            return;
        }
        c = c.getApplicationContext();
        Toast.makeText(c, id, Toast.LENGTH_SHORT).show();
    }

    public static void showCenter(Context c, String str) {
        if (c == null) {
            return;
        }
        c = c.getApplicationContext();
        Toast toast = Toast.makeText(c, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
