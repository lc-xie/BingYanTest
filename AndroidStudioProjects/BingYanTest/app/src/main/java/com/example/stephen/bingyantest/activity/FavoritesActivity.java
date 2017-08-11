package com.example.stephen.bingyantest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stephen.bingyantest.R;

public class FavoritesActivity extends AppCompatActivity {

    private ImageView back;
    private TextView topText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //初始化头布局
        topText=(TextView)findViewById(R.id.top_text);
        topText.setText("收藏");
        back=(ImageView)findViewById(R.id.back_book_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
