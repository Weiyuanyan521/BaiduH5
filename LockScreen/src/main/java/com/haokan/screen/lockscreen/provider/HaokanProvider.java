package com.haokan.screen.lockscreen.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.haokan.screen.database.MyDatabaseHelper;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.io.File;

/**
 * Created by wangzixu on 2017/3/13.
 */
public class HaokanProvider extends ContentProvider {
    private static final String TAG = "LocalImageProvider";

    public static final String ProviderUri = "content://" + Values.PACKAGE_NAME + ".haokan.provider/";

    public static final String TABLE_LOCKCOLLECTION = "tablelockcollection";
    public static final Uri URI_PROVIDER_LOCK_COLLECT = Uri.parse(ProviderUri + TABLE_LOCKCOLLECTION);

    public static final String TABLE_WIFI_REQUEST = "tablewifirequest";
    public static final Uri URI_PROVIDER_WIFI_REQUEST = Uri.parse(ProviderUri + TABLE_WIFI_REQUEST);

    /**
     * 自动更新offline图片的时间
     * 如果要携带时间过来, 请用这种格式<br/>
     * ContentValues 中 time属性
     */
    public static final String TABLE_OFFLINE_AUTO_SWITCH_TIME = "offlineswitchtime";
    public static final Uri  URI_PROVIDER_OFFLINE_AUTO_SWITCH_TIME = Uri.parse(ProviderUri + TABLE_OFFLINE_AUTO_SWITCH_TIME);

    /**
     * 自动更新offline图片的开关
     * 请携带boolean变量过来, 请用这种格式<br/>
     * ContentValues 中 switch
     */
    public static final String TABLE_OFFLINE_AUTO_SWITCH = "offlineswitch";
    public static final Uri URI_PROVIDER_OFFLINE_AUTO_SWITCH = Uri.parse(ProviderUri + TABLE_OFFLINE_AUTO_SWITCH);

    /**
     * 初次被systemui反射时会设置每日的自动更新时间, 记下这个时间, 以备后续使用和测试
     * ContentValues 中 time
     */
    public static final String TABLE_OFFLINE_AUTO_SWITCH_FIRST_TIME = "offlinesfirsttime";
    public static final Uri URI_PROVIDER_OFFLINE_AUTO_SWITCH_FIRST_TIME = Uri.parse(ProviderUri + TABLE_OFFLINE_AUTO_SWITCH_FIRST_TIME);

    /**
     * app的did信息, 因为在systemui端无法访问app的sharedpreference, 无法直接读取app生成的did
     * 而systemui端和app显然要用同一个did等唯一表示信息
     */
    public static final String TABLE_DIDINFO = "didinfo";
    public static final Uri URI_PROVIDER_DIDINFO = Uri.parse(ProviderUri + TABLE_DIDINFO);

    /**
     * 点击换一换允许使用4g流量时
     */
    public static final String TABLE_ALLOW_MOBILE_NET= "allowmobilenet";
    public static final Uri URI_PROVIDER_ALLOW_MOBLILE_NET = Uri.parse(ProviderUri + TABLE_ALLOW_MOBILE_NET);

    /**
     * 更新图片时是否拉起自动推荐图片
     * 请携带boolean变量过来, 请用这种格式<br/>
     * ContentValues 中 switch
     */
    public static final String TABLE_OFFLINE_AUTO_RECOM_SWITCH = "offlineautoremswitch";
    public static final Uri URI_PROVIDER_OFFLINE_AUTO_RECOM_SWITCH = Uri.parse(ProviderUri + TABLE_OFFLINE_AUTO_RECOM_SWITCH);

    /**
     * 离线wifi图片自动更新--可有推荐调用后时间存储
     */
    public static final String TABLE_OFFLINE_AUTOUP_TIME = "offlineautoupwifitime";
    public static final Uri URI_PROVIDER_TABLE_OFFLINE_AUTOUP_TIME = Uri.parse(ProviderUri + TABLE_OFFLINE_AUTOUP_TIME);

    /**
     * 调用换一换接口需要穿page index
     */
    public static final String TABLE_PAGE_CHANGE_INDEX = "offlinechangepageindex";
    public static final Uri URI_PROVIDER_TABLE_PAGE_CHANGE_INDEX = Uri.parse(ProviderUri + TABLE_PAGE_CHANGE_INDEX);

    @Override
    public boolean onCreate() {
        LogHelper.d(TAG, "onCreate ----");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String str = uri.toString();
        LogHelper.d(TAG, "query uri values = " + str);
        if(str.indexOf("language") != -1){
            String lan = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE, Values.LANGUAGE_CODE.KEY_LAN_ENGLISH);
            String country = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Values.PreferenceKey.KEY_SP_SETTING_COUNTRY, Values.COUNTRY_CODE.KEY_COUNTRY_India);
            String row[] = new String[2];
            row[0] = lan;
            row[1] = country;

            String[] columns = new String[] { "language", "country" };
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(row);

            return matrixCursor;
        } else if(str.indexOf(TABLE_LOCKCOLLECTION) != -1){
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getReadableDatabase();
            Cursor cursor = database.query(TABLE_LOCKCOLLECTION, projection, selection, selectionArgs, null, null, sortOrder);
            LogHelper.d(TAG, "query collection cursor = " + cursor);
            return cursor;
        } else if(str.indexOf(TABLE_WIFI_REQUEST) != -1){
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getReadableDatabase();
            Cursor cursor = database.query(TABLE_WIFI_REQUEST, projection, selection, selectionArgs, null, null, sortOrder);
            LogHelper.d(TAG, "query wifi request cursor = " + cursor);
            return cursor;
        } else if(str.indexOf(TABLE_OFFLINE_AUTO_SWITCH_TIME) != -1){//记录当前提示的日期
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            long time = preferences.getLong(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_TIME, 0l);

            Long[] longs = new Long[1];
            longs[0] = time;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_TIME};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(longs);

            return matrixCursor;
        } else if(str.indexOf(TABLE_OFFLINE_AUTO_SWITCH) != -1){//自动更新
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int sw = preferences.getInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH, 1);

            Integer[] off = new Integer[1];
            off[0] = sw==1?1:0;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(off);

            return matrixCursor;
        } else if(str.indexOf(TABLE_ALLOW_MOBILE_NET) != -1){//是否允许4g
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int kssw = preferences.getInt(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, 0);

            Integer[] off = new Integer[1];
            off[0] = kssw==1?1:0;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_SWITCH_WIFI};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(off);

            return matrixCursor;
        } else if(str.indexOf(TABLE_OFFLINE_AUTO_RECOM_SWITCH) != -1){//自动更新是否有推荐
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int sw = preferences.getInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_RECOM_SWITCH, 1);

            Integer[] off = new Integer[1];
            off[0] = sw==1?1:0;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_RECOM_SWITCH};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(off);

            return matrixCursor;
        } else if(str.indexOf(TABLE_OFFLINE_AUTO_SWITCH_FIRST_TIME) != -1){//每日自动更新时间记录
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            long time = preferences.getLong(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_FIRST_TIME, 0l);

            Long[] longs = new Long[1];
            longs[0] = time;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_FIRST_TIME};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(longs);

            return matrixCursor;
        } else if(str.indexOf(TABLE_DIDINFO) != -1){
            String did = CommonUtil.getDid(getContext());

            String[] infos = new String[1];
            infos[0] = did;

            String[] columns = new String[] {"did"};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(infos);

            return matrixCursor;
        } else if(str.indexOf(TABLE_OFFLINE_AUTOUP_TIME) != -1){//调用接口702303完成最近时间
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            long sucessTime = preferences.getLong(Values.PreferenceKey.KEY_SP_AUTO_MANYUP_TODAY_TIME, 0l);

            Long[] longs = new Long[1];
            longs[0] = sucessTime;

            String[] columns = new String[] {Values.PreferenceKey.KEY_SP_AUTO_MANYUP_TODAY_TIME};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            matrixCursor.addRow(longs);

            return matrixCursor;
        }else {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            Cursor cursor = null;
            try {
                SQLiteDatabase database = instance.getReadableDatabase();
                cursor = database.rawQuery("select * from homecache_table", null);
            } catch (Exception e) {
                LogHelper.d(TAG, "query Exception ----");
                e.printStackTrace();
            }
            return cursor;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        LogHelper.d(TAG, "getType uri = " + uri);
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LogHelper.d(TAG, "insert uri values = " + uri + ", " + values);
        String string = uri.toString();
        if (string.indexOf(TABLE_LOCKCOLLECTION) != -1) {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getWritableDatabase();
            long insert = database.insert(TABLE_LOCKCOLLECTION, null, values);
            LogHelper.d(TAG, "insert collection result = " + insert);
            getContext().getContentResolver().notifyChange(URI_PROVIDER_LOCK_COLLECT, null);
        } else if (string.indexOf(TABLE_WIFI_REQUEST) != -1) {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getWritableDatabase();
            long insert = database.insert(TABLE_WIFI_REQUEST, null, values);
            LogHelper.d(TAG, "insert wifi request result = " + insert);
        } else if (string.indexOf(TABLE_OFFLINE_AUTO_SWITCH_TIME) != -1) {
            long time;

            try {
                time = values.getAsLong("time");
            } catch (Exception e) {
                e.printStackTrace();
                time = System.currentTimeMillis();
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putLong(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_TIME, time).apply();
        } else if (string.indexOf(TABLE_OFFLINE_AUTO_SWITCH) != -1) {
            if (values != null) {
                int on = values.getAsInteger("switch");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH, on).apply();
            }
        } else if (string.indexOf(TABLE_OFFLINE_AUTO_RECOM_SWITCH) != -1) {//自动更新-推荐
            if (values != null) {
                int on = values.getAsInteger("switch_rem");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_RECOM_SWITCH, on).apply();
            }
        }else if (string.indexOf(TABLE_OFFLINE_AUTO_SWITCH_FIRST_TIME) != -1) {
            long time;

            try {
                time = values.getAsLong("time");
            } catch (Exception e) {
                e.printStackTrace();
                time = System.currentTimeMillis();
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putLong(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH_FIRST_TIME, time).apply();
        }else if(string.indexOf(TABLE_ALLOW_MOBILE_NET) != -1){
            if (values != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, true).apply();
            }
        }else if (string.indexOf(TABLE_OFFLINE_AUTOUP_TIME) != -1) {//调用接口702303完成最近时间插入
            long sucesstime;

            try {
                sucesstime = values.getAsLong("updatetime");
            } catch (Exception e) {
                e.printStackTrace();
                sucesstime = System.currentTimeMillis();
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putLong(Values.PreferenceKey.KEY_SP_AUTO_MANYUP_TODAY_TIME, sucesstime).apply();
        }else if (string.indexOf(TABLE_PAGE_CHANGE_INDEX) != -1) {
            if (values != null) {
                String  on = values.getAsString("page_change_index");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(Values.PreferenceKey.KEY_SP_CHANGE_PAGE_INDEX, on).apply();
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogHelper.d(TAG, "delete uri = " + uri + ", selection = " + selection + ", selectionArgs" + selectionArgs);
        String string = uri.toString();
        if (string.indexOf(TABLE_LOCKCOLLECTION) != -1) {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getWritableDatabase();

            Cursor cursor = database.query(TABLE_LOCKCOLLECTION, new String[]{"image_url"}, selection, selectionArgs, null, null, null);
            LogHelper.d(TAG, "delete collection cursor count = " + cursor.getCount());
            while (cursor.moveToNext()){
                String path = cursor.getString(0);
                File file = new File(path);
                if(file.exists()) {
                    file.delete();
                }
            }

            database.delete(TABLE_LOCKCOLLECTION, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(URI_PROVIDER_LOCK_COLLECT, null);
            LogHelper.d(TAG, "delete collection success");
        } else if (string.indexOf(TABLE_WIFI_REQUEST) != -1) {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getWritableDatabase();

            database.delete(TABLE_WIFI_REQUEST, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(URI_PROVIDER_LOCK_COLLECT, null);
            LogHelper.d(TAG, "delete wifi table success");
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LogHelper.d(TAG, "update uri = " + uri);
        String string = uri.toString();
        if (string.indexOf(TABLE_LOCKCOLLECTION) != -1) {
            MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getContext());
            SQLiteDatabase database = instance.getWritableDatabase();
            int count = database.update(TABLE_LOCKCOLLECTION,values,selection,selectionArgs);
            LogHelper.d(TAG, "update success count = " + count);
            getContext().getContentResolver().notifyChange(URI_PROVIDER_LOCK_COLLECT, null);
            return count;
        }
        return 0;
    }

}
