package com.haokan.screen.clipphoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.haokan.screen.view.ZoomImageView;

/**
 * Created by wangzixu on 2016/12/6.
 */
public class ClipPhotoUtil {

    public static Bitmap getClipBitmap(ZoomImageView ziv, int destW, int destH) {
        if (ziv == null) {
            return null;
        }
        Bitmap source = ziv.getOriginalBmp();
        if (source == null) {
            return null;
        }

        Matrix matrix = ziv.getmMatrix();
        float[] f = new float[9];
        matrix.getValues(f);

        RectF clipRect = ziv.getMinLimitRect();
        float scale = f[Matrix.MSCALE_X];
        float transX = clipRect.left - f[Matrix.MTRANS_X];
        float transY = clipRect.top - f[Matrix.MTRANS_Y];
        float offsetX = Math.max(0, transX / scale);
        float offsetY = Math.max(0, transY / scale);
        float clipWidth = clipRect.width() / scale;
        float clipHight = clipRect.height() / scale;

        Bitmap destBitmap = null;
        try {
            Canvas canvas = new Canvas();
            Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
            final Bitmap.Config config = source.getConfig();
            if (config != null) {
                switch (config) {
                    case RGB_565:
                        newConfig = Bitmap.Config.RGB_565;
                        break;
                    case ALPHA_8:
                        newConfig = Bitmap.Config.ALPHA_8;
                        break;
                    // noinspection deprecation
                    case ARGB_4444:
                    case ARGB_8888:
                    default:
                        newConfig = Bitmap.Config.ARGB_8888;
                        break;
                }
            }
            destBitmap = Bitmap.createBitmap(destW, destH, newConfig);
            Rect srcR = new Rect((int)offsetX, (int)offsetY, (int)(offsetX + clipWidth), (int)(offsetY + clipHight));
            RectF dstR = new RectF(0, 0, destW, destH);
            destBitmap.setDensity(source.getDensity());
            destBitmap.setHasAlpha(source.hasAlpha());
            // destBitmap.setPremultiplied(source.isPremultiplied()); // api
            // 19

            canvas.setBitmap(destBitmap);
            canvas.drawBitmap(source, srcR, dstR, null);
            canvas.setBitmap(null);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destBitmap;
    }
}
