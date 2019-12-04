package com.hejun.addresslist.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.hejun.addresslist.bean.UserBean;
import com.hejun.addresslist.bean.PhoneNumber;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqlHelper extends OrmLiteSqliteOpenHelper {

    // 数据库名称
    public static final String DATABASE_NAME = "user.db";

    // 本类的单例实例
    private static SqlHelper instance;

    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();

    // 获取本类单例对象的方法
    public static synchronized SqlHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SqlHelper.class) {
                if (instance == null) {
                    instance = new SqlHelper(context);
                }
            }
        }
        return instance;
    }

    // 私有的构造方法
    private SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, 9);
    }

    // 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
    public synchronized Dao getDao(Class mclass) throws SQLException {
        Dao dao = null;
        String className = mclass.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(mclass);
            daos.put(className, dao);
        }
        return dao;
    }

    @Override // 创建数据库时调用的方法
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserBean.class);
            TableUtils.createTable(connectionSource, PhoneNumber.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override // 数据库版本更新时调用的方法
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, UserBean.class, true);
            TableUtils.dropTable(connectionSource, PhoneNumber.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
