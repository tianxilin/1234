package com.hejun.addresslist.adapter;


import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import com.hejun.addresslist.bean.PhoneNumber;
import com.hejun.addresslist.R;
import java.util.HashMap;
import java.util.List;

public class PhoneAdapter extends BaseAdapter {

    private Context context;

    private List<PhoneNumber> list;
    private List<PhoneNumber> newList;
    private PhoneNumber phoneNumber;
    private boolean flag;
    private String newNumber;
    private PhoneNumber phoneNumber1;
    private HashMap<Integer,EditText> edMap;
    private HashMap<Integer,Spinner> spMap;
    public PhoneAdapter(Context context, List<PhoneNumber> list,HashMap<Integer,EditText> map, HashMap<Integer,Spinner> spMap) {
        this.context = context;
        this.list = list;
        this.edMap = map;
        this.spMap = spMap;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PhoneNumber getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private Integer index = -1;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PhoneAdapter.ViewHodler viewHodler = null;

        if (convertView == null) {
            viewHodler = new PhoneAdapter.ViewHodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.phone_list_item, null, false);
            viewHodler.spinner = convertView.findViewById(R.id.pnone_spinner);
            viewHodler.number = convertView.findViewById(R.id.pnone_number);
            viewHodler.imageView = convertView.findViewById(R.id.imageView4);
            //put 保存
            edMap.put(position,viewHodler.number);
            spMap.put(position,viewHodler.spinner);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (PhoneAdapter.ViewHodler) convertView.getTag();
            edMap.put(position,viewHodler.number);
            spMap.put(position,viewHodler.spinner);

        }


        phoneNumber = list.get(position);

        String[] types = context.getResources().getStringArray(R.array.number_type);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, types);

        viewHodler.spinner.setAdapter(arrayAdapter);

        viewHodler.spinner.setSelection(getTypePosition(types, phoneNumber.getType()));


        //1.在设置text前,先移除监听
        if (viewHodler.number.getTag() instanceof TextWatcher) {
            viewHodler.number.removeTextChangedListener((TextWatcher) viewHodler.number.getTag());
        }
        if (TextUtils.isEmpty(phoneNumber.getNumber())){
            viewHodler.number.setText("");
        }else {
            viewHodler.number.setText(phoneNumber.getNumber());
        }

        if (phoneNumber.isFocus()) {
            if (!viewHodler.number.isFocused()) {
                viewHodler.number.requestFocus();
            }
            CharSequence text = phoneNumber.getNumber();
            viewHodler.number.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
        } else {
            if (viewHodler.number.isFocused()) {
                viewHodler.number.clearFocus();
            }
        }
        final ViewHodler finalViewHodler = viewHodler;
        viewHodler.number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    final boolean focus = phoneNumber.isFocus();
                    check(position);
                    if (!focus && !finalViewHodler.number.isFocused()) {
                        finalViewHodler.number.requestFocus();
                        finalViewHodler.number.onWindowFocusChanged(true);
                    }
                }
                return false;
            }
        });

        //2.新建监听类
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    phoneNumber.setNumber(null);
                    finalViewHodler.imageView.setVisibility(View.GONE);
                } else {
                    phoneNumber.setNumber(s.toString());
                    finalViewHodler.imageView.setVisibility(View.VISIBLE);
                }
                Log.e("tag", "afterTextChanged: " +s.toString() );
            }
        };
        //3.添加监听器
        viewHodler.number.addTextChangedListener(watcher);
        viewHodler.number.setTag(watcher);

        final ViewHodler finalViewHodler1 = viewHodler;
        viewHodler.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                phoneNumber.setType(finalViewHodler1.spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHodler.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(phoneNumber);
                notifyDataSetChanged();
            }
        });


        return convertView;

    }
    private void check(int position) {
        for (PhoneNumber l : list) {
            l.setFocus(false);
        }
        list.get(position).setFocus(true);
    }

    class ViewHodler {
        Spinner spinner;
        EditText number;
        ImageView imageView;

    }

    public static int getTypePosition(String[] strings, String type) {
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (s.equals(type)) {
                return i;
            }
        }
        return 0;
    }


}
