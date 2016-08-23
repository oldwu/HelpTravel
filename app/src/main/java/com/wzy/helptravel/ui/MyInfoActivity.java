package com.wzy.helptravel.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.adapter.HelpInfoAdapter;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.dao.InfoModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wzy on 2016/7/31.
 */
public class MyInfoActivity extends BaseToolBarActivity {

    @Bind(R.id.myInfo)
    RecyclerView myInfo;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;

    HelpInfoAdapter adapter;
    List<HelpInfo> list = new ArrayList<>();

    @Override
    public String setTitle() {
        return "我发布的信息";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initBarView();
        adapter = new HelpInfoAdapter(list, this);
        myInfo.setLayoutManager(new LinearLayoutManager(this));
        myInfo.setAdapter(adapter);
        init();
    }


    private void init() {
        swRefresh.setEnabled(true);
//        swRefresh.setRefreshing(true);
        query();
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swRefresh.setRefreshing(true);
                query();
            }
        });


    }

    private void query() {
        InfoModel.getInstance().queryInfoByUser(getCurrentUID(), new FindListener<HelpInfo>() {
            @Override
            public void onSuccess(List<HelpInfo> list) {
                save(list);
                adapter.addData(list);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(int i, String s) {

            }
        });
        swRefresh.setRefreshing(false);

    }


    private boolean save(List<HelpInfo> list) {
        SharedPreferences mySharedPreferences = getSharedPreferences("helpInfo",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("infoSize", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putString("info_" + i + "_id", list.get(i).getObjectId());
            editor.putString("info_" + i + "_img", list.get(i).getPicture());
            editor.putString("info_" + i + "_title", list.get(i).getTitle());
            editor.putString("info_" + i + "_time", list.get(i).getUpdatedAt());
        }

        return editor.commit();
    }

    private List<HelpInfo> get() {
        SharedPreferences mySharedPreferences = getSharedPreferences("helpInfo",
                Activity.MODE_PRIVATE);
        List<HelpInfo> list = new ArrayList<>();
        int size = mySharedPreferences.getInt("infoSize", 0);
        for (int i = 0; i < size; i++) {
            HelpInfo info = new HelpInfo();
            info.setTitle(mySharedPreferences.getString("info_" + i + "_title", ""));
            info.setObjectId(mySharedPreferences.getString("info_" + i + "_id", ""));
            info.setPicture(mySharedPreferences.getString("info_" + i + "_img", ""));
            info.setValue("updatedAt", mySharedPreferences.getString("info_" + i + "_time", ""));
            list.add(info);
        }
        return list;
    }


}
