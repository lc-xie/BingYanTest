package com.example.stephen.bingyantest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChapterReadingActivity extends AppCompatActivity {

    private ImageView back;
    private TextView topText;
    private TextView chapterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_reading);

        Intent intent=getIntent();
        String chapterName=intent.getStringExtra("chapter_name");
        String chapterContent=intent.getStringExtra("chapter_content");

        topText=(TextView)findViewById(R.id.top_text);
        topText.setText(chapterName);
        back=(ImageView)findViewById(R.id.back_book_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chapterText=(TextView)findViewById(R.id.chapter_text);
        chapterText.setText(chapterContent);

    }
}
