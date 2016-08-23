package com.wzy.helptravel.ui.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wzy.helptravel.R;
import com.wzy.helptravel.adapter.HelpInfoAdapter;
import com.wzy.helptravel.base.BaseToolBarFragment;
import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.InfoModel;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.QueryUserListener;
import com.wzy.helptravel.ui.AddInfoActivity;
import com.wzy.helptravel.ui.HelpInfoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by wzy on 2016/7/8.
 */
public class HomeFragment extends BaseToolBarFragment {

    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.info_rc)
    RecyclerView recyclerView;
    @Bind(R.id.toHelp)
    Button toHelpBtn;
    @Bind(R.id.forHelp)
    Button forHelpBtn;
    @Bind(R.id.add_info)
    FloatingActionButton addInfo;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    HelpInfoAdapter adapter;
    List<HelpInfo> dataList = new ArrayList<>();

    BmobGeoPoint point = null;
    String type = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        initSwipeLayout();

        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        point = new BmobGeoPoint(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                        Log.d("point", point.getLatitude() + " " + point.getLongitude());
//                        System.out.println(aMapLocation.getLatitude());
                    }
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);


        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);

        if (mLocationOption.isOnceLocationLatest()) {
            mLocationOption.setOnceLocationLatest(true);
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }

        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        return rootView;
    }

    @Override
    protected String title() {
        return "旅游帮帮忙";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();

        toast(mLocationClient.isStarted());
    }

    private void initSwipeLayout() {
        dataList = get();
        swipeRefreshLayout.setEnabled(true);
        adapter = new HelpInfoAdapter(dataList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new HelpInfoAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String id) {
                BmobQuery<HelpInfo> query = new BmobQuery<>();
                query.getObject(getContext(), id, new GetListener<HelpInfo>() {
                    @Override
                    public void onSuccess(HelpInfo info) {
                        final Bundle bundle = new Bundle();
                        bundle.putSerializable("helpInfo", info);
                        UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                            @Override
                            public void done(User s, BmobException e) {
                                if (e == null) {
                                    bundle.putSerializable("userBy", s);
                                    startActivity(HelpInfoActivity.class, bundle);
                                } else {
                                    System.out.println("error");
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swipeRefreshLayout.setRefreshing(true);
                query();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });

    }

    @OnClick(R.id.toHelp)
    public void onToHelp(View view) {
        type = "帮助";
        query();
    }

    @OnClick(R.id.forHelp)
    public void onForHelp(View view) {
        type = "求助";
        query();
    }

    @OnClick(R.id.add_info)
    public void onAddInfo(View view) {
        startActivity(AddInfoActivity.class, null);
    }

    /**
     * 数据更新
     */
    private void query() {
        InfoModel.getInstance().queryAllInfo(type, point, new FindListener<HelpInfo>() {
            @Override
            public void onSuccess(List<HelpInfo> list) {
                toast(list.size());
                adapter.addData(list);
                adapter.notifyDataSetChanged();
                save(list);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 将数据保存到本地
     *
     * @param list
     * @return
     */
    private boolean save(List<HelpInfo> list) {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("helpInfo",
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

    /**
     * 从本地获取数据
     *
     * @return
     */
    private List<HelpInfo> get() {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("helpInfo",
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
