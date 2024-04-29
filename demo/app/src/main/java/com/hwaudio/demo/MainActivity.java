package com.hwaudio.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hwaudio.player.AudioPlayerBase;
import com.hwaudio.record.AudioRecorderBase;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private AudioRecorderBase mRecorder;
    private AudioPlayerBase mPlayer;
    private String TAG = "OlabLog";

    private static String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //控制代码r
        Button recordBt = findViewById(R.id.record);
        Button stopBt = findViewById(R.id.stop);
        Button playerBt = findViewById(R.id.player);
        verifyPermissions(this);

        try {
            mRecorder = new AudioRecorderBase(48000);
            mPlayer = new AudioPlayerBase();
        } catch (IOException e) {
            Log.e(TAG, "onCreate: " + e);;
        }

        playerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "start playing");
                mPlayer.startPlaying();
            }
        });

        recordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRecorder.startRecording();
                    Log.d(TAG, "record: start ok");
                } catch (IOException e) {
                    Log.d(TAG, "reorder erro: " + e);
                }

            }
        });
        stopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAll();
            }
        });

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private static void verifyPermissions(Activity activity){
        ArrayList<String> permissionsNotPassed = new ArrayList<String>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotPassed.add(PERMISSIONS[i]);
            }
        }

        if (permissionsNotPassed.size() > 0) {
            String[] request = new String[permissionsNotPassed.size()];
            for(int i=0;i<permissionsNotPassed.size();i++){
                request[i] = permissionsNotPassed.get(i);
            }
            ActivityCompat.requestPermissions(activity, request, 5);
        }
    }

    private void closeAll(){
        try {
            mRecorder.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"onClick: stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeAll();
    }
}
