package com.example.stephen.bingyantest;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stephen.bingyantest.adapter.FragmentAdapter;
import com.example.stephen.bingyantest.fragment.ArticalFragment;
import com.example.stephen.bingyantest.fragment.BookshelfFragment;
import com.example.stephen.bingyantest.fragment.VoiceFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> FragmentList;
    private TextView articalPager1,voicePager2,bookshelfPager3;
    private ImageView cursor;
    private LinearLayout linearPagerCursor;
    public static File photoCache;//图片缓存

    private int bmpW = 0; // 游标宽度
    private int offset = 0;// // 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int screenX=0;      //非cursor滑动区的半长度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        photoCache=new File(Environment.getExternalStorageDirectory(), "photoCache");
        if(!photoCache.exists()){
            photoCache.mkdirs();
        }
        this.bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
                .getWidth();                            // 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        linearPagerCursor.measure(0,0);

        // 获取分辨率宽度
        int screenW=dm.widthPixels;
        int linearW = screenW-400;
        this.offset = (linearW /3 - bmpW) / 2;// 计算偏移量
        Log.d("MainActivity",linearW+","+offset+","+bmpW);

        screenX=(screenW-linearW)/2;

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);

        articalPager1.setOnClickListener(new MyOnClickListener(0));
        voicePager2.setOnClickListener(new MyOnClickListener(1));
        bookshelfPager3.setOnClickListener(new MyOnClickListener(2));

        FragmentList=new ArrayList<Fragment>();
        FragmentList.add(new ArticalFragment());
        FragmentList.add(new VoiceFragment());
        FragmentList.add(new BookshelfFragment());

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentAdapter=new FragmentAdapter(fragmentManager,FragmentList);

        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        viewPager.setCurrentItem(0);
    }

    private void init(){
        articalPager1=(TextView)findViewById(R.id.viewpager_item_artical);
        voicePager2=(TextView)findViewById(R.id.viewpager_item_voice);
        bookshelfPager3=(TextView)findViewById(R.id.viewpager_item_bookshelf);

        viewPager=(ViewPager)findViewById(R.id.view_pager);

        cursor=(ImageView)findViewById(R.id.cursor);

        linearPagerCursor=(LinearLayout)findViewById(R.id.linear_cursor);

    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;

        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);

        }
    }


    //页面改变监听器
    public class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡偏移量
        int two=2*one;

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            cleanTextBold();
            switch (arg0){
                case 0:
                    setPagerTextBold(0);
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one,0, 0, 0);
                    }else if(currIndex==2){
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    setPagerTextBold(1);
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, one, 0, 0);
                    }else if(currIndex==2){
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    setPagerTextBold(2);
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }else if(currIndex==0){
                        animation = new TranslateAnimation(0, two, 0, 0);
                    }
                    break;
                default:break;
            }

            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(400);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    //将text中的高亮取消
    private void cleanTextBold(){
        articalPager1.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
        voicePager2.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
        bookshelfPager3.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
    }
    //设置某一页的text高亮
    private void setPagerTextBold(int i){
        switch (i){
            case 0:
                articalPager1.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            case 1:
                voicePager2.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            case 2:
                bookshelfPager3.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            default:break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空缓存
        File[] files = photoCache.listFiles();
        for(File file :files){
            file.delete();
        }
        photoCache.delete();
    }
}
