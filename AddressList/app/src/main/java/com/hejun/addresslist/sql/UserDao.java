package com.hejun.addresslist.sql;

import android.content.Context;
import com.hejun.addresslist.bean.UserBean;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private Context context;

    private Dao<UserBean,Integer> dao;

    public UserDao(Context context){
        this.context = context;
        try {
            dao = SqlHelper.getInstance(context).getDao(UserBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入数据
    public int insert(UserBean userBean){
        try {
            return dao.create(userBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //删除数据
    public int delete(UserBean userBean){
        try {
            return dao.delete(userBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //修改数据
    public int update(UserBean userBean){

        try {
           return dao.update(userBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //查询所有数据
    public List<UserBean> qurreyAll(){
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //根据id查询
    public UserBean qurreyById(int id){
        try {
            return (UserBean) dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<UserBean> queryByType(String type) {
        try {
            return dao.queryBuilder().where().eq("type", type).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
