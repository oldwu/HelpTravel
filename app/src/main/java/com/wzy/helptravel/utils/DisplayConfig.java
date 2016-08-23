package com.wzy.helptravel.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by wzy on 2016/7/8.
 */
public class DisplayConfig {

    /**
     * 设置UIL默认显示配置，圆角
     * @param hasRounded
     * @param defaultRes
     * @return
     */
    public static DisplayImageOptions getDefaultOptions(boolean hasRounded, int defaultRes){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .delayBeforeLoading(100)
                .resetViewBeforeLoading(true);

        if (hasRounded){
            builder.displayer(new RoundedBitmapDisplayer(12));
        }
        if (defaultRes != 0){
            builder.showImageForEmptyUri(defaultRes)
                    .showImageOnFail(defaultRes);
        }
        return builder.build();

    }
}
