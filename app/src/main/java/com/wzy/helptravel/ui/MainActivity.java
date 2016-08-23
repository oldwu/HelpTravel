package com.wzy.helptravel.ui;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wzy.helptravel.R;
import com.wzy.helptravel.adapter.MainFragmentAdapter;
import com.wzy.helptravel.base.BaseActivity;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.InfoModel;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;
import com.wzy.helptravel.ui.Fragment.FindFragment;
import com.wzy.helptravel.ui.Fragment.HomeFragment;
import com.wzy.helptravel.ui.Fragment.MeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_homepage_main)
    LinearLayout homeBtn;
    @Bind(R.id.btn_find_main)
    LinearLayout findBtn;
    @Bind(R.id.btn_me_main)
    LinearLayout meBtn;
    @Bind(R.id.vp_main)
    ViewPager mViewPager;

    LinearLayout[] btns;
    private MainFragmentAdapter adapter;
    private List<Fragment> mFragmentList;
    private int index;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        user = BmobUser.getCurrentUser(this, User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    log("test");

                } else {
                    log("tes2t");
                }
            }
        });
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast("" + status.getMsg());
            }
        });


    }

    @Override
    protected void initView() {
        super.initView();

        btns = new LinearLayout[3];
        btns[0] = homeBtn;
        btns[1] = findBtn;
        btns[2] = meBtn;
        btns[0].setSelected(true);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new FindFragment());
        mFragmentList.add(new MeFragment());
        adapter = new MainFragmentAdapter(this, mFragmentList, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        setButtonSelected(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtonSelected(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 绑定到按键
     *
     * @param v
     */
    public void onTabSelect(View v) {
        switch (v.getId()) {
            case R.id.btn_homepage_main:
                index = 1;

                break;
            case R.id.btn_find_main:
                index = 2;

                break;
            case R.id.btn_me_main:
                index = 3;

                break;
            default:
                break;
        }

        mViewPager.setCurrentItem(index - 1, false);
        setButtonSelected(index);
    }

    /**
     * 根据index设置按键被选中
     *
     * @param index
     */
    public void setButtonSelected(int index) {
        log(index + "");
//        for (int i = 0; i < 3; i++) {
//            btns[i].setPressed(true);
//        }
        for (int i = 0; i < 3; i++) {
            btns[i].setBackgroundColor(Color.parseColor("#E3E3E7"));
        }
        btns[index - 1].setBackgroundColor(Color.parseColor("#AAAAAA"));
    }
}
