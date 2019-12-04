package com.hejun.addresslist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.hejun.addresslist.R;
import com.hejun.addresslist.bean.UserBean;

public class MyDilog extends Dialog {

    private String sex ;
    private String type;

    public MyDilog(Context context) {
        super(context, R.style.mydialog);
        setContentView(R.layout.dialog);
        final EditText name = findViewById(R.id.editText2);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        final Spinner spinner = findViewById(R.id.spinner3);
        final EditText number = findViewById(R.id.editText4);
        Button button = findViewById(R.id.button);


        final String[] types = context.getResources().getStringArray(R.array.user_type);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.dialog_spinner, types);

        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               type = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    type = types[0];
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton:
                        sex = "男";
                        break;
                    case R.id.radioButton2:
                        sex = "女";
                        break;
                }
            }
        });
        radioGroup.check(R.id.radioButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCallBack != null){
                    UserBean userBean = new UserBean();
                    userBean.setName(name.getText().toString());
                    userBean.setSex(sex);
                    userBean.setType(type);
                    userBean.setPhonrNumber(number.getText().toString());
                    userBean.setPhoneType("手机");
                    dialogCallBack.getUserBean(userBean);
                    dismiss();
                }
            }
        });

    }

    //接口回调
 private DialogCallBack dialogCallBack;

    public void setDialogCallBack(DialogCallBack DialogCallBack){
        this.dialogCallBack = DialogCallBack;
    }
    public interface DialogCallBack{
        void getUserBean(UserBean userBean);
    }
}
