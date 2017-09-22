package com.haokan.screen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.haokan.screen.bean.HomeCache_ImageBean;
import com.haokan.screen.database.bean.HttpRequestForWifiBean;
import com.haokan.screen.database.bean.LockScreenCollectionBean;
import com.haokan.screen.database.bean.LockScreenFollowCp;
import com.haokan.screen.util.LogHelper;
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
    private static final String DB_NAME = "haokanyitu.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 20;

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
        Context context1 = context.getApplicationContext();
        if (context1 != null) {
            context = context1;
        }
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
            TableUtils.createTable(connectionSource, HomeCache_ImageBean.class);
            TableUtils.createTable(connectionSource, LockScreenCollectionBean.class);
            TableUtils.createTable(connectionSource, HttpRequestForWifiBean.class);
            TableUtils.createTable(connectionSource, LockScreenFollowCp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        LogHelper.d(DB_NAME, "onUpgrade is called, oldV, newV = " + oldVersion + ", " + newVersion);
        int version = oldVersion;
        if (version < 13) { //之前所有的数据库都不再维护, 现在只用一个缓冲的数据库
            try {
                TableUtils.createTable(connectionSource, HomeCache_ImageBean.class);
                version = 13;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (version < 15) {
            try {
                TableUtils.createTable(connectionSource, LockScreenCollectionBean.class);
                version = 15;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (version < 17) {
            try {
                TableUtils.createTable(connectionSource, HttpRequestForWifiBean.class);
                version = 17;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (version < 19) {
            try {
                TableUtils.createTable(connectionSource, LockScreenFollowCp.class);
                version = 19;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (version < 20) {
            DatabaseUtil.upgradeTable(sqLiteDatabase,connectionSource, LockScreenFollowCp.class, DatabaseUtil.OPERATION_TYPE.ADD);
            version = 20;
        }
    }
}