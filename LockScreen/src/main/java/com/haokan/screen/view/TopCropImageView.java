package com.haokan.screen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by haokao on 2016/8/22.
 */
public class TopCropImageView extends ImageView {
    Matrix mMatrix = new Matrix();

    public TopCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }

    public TopCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
    }

    public TopCropImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

//    @Override
//    protected boolean setFrame(int l, int t, int r, int b) {
//        if (getDrawable() == null) {
//            return super.setFrame(l, t, r, b);
//        }
////        Matrix matrix = getImageMatrix();
////        float scaleWidth = getWidth() / (float) getDrawable().getIntrinsicWidth();
////        float scaleHeight = getHeight() / (float) getDrawable().getIntrinsicHeight();
////        float scaleFactor = (scaleWidth > scaleHeight) ? scaleWidth : scaleHeight;
////        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
////        if (scaleFactor == scaleHeight) {
////            float tanslateX = ((getDrawable().getIntrinsicWidth() * scaleFactor) - getWidth()) / 2;
////            matrix.postTranslate(-tanslateX, 0);
////        }
////        setImageMatrix(matrix);
//        initCenterTopMatrix();
//        return super.setFrame(l, t, r, b);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initCenterTopMatrix();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initCenterTopMatrix();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initCenterTopMatrix();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initCenterTopMatrix();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initCenterTopMatrix();
    }

    private void initCenterTopMatrix() {
        if (getDrawable() == null) {
            return;
        }
//        LogHelper.d("TopCropImage", "initCenterTopMatrix is called---- = " + this);
        float scaleWidth = getWidth() / (float) getDrawable().getIntrinsicWidth();
        float scaleHeight = getHeight() / (float) getDrawable().getIntrinsicHeight();
        float scaleFactor = (scaleWidth > scaleHeight) ? scaleWidth : scaleHeight;
        mMatrix.setScale(scaleFactor, scaleFactor, 0, 0);
        if (scaleFactor == scaleHeight) {
            float tanslateX = ((getDrawable().getIntrinsicWidth() * scaleFactor) - getWidth()) / 2;
            mMatrix.postTranslate(-tanslateX, 0);
        }
        setImageMatrix(mMatrix);
    }
}
