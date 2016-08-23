package com.wzy.helptravel.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.wzy.helptravel.Config;

/**
 * Created by wzy on 2016/7/20.
 */
public class BaseFragment extends Fragment {

    private Toast toast;

    protected final static String NULL = "";


    protected void runOnMain(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    public void toast(final Object obj) {
        try {

            runOnMain(new Runnable() {
                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(getActivity(), NULL, Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), target);
        if (bundle != null)
            intent.putExtra(getActivity().getPackageName(), bundle);
        getActivity().startActivity(intent);
    }

    public void log(String msg) {
        if (Config.DEBUG) {
            Logger.i(msg);
        }
    }
}
