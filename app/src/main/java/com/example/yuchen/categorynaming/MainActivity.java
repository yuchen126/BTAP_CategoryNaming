package com.example.yuchen.categorynaming;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public int counter;
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= (Button) findViewById(R.id.btn_start);
        textView= (TextView) findViewById(R.id.tv_timer);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new CountDownTimer(30000, 1000){
                    public void onTick(long millisUntilFinished){
                        textView.setText(String.valueOf(counter));
                        counter++;
                    }
                    public  void onFinish(){
                        textView.setText("FINISH!!");
                    }
                }.start();
            }
        });
    }
}
