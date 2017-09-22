package com.haokan.screen.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HaokanTextUtil {

    /**
     * 关键字高亮显示
     *
     * @param target  需要高亮的关键字
     * @param text       需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highlight(final Context context, String text, String target, final View.OnClickListener listener) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        ClickableSpan span;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            //span1 = new ForegroundColorSpan(Color.BLUE);// 需要重复
            span = new Clickable(0, 24) {
                @Override
                public void onClick(View widget) {
                    if (listener != null) {
                        listener.onClick(widget);
                    }
                }
            };

            LogHelper.i("HaokanTextUtil", "highlight m.start() m.end() = " + m.start() + ", " +m.end());
            spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannable;
    }

    public static SpannableStringBuilder getClickableDesc(String content, final String clickString, final clickTextListener listener
            , int clickcolor , int textSize) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(content + clickString);
        int start = content.length();
        ClickableSpan span = new Clickable(clickcolor , textSize) {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    listener.onClick(clickString);
                }
            }
        };
        spannable.setSpan(span, start + 1, clickString.length() + start, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;
    }

    public interface clickTextListener {
        void onClick(String clickString);
    }

    /**
     * 用于截获点击富文本后的事件
     */
    abstract static class Clickable extends ClickableSpan {
        int color;
        int size;
        public Clickable(int color, int textSize) {
            this.color = color;
            this.size = textSize;
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(color);
            ds.setTextSize(size);
            ds.setUnderlineText(true);    //去除超链接的下划线
        }
    }

//    /**
//     * 用于截获点击富文本后的事件
//     */
//    abstract static class Clickable extends ClickableSpan {
//        int size;
//
//        public Clickable(int size) {
//            this.size = size;
//        }
//
//        @Override
//        public void updateDrawState(@NonNull TextPaint ds) {
//            ds.setColor(ds.linkColor);
//            ds.setTextSize(size);
//            ds.setUnderlineText(true);    //去除超链接的下划线
//        }
//    }
}
