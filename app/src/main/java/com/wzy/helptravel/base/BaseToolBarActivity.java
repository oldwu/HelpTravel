package com.wzy.helptravel.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.bean.User;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;

/**
 * Created by wzy on 2016/7/8.
 */
public abstract class BaseToolBarActivity extends BaseActivity {


    public interface ToolBarListener {
        void clickLeft();

        void clickRight();
    }

    private ToolBarListener listener;

    TextView title;

    ImageView left;

    TextView right;

    public abstract String setTitle();

    public Object setLeft() {
        return null;
    }

    public Object setRight() {
        return null;
    }

    public ToolBarListener setToolBarListener() {
        return null;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_left:
                    if (listener == null)
                        finish();
                    else {
                        listener.clickLeft();
                    }
                    break;
                case R.id.tv_right:
                    if (listener != null)
                        listener.clickRight();
                    break;

                default:
                    break;
            }
        }
    };

    public void initBarView() {
        setTabListener(setToolBarListener());
        left = getView(R.id.tv_left);
        right = getView(R.id.tv_right);
        title = getView(R.id.tv_title);
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        title.setText(setTitle());
        setTop();
    }

    protected void setTabListener(ToolBarListener listener) {
        this.listener = listener;
    }

    protected void setTop() {
        setLeftView(setLeft() == null ? R.drawable.base_action_bar_back_bg_selector : setLeft());
        setRightView(setRight() == null ? null : setRight());
        title.setText(setTitle());
    }

    /**
     * 设置左边视图
     *
     * @param obj
     */
    private void setLeftView(Object obj) {
        if (obj != null && !obj.equals("")) {
            left.setVisibility(View.VISIBLE);
            if (obj instanceof Integer) {
                left.setImageResource(Integer.parseInt(obj.toString()));
            } else {
                left.setImageResource(R.drawable.base_action_bar_back_bg_selector);
            }
        } else {
            left.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置右边视图
     *
     * @param obj
     */
    protected void setRightView(Object obj) {
        if (obj != null && !obj.equals("")) {
            right.setVisibility(View.VISIBLE);
            if (obj instanceof Integer) {
                right.setBackgroundResource(Integer.parseInt(obj.toString()));
            } else {
                right.setText(obj.toString());
                right.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            right.setVisibility(View.INVISIBLE);
        }
    }

    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null) {
            intent.putExtra(this.getPackageName(), bundle);
        }
        startActivity(intent);
    }


    public String getCurrentUID() {
        BmobUser user = BmobUser.getCurrentUser(this);
        if (user != null) {
            return BmobUser.getCurrentUser(this, User.class).getObjectId();
        } else {
            return null;
        }
    }

    public String getCurrentUsername() {
        BmobUser user = BmobUser.getCurrentUser(this);
        if (user != null) {
            return BmobUser.getCurrentUser(this, User.class).getUsername();
        } else {
            return null;
        }
    }

    public String getCurrentAvatar() {
        BmobUser user = BmobUser.getCurrentUser(this);
        if (user != null) {
            return BmobUser.getCurrentUser(this, User.class).getAvatar();
        } else {
            return null;
        }
    }

}
