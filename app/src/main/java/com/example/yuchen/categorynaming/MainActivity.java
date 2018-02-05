package com.example.yuchen.categorynaming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public int counter;
    Button btn_start;
    Button btn_next;
    Button btn_play;
    Button btn_stopPlay;
    TextView textView_timer;
    TextView textView_cate;
    Context context;
    BufferedReader reader;
    String line;
    private String outputfile = null;
    private MediaRecorder mediaRecorder ;
    private MediaPlayer mediaPlayer ;
    Date createdTime = new Date();
    public static final int RequestPermissionCode = 1;
    boolean isCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start= (Button) findViewById(R.id.btn_start);
        btn_next= (Button) findViewById(R.id.btn_next);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stopPlay = (Button) findViewById(R.id.btn_stopPlay);
        textView_timer= (TextView) findViewById(R.id.tv_timer);
        textView_cate = (TextView) findViewById(R.id.tv_cate);

        btn_play.setEnabled(false);
        btn_stopPlay.setEnabled(false);


        try {
            InputStream in = this.getAssets().open("category.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            line = reader.readLine();

        } catch(Exception e) {
            System.out.println("CANNOT READ FILE");
        }
        textView_cate.setText(line);

        btn_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                recordVoice();
                timer();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    line = reader.readLine();
                } catch(Exception e) {
                    System.out.println("CANNOT READ FILE");
                }
                if (line != null) {
                    textView_cate.setText(line);
                    timer();
                } else {
                    Toast.makeText(MainActivity.this, "No more category", Toast.LENGTH_SHORT).show();
                    btn_play.setEnabled(true);
                    stopRecording();
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                btn_start.setEnabled(false);
                btn_next.setEnabled(false);
                btn_play.setEnabled(false);
                btn_stopPlay.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputfile);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Playing Audio", Toast.LENGTH_LONG).show();
            }
        });

        btn_stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                btn_stopPlay.setEnabled(false);
                btn_start.setEnabled(true);
                btn_next.setEnabled(false);
                btn_play.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }
    public void recordVoice() {
        if(checkPermission()) {
            File f = new File(Environment.getExternalStorageDirectory() + "/CateAudioRecorded/");
            if(!f.isDirectory()) {
                File audioDirct = new File(Environment.getExternalStorageDirectory() + "/CateAudioRecorded/");
                audioDirct .mkdirs();
            }
            outputfile = Environment.getExternalStorageDirectory() + "/CateAudioRecorded/" + createdTime + "CateAudioRecorded.mp3";

            MediaRecorderReady();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btn_start.setEnabled(false);
            Toast.makeText(MainActivity.this, "Start Recording", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/CateAudioRecorded/")));
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputfile);
    }

    public void stopRecording() {
        isCancel = true;
        textView_timer.setText(String.valueOf(30));
        mediaRecorder.stop();
        btn_play.setEnabled(true);
        btn_start.setEnabled(true);
        btn_next.setEnabled(true);

        Toast.makeText(MainActivity.this, "Recorded Successfully", Toast.LENGTH_LONG).show();
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void timer() {
        counter = 30;
        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                if (isCancel) {
                    this.cancel();
                    System.out.println("canceled");
                } else {
                    btn_next.setEnabled(false);
                    textView_timer.setText(String.valueOf(counter));
                    counter--;
                    System.out.println("counter" + counter);
                }
            }
            public void onFinish(){
                textView_timer.setText("FINISH!!");
                btn_next.setEnabled(true);
            }
        }.start();
    }
}

