package com.wzy.helptravel.dao.i;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;


/**
 * Created by wzy on 2016/7/11.
 */
public abstract class UpdateCacheListener extends BmobListener {
    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
