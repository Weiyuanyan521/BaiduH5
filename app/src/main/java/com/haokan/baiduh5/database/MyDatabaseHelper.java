package com.haokan.baiduh5.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.haokan.baiduh5.bean.CollectionBean;
import com.haokan.baiduh5.bean.HistoryRecordBean;
import com.haokan.baiduh5.util.LogHelper;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MyDatabaseHelper extends OrmLiteSqliteOpenHelper {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "haokanh5.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 2;

    /**
     * DAO对象的缓存
     */
    private Map<String, Dao> mDaos = new HashMap<String, Dao>();

    /**
     * DBHelper的单利
     */
    private static MyDatabaseHelper sInstance = null;

    /**
     * 单例获取该Helper
     */
    public static MyDatabaseHelper getInstance(Context context) {
        //不适用传入进来的context，防止传入的是activity的话导致activity无法被释放
        context = context.getApplicationContext();
        if (sInstance == null) {
            synchronized (MyDatabaseHelper.class) {
                if (sInstance == null) {
                    sInstance = new MyDatabaseHelper(context);
                }
            }
        }
        return sInstance;
    }

    private MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized Dao getDaoQuickly(Class clazz) throws Exception {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (mDaos.containsKey(className)) {
            dao = mDaos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            mDaos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        mDaos.clear();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        LogHelper.d(DB_NAME, "onCreate is called");
        try {
            TableUtils.createTable(connectionSource, CollectionBean.class);
            TableUtils.createTable(connectionSource, HistoryRecordBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        LogHelper.d(DB_NAME, "onUpgrade is called, oldV, newV = " + oldVersion + ", " + newVersion);
        int version = oldVersion;
        if (version < 1) { //版本1时只加了CollectionBean表
            try {
                TableUtils.createTable(connectionSource, CollectionBean.class);
                version = 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (version < 2) { //版本2时加了HistoryRecordBean表
            try {
                TableUtils.createTable(connectionSource, HistoryRecordBean.class);
                version = 2;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
