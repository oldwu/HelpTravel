package com.wzy.helptravel.base;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


/**
 * Created by wzy on 2016/7/8.
 */
public interface ILoader {

    /**
     * 加载头像
     * @param iv
     * @param url
     * @param defaultRes
     */
    void loadAvatar(ImageView iv, String url, int defaultRes);


    /**
     * 加载图片
     * @param iv
     * @param url
     * @param defaultRes
     * @param listener
     */
    void load(ImageView iv, String url, int defaultRes, ImageLoadingListener listener);
}
