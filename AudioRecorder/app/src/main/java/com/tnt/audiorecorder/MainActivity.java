package com.tnt.audiorecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORDER_SAMPLE_RATE = 48000 ;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO ;

    private static final String path = "/sdcard/voice48K16bitSTEREO.pcm";

    private Button mStart ;
    private Button mStop ;
    private Button mQuite ;

    private Button mTest ;

    private AudioRecord  mAudioRecorder ;

    private Thread mRecordingThread ;
    private boolean mIsRecording =false ;

    private int mState ;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStart = findViewById(R.id.start_record) ;
        mStop = findViewById(R.id.stop_record) ;
        mQuite = findViewById(R.id.exit) ;

        mTest = findViewById(R.id.test) ;

        ViewClickListener listener = new ViewClickListener() ;
        //initAudioRecorder() ;
        mStart.setOnClickListener(listener);
        mStop.setOnClickListener(listener);
        mQuite.setOnClickListener(listener);

        mTest.setOnClickListener(listener);

        checkRecordPermission() ;


    }


    private void initAudioRecorder () {
        mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING,BufferElements2Rec * BytesPerElement) ;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void startRecording() {
        initAudioRecorder() ;
        if (null == mAudioRecorder){
            Log.i("test","recording is null") ;
            return;
        }
        try{
            mState =mAudioRecorder.getState() ;
            Log.i("test","(0:STATE_UNINITIALIZED  1:STATE_INITIALIZED)state="+mState)  ;
            mAudioRecorder.startRecording();
        }catch (IllegalStateException e ){
            Log.i("test","e"+e.toString()) ;
        }

        if (!mIsRecording) {
            mIsRecording = true ;
            mRecordingThread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");
            mRecordingThread.start();
        }

    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.start_record, !isRecording);
        enableButton(R.id.stop_record, isRecording);
    }
    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }


    private void checkRecordPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100) ;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("test","request code = "+requestCode + "  permissions = "+permissions +"  grantResults = "+grantResults) ;
    }

    private void writeAudioDataToFile() {
            // Write the output audio in byte

            String filePath = "/sdcard/voice48K16bitSTEREO.pcm" ;
            short sData[] = new short[BufferElements2Rec];

            FileOutputStream os = null;
            try {
                os = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("test","file not found ") ;
            }catch (SecurityException e){
                Log.i("test","security "+e.toString()) ;
            }

            while (mIsRecording) {
                // gets the voice output from microphone to byte format

                mAudioRecorder.read(sData, 0, BufferElements2Rec);
                System.out.println("Short wirting to file" + sData.toString());
                try {
                    // // writes the data to file from buffer
                    // // stores the voice buffer
                    byte bData[] = short2byte(sData);
                    os.write(bData, 0, BufferElements2Rec * BytesPerElement);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("test"," os is null") ;

                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

        //convert short to byte
    private byte[] short2byte(short[] sData) {
            int shortArrsize = sData.length;
            byte[] bytes = new byte[shortArrsize * 2];
            for (int i = 0; i < shortArrsize; i++) {
                bytes[i * 2] = (byte) (sData[i] & 0x00FF);
                bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
                sData[i] = 0;
            }
            return bytes;
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != mAudioRecorder) {
            mIsRecording = false;
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;
            mRecordingThread = null;
        }
    }

    private void openZMP6() {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        if (am != null) {
            Log.i("test", "设置双MIC录音 AudioManager.setParameters(\"MIC_TEST=1\");");
            am.setParameters("MIC_TEST=1");
        }
    }

    private void closeZMP6() {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        if (am != null) {
            Log.i("test", "退出双MIC录音 AudioManager.setParameters(\"MIC_TEST=0\");");
            am.setParameters("MIC_TEST=0");
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    class ViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.start_record) {
                Log.i("test","start record") ;
                openZMP6() ;
                startRecording() ;
                enableButtons(true) ;
            }else if (v.getId() == R.id.stop_record) {
                Log.i("test","stop record") ;
                stopRecording();
                enableButtons(false) ;
            }else if (v.getId() == R.id.exit) {
                stopRecording();
                closeZMP6();
                finish();
            }else if (v.getId()==R.id.test) {
                Intent intent = new Intent(MainActivity.this,DhcpActivity.class) ;
                startActivity(intent) ;

            }

        }
    }

}
