package com.haokan.screen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 大图页底部分享界面用的模糊背景
 */
public class BgImageView extends ImageView {
    private int mTop;
    public BgImageView(Context context) {
        super(context);
    }

    public BgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopEdge(int top) {
        mTop = top;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mTop, getWidth(), getHeight());
        super.onDraw(canvas);
        canvas.restore();
    }
}
