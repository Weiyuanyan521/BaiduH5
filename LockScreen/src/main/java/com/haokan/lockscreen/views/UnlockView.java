package com.haokan.lockscreen.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.haokan.lockscreen.R;
import com.haokan.screen.util.DisplayUtil;


public class UnlockView extends ImageView {

    /**
     * Height of the gradient region
     */
    private int mRadius;

    /**
     * the visible region when slide up
     */
    private Rect mDisplayRect = new Rect();

    /**
     * the rect to draw when slide up
     */
    private Rect mBlurRect = new Rect();
    private Rect mBlurRegionInBitmap = new Rect();

    /**
     * the transY canvas will translate when canvas drawing blur region
     */
    private int mBlurRectTransY;

    /**
     * screen width and height
     */
    private int mWidth, mHeight;

    private int mDownY;

    /**
     * deltaY when slide
     */
    private int mDeltaY;

    /**
     * the canvas used to draw displayed view when slide up
     */
    private Canvas mCanvas;

    /**
     * paint used for draw blur region
     */
    private Paint mBlurPaint = new Paint();
    private LinearGradient mLinearGradient;
    private PorterDuffXfermode mXFermode;
    private Context context;

    /**
     * the bitmap used to draw displayed view when slide up
     */
    // private static Bitmap mBlurBitmap;

    /**
     * the image when of this view
     */
    private Bitmap mImageBitmap;

    /**
     * should draw view ourself
     */
    boolean isTouch = false;

    public UnlockView(Context context) {
        super(context);
        this.context = context;
    }

    public UnlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TouchTransparentView);
        try {
            mRadius = a.getDimensionPixelSize(R.styleable.TouchTransparentView_transparent_radius, 100);
        } finally {
            a.recycle();
        }
        init();
    }

    public void init() {
        mWidth = DisplayUtil.screenWidth(context);
        mHeight = DisplayUtil.screenHeight(context);
        mBlurRect.set(0, 0, mWidth, mRadius);
        mLinearGradient = new LinearGradient(0, 0, 0, mRadius, 0xFFFFFFFF, 0x00FFFFFF, TileMode.MIRROR);
        mXFermode = new PorterDuffXfermode(Mode.DST_ATOP);
        mBlurPaint.setShader(mLinearGradient);
        mBlurPaint.setXfermode(mXFermode);
        if (HaokanLockView.mBlurBitmap == null) {
            HaokanLockView.mBlurBitmap = Bitmap.createBitmap(mWidth, mRadius, Config.ARGB_8888);
        }
        mCanvas = new Canvas(HaokanLockView.mBlurBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
        this.mImageBitmap = bm;
        mBitmapH = mImageBitmap.getHeight();
        mBitmapW = mImageBitmap.getWidth();
    }

    private int passType = 0;

    public void setPassType(int passType) {
        this.passType = passType;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        try {

            super.dispatchTouchEvent(event);
            if (passType != 0) {
                return false;
            }
            int action = event.getAction();
            int y = (int) event.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = y;
                    if (mImageBitmap == null) {
                        buildDrawingCache();
                        // setDrawingCacheEnabled(true);
                        if (getDrawingCache() != null) {
                            mImageBitmap = Bitmap.createBitmap(getDrawingCache());
                        }
                        destroyDrawingCache();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    mDeltaY = mDownY - y;
                    if (mDeltaY > 0) {
                        isTouch = true;
                        int foreGroundbottom = mHeight - mRadius / 2 - mDeltaY;
                        calcDisplayRect(foreGroundbottom);
                    } else {
                        isTouch = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mDownY - event.getY() > DisplayUtil.dip2px(context, 150)) {
                        startAnim(mDisplayRect.bottom, 0);
                    } else {
                        startAnim(mDisplayRect.bottom, mHeight);
                    }
                    break;

                default:
                    break;
            }
            invalidate();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 原图biamap的宽高
    private int mBitmapH, mBitmapW;

    // 原图bitmap中要被画的上班部分
    private Rect mTopRectInBitmap = new Rect();

    /**
     * //此效果分两部分，上面是原图清晰的部分，下面是带遮罩效果的由不透明到全透明的国度区域，foreGroundbottom
     * 这个数值代表此两部分分界线
     */
    private void calcDisplayRect(int foreGroundbottom) {
        // 要把原图画到目标屏幕上，分成两部分，上面是不透明部分，下面是透明渐变部分
        // 所以我们要知道4个方框：原图的上半部分方框，原图的半透明部分方框，目标屏幕的上半部分，目标屏幕的下半部分。

        // 目标屏幕要画的两个区域大小基于我们手指上划的距离，传进来的foreGroundbottom代表目标屏幕上面区域的底边
        // 所以可以根据这个值很容易计算出目标屏幕的两个方框，如下：
        // 1，目标屏幕的上边部分
        mDisplayRect.set(0, 0, mWidth, foreGroundbottom);
        // 2，目标屏幕的下半部分
        // 其实就是我们根据透明半径创建的mBlurRect大小， mBlurRect.set(0, 0, mWidth, mRadius)，
        // 我们在画时动态的向下移动canvas即可，移动的距离即：
        mBlurRectTransY = foreGroundbottom;

        // 3，原图的上半部分，原图的宽高为mBitmapH, mBitmapW,左上都是0，右边是宽，我们只需要计算下边就可以，即：
        float bottom = mBitmapH * ((float) mBlurRectTransY / mHeight); // ----根据目标屏幕上半部分的比例，来计算原图大小。
        mTopRectInBitmap.set(0, 0, mBitmapW, (int) bottom);

        // 4,原图的下半部分，我们需要根据目标屏幕中下半部分方框高所占的比例，来计算出原图中半透明区域的高度：
        float radiusInBitmap = mBitmapH * ((float) mRadius / mHeight);
        mBlurRegionInBitmap.set(0, (int) bottom, mBitmapW, (int) (bottom + radiusInBitmap));
    }

    private void startAnim(int start, int end) {
        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        int d = (Math.min(300, mDisplayRect.bottom >> 2) > 0) ? Math.min(300, mDisplayRect.bottom >> 2) : 0;
        anim.setDuration(d);
        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int foreGroundbottom = (Integer) animation.getAnimatedValue();
                calcDisplayRect(foreGroundbottom);
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isTouch = false;
                // setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isTouch) {
            drawForeGround(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawForeGround(Canvas canvas) {
        canvas.drawBitmap(mImageBitmap, mTopRectInBitmap, mDisplayRect, null);

        mCanvas.drawBitmap(mImageBitmap, mBlurRegionInBitmap, mBlurRect, null);
        mCanvas.drawRect(mBlurRect, mBlurPaint);

        canvas.save();
        canvas.translate(0, mBlurRectTransY);

        canvas.drawBitmap(HaokanLockView.mBlurBitmap, 0, 0, null);
        canvas.restore();
    }
}
