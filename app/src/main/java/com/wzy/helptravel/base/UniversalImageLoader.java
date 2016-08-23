package com.wzy.helptravel.base;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzy.helptravel.utils.DisplayConfig;

/**
 * Created by wzy on 2016/7/8.
 */
public class UniversalImageLoader implements ILoader {
    @Override
    public void loadAvatar(ImageView iv, String url, int defaultRes) {
        if (!TextUtils.isEmpty((url))) {
            display(iv, url, true, defaultRes, null);
        } else {
            iv.setImageResource(defaultRes);
        }
    }

    @Override
    public void load(ImageView iv, String url, int defaultRes, ImageLoadingListener listener) {
        if (!TextUtils.isEmpty(url)) {
            display(iv, url.trim(), true, defaultRes, listener);
        } else {
            iv.setImageResource(defaultRes);
        }
    }

    /**
     * 展示图片
     *
     * @param iv
     * @param url
     * @param isCircle
     * @param defaultRes
     * @param listener
     */
    private void display(ImageView iv, String url, boolean isCircle, int defaultRes, ImageLoadingListener listener) {
        //设置标记，减少网络访问次数
        if (!url.equals(iv.getTag())) {
            iv.setTag(url);
            ImageAware imageAware = new ImageViewAware(iv, false);
            ImageLoader.getInstance().displayImage(url, iv, DisplayConfig.getDefaultOptions(isCircle, defaultRes));
        }
    }

    /**
     * 初始化Imageloader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPoolSize(3)
                .memoryCache(new WeakMemoryCache())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO);

        ImageLoader.getInstance().init(config.build());
    }


}
