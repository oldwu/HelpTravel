package com.wzy.helptravel;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.Logger;
import com.wzy.helptravel.base.UniversalImageLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;

/**
 * Created by wzy on 2016/7/11.
 */
public class TravelHelpApplication extends Application {

    public final static String SMS_TEMPLATE = "msg";
    private static TravelHelpApplication INSTANCE;

    public static TravelHelpApplication INSTANCE() {
        return INSTANCE;
    }

    private void setInstance(TravelHelpApplication app) {
        setTravelHelpApplication(app);
    }

    private static void setTravelHelpApplication(TravelHelpApplication a) {
        TravelHelpApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        //初始化
        Logger.init("smile");
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            Bmob.initialize(this, Config.APP_KEY);
            BmobSMS.initialize(this, Config.APP_KEY);
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
        //uil初始化
        UniversalImageLoader.initImageLoader(this);
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
