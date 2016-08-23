package com.wzy.helptravel.dao.i;

import com.wzy.helptravel.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;


/**
 * Created by wzy on 2016/7/11.
 */
public abstract class QueryUserListener extends BmobListener<User> {

    public abstract void done(User s, BmobException e);

    @Override
    protected void postDone(User o, BmobException e) {
        done(o, e);
    }
}
