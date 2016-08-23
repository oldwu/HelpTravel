package com.wzy.helptravel.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wzy on 16-4-9.
 */
public class AudioRecordManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioRecordManager mInstance;

    private boolean isPrepared;

    private AudioRecordManager(String dir) {
        mDir = dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        public void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static AudioRecordManager getInstance(String dir) {

        if (mInstance == null) {
            synchronized (AudioRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManager(dir);
                }
            }
        }
        return mInstance;
    }


    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();

            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());

            //设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

            //设置音频的编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();

            mMediaRecorder.start();
            isPrepared = true;

            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 随机生成文件名称
     *
     * @return
     */
    private String generateFileName() {

        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {

        if (isPrepared) {
            //getMaxAmplitude 最大值32767
            try {
                int level = (maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1);
                return level;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {
        release();

        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
        }
        mCurrentFilePath = null;

    }


}
