package com.wzy.helptravel.dao;

import android.net.Uri;

import com.wzy.helptravel.bean.HelpInfo;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.dao.i.QueryInfoListener;
import com.wzy.helptravel.dao.i.UpdateInfoListener;
import com.wzy.helptravel.utils.BitmapTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by wzy on 2016/7/18.
 */
public class InfoModel extends BaseModel {

    private static InfoModel infoModel = new InfoModel();

    private InfoModel() {
    }

    public static InfoModel getInstance() {
        return infoModel;
    }

    /**
     * 添加帮助或求助信息
     *
     * @param info
     * @param listener
     */
    public void addInfo(final HelpInfo info, final UpdateInfoListener listener) {

        final HelpInfo updateinfo = new HelpInfo();
        updateinfo.setTitle(info.getTitle());
        updateinfo.setLocation(info.getLocation());
        updateinfo.setInfo(info.getInfo());
        updateinfo.setUserId(info.getUserId());
        updateinfo.setType(info.getType());
        if (info.getPicture() != null) {
            Uri uri = Uri.parse(info.getPicture());
            final BmobFile avatarFile = new BmobFile(new File(BitmapTools.getRealFilePath(getContext(), uri)));
            avatarFile.uploadblock(getContext(), new UploadFileListener() {
                @Override
                public void onSuccess() {
                    updateinfo.setPicture(avatarFile.getUrl());
                    updateinfo.save(getContext(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            listener.done(null);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            listener.done(new BmobException(i, s));
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    System.out.println(i + s);
                }
            });
        } else {
            updateinfo.save(getContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    listener.done(null);
                }

                @Override
                public void onFailure(int i, String s) {
                    listener.done(new BmobException(i, s));
                }
            });
        }
    }

    /**
     * 删除帮助或求助信息
     *
     * @param info
     * @param listener
     */
    public void deleteInfo(HelpInfo info, final UpdateInfoListener listener) {
        info.delete(getContext(), new DeleteListener() {
            @Override
            public void onSuccess() {
                listener.done(null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(new BmobException(i, s));
            }
        });
    }

    /**
     * 更新信息
     *
     * @param info
     * @param listener
     */
    public void updateInfo(final HelpInfo info, final UpdateInfoListener listener) {

        info.update(getContext(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.done(null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(new BmobException(i, s));
            }
        });
    }

    /**
     * 查询信息
     *
     * @param userId
     * @param listener
     */
    public void queryInfoByUser(String userId, final FindListener<HelpInfo> listener) {
        BmobQuery<HelpInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", userId);
        query.findObjects(getContext(), new FindListener<HelpInfo>() {
            @Override
            public void onSuccess(List<HelpInfo> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(-1, "查不到信息");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    public void queryInfoById(String id, final FindListener<HelpInfo> listener) {
        BmobQuery<HelpInfo> query = new BmobQuery<>();
        query.getObject(getContext(), id, new GetListener<HelpInfo>() {
            @Override
            public void onSuccess(HelpInfo info) {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


    /**
     * @param type
     * @param location
     * @param listener
     */
    public void queryAllInfo(String type, BmobGeoPoint location, final FindListener<HelpInfo> listener) {
        BmobQuery<HelpInfo> query = new BmobQuery<>();
        query.order("-updatedAt");
        if (type != null)
            query.addWhereContains("type", type);
        if (location != null)
            query.addWhereNear("location", location);
        query.setLimit(200);    //获取最接近用户地点的10条数据
        query.findObjects(getContext(), new FindListener<HelpInfo>() {
            @Override
            public void onSuccess(List<HelpInfo> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(-1, "查不到信息");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

}
