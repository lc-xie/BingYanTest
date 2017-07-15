package com.example.stephen.bingyantest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class VoicePlayActivity extends AppCompatActivity {

    private ImageView back;
    private TextView topText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_play);
        //初始化头布局
        topText=(TextView)findViewById(R.id.top_text);
        topText.setText("播放");
        back=(ImageView)findViewById(R.id.back_book_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
