package com.hwaudio.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorderBase {
    private String TAG = "OlabLog";
    private AudioRecord mAudioRecord;
    private boolean mIsRecording = false;
    private int mSamplingRate = 48000;
    private static final int mChannels = AudioFormat.CHANNEL_IN_STEREO;
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static int mBufferSize = 0;

    // 替换自己的录制路径
    private String mFileOutPath = "/sdcard/dumpData/my_audio.pcm";
    private FileOutputStream outputStream = null;

    public AudioRecorderBase(int samplingRate) throws IOException {
        mSamplingRate = samplingRate;
        //根据自己算法，自行设置录音buffer：480
        mBufferSize = 480 * 2 * 2;

    }

    public void startRecording() throws IOException {
        if (mIsRecording) {
            return;
        }
        init();
        mAudioRecord.startRecording();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeAudioDataToFile();
                } catch (IOException e) {
                    Log.e(TAG, "startRecording: " + e);
                }
            }
        }).start();
    }

    public void stop() throws IOException {
        if (mIsRecording) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mIsRecording = false;
        }
        close();
    }

    // 将音频数据写入文件（或者修改成算法处理）
    private void writeAudioDataToFile() throws IOException {
        Log.d(TAG, "start writeAudioDataToFile");
        byte[] buffer = new byte[mBufferSize];
        int read = 0;
        Log.d(TAG, "writeAudioDataToFile: " + mIsRecording);
        while (mIsRecording && (read = mAudioRecord.read(buffer, 0, buffer.length)) != AudioRecord.ERROR_INVALID_OPERATION) {
            Log.d(TAG, "writeAudioDataToFile: " + buffer[10]);
            outputStream.write(buffer, 0, read);
        }
        Log.d(TAG, "writeAudioDataToFile: over");
        close();
    }


    private void init() throws IOException {
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, mSamplingRate, mChannels, mAudioFormat, mBufferSize);
        createTmpFile(new File(mFileOutPath));

        try {
            outputStream = new FileOutputStream(mFileOutPath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "startRecording: " + e);
        }
        mIsRecording = true;
    }


    private void close() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        Log.d(TAG, "close ok:");
    }

    private void createTmpFile(File file) throws IOException {
        if (!file.exists()) {
            boolean ret = file.createNewFile();
            Log.d(TAG, "createTmpFile: " + ret);
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(new byte[mBufferSize * 2]);
        fos.close();
    }
}

