package com.hejun.addresslist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hejun.addresslist.R;
import com.hejun.addresslist.bean.UserBean;
import java.util.List;

public class UserAdpater extends BaseAdapter {

    private Context context;
    private List<UserBean> list;
    private int index;

    public UserAdpater(Context context, List<UserBean> list,int index) {
        this.context = context;
        this.list = list;
        this.index = index;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UserBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        if (convertView == null){
            viewHodler = new ViewHodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null,false);
            viewHodler.textView1 = convertView.findViewById(R.id.textView);
            viewHodler.textView2 = convertView.findViewById(R.id.textView2);
            convertView.setTag(viewHodler);
        }else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
            UserBean userBean = list.get(position);

        if (index == 1){
            if (position == 0){
                viewHodler.textView1.setVisibility(View.VISIBLE);
                viewHodler.textView1.setText(userBean.getType());
            }else {
                UserBean userBean1 = list.get(position -1);
                String type = userBean1.getType();
                if (userBean.getType().equals(type)){
                    viewHodler.textView1.setVisibility(View.GONE);
                    viewHodler.textView1.setText(userBean.getType());
                }else {
                    viewHodler.textView1.setVisibility(View.VISIBLE);
                    viewHodler.textView1.setText(userBean.getType());
                }
            }

        }else {
            int selection = getSectionForPosition(position);
            if (position == getPositionForSection(selection)){
                viewHodler.textView1.setVisibility(View.GONE);
//                viewHodler.textView1.setVisibility(View.VISIBLE);
//                viewHodler.textView1.setText(userBean.getFirstName());
            }else {
                viewHodler.textView1.setVisibility(View.GONE);
            }
        }


        viewHodler.textView2.setText(userBean.getName());
        return convertView;

    }

    class ViewHodler{
        TextView textView1,textView2;
    }
    /**
     * 得到首字母的ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstName().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstName();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

}
