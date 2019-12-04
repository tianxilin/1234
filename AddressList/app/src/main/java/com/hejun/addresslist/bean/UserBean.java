package com.hejun.addresslist.bean;


import com.hejun.addresslist.util.CharacterParser;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "user_info")
public class UserBean implements Serializable{
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String sex;
    @DatabaseField
    private String phonrNumber;
    @DatabaseField
    private String type;
    @DatabaseField
    private String firstName;
    @DatabaseField
    private String phoneType;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<PhoneNumber> phoneNumbers;

    public UserBean() {

    }

    public UserBean(String name) {
        this.name = name;
        this.firstName = CharacterParser.getInstance().getSelling(name);
    }

    public UserBean(String name, String sex, String phonrNumber, String type) {
        this.name = name;
        this.sex = sex;
        this.phonrNumber = phonrNumber;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.firstName = CharacterParser.getInstance().getSelling(name);
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public ForeignCollection<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ForeignCollection<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getPhonrNumber() {
        return phonrNumber;
    }

    public void setPhonrNumber(String phonrNumber) {
        this.phonrNumber = phonrNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
}
