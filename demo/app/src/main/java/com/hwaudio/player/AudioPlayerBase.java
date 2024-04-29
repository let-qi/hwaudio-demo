package com.hwaudio.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayerBase {
    private String TAG = "OlabLog";

    private AudioTrack mAudioPlayer = null;

    private String mFile = "/sdcard/dumpData/音频R.pcm";
    private  boolean mIsplaying = false;

    private FileInputStream pcmReadStream = null;
    private int mSampleRate = 48000;
    private int mChannels = AudioFormat.CHANNEL_OUT_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannels, mAudioFormat);

    public AudioPlayerBase() throws FileNotFoundException {
        if (mBufferSize < 0) {
            throw new IllegalArgumentException("Min Buffer Size not supported");
        }
    }

    public void stop() throws IOException {
        if (mAudioPlayer != null){
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer = null;
            mIsplaying = false;
        }
        close();
    }

    public void startPlaying(){
        if (mIsplaying){
            return;
        }
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playing();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void init() {
        if (mAudioPlayer == null){
            mAudioPlayer = new AudioTrack(AudioManager.STREAM_NOTIFICATION, mSampleRate, mChannels, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
        }

        try{
            pcmReadStream = new FileInputStream(mFile);
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "init: " + e);
        }
        mIsplaying = true;
        Log.d(TAG, "init ok");

    }

    private void playing() throws IOException {
        mAudioPlayer.play();
        byte[] audioBuffer = new byte[mBufferSize];
        while (pcmReadStream.read(audioBuffer) > 0 && mAudioPlayer.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            Log.d(TAG, "playing: " + audioBuffer[10]);
            mAudioPlayer.write(audioBuffer, 0, audioBuffer.length) ;
        }
        stop();
    }
    private void close() throws IOException {
        if (pcmReadStream != null) {
            pcmReadStream.close();
            pcmReadStream = null;
        }
    }
}