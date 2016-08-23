package com.wzy.helptravel.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;
import com.wzy.helptravel.ui.MeActivity;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;

/**
 * 接收到的文本类型
 */
public class ReceiveTextHolder extends BaseViewHolder {

    @Bind(R.id.iv_avatar)
    protected ImageView iv_avatar;

    @Bind(R.id.tv_time)
    protected TextView tv_time;

    @Bind(R.id.tv_message)
    protected TextView tv_message;

    private BmobIMConversation conversation;

    public ReceiveTextHolder(Context context, ViewGroup root, BmobIMConversation conversation, OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_message, onRecyclerViewListener);
        this.conversation = conversation;
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        tv_time.setText(time);
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        UniversalImageLoader loader = new UniversalImageLoader();
        loader.loadAvatar(iv_avatar, conversation.getConversationIcon(), R.mipmap.default_avator);
        String content = message.getContent();
        tv_message.setText(content);
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
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}