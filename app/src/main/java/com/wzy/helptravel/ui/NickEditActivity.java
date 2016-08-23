package com.wzy.helptravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;

import butterknife.Bind;


/**
 * 昵称编辑界面
 */
public class NickEditActivity extends BaseToolBarActivity {


    @Bind(R.id.nick_nickEdit_edit)
    EditText nickName;

    @Override
    public String setTitle() {
        return "设置昵称";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_edit);
        initBarView();
        init();
    }


    private void init() {

        nickName.setText(getBundle().get("nickName").toString());
    }


    private void back() {
        String nickNameStr = nickName.getText().toString().trim();
        if (nickNameStr.equals("")) {
            toast("还未填写昵称呢！");
        } else {
            Intent intent = new Intent();
            intent.putExtra("nickName", nickNameStr);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public ToolBarListener setToolBarListener() {
        return new ToolBarListener() {
            @Override
            public void clickLeft() {
                back();
            }

            @Override
            public void clickRight() {

            }
        };
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
