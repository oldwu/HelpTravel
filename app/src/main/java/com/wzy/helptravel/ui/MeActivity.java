package com.wzy.helptravel.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.base.ImageLoaderFactory;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.UserModel;
import com.wzy.helptravel.dao.i.UpdateCacheListener;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by wzy on 2016/7/8.
 */
public class MeActivity extends BaseToolBarActivity {

    private final int PHOTO_FILE = 0x01;
    private final int PHOTO_CAMERA = 0x02;
    private final int NICK_EDIT = 0x03;
    private final int SIGNATURE_EDIT = 0x04;
    private final int AREA_EDIT = 0x05;
    private final int CROP_CAMERA_PHOTO = 0x06;
    private final int CROP_FILE_PHOTO = 0x07;

    private boolean hasChanged = false;

    @Bind(R.id.me)
    View parent;

    @Bind(R.id.avatar_me_view)
    View avatarSet;
    @Bind(R.id.nick_me_view)
    View nickSet;
    @Bind(R.id.gender_me_view)
    View genderSet;
    @Bind(R.id.area_me_view)
    View areaSet;
    @Bind(R.id.birth_me_view)
    View birthSet;
    @Bind(R.id.signature_me_view)
    View signatureSet;
    @Bind(R.id.avatar_me_img)
    ImageView avatar;
    @Bind(R.id.nickname_me_tv)
    TextView nickname;
    @Bind(R.id.gender_me_tv)
    TextView gender;
    @Bind(R.id.area_me_tv)
    TextView area;
    @Bind(R.id.birth_me_tv)
    TextView birth;
    @Bind(R.id.signature_me_tv)
    TextView signature;

    private View filePic;
    private View cameraPic;
    private PopupWindow popupWindow;

    private Uri imageUri;

    private User user;

    private boolean canChanged = true;


    @Override
    public String setTitle() {
        return "我的信息";
    }

    @Override
    public Object setRight() {

        if (canChanged)
            return "保存";
        else
            return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getBundle().getSerializable("user");
        try {
            canChanged = getBundle().getBoolean("canChanged");
        } catch (Exception e) {
            canChanged = true;
        }
        setContentView(R.layout.activity_me);
        initBarView();
        initPopupWindow();


        initClickState();


    }

    @Override
    public ToolBarListener setToolBarListener() {
        return new ToolBarListener() {
            @Override
            public void clickLeft() {

                if (hasChanged) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
                    builder.setTitle("确定退出么？");
                    builder.setMessage("还未保存");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.setNegativeButton("否", null);

                    builder.create().show();
                } else {
                    finish();
                }
            }

            @Override
            public void clickRight() {
                toast("保存");
                User user = UserModel.getInstance().getCurrentUser();
                user.setAvatar(imageUri != null ? imageUri.toString() : null);
                user.setNickName(nickname.getText().toString().trim());
                user.setGender(gender.getText().toString().trim());
                user.setBirthday(birth.getText().toString().trim());
                user.setArea(area.getText().toString().trim());
                user.setSignature(signature.getText().toString().trim());
                final ProgressDialog proDialog = android.app.ProgressDialog.show(MeActivity.this, "信息保存", "信息保存中！");
                UserModel.getInstance().updateUserInfo(user, new UpdateCacheListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            toast("更新成功");
                            proDialog.dismiss();
                            hasChanged = false;
                        } else {
                            toast(e.getMessage());
                            proDialog.dismiss();
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
                    avatar.setImageBitmap(getBitmapFromUri(imageUri));
                }
                break;
            case CROP_CAMERA_PHOTO:
                if (resultCode == RESULT_OK) {
                    avatar.setImageBitmap(getBitmapFromUri(imageUri));
                }
                break;
            case NICK_EDIT:
                nickname.setText(data.getExtras().get("nickName").toString());
                break;
            case AREA_EDIT:
                area.setText(data.getExtras().get("area").toString());
                break;
            case SIGNATURE_EDIT:
                signature.setText(data.getExtras().get("signature").toString());
                break;
        }
        hasChanged = true;
    }

    @Override
    protected void initView() {
        super.initView();
        ImageLoaderFactory.getLoader().load(avatar, user.getAvatar(), R.mipmap.default_avator, null);
        nickname.setText(user.getNickName());
        gender.setText(user.getGender());
        area.setText(user.getArea());
        birth.setText(user.getBirthday());
        signature.setText(user.getSignature());

    }

    private void initClickState() {
        if (!canChanged) {
            avatarSet.setEnabled(false);
            nickSet.setEnabled(false);
            genderSet.setEnabled(false);
            areaSet.setEnabled(false);
            signatureSet.setEnabled(false);
            birthSet.setEnabled(false);
        } else {
            avatarSet.setEnabled(true);
            nickSet.setEnabled(true);
            genderSet.setEnabled(true);
            areaSet.setEnabled(true);
            signatureSet.setEnabled(true);
            birthSet.setEnabled(true);
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

    /**
     * 设置头像监听
     *
     * @param v
     */
    public void onSetAvatar(View v) {
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 设置昵称监听
     *
     * @param v
     */
    public void onSetNick(View v) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("nickName", nickname.getText());
        startActivityForResult(NickEditActivity.class, bundle, NICK_EDIT);

    }

    /**
     * 设置性别监听
     *
     * @param v
     */
    public void onSetgender(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"男", "女"}, gender.getText().toString().trim().equals("男") ? 0 : 1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    gender.setText("男");
                    dialog.dismiss();
                } else {
                    gender.setText("女");
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        hasChanged = true;
    }

    /**
     * 设置地区监听
     *
     * @param v
     */
    public void onSetArea(View v) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("area", area.getText());
        startActivityForResult(AreaActivity.class, bundle, AREA_EDIT);
    }

    /**
     * 设置生日监听
     *
     * @param v
     */
    public void onSetBirth(View v) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "";
                if (monthOfYear < 10)
                    date = date + "-0" + (monthOfYear + 1);
                else
                    date = date + "-" + (monthOfYear + 1);
                if (dayOfMonth < 10)
                    date = date + "-0" + dayOfMonth;
                else
                    date = date + "-" + dayOfMonth;
                birth.setText(date);
            }
        }, year, month, day).show();
        hasChanged = true;
    }


    /**
     * 设置签名监听
     *
     * @param v
     */
    public void onSetSignature(View v) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("signature", signature.getText());
        startActivityForResult(SignatureActivity.class, bundle, SIGNATURE_EDIT);
    }


    /**
     * 启动图片裁剪，剪成正方形
     */
    private void picCropIntent(Uri resourceUri, Uri cropUri, int requestCode) {

        if (!resourceUri.equals(cropUri)) {
            copyFile(resourceUri.getPath(), cropUri.getPath(), true);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(resourceUri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
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
        File outputImage = new File(Environment.getExternalStorageDirectory(), "/HelpTravel/avatar/" + user.getUsername() + "_avatar.jpg");
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
        File outputImage = new File(Environment.getExternalStorageDirectory(), "/HelpTravel/avatar/" + user.getUsername() + "_avatar.jpg");
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
