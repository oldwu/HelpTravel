package com.wzy.helptravel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzy.helptravel.R;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;
import com.wzy.helptravel.ui.MeActivity;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * 接收到的文本类型
 */
public class ReceiveImageHolder extends BaseViewHolder {

    @Bind(R.id.iv_avatar)
    protected ImageView iv_avatar;

    @Bind(R.id.tv_time)
    protected TextView tv_time;

    @Bind(R.id.iv_picture)
    protected ImageView iv_picture;
    @Bind(R.id.progress_load)
    protected ProgressBar progress_load;

    private BmobIMConversation conversation;

    public ReceiveImageHolder(Context context, ViewGroup root, BmobIMConversation c, OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_image, onRecyclerViewListener);
        this.conversation = c;
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage) o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        UniversalImageLoader loader = new UniversalImageLoader();
        loader.loadAvatar(iv_avatar, conversation.getConversationIcon(), R.mipmap.default_avator);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        //可使用buildFromDB方法转化为指定类型的消息
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(false, msg);
        //显示图片
        loader.load(iv_picture, message.getRemoteUrl(), R.mipmap.ic_launcher, new ImageLoadingListener() {
            ;

            @Override
            public void onLoadingStarted(String s, View view) {
                progress_load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progress_load.setVisibility(View.INVISIBLE);
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putBoolean("canChanged", false);
                UserModel.getInstance().queryUserInfo(conversation.getConversationId() + "", new QueryUserListener() {
                    @Override
                    public void done(User s, BmobException e) {
                        if (e == null) {
                            bundle.putSerializable("user", s);
                            startActivity(MeActivity.class, bundle);
                        }
                    }
                });

            }
        });

        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击图片:" + message.getRemoteUrl() + "");
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        iv_picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });

    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}