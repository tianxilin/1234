package com.hejun.addresslist.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

@DatabaseTable(tableName = "PhoneNumber")
public class PhoneNumber implements Serializable{
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String type;
    @DatabaseField
    private String number;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true,
            foreignColumnName = "id")
    private UserBean userBeanId;
    @DatabaseField
    private boolean focus;
    public PhoneNumber() {
    }

    public PhoneNumber(String type, String number) {
        this.type = type;
        this.number = number;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public UserBean getUserBeanId() {
        return userBeanId;
    }

    public void setUserBeanId(UserBean userBeanId) {
        this.userBeanId = userBeanId;
    }
}
