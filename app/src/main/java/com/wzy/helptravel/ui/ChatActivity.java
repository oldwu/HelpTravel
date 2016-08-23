package com.wzy.helptravel.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.orhanobut.logger.Logger;
import com.wzy.helptravel.R;
import com.wzy.helptravel.adapter.ChatAdapter;
import com.wzy.helptravel.adapter.OnRecyclerViewListener;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.bean.User;
import com.wzy.helptravel.utils.BitmapTools;
import com.wzy.helptravel.utils.RecordDialogManager;
import com.wzy.helptravel.view.AudioRecordButton;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.bean.BmobIMVideoMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by wzy on 2016/7/13.
 */
public class ChatActivity extends BaseToolBarActivity implements AudioRecordButton.AudioFinishRecorderListener, ObseverListener, MessageListHandler {

    private final int PHOTO_FILE = 0x01;
    private final int PHOTO_CAMERA = 0x02;

    @Bind(R.id.ll_chat)
    LinearLayout llChat;
    @Bind(R.id.btn_chat_add)
    Button chatAdd;
    @Bind(R.id.edit_msg)
    EditText msgEdit;
    @Bind(R.id.btn_msg_voice)
    AudioRecordButton msgVoiceEdit;
    @Bind(R.id.btn_chat_voice)
    Button chatVoice;
    @Bind(R.id.btn_chat_keyboard)
    Button chatKeyBoard;
    @Bind(R.id.btn_chat_send)
    Button chatSend;

    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.rv_chat_list)
    RecyclerView rvChatList;

    private View filePic;
    private View cameraPic;
    private PopupWindow popupWindow;


    private User user;
    private BmobIMConversation conversation;

    private final int MSG_MODE_NORMAL = 0;
    private final int MSG_MODE_TEXT = 1;
    private final int MSG_MODE_VOICE = 2;

    private ChatAdapter adapter;
    protected LinearLayoutManager layoutManager;

    private Uri imageUri;


    @Override
    public String setTitle() {
        return conversation.getConversationTitle();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));

        setMsgMode(MSG_MODE_NORMAL);
        initBarView();
        initBottom();
        initSwipeLayout();
        initPopupWindow();
    }

    @Override
    public void onFinish(float seconds, String filePath) {
        log("录音完成");
        sendVoiceMessage(filePath, seconds);
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (conversation != null) {
            conversation.updateLocalCache();
        }
        hideSoftInputView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_FILE:
                if (resultCode == RESULT_OK) {
                    sendLocalImageMessage(BitmapTools.getRealFilePath(this, data.getData()));
                }
                break;
            case PHOTO_CAMERA:
                if (resultCode == RESULT_OK) {
                    sendLocalImageMessage(BitmapTools.getRealFilePath(this, imageUri));
                }
                break;
        }
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    private void initSwipeLayout() {
        swRefresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rvChatList.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, conversation);
        rvChatList.setAdapter(adapter);
        llChat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llChat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                swRefresh.setRefreshing(true);
                queryMessages(null);
            }
        });
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Logger.i("" + position);
            }

            @Override
            public boolean onItemLongClick(int position) {
                conversation.deleteMessage(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    public void initBottom() {
        msgEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        msgEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    setMsgMode(MSG_MODE_NORMAL);
                } else {
                    setMsgMode(MSG_MODE_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        msgVoiceEdit.setAudioFinishRecorderListener(this);
    }

    private void setMsgMode(int mode) {
        switch (mode) {
            case MSG_MODE_NORMAL:
                chatAdd.setVisibility(View.VISIBLE);
                msgEdit.setVisibility(View.VISIBLE);
                chatVoice.setVisibility(View.VISIBLE);
                chatKeyBoard.setVisibility(View.GONE);
                chatSend.setVisibility(View.GONE);
                msgVoiceEdit.setVisibility(View.GONE);
                break;
            case MSG_MODE_TEXT:
                chatAdd.setVisibility(View.VISIBLE);
                msgEdit.setVisibility(View.VISIBLE);
                chatVoice.setVisibility(View.GONE);
                chatKeyBoard.setVisibility(View.GONE);
                chatSend.setVisibility(View.VISIBLE);
                msgVoiceEdit.setVisibility(View.GONE);
                break;
            case MSG_MODE_VOICE:
                chatAdd.setVisibility(View.VISIBLE);
                msgEdit.setVisibility(View.GONE);
                chatVoice.setVisibility(View.GONE);
                chatKeyBoard.setVisibility(View.VISIBLE);
                chatSend.setVisibility(View.GONE);
                msgVoiceEdit.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onChatAdd(View view) {
        log("chatAdd");
        popupWindow.showAtLocation(llChat, Gravity.BOTTOM, 0, 0);
    }

    public void onChatVoice(View view) {
        setMsgMode(MSG_MODE_VOICE);
    }

    public void onChatKeyBoard(View view) {
        setMsgMode(MSG_MODE_NORMAL);
    }

    public void onChatSend(View view) {
        setMsgMode(MSG_MODE_NORMAL);
        sendMessage();

        msgEdit.setText("");

    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = msgEdit.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            toast("请输入内容");
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        //可设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        conversation.sendMessage(msg, listener);
    }

    /**
     * 直接发送远程图片地址
     */
    public void sendRemoteImageMessage() {
        BmobIMImageMessage image = new BmobIMImageMessage();
        image.setRemoteUrl("http://img.lakalaec.com/ad/57ab6dc2-43f2-4087-81e2-b5ab5681642d.jpg");
        conversation.sendMessage(image, listener);
    }

    /**
     * 发送本地图片地址
     */
    public void sendLocalImageMessage(String path) {
        //正常情况下，需要调用系统的图库或拍照功能获取到图片的本地地址，开发者只需要将本地的文件地址传过去就可以发送文件类型的消息
        BmobIMImageMessage image = new BmobIMImageMessage(path);
        conversation.sendMessage(image, listener);
//        //因此也可以使用BmobIMFileMessage来发送文件消息
//        BmobIMFileMessage file =new BmobIMFileMessage("文件地址");
//        c.sendMessage(file,listener);
    }

    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     * @return void
     * @Title: sendVoiceMessage
     */
    private void sendVoiceMessage(String local, float length) {
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        //可设置额外信息-开发者设置的额外信息，需要开发者自己从extra中取出来
        Map<String, Object> map = new HashMap<>();
        map.put("from", "优酷");
        audio.setExtraMap(map);
        //设置语音文件时长：可选
        audio.setDuration(length);
        conversation.sendMessage(audio, listener);
    }

    /**
     * 发送视频文件
     */
    private void sendVideoMessage() {
        BmobIMVideoMessage video = new BmobIMVideoMessage("/storage/sdcard0/bimagechooser/11.png");
        conversation.sendMessage(video, listener);
    }

    /**
     * 发送地理位置
     */
    public void sendLocationMessage() {
        //测试数据，真实数据需要从地图SDK中获取
        BmobIMLocationMessage location = new BmobIMLocationMessage("广州番禺区", 23.5, 112.0);
        Map<String, Object> map = new HashMap<>();
        map.put("from", "百度地图");
        location.setExtraMap(map);
        conversation.sendMessage(location, listener);
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.i("onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            msgEdit.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            msgEdit.setText("");
            scrollToBottom();
            if (e != null) {
                toast(e.getMessage());
            }
        }
    };


    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        conversation.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Logger.i("聊天页面接收到消息：" + list.size());
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }

    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (conversation != null && event != null && conversation.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                conversation.updateReceiveStatus(msg);
            }
            scrollToBottom();
        } else {
            Logger.i("不是与当前聊天对象的消息");
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
        File outputImage = new File(Environment.getExternalStorageDirectory(), new Date().toString() + ".jpg");
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
        File outputImage = new File(Environment.getExternalStorageDirectory(), new Date().toString() + ".jpg");
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


}
