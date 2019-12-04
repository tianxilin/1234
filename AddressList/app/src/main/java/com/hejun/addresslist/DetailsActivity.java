package com.hejun.addresslist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.hejun.addresslist.adapter.PhoneAdapter;
import com.hejun.addresslist.bean.PhoneNumber;
import com.hejun.addresslist.bean.UserBean;
import com.hejun.addresslist.sql.PhoneDao;
import com.hejun.addresslist.sql.UserDao;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = "DetailsActivity";
    private EditText name;
    private Spinner sex;
    private Spinner group;
    private ListView listView;
    private Toolbar toolbar;
    private List<PhoneNumber> list;
    private List<PhoneNumber> newList;
    private UserBean userBean;
    private UserDao userDao;
    private PhoneDao phoneDao;
    private PhoneNumber phoneNumber;
    private Spinner phoneSpinner;
    private EditText phoneEd;
    private ImageView phoneImg;
    private PhoneAdapter phoneAdapter;
    private HashMap<Integer, EditText> map1 = new HashMap<>();
    private HashMap<Integer, Spinner> map2 = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //init view
        userDao = new UserDao(this);
        phoneDao = new PhoneDao(this);
        name = findViewById(R.id.editText3);
        sex = findViewById(R.id.spinner);
        group = findViewById(R.id.spinner2);
        listView = findViewById(R.id.details_listview);
        phoneSpinner = findViewById(R.id.details_spinner);
        phoneEd = findViewById(R.id.details_number);
        phoneImg = findViewById(R.id.details_img);
        toolbar = findViewById(R.id.toolbar2);

        toolbar.setTitle("详情页");
        toolbar.setTitleTextColor(getResources().getColor(R.color.dimgrey));
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true);

        final PhoneDao phoneDao = new PhoneDao(this);

        String[] sexTypes = this.getResources().getStringArray(R.array.sex);
        String[] userTypes = this.getResources().getStringArray(R.array.user_type);
        String[] phoneTypes = this.getResources().getStringArray(R.array.number_type);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dialog_spinner, sexTypes);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.dialog_spinner, userTypes);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.dialog_spinner, phoneTypes);

        sex.setAdapter(arrayAdapter);
        group.setAdapter(arrayAdapter1);
        phoneSpinner.setAdapter(arrayAdapter2);

        if (getIntent() != null) {
            userBean = (UserBean) getIntent().getSerializableExtra("UserBean");
            name.setText(userBean.getName());

            Log.e(TAG, "onCreate: " + userBean.getPhonrNumber());
            String userSex = userBean.getSex();

            if (userSex.equals("女")) {
                sex.setSelection(1);
            } else {
                sex.setSelection(0);
            }

            group.setSelection(PhoneAdapter.getTypePosition(userTypes, userBean.getType()));
            phoneSpinner.setSelection(PhoneAdapter.getTypePosition(phoneTypes, userBean.getPhoneType()));
            phoneEd.setText(userBean.getPhonrNumber());
            list = phoneDao.queryByUserId(userBean.getId());


            phoneAdapter = new PhoneAdapter(this, list, map1, map2);
            listView.setAdapter(phoneAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo 拨打电话
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                PhoneAdapter phoneAdapter = (PhoneAdapter) parent.getAdapter();

                call(phoneAdapter.getItem(position).getNumber());
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startMainActivity(DetailsActivity.this);
                finish();
            }
        });

        phoneEd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                call(phoneEd.getText().toString());

                return true;
            }
        });
        phoneEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userBean.setPhonrNumber(s.toString());
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userBean.setName(s.toString());
            }
        });
        phoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hold();
                list.add(new PhoneNumber());
                phoneAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.detail_finish) {
            //todo 保存操作
            hold();
            Toast.makeText(DetailsActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            MainActivity.startMainActivity(DetailsActivity.this);

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        MainActivity.startMainActivity(this);
        finish();
    }


    public void hold() {
        userBean.setId(userBean.getId());
        userBean.setName(name.getText().toString());
        userBean.setType(group.getSelectedItem().toString());
        userBean.setSex(sex.getSelectedItem().toString());
        userBean.setPhoneType(phoneSpinner.getSelectedItem().toString());
        userDao.update(userBean);
//        phoneDao.deleteByUserId(userBean);
        Log.e(TAG, "hold: " + list.size() );
        List<PhoneNumber> phoneNumbers = phoneDao.queryByUserId(userBean.getId());
        for (PhoneNumber phoneNumber:phoneNumbers){
            phoneDao.delete(phoneNumber);
        }


        Log.e(TAG, "hold: " + phoneDao.queryByUserId(userBean.getId()).size());
        for (int i = 0; i < list.size(); i++) {
            EditText editText = map1.get(i);
            Spinner spinner = map2.get(i);
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber(editText.getText().toString());
            phoneNumber.setType(spinner.getSelectedItem().toString());
            Log.e(TAG, "onMenuItemClick: " + phoneNumber.getType() + phoneNumber.getNumber());
            phoneNumber.setUserBeanId(DetailsActivity.this.userBean);
            if (TextUtils.isEmpty(phoneNumber.getNumber())) {

            } else {
                phoneDao.update(phoneNumber);
            }

        }
    }

    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"10086");
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone){
        if(checkReadPermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)){
            //要在telPhone前加上字符串tel:
            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + telPhone));
            startActivity(intent);
        }
    }


}
