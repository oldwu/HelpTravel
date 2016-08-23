package com.wzy.helptravel.adapter;

import android.content.Context;
import android.view.View;

import com.wzy.helptravel.R;
import com.wzy.helptravel.bean.Conversation;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;
import com.wzy.helptravel.utils.TimeUtil;

import java.util.Collection;

import cn.bmob.v3.exception.BmobException;

/**
 * 使用进一步封装的Conversation,教大家怎么自定义会话列表
 * @author smile
 */
public class ConversationAdapter extends BaseRecyclerAdapter<Conversation> {

    public ConversationAdapter(Context context, IMutlipleItem<Conversation> items, Collection<Conversation> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(final BaseRecyclerHolder holder, final Conversation conversation, int position) {
        holder.setText(R.id.tv_recent_msg,conversation.getLastMessageContent());
        holder.setText(R.id.tv_recent_time, TimeUtil.getChatTime(false,conversation.getLastMessageTime()));
        //会话图标
        Object obj = conversation.getAvatar();
        if(obj instanceof String){
            String avatar=(String)obj;
            holder.setImageView(avatar, R.mipmap.default_avator, R.id.iv_recent_avatar);
        }else{
            int defaultRes = (int)obj;
            holder.setImageView(null, defaultRes, R.id.iv_recent_avatar);
        }
        UserModel.getInstance().queryUserInfo(conversation.getcId(), new QueryUserListener() {
            @Override
            public void done(User s, BmobException e) {
                if (e == null) {
                    //会话标题
                    holder.setText(R.id.tv_recent_name, s.getNickName() == null ? s.getUsername() : s.getNickName());
                    //查询指定未读消息数
                    long unread = conversation.getUnReadCount();
                    if(unread>0){
                        holder.setVisible(R.id.tv_recent_unread, View.VISIBLE);
                        holder.setText(R.id.tv_recent_unread, String.valueOf(unread));
                    }else{
                        holder.setVisible(R.id.tv_recent_unread, View.GONE);
                    }
                }
            }
        });

    }
}