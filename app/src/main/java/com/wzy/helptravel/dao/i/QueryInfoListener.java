package com.wzy.helptravel.dao.i;

import com.wzy.helptravel.bean.HelpInfo;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * Created by wzy on 2016/7/18.
 */
public abstract class QueryInfoListener extends BmobListener<HelpInfo> {

    @Override
    protected void postDone(HelpInfo info, BmobException e) {
        done(e);
    }

    public abstract void done(BmobException e);
}
