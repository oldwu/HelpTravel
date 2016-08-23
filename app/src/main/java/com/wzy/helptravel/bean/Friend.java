package com.wzy.helptravel.bean;

import cn.bmob.v3.BmobObject;


/**
 * Created by wzy on 2016/7/11.
 */
public class Friend extends BmobObject {

    private User user;
    private User friendUser;

    //拼音
    private transient String pinyin;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
