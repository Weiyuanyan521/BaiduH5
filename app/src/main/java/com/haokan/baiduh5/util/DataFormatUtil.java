package com.haokan.baiduh5.util;

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

    public static String format(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formatter.format(new Date(time));
    }

	public static String getCustomFormatTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
		return formatter.format(new Date(time));
	}

}
