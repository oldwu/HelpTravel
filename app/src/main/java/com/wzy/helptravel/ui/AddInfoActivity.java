package com.wzy.helptravel.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.InfoModel;
import com.wzy.helptravel.dao.i.UpdateInfoListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by wzy on 2016/7/18.
 */
public class AddInfoActivity extends BaseToolBarActivity implements AMap.OnMapClickListener,
        AMap.OnMapLongClickListener, AMap.OnCameraChangeListener, LocationSource,
        AMapLocationListener {

    private final int PHOTO_FILE = 0x01;
    private final int PHOTO_CAMERA = 0x02;
    private final int NICK_EDIT = 0x03;
    private final int SIGNATURE_EDIT = 0x04;
    private final int AREA_EDIT = 0x05;
    private final int CROP_CAMERA_PHOTO = 0x06;
    private final int CROP_FILE_PHOTO = 0x07;

    @Bind(R.id.info_edit)
    EditText infoEdit;
    @Bind(R.id.info_title)
    EditText infoTitle;
    @Bind(R.id.info_img)
    ImageView infoImg;
    @Bind(R.id.add_info)
    View parent;
    @Bind(R.id.info_type)
    RadioGroup infoType;


    MapView mapView;

    AMap aMap;

    private View filePic;
    private View cameraPic;
    private PopupWindow popupWindow;
    private Uri imageUri;
    private User user;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLng mLocalLatlng;

    private String infoTypeValue = "帮助";


    @Override
    public String setTitle() {
        return "发布信息";
    }

    @Override
    public Object setRight() {
        return "发布";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        initBarView();
        initPopupWindow();
        user = BmobUser.getCurrentUser(this, User.class);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        infoType.check(R.id.help);
        infoType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.help) {
                    infoTypeValue = "帮助";
                } else {
                    infoTypeValue = "求助";
                }
            }
        });


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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * amap添加一些事件监听器
     */
    private void setUpMap() {
//        aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器
//        aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器


        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式，参见类AMap。
//初始化定位
        mlocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mlocationClient.setLocationListener(this);
    }

    @Override
    public void initBarView() {
        super.initBarView();
    }

    @Override
    public ToolBarListener setToolBarListener() {
        return new ToolBarListener() {
            @Override
            public void clickLeft() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddInfoActivity.this);
                builder.setTitle("确定退出发布信息么？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("否", null);

                builder.create().show();
            }

            @Override
            public void clickRight() {
                toast("上传");
                HelpInfo info = new HelpInfo();
                BmobGeoPoint location = new BmobGeoPoint(mLocalLatlng.longitude, mLocalLatlng.latitude);
                info.setTitle(infoTitle.getText().toString().trim());
                info.setLocation(location);
                info.setPicture(imageUri != null ? imageUri.toString() : null);
                info.setInfo(infoEdit.getText().toString().trim());
                info.setUserId(user.getObjectId());
                info.setType(infoTypeValue);
                InfoModel.getInstance().addInfo(info, new UpdateInfoListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            toast("发布成功");
                        } else {
                            toast(e.getErrorCode() + e.getMessage());
                        }
                    }
                });

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_FILE:
                if (resultCode == RESULT_OK) {
                    picCropIntent(data.getData(), imageUri, CROP_FILE_PHOTO);
                    log(imageUri.toString());
                }
                break;
            case PHOTO_CAMERA:
                if (resultCode == RESULT_OK) {
                    picCropIntent(imageUri, imageUri, CROP_CAMERA_PHOTO);
                    log(imageUri.toString());
                }
                break;
            case CROP_FILE_PHOTO:
                if (resultCode == RESULT_OK) {
                    infoImg.setImageBitmap(getBitmapFromUri(imageUri));
                }
                break;
            case CROP_CAMERA_PHOTO:
                if (resultCode == RESULT_OK) {
                    infoImg.setImageBitmap(getBitmapFromUri(imageUri));
                }
                break;
        }
    }

    /**
     * popupwindow初始化
     */
    public void initPopupWindow() {
        View contentView = getLayoutInflater().inflate(R.layout.popwindow_picture_select, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        /**设置PopupWindow弹出和退出时候的动画效果*/
        popupWindow.setAnimationStyle(R.style.animation);
        filePic = contentView.findViewById(R.id.select_from_file);
        cameraPic = contentView.findViewById(R.id.select_from_camera);
        filePic.setOnClickListener(new viewOnClickListener());
        cameraPic.setOnClickListener(new viewOnClickListener());
    }

    public void onInfoImg(View view) {
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        hideSoftInputView();
    }

    /**
     * 启动图片裁剪任意矩形
     */
    private void picCropIntent(Uri resourceUri, Uri cropUri, int requestCode) {

        if (!resourceUri.equals(cropUri)) {
            copyFile(resourceUri.getPath(), cropUri.getPath(), true);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(resourceUri, "image/*");
        intent.putExtra("crop", true);
//        intent.putExtra("aspectX", 16);// 裁剪框比例
//        intent.putExtra("aspectY", 9);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 根据uri获取bitmap
     *
     * @param imageUri
     * @return
     */
    private Bitmap getBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        String[] strings = cameraPosition.toString().split("\\(");
        String[] strings2 = strings[1].split("\\)");
        String[] locations = strings2[0].split(",");
        aMap.clear();
        aMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]))));
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        String[] strings = cameraPosition.toString().split("\\(");
        String[] strings2 = strings[1].split("\\)");
        String[] locations = strings2[0].split(",");
        aMap.clear();
        mLocalLatlng = new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]));
        aMap.addMarker(new MarkerOptions().position(mLocalLatlng));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        log(latLng.longitude + "" + latLng.latitude);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.clear();
                mLocalLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalLatlng, 15));
                mlocationClient.stopLocation();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();

        }
//        aMap.clear();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * popupWindow的按键监听
     */
    class viewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.select_from_file:
                    selectPicFromFile();
                    break;
                case R.id.select_from_camera:
                    selectPicFromCamera();
                    break;
                default:
                    break;
            }
            popupWindow.dismiss();
        }
    }

    /**
     * 从文件中选取图片
     */
    private void selectPicFromFile() {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "/HelpTravel/info/" + user.getUsername() + new Date() + "_info.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO_FILE);
    }

    /**
     * 从相机中拍摄图片
     */
    private void selectPicFromCamera() {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "/HelpTravel/info/" + user.getUsername() + new Date() + "_info.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_CAMERA);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param destFileName 目标文件名
     * @param overlay      如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    private boolean copyFile(String srcFileName, String destFileName,
                             boolean overlay) {
        File srcFile = new File(srcFileName);
        String MESSAGE = "";
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            MESSAGE = "源文件：" + srcFileName + "不存在！";
            return false;
        } else if (!srcFile.isFile()) {
            MESSAGE = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
