package com.wzy.helptravel.dao;

import android.content.Context;

import com.wzy.helptravel.TravelHelpApplication;



/**
 * Created by wzy on 2016/7/11.
 */
public abstract class BaseModel {

    public int CODE_NULL=1000;
    public static int CODE_NOT_EQUAL=1001;

    public static final int DEFAULT_LIMIT=20;

    public Context getContext(){
        return TravelHelpApplication.INSTANCE();
    }
}
