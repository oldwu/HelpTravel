package com.wzy.helptravel.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.ui.MeActivity;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by wzy on 2016/7/15.
 */
public class SendTextHolder extends BaseViewHolder {

    @Bind(R.id.iv_avatar)
    ImageView iv_avatar;

    @Bind(R.id.iv_fail_resend)
    protected ImageView iv_fail_resend;

    @Bind(R.id.tv_time)
    protected TextView tv_time;

    @Bind(R.id.tv_message)
    protected TextView tv_message;
    @Bind(R.id.tv_send_status)
    protected TextView tv_send_status;

    @Bind(R.id.progress_load)
    protected ProgressBar progress_load;

    private BmobIMConversation conversation;

    private User currentUser;

    public SendTextHolder(Context context, ViewGroup root, BmobIMConversation c, OnRecyclerViewListener listener) {
        super(context, root, R.layout.item_chat_sent_message, listener);
        this.conversation = c;
        currentUser = BmobUser.getCurrentUser(context, User.class);
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final BmobIMUserInfo info = message.getBmobIMUserInfo();

        UniversalImageLoader loader = new UniversalImageLoader();
        loader.loadAvatar(iv_avatar, currentUser.getAvatar(), R.mipmap.default_avator);
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        tv_message.setText(content);
        tv_time.setText(time);

        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.VISIBLE);
        } else {
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
        }

        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击" + message.getContent());
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        tv_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("canChanged", true);
                User user = BmobUser.getCurrentUser(getContext(), User.class);
                bundle.putSerializable("user", user);
                startActivity(MeActivity.class, bundle);
            }
        });

        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversation.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progress_load.setVisibility(View.VISIBLE);
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_send_status.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            tv_send_status.setVisibility(View.VISIBLE);
                            tv_send_status.setText("已发送");
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        } else {
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            progress_load.setVisibility(View.GONE);
                            tv_send_status.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
