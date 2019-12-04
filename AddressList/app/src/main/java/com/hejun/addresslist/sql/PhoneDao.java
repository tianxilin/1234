package com.hejun.addresslist.sql;

import android.content.Context;
import com.hejun.addresslist.bean.PhoneNumber;
import com.hejun.addresslist.bean.UserBean;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.List;

public class PhoneDao {

    private Context context;

    private Dao<PhoneNumber,Integer> dao;

    public PhoneDao(Context context) {
        this.context = context;
        try {
            dao = SqlHelper.getInstance(context).getDao(PhoneNumber.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入数据
    public int insert(PhoneNumber phoneNumber){
        try {
            return dao.create(phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //删除数据
    public void deleteByUserId(UserBean userBean){
        try {
           dao.deleteBuilder().where().eq("userBeanId_id",userBean.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void delete(PhoneNumber phoneNumber){
        try {
            dao.delete(phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //修改数据
    public void update(PhoneNumber phoneNumber){

        try {
             dao.createOrUpdate(phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //查询所有数据
    public List<PhoneNumber> qurreyAll(){
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<PhoneNumber> queryByUserId(int user_id) {
        try {
            return dao.queryBuilder().where().eq("userBeanId_id", user_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
