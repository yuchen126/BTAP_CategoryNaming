package com.example.yuchen.categorynaming;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public int counter = 30;
    Button btn_start;
    Button btn_next;
    TextView textView_timer;
    TextView textView_cate;
    Context context;
    BufferedReader reader;
    String line;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start= (Button) findViewById(R.id.btn_start);
        btn_next= (Button) findViewById(R.id.btn_next);
        textView_timer= (TextView) findViewById(R.id.tv_timer);
        textView_cate = (TextView) findViewById(R.id.tv_cate);

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
                new CountDownTimer(30000, 1000){
                    public void onTick(long millisUntilFinished){
                        textView_timer.setText(String.valueOf(counter));
                        counter--;
                    }
                    public void onFinish(){
                        textView_timer.setText("FINISH!!");
                    }
                }.start();
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
                } else {
                    Toast.makeText(MainActivity.this, "No more category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
