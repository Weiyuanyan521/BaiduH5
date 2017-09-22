package com.haokan.screen.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFormatUtil {

	/**
	 * 获取当前时间
	 */
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date());
	}

	/**
	 * yyyy-MM-dd形式的日期
	 * @param time
	 * @return
     */
    public static String format(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.format(new Date(time));
    }

	/**
	 * yyyy-MM-dd形式的日期
	 * @param time
	 * @return
	 */
	public static String formatForDay(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		return formatter.format(new Date(time));
	}

	/**
	 * yyyy-MM-dd HH:mm:ss形式的日期
	 * @param time
	 * @return
	 */
	public static String formatForSecond(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		return formatter.format(new Date(time));
	}

	public static String getCustomFormatTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
		return formatter.format(new Date(time));
	}

}
