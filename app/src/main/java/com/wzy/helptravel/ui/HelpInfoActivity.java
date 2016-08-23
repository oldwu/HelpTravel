package com.wzy.helptravel.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.InfoModel;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by wzy on 2016/7/28.
 */
public class HelpInfoActivity extends BaseToolBarActivity {
    @Override
    public String setTitle() {
        return "信息详情";
    }

    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.info_img)
    ImageView infoImg;
    @Bind(R.id.info)
    LinearLayout helpInfo;
    @Bind(R.id.user_info)
    RelativeLayout userInfo;
    @Bind(R.id.phone)
    ImageButton phone;
    @Bind(R.id.message)
    ImageButton message;
    @Bind(R.id.helpInfo_tv)
    TextView helpInfoTv;
    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.nickname)
    TextView nickName;
    @Bind(R.id.location)
    ImageButton locationBtn;


    float firstPosition = 0;
    boolean isScaling = false;
    boolean canReply = true;
    private DisplayMetrics metric;

    int py = 0;
    int TouchSlop;
    int scollHeight = 0;
    int helpInfoHeight = 0;

    private HelpInfo helpInfobean;

    private User user;

    private BmobGeoPoint bmobGeoPoint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpInfobean = (HelpInfo) getBundle().getSerializable("helpInfo");
        user = (User) getBundle().getSerializable("userBy");

        setContentView(R.layout.activity_help_info);
        initBarView();


        // 获取屏幕宽高
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        TouchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();

        scollHeight = dip2px(this, 48);
        helpInfoHeight = dip2px(this, 200);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) infoImg.getLayoutParams();
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams) helpInfo.getLayoutParams();
                switch (motionEvent.getAction()) {
                    //手指抬起时触发
                    case MotionEvent.ACTION_UP:
                        // 手指离开后恢复图片
                        isScaling = false;
                        replyImage();
                        break;
                    //手指移动时触发
                    case MotionEvent.ACTION_MOVE:
                        if (!isScaling) {
                            if (scrollView.getScrollY() == 0) {
                                firstPosition = motionEvent.getRawY();// 滚动到顶部时记录位置，否则正常返回
                            } else {
                                break;
                            }
                        }
                        int distance = (int) ((motionEvent.getRawY() - firstPosition)); // 滚动距离
                        if (distance < 0) { // 如果当前位置比记录位置要小，正常返回
                            Log.d("topMargin", lp3.topMargin + " " + dip2px(HelpInfoActivity.this, 100));
                            break;
                        }

                        // 处理放大的关键代码
                        isScaling = true;
                        lp.width = (int) (metric.widthPixels + distance);
                        lp.height = (int) ((metric.widthPixels + distance) * 9 / 16);

                        infoImg.setLayoutParams(lp);
                        lp3.topMargin = lp.height;
                        helpInfo.setLayoutParams(lp3);
                        return true; // 返回true表示已经消费该事件
                }
                return false;

            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        new UniversalImageLoader().load(infoImg, helpInfobean.getPicture(), R.mipmap.default_camera, null);
        if (helpInfobean.getType().equals("帮助"))
            helpInfoTv.setText("帮助：\n\n" + helpInfobean.getInfo());
        else
            helpInfoTv.setText("求助：\n\n" + helpInfobean.getInfo());
        new UniversalImageLoader().load(avatar, user.getAvatar(), R.mipmap.default_avator, null);
        nickName.setText(user.getNickName() == null ? user.getUsername() : user.getNickName());
        bmobGeoPoint = helpInfobean.getLocation();
    }

    // 手指抬起图片回弹动画 (使用了属性动画)
    public void replyImage() {
        final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) infoImg.getLayoutParams();
        final RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) helpInfo.getLayoutParams();
        final float w = infoImg.getLayoutParams().width;// 图片当前宽度
        final float h = infoImg.getLayoutParams().height;// 图片当前高度
        final float newW = metric.widthPixels;// 图片原宽度
        final float newH = metric.widthPixels * 9 / 16;// 图片原高度
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                lp.width = (int) (w - (w - newW) * cVal);
                lp.height = (int) (h - (h - newH) * cVal);
                infoImg.setLayoutParams(lp);
                lp2.topMargin = infoImg.getHeight();
                helpInfo.setLayoutParams(lp2);
            }
        });
        //开启动画
        anim.start();
    }

    public void onUserInfo(View view) {
        toast("userinfo");
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putBoolean("canChanged", false);
        startActivity(MeActivity.class, bundle);

    }

    public void onLocation(View view){
        Bundle bundle = new Bundle();
        bundle.putSerializable("location", bmobGeoPoint);
        startActivity(LocationActivity.class, bundle, false);
    }

    public void onPhone(View view) {
        toast("电话");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + user.getMobilePhoneNumber());
        intent.setData(data);
        startActivity(intent);
    }

    public void onMessage(View view) {
        toast("信息");
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getNickName() == null ? user.getUsername() : user.getNickName(), user.getAvatar());
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", c);
        startActivity(ChatActivity.class, bundle, false);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
