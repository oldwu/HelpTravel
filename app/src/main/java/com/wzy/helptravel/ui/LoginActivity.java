package com.wzy.helptravel.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.dao.UserModel;

import org.w3c.dom.Text;

import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by wzy on 2016/7/22.
 */
public class LoginActivity extends BaseToolBarActivity {


    @Bind(R.id.username_login_edit)
    EditText usernameEdit;
    @Bind(R.id.password_login_edit)
    EditText passwordEdit;
    @Bind(R.id.login_login_btn)
    CircularProgressButton loginBtn;
    @Bind(R.id.register_login_tv)
    TextView registerTV;


    @Override
    public String setTitle() {
        return "登陆";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!TextUtils.isEmpty(getCurrentUsername())){
            startActivity(MainActivity.class, null, true);
        }
        initBarView();

    }

    public void onLogin(View view) {
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        loginBtn.setIndeterminateProgressMode(true); // turn on indeterminate progress
        loginBtn.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
        UserModel.getInstance().login(username, password, new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    toast("登陆成功");
                    loginBtn.setProgress(100);
                    startActivity(MainActivity.class, null);
                    finish();

                } else {
                    loginBtn.setProgress(-1);
                    toast(e.getMessage());
                }
            }
        });

    }

    public void onRegister(View view){
        startActivity(RegisterActivity.class, null, false);
    }


}
