package com.wzy.helptravel.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.wzy.helptravel.R;
import com.wzy.helptravel.TravelHelpApplication;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.dao.UserModel;

import butterknife.Bind;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by wzy on 2016/7/22.
 */
public class RegisterActivity extends BaseToolBarActivity {

    @Bind(R.id.username_register_edit)
    EditText usernameEdit;
    @Bind(R.id.password_register_edit)
    EditText passwordEdit;
    @Bind(R.id.phonenum_register_edit)
    EditText phonenumEdit;
    @Bind(R.id.verify_register_edit)
    EditText verifyEdit;
    @Bind(R.id.reciveverify_register_btn)
    Button reciveVerifyBtn;
    @Bind(R.id.register_register_btn)
    CircularProgressButton registerBtn;

    int sec = 60;//短信验证60s
    RegHandler handler = new RegHandler();


    @Override
    public String setTitle() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initBarView();
    }

    public void onReceive(View view) {
        hideSoftInputView();
        String phoneNum = phonenumEdit.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            toast("电话号码不能为空~");
            return;
        }
        reciveVerifyBtn.setBackgroundResource(R.drawable.login_register_btn_bg_refuse);
        reciveVerifyBtn.setClickable(false);
        new Thread() {
            @Override
            public void run() {
                super.run();

                while (sec-- > 0) {
                    try {
                        handler.sendEmptyMessage(1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(2);
            }
        }.start();
        BmobSMS.requestSMSCode(getBaseContext(), phoneNum, TravelHelpApplication.SMS_TEMPLATE,
                new RequestSMSCodeListener() {

                    @Override
                    public void done(Integer smsId, BmobException ex) {
                        if (ex == null) {//验证码发送成功
                            log("短信id：" + smsId);//用于查询本次短信发送详情
                        }else{
                            log(ex.getErrorCode()+ ex.getMessage());
                            if (ex.getErrorCode() == 10010){
                                toast("该手机号验证已达限制，请过一段时间再试");
                                sec = 0;
                            }
                        }
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sec = 0;
    }

    public void onRegister(View v) {
        hideSoftInputView();
        final String phoneNum = phonenumEdit.getText().toString().trim();
        String verifyCode = verifyEdit.getText().toString().trim();
        final String username = usernameEdit.getText().toString().trim();
        final String password = passwordEdit.getText().toString().trim();
        registerBtn.setIndeterminateProgressMode(true); // turn on indeterminate progress
        registerBtn.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
        BmobSMS.verifySmsCode(this, phoneNum, verifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    log("验证成功");
                    UserModel.getInstance().register(username, password, phoneNum, new LogInListener() {
                        @Override
                        public void done(Object o, cn.bmob.v3.exception.BmobException e) {
                            if (e == null) {
                                toast("注册成功");
                                registerBtn.setProgress(100);
                                finish();
                            } else {
                                log(e.getErrorCode() + e.getMessage());
                                registerBtn.setProgress(-1);
                            }
                        }
                    });
                } else {
                    log(e.getErrorCode() + e.getMessage());
                    registerBtn.setProgress(-1);
                }
            }
        });


    }


    class RegHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    reciveVerifyBtn.setText(sec + "秒后重新发送");
                    break;
                case 2:
                    reciveVerifyBtn.setText("接收短信\n验证码");
                    reciveVerifyBtn.setBackgroundResource(R.drawable.login_register_btn_bg_normal);
                    reciveVerifyBtn.setClickable(true);
                    sec = 60;
                    break;

            }
        }
    }
}
