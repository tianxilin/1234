package com.hejun.addresslist;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hejun.addresslist.adapter.UserAdpater;
import com.hejun.addresslist.bean.UserBean;
import com.hejun.addresslist.dialog.MyDilog;
import com.hejun.addresslist.sql.PhoneDao;
import com.hejun.addresslist.sql.SqlHelper;
import com.hejun.addresslist.sql.UserDao;
import com.hejun.addresslist.util.CharacterParser;
import com.hejun.addresslist.util.PinyinComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private EditText editText;
    private ImageView imageView;
    private ListView listView;
    private Toolbar toolbar;
    private SqlHelper sqlHelper;
    private UserDao userDao;
    private TextView textView11;
    private UserAdpater userAdpater;
    private static List<UserBean> list1;
    private static List<UserBean> list2;
    private PinyinComparator pinyinComparator;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private PhoneDao phoneDao;
    private CharacterParser characterParser;
    private UserAdpater userAdpater2;
    private boolean flag;
    private TextView textView12;
    private ImageView imageView5;
    private LinearLayout linearLayout;
    private Handler handler;
    private ScrollView scrollView;
    private TextView textView14;


    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper = SqlHelper.getInstance(this);
        userDao = new UserDao(this);
        phoneDao = new PhoneDao(this);
        characterParser = new CharacterParser();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what ==  10010){
                    refreshUi();
                }
            }
        };

        //init view

        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.list_view);
        textView11 = findViewById(R.id.textView11);
        textView12 = findViewById(R.id.textView12);
        imageView5 = findViewById(R.id.imageView5);
        linearLayout = findViewById(R.id.linearLayout2);
        scrollView = findViewById(R.id.scrollView);
        toolbar = findViewById(R.id.toolbar);
        textView14 = findViewById(R.id.textView14);
        toolbar.setTitle("本地联系人");
        toolbar.setTitleTextColor(getResources().getColor(R.color.dimgrey));
        setSupportActionBar(toolbar);
        pinyinComparator = new PinyinComparator();
        //最近联系人
        final SharedPreferences sharedPreferences = getSharedPreferences("user", 0);

        int id = sharedPreferences.getInt("userId", -1);
        final UserBean userBean2 = userDao.qurreyById(id);
        if ( userBean2 == null) {
            imageView5.setVisibility(View.GONE);

            textView12.setText("暂无最近查看联系人");
        } else {
            textView12.setText(userBean2.getName());
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBean2 != null) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("UserBean", userBean2);
                    startActivity(intent);
                    finish();
                }
            }
        });

        list1 = userDao.qurreyAll();
        if (list1.isEmpty()) {
            textView11.setVisibility(View.VISIBLE);
            textView14.setVisibility(View.GONE);
        }
        Collections.sort(list1, pinyinComparator);
        userAdpater = new UserAdpater(this, list1, 0);
        listView.setAdapter(userAdpater);


        //set listener
        toolbar.setOnMenuItemClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo 跳转详情页
                UserAdpater userAdpater2 = (UserAdpater) parent.getAdapter();
                UserBean userBean = userAdpater2.getItem(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userId", userBean.getId())
                        .apply();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("UserBean", userBean);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popu_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //todo 删除操作
                        UserAdpater userAdpater1 = (UserAdpater) parent.getAdapter();
                        UserBean userBean = userAdpater1.getItem(position);
                        userDao.delete(userBean);
                        notification(userBean.getName());
                        refreshUi();
                        return false;
                    }
                });

                popupMenu.show();
                return true;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                if (flag) {
                    List<UserBean> beanList = sort1(userDao.queryByType(str));
                    list2.clear();
                    list2.addAll(beanList);
                    userAdpater2.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < list1.size(); i++) {
                        UserBean userBean = list1.get(i);
                        if (str.equals(userBean.getName())) {
                            listView.setSelection(i);
                        }
                    }
                }
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //点击listview里面滚动停止时，scrollview拦截listview的触屏事件，就是scrollview该滚动了
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }else{
                    //当listview在滚动时，不拦截listview的滚动事件；就是listview可以滚动，
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

    }

    private List<UserBean> filledData(String[] date) {
        List<UserBean> mSortList = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            UserBean sortModel = new UserBean();
            sortModel.setName(date[i]);
            String pinyin = CharacterParser.getInstance().getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setFirstName(sortString.toUpperCase());
            } else {
                sortModel.setFirstName("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_add:
                //todo 添加联系人
                MyDilog myDilog = new MyDilog(MainActivity.this);
                myDilog.setDialogCallBack(new MyDilog.DialogCallBack() {
                    @Override
                    public void getUserBean(UserBean userBean) {

                        int i = userDao.insert(userBean);
                        if (i != -1) {
//                            Toast.makeText(MainActivity.this, "插入成功" + i, Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshUi();
                                }
                            });

                        }

                    }
                });
                myDilog.show();
                Window mWindow = myDilog.getWindow();
                mWindow.setGravity(Gravity.CENTER);
                break;
            case R.id.menu_about:
                //todo 关于
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("关于")
                        .setMessage("作者: TZ");
                builder.show();
                break;
            case R.id.mene_grop:
                if (menuItem.getTitle().equals("分组查看")) {
                    flag = true;
                    editText.setHint("输入查询分组名");
                    menuItem.setTitle("取消分组查看");
                    List<UserBean> beanList = userDao.qurreyAll();
                    list2 = sort1(beanList);
                    userAdpater2 = new UserAdpater(this, list2, 1);
                    listView.setAdapter(userAdpater2);
                } else {
                    flag = false;
                    menuItem.setTitle("分组查看");
                    editText.setHint("请输入查询联系人的姓名");
                    userAdpater.notifyDataSetChanged();
                    listView.setAdapter(userAdpater);

                }

                break;
            case  R.id.menu_addData:
                MyThread myThread = new MyThread();
                myThread.start();
        }
        return false;
    }

    public void refreshUi() {
        if (flag) {
            List<UserBean> newlist = sort1(userDao.qurreyAll());
            list2.clear();
            list2.addAll(newlist);
            if (list2.isEmpty()) {
                textView11.setVisibility(View.VISIBLE);
                textView14.setVisibility(View.GONE);
            }else {
                textView11.setVisibility(View.GONE);
                textView14.setVisibility(View.VISIBLE);
            }
            userAdpater2.notifyDataSetChanged();
        } else {
            List<UserBean> newlist = userDao.qurreyAll();
            list1.clear();
            list1.addAll(newlist);
            Collections.sort(MainActivity.list1, pinyinComparator);
            if (list1.isEmpty()) {
                textView11.setVisibility(View.VISIBLE);
                textView14.setVisibility(View.GONE);
            }else {
                textView11.setVisibility(View.GONE);
                textView14.setVisibility(View.VISIBLE);
            }
            userAdpater.notifyDataSetChanged();
        }


    }

    void notification(String name) {
        String name1 = "my_package_channel";//渠道名字
        String id = "my_package_channel_1"; // 渠道ID
        String description = "my_package_first_channel"; // 渠道解释说明
        PendingIntent pendingIntent;//非紧急意图，可设置可不设置
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //判断是否是8.0上设备
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = null;
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name1, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true); //是否在桌面icon右上角展示小红点
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationBuilder = new Notification.Builder(this);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            notificationBuilder
                    .setSmallIcon(R.drawable.small)
                    .setContentTitle("通讯录提示")
                    .setContentText(name + "\t" + "删除成功")
                    .setContentIntent(pendingIntent)
                    .setChannelId(id)
                    .setAutoCancel(true);
        } else {
            notificationBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.small)
                    .setContentTitle("通讯录提示")
                    .setContentText(name + "\t" + "删除成功")
                    .setAutoCancel(true);

        }


        notificationManager.notify(0, notificationBuilder.build());

    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public List<UserBean> sort1(List<UserBean> userBeanList) {
        String[] usertypes = getResources().getStringArray(R.array.user_type);
        List<UserBean> userBeans = new ArrayList<>();
        for (int i = 0; i < usertypes.length; i++) {
            String s = usertypes[i];
            for (UserBean user : userBeanList) {
                if (s.equals(user.getType())) {
                    userBeans.add(user);
                }
            }
        }
        return userBeans;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否确认退出?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setNegativeButton("否", null).show();
    }

    //添加测试数据
    class MyThread extends Thread {
        @Override
        public void run() {
            String[] user = getResources().getStringArray(R.array.date);
            for (int i = 0; i < user.length; i++) {
                UserBean userBean = new UserBean();
                userBean.setName(user[i]);
                userBean.setPhoneType("手机");
                userBean.setPhonrNumber(String.valueOf(getNumber()));
                userBean.setType(getType());
                userBean.setSex("男");
                userDao.insert(userBean);
            }
            handler.sendEmptyMessage(10010);
        }

        public Long getNumber() {
            Long max = 18988888888l;// 随机生成手机号的最大值
            Long min = 13000000000l;// 随机生成手机号的最小值
            Long a = (long) (Math.random() * (max - min) + min);// 随机生成的手机号
            String d = "" + a;// 生成的号码转字符串
            String hm = d.substring(0, 3);// 提取字符串前3位
            int zs = Integer.parseInt(hm);// 提取出的前三位转整形
            if ((zs >= 130 && zs < 140) || (zs >= 150 && zs < 160) || (zs >= 180 && zs < 190 && zs != 184) || (zs == 176) || zs == 147) {//符合条件进入打印这个号码
                return a;
            }
            return max;
        }
        public String getType(){
            String type = null;
            String[] types = getResources().getStringArray(R.array.user_type);
            int id = (int) (Math.random()*(types.length-1));//随机产生一个index索引
            type = types[id];
            return type;
        }
    }
}