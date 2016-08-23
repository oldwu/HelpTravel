package com.wzy.helptravel.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarFragment;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.ui.LoginActivity;
import com.wzy.helptravel.ui.MainActivity;
import com.wzy.helptravel.ui.MeActivity;
import com.wzy.helptravel.ui.MyInfoActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;

/**
 * Created by wzy on 2016/7/8.
 */
public class MeFragment extends BaseToolBarFragment {

    @Bind(R.id.avatar_bg)
    LinearLayout avatarBg;
    @Bind(R.id.avatar_me_img)
    ImageView avatar;
    @Bind(R.id.username_me_tv)
    TextView username;

    @Bind(R.id.meInfo)
    View meInfo;
    @Bind(R.id.helpInfo)
    View helpInfo;
    @Bind(R.id.logout_me_view)
    View logout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
    }

    @Override
    protected String title() {
        return "";
    }

    @OnClick(R.id.meInfo)
    void onMeInfo() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", MainActivity.user);
        bundle.putBoolean("canChanged", true);
        startActivity(MeActivity.class, bundle);
    }

    @OnClick(R.id.helpInfo)
    void onHelpInfo() {
        startActivity(MyInfoActivity.class, null);
    }


    @OnClick(R.id.logout_me_view)
    void onLogout(){
        UserModel.getInstance().logout();
        startActivity(LoginActivity.class, null);
        getActivity().finish();
    }


    private void initUserInfo() {
        User user = UserModel.getInstance().getCurrentUser();
        new UniversalImageLoader().load(avatar, user.getAvatar(), R.mipmap.default_avator, null);
        username.setText(user.getNickName() != null ? user.getNickName() : user.getUsername());
    }
}
