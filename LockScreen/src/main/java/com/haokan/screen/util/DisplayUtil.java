package com.haokan.screen.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * dp、sp 转换为 px 的工具类
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕真实的宽高，包括虚拟导航栏等
     *
     * @param context
     * @return
     */
    public static Point getRealScreenPoint(Context context) {
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        Point realSize = new Point();

        // since SDK_INT = 1;
        int widthPixels, heightPixels;
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

        realSize.set(widthPixels, heightPixels);
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
//            widthPixels = realSize.x;
//            heightPixels = realSize.y;
            } catch (Exception e) {
                e.printStackTrace();
            }

        return realSize;
    }

    public static int screenWidth(Context context) {
        int width = 1080;
        if (SharePreference.getInstance(context).readInt(Values.SF_SCREEN_WIDTH, 0) == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
                width = dm.widthPixels;
                SharePreference.getInstance(context).saveInt(Values.SF_SCREEN_WIDTH, width);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            width = SharePreference.getInstance(context).readInt(Values.SF_SCREEN_WIDTH, 1080);
        }
        if (width == 0) {
            width = 1080;
        }
        return width;
    }

    public static int screenHeight(Context context) {
        int height = 1920;
        if (SharePreference.getInstance(context).readInt(Values.SF_SCREEN_HEIGHT, 0) == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
                height = dm.heightPixels;
                SharePreference.getInstance(context).saveInt(Values.SF_SCREEN_HEIGHT, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            height = SharePreference.getInstance(context).readInt(Values.SF_SCREEN_HEIGHT, 1920);
        }
        if (height == 0) {
            height = 1920;
        }
        return height;
    }

    /**
     *  处理图片
     * @param bm 所要转换的bitmap
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}