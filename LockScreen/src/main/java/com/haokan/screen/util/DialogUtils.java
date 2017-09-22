package com.haokan.screen.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.lockscreen.R;

/**
 * Created by xiefeng on 2017/1/6.
 */

public class DialogUtils {

    public interface OnClickListener {
        void Yes();
        void No();
    }

    public static AlertDialog dialog(Context context, int titleId, int textId, int yesId, int noId, @NonNull final OnClickListener listener) {
        return dialog(context, context.getString(titleId), context.getString(textId), null, context.getString(yesId), context.getString(noId), listener);
    }

    public static AlertDialog dialog(Context context, String title, String desc, View cv, String yes, String no, @NonNull final OnClickListener listener) {
        if (cv == null) {
            cv = LayoutInflater.from(context).inflate(R.layout.dialog_layout_askexternalsd, null);
            TextView tv_permission_text = (TextView) cv.findViewById(R.id.tv_permission_text);
            tv_permission_text.setText(desc);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(cv)
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.No();
                        }
                    }
                }).setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.Yes();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.No();
                }
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
        return alertDialog;
    }
}
