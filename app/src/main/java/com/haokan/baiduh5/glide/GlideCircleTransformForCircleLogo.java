package com.haokan.baiduh5.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Weidongjian on 2015/7/29.
 */
public class GlideCircleTransformForCircleLogo extends BitmapTransformation {
    public GlideCircleTransformForCircleLogo(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//        LogHelper.d("CircleLogo", "outWidth = " + outWidth + ", outHeight " + outHeight);
        int r = Math.max(outWidth,  outHeight);
        Bitmap result = pool.get(r, r, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        }

        int sw = toTransform.getWidth();
        int sh = toTransform.getHeight();
        int sr = (int) Math.sqrt(sw*sw + sh*sh);
        int w = (r * sw) / sr - 4;
        int h = (r * sh) / sr - 4;

        Canvas canvas = new Canvas(result);
        canvas.save();
        int x = ((r - w) / 2);
        int y = ((r - h) / 2);
        canvas.drawBitmap(toTransform, null, new Rect(x, y, x+w, y+h), null);
        return result;
    }

    @Override public String getId() {
        return getClass().getName();
    }
}