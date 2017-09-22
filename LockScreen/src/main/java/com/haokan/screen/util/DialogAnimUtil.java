package com.haokan.screen.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.haokan.lockscreen.R;

public class DialogAnimUtil {
    public static void dissMissLoadingLayout(Context context, final View mLoadingLayout) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.my_dialog_exit);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadingLayout.post(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.startAnimation(animation);
            }
        });
    }

    public static void showLoadingLayout(Context context, final View mLoadingLayout) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.my_dialog_in);
        mLoadingLayout.post(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.startAnimation(animation);
            }
        });
    }
}
