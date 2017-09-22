package com.haokan.screen.util;

import android.content.Context;
import android.text.TextPaint;
import android.widget.TextView;

import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.bean_old.TagBean;

import java.util.List;

/**
 * 乱序排列图片，排列标签的工具类
 */
public class ImgAndTagWallManager {
    private int mScreenW;

    private int mTagTextSize; //tag的字体大小
    private int mTagTextPaddingH; //左右上下各有的padding，好点击
    private int mTagTextPaddingW; //左右上下各有的padding，好点击
    private int mTagParentMargin; //tag的父容易左右各xxdp margin，用来求tag父容易的宽
    private int mTagBaseW; //tags第一行的左边偏移量
    private int mTagH; //tag的高
    private int mMaxTagW; //tag的最大允许宽度
    private int mTagMarginW; //2个tag之间的水平margin
    private int mTagMarginH; //2个tag之间的竖直margin

    private static ImgAndTagWallManager singInstance;
    public static ImgAndTagWallManager getInstance(Context context) {
        if (singInstance == null) {
            synchronized (ImgAndTagWallManager.class) {
                if (singInstance == null) {
                    singInstance = new ImgAndTagWallManager(context);
                }
            }
        }
        return singInstance;
    }

    public void setTagTextPadding(TextView view) {
        view.setPadding(mTagTextPaddingW, mTagTextPaddingH, mTagTextPaddingW, mTagTextPaddingH);
    }

    public int getTagTextPaddingW() {
        return mTagTextPaddingW;
    }
    public int getTagTextPaddingH() {
        return mTagTextPaddingH;
    }

    private ImgAndTagWallManager(Context context) {
        mScreenW = context.getResources().getDisplayMetrics().widthPixels;

        mTagTextSize = DisplayUtil.sp2px(context, 12);
        mTagTextPaddingH = DisplayUtil.dip2px(context, 4);
        mTagTextPaddingW = DisplayUtil.dip2px(context, 6);
//        mTagBaseW = DisplayUtil.dip2px(context, 18);
        mTagBaseW = 0;
        mTagParentMargin = DisplayUtil.dip2px(context, 15);
        mTagH = DisplayUtil.dip2px(context, 20);
        mMaxTagW = DisplayUtil.dip2px(context, 100);
        mTagMarginW = DisplayUtil.dip2px(context, 10);
        mTagMarginH = DisplayUtil.dip2px(context, 10);
    }

    public void initTagsPosition(List<MainImageBean> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MainImageBean imageBean = list.get(i);
            List<TagBean> tags = imageBean.getTag_info();
            initTagsWallForItem0(tags); //处理标签的位置
        }
    }

    public void initTagsWallForItem0(List<TagBean> tags) {
        if (tags == null || tags.size() == 0) {
            return;
        }
        int rlWidth = mScreenW - mTagParentMargin * 2; //左右各15dp
        int baseW = mTagBaseW;
        int currentTop = 0;
        initTagsWallSingLineScrolled(tags, 2 * mTagTextPaddingW, 2 * mTagTextPaddingH, rlWidth, baseW, currentTop
                , mTagTextSize, mTagTextSize, 0, 0, mTagMarginW, mTagMarginH);
    }

    /**
     * 专门给tag页用的方法
     */
    public void initTagsWallForItem0TagPage(List<TagBean> tags) {
        if (tags == null || tags.size() == 0) {
            return;
        }
        int rlWidth = mScreenW - mTagParentMargin * 2; //左右各15dp
        int baseW = mTagBaseW;
        int currentTop = 0;
        initTagsWallSingLineScrolledForTagPage(tags, 2 * mTagTextPaddingW, 2 * mTagTextPaddingH, rlWidth, baseW, currentTop
                , mTagTextSize, mTagTextSize, 0, 0, mTagMarginW, mTagMarginH);
    }

    public int getTagH() {
        return mTagH;
    }

    /**
     * 标签显示的位置计算，暂时只考虑了水平有drawable情况，竖直没有drawable。
     * @param tags 标签实体类
     * @param textPaddingW 标签的水平padding，左padding+右padding
     * @param textPaddingH 标签的竖直padding，上padding + 下padding
     * @param rlWidth 标签父容器的width
     * @param baseW 第一行tag的左偏移量
     * @param currentTop 第一行tag的顶部起始偏移量
     * @param textSize 字体大小
     * @param textHeight 字体高，可能drawable比较高，所以字高不一定能用textsize
     * @param drawablePadding 水平drawable padding
     * @param drawableWidth 水平drawable宽
     * @param textMarginW 标签的水平margin
     * @param textMarginH 标签的竖直margin
     */
    public void initTagsWall(List<TagBean> tags, int textPaddingW, int textPaddingH
            , int rlWidth, int baseW, int currentTop, int textSize, int textHeight, int drawablePadding, int drawableWidth
            , int textMarginW, int textMarginH) {
        if (tags == null) {
            return;
        }
        int currentLineCount = 0; //当前行有几个标签了，每行至少有一个标签，长过一行，后面省略
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        int minTextWidth = (int) paint.measureText("最小"); //一个字时求出的宽度太小，最小是两个字的宽度
        for (int i = 0; i < tags.size(); i++) {
            TagBean bean = tags.get(i);
            String tag = bean.getTag_name();
            int textW = (int) (paint.measureText(tag) + 0.5f);
            if (textW < minTextWidth) {
                textW = minTextWidth;
            }
            int tagw = textW + textPaddingW + drawablePadding + drawableWidth + textMarginW; //tag有多宽
            bean.setItemWidth(tagw);
            bean.setMarginTop(currentTop);
            bean.setMarginLeft(baseW);
            baseW = baseW + tagw;
            int delta = rlWidth - baseW; //当前行右边还剩下多少空间
            if (delta < 0 && currentLineCount > 0) {//加上此行溢出了，所以要换行。textPadding + 2 * mTagTextSize
                currentLineCount = 0;
                i--;
                currentTop = currentTop + textHeight + textPaddingH + textMarginH;
                baseW = 0;
            } else {
                currentLineCount ++;
            }
        }
    }

    /**
     * 不换行的确定标签位置,并且不可以左右滑动，超出指定宽度的标签左右margin为-100
     * @param tags 标签实体类
     * @param textPaddingW 标签的水平padding，左padding+右padding
     * @param textPaddingH 标签的竖直padding，上padding + 下padding
     * @param rlWidth 标签父容器的width
     * @param baseW 第一行tag的左偏移量
     * @param currentTop 第一行tag的顶部起始偏移量
     * @param textSize 字体大小
     * @param textHeight 字体高，可能drawable比较高，所以字高不一定能用textsize
     * @param drawablePadding 水平drawable padding
     * @param drawableWidth 水平drawable宽
     * @param textMarginW 标签的水平margin
     * @param textMarginH 标签的竖直margin
     */
    public void initTagsWallSingLineNoScroll(List<TagBean> tags, int textPaddingW, int textPaddingH
            , int rlWidth, int baseW, int currentTop, int textSize, int textHeight, int drawablePadding, int drawableWidth
            , int textMarginW, int textMarginH) {
        if (tags == null|| tags.size() == 0) {
            return;
        }
        int currentLineCount = 0; //当前行有几个标签了，每行至少有一个标签，长过一行，后面省略
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        int minTextWidth = (int) paint.measureText("最"); //一个字时求出的宽度太小，最小是两个字的宽度
        int j = 0;
        for (int i = 0; i < tags.size(); i++) {
            TagBean bean = tags.get(i);
            String tag = bean.getTag_name();
            int textW = (int) (paint.measureText(tag) + 0.5f);
            if (textW < minTextWidth) {
                textW = minTextWidth;
            }
            int tagw = textW + textPaddingW + drawablePadding + drawableWidth + textMarginW; //tag有多宽
            bean.setItemWidth(tagw);
            bean.setMarginTop(currentTop);
            bean.setMarginLeft(baseW);
            baseW = baseW + tagw;
            int delta = rlWidth - baseW; //当前行右边还剩下多少空间
            if (delta < -5 && currentLineCount > 0) {//加上此行溢出了，所以要换行。textPadding + 2 * mTagTextSize
                j = i;
                break;
            } else {
                currentLineCount ++;
            }
        }
        if (j != 0) {
            for (int i = j; i < tags.size(); i++) {
                TagBean bean = tags.get(i);
                bean.setMarginTop(-100);
                bean.setMarginLeft(-100);
            }
        }
    }

    /**
     * 不换行的确定标签位置,并且可以左右滑动
     * @param tags 标签实体类
     * @param textPaddingW 标签的水平padding，左padding+右padding
     * @param textPaddingH 标签的竖直padding，上padding + 下padding
     * @param rlWidth 标签父容器的width
     * @param baseW 第一行tag的左偏移量
     * @param currentTop 第一行tag的顶部起始偏移量
     * @param textSize 字体大小
     * @param textHeight 字体高，可能drawable比较高，所以字高不一定能用textsize
     * @param drawablePadding 水平drawable padding
     * @param drawableWidth 水平drawable宽
     * @param textMarginW 标签的水平margin
     * @param textMarginH 标签的竖直margin
     */
    public void initTagsWallSingLineScrolled(List<TagBean> tags, int textPaddingW, int textPaddingH
            , int rlWidth, int baseW, int currentTop, int textSize, int textHeight, int drawablePadding, int drawableWidth
            , int textMarginW, int textMarginH) {
        if (tags == null || tags.size() == 0) {
            return;
        }
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        int minTextWidth = (int) paint.measureText("最小"); //一个字时求出的宽度太小，最小是两个字的宽度
        for (int i = 0; i < tags.size(); i++) {
            TagBean bean = tags.get(i);
            String tag = bean.getTag_name();
            int textW = (int) (paint.measureText(tag) + 0.5f);
            if (textW < minTextWidth) {
                textW = minTextWidth;
            }
            int tagw = textW + textPaddingW + drawablePadding + drawableWidth + textMarginW; //tag有多宽
            bean.setItemWidth(tagw);
            bean.setMarginTop(currentTop);
            bean.setMarginLeft(baseW);
            baseW = baseW + tagw;
        }
    }

    /**
     * 专门给tag页用的方法
     */
    public void initTagsWallSingLineScrolledForTagPage(List<TagBean> tags, int textPaddingW, int textPaddingH
            , int rlWidth, int baseW, int currentTop, int textSize, int textHeight, int drawablePadding, int drawableWidth
            , int textMarginW, int textMarginH) {
        if (tags == null || tags.size() == 0) {
            return;
        }
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        int minTextWidth = (int) paint.measureText("最小"); //一个字时求出的宽度太小，最小是两个字的宽度
        for (int i = 0; i < tags.size(); i++) {
            TagBean bean = tags.get(i);
            String tag = bean.getTag_name();
            int textW = (int) (paint.measureText(tag) + 0.5f);
            if (textW < minTextWidth) {
                textW = minTextWidth;
            }
            int tagw;
            if (i == 0) {
                tagw = textW + textPaddingW*3 + drawablePadding + drawableWidth + textMarginW; //tag有多宽
            } else {
                tagw = textW + textPaddingW + drawablePadding + drawableWidth + textMarginW; //tag有多宽
            }
            bean.setItemWidth(tagw);
            bean.setMarginTop(currentTop);
            bean.setMarginLeft(baseW);
            baseW = baseW + tagw;
        }
    }

    /**
     * 处理tag的位置
     */
    public void processTags(List<MainImageBean> imgs) {
        if (imgs == null || imgs.size() == 0) {
            return;
        }
        for (int i = 0; i < imgs.size(); i++) {
            MainImageBean imageBean = imgs.get(i);
            List<TagBean> tags = imageBean.getTag_info();
            initTagsWallForItem0(tags);
        }
    }
}
