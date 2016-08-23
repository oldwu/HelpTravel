package com.wzy.helptravel.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.wzy.helptravel.R;
import com.wzy.helptravel.utils.AudioRecordManager;
import com.wzy.helptravel.utils.RecordDialogManager;


/**
 * Created by wzy on 16-4-9.
 */
public class AudioRecordButton extends Button implements AudioRecordManager.AudioStateListener {


    private static final int DISTANCE_Y_CANCEL = 100;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    private RecordDialogManager mDialogManager;

    private AudioRecordManager mAudioManager;

    private float mTime;

    private String dir;

    //是否触发longClick
    private boolean mReady;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new RecordDialogManager(context);

        dir = null;
        try {
            dir = Environment.getExternalStorageDirectory() + "/chat_test_audios";
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioManager = AudioRecordManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);


        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }


    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }


    /**
     * 获取音量大小的runnable
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    mDialogManager.showRecordingDialog();
                    isRecording = true;

                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
            }

        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:

                if (isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:


                mDialogManager.tooShort();

                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording || mTime < 0.6f) {
                    System.out.println(mTime);
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurState == STATE_RECORDING) {
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }

                } else if (mCurState == STATE_WANT_TO_CANCEL) {

                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }


        return super.onTouchEvent(event);
    }


    /**
     * 恢复标志位
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mTime = 0;
        mReady = false;
    }

    private boolean wantToCancel(int x, int y) {

        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }


    /**
     * 设置不同状态下的按钮样式
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recode_normal);
                    setText(R.string.recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recode_recording);
                    setText(R.string.recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recode_cancel);
                    setText(R.string.recorder_want_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }


    }


}
