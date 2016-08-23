package com.wzy.helptravel.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by wzy on 2016/7/29.
 */
public class LocationActivity extends BaseToolBarActivity{

    MapView mapView;

    AMap aMap;

    private LatLng mLocalLatlng;

    @Override
    public String setTitle() {
        return "位置信息";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        BmobGeoPoint bmobGeoPoint = (BmobGeoPoint) getBundle().getSerializable("location");
        mLocalLatlng = new LatLng(bmobGeoPoint.getLatitude(), bmobGeoPoint.getLongitude());
        initBarView();
        mapView = (MapView) findViewById(R.id.map2);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalLatlng, 10));
    }

    private void setUpMap() {
        aMap.addMarker(new MarkerOptions().position(mLocalLatlng));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}
