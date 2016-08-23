package com.wzy.helptravel.base;

/**
 * Created by wzy on 2016/7/8.
 */
public class ImageLoaderFactory {
    private static volatile ILoader instance;

    private ImageLoaderFactory() {
    }

    public static ILoader getLoader() {
        if (instance == null) {
            synchronized (ImageLoaderFactory.class) {
                if (instance == null) {
                    instance = new UniversalImageLoader();
                }
            }
        }
        return instance;
    }

}
