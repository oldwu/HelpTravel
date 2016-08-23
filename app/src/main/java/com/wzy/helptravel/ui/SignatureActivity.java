package com.wzy.helptravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;

import butterknife.Bind;


/**
 * 个性签名编辑界面
 */
public class SignatureActivity extends BaseToolBarActivity implements TextWatcher {

    @Bind(R.id.signature_signature_edit)
    EditText signature;
    @Bind(R.id.strlen_signature_tv)
    TextView strlen;

    private int selectionStart;
    private int selectionEnd;
    private String temp;

    @Override
    public String setTitle() {
        return "设置签名";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        initBarView();
        init();
    }


    private void init() {


        signature.setText(getBundle().get("signature").toString());
        signature.addTextChangedListener(this);
        strlen.setText(28 - signature.getText().length() + "");
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

    private void back() {
        String SignatureStr = signature.getText().toString().trim();
        if (SignatureStr.equals("")) {
            toast("还未填写签名呢！");
        } else {
            Intent intent = new Intent();
            intent.putExtra("signature", SignatureStr);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int number = 28 - s.length();
        strlen.setText("" + number);
        selectionStart = signature.getSelectionStart();
        selectionEnd = signature.getSelectionEnd();
        if (temp.length() > 28) {
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionStart;
            signature.setText(s);
            signature.setSelection(tempSelection);//设置光标在最后
        }
    }
}
