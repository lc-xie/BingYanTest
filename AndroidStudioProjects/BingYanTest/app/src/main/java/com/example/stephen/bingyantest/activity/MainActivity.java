package com.example.stephen.bingyantest.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.FragmentAdapter;
import com.example.stephen.bingyantest.fragment.ArticleFragment;
import com.example.stephen.bingyantest.fragment.BookshelfFragment;
import com.example.stephen.bingyantest.fragment.VoiceFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private TextView articlePager1, voicePager2, bookshelfPager3;
    private ImageView cursor;               //游标
    private LinearLayout linearPagerCursor;
    public static File photoCache;          //图片缓存

    private int bmpW = 0;                   // 游标宽度
    private int offset = 0;                 // 动画图片偏移量
    private int currIndex = 0;              // 当前页卡编号
    private int linearX;                    //cursor起始位置距离屏幕左边距离

    private Toolbar toolbar;
    public static final String GET_TODAY_ARTICAL = "com.example.stephen.bingyantest.action.GET_TODAY_ARTICAL";
    public static final String GET_RANDOM_ARTICAL = "com.example.stephen.bingyantest.action.GET_RANDOM_ARTICAL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //游标相关设置
        this.bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
                .getWidth();                            // 获取游标宽度
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获取分辨率宽度
        int width = dm.widthPixels;
        //获取密度
        float density = dm.density;
        //宽度，单位为dp
        //int screenWidth=(int) (width/density);//411
        //linearPagerCursor宽度
        int linearW = (int) (200 * density);
        //偏移量
        offset = (linearW / 3 - bmpW) / 2;
        linearX = (width - linearW) / 2 + offset;
        //图片三级缓存
        photoCache = new File(Environment.getExternalStorageDirectory(), "photoCache");
        if (!photoCache.exists()) {
            photoCache.mkdirs();
        }

        Matrix matrix = new Matrix();
        //matrix.postTranslate(offset+linearX, 0);//移动至(offset,0)处
        cursor.setImageMatrix(matrix);
        //将cursor设至起始位置
        //cursor.setX(linearX);
        Animation animation = new TranslateAnimation(0, linearX, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(1);
        cursor.startAnimation(animation);


        articlePager1.setOnClickListener(new MyOnClickListener(0));
        voicePager2.setOnClickListener(new MyOnClickListener(1));
        bookshelfPager3.setOnClickListener(new MyOnClickListener(2));

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new ArticleFragment());
        fragmentList.add(new VoiceFragment());
        fragmentList.add(new BookshelfFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, fragmentList);

        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
    }

    /**
     * 控件初始化
     */
    private void init() {
        //三个主页面的textview
        articlePager1 = (TextView) findViewById(R.id.viewpager_item_artical);
        voicePager2 = (TextView) findViewById(R.id.viewpager_item_voice);
        bookshelfPager3 = (TextView) findViewById(R.id.viewpager_item_bookshelf);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //滑动游标
        cursor = (ImageView) findViewById(R.id.cursor);
        //滑动游标所在linearLayout
        linearPagerCursor = (LinearLayout) findViewById(R.id.linear_cursor);
        //toolbar初始化
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        int two = 2 * one;

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            cleanTextBold();
            switch (arg0) {
                case 0:
                    setPagerTextBold(0);
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one + linearX, linearX, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two + linearX, linearX, 0, 0);
                    }
                    break;
                case 1:
                    setPagerTextBold(1);
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(linearX, one + linearX, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two + linearX, one + linearX, 0, 0);
                    }
                    break;
                case 2:
                    setPagerTextBold(2);
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one + linearX, two + linearX, 0, 0);
                    } else if (currIndex == 0) {
                        animation = new TranslateAnimation(linearX, two + linearX, 0, 0);
                    }
                    break;
                default:
                    break;
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
    private void cleanTextBold() {
        articlePager1.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
        voicePager2.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
        bookshelfPager3.setTextColor(getResources().getColor(R.color.viewpagerTextLow));
    }

    //设置某一页的text高亮
    private void setPagerTextBold(int i) {
        switch (i) {
            case 0:
                articlePager1.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            case 1:
                voicePager2.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            case 2:
                bookshelfPager3.setTextColor(getResources().getColor(R.color.viewpagerTextHigh));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //右上角菜单键事件响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.today_article:
                ((ArticleFragment) fragmentList.get(0)).showTodayArticle();
                if (currIndex != 0) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.random_article:
                ((ArticleFragment) fragmentList.get(0)).showRandomArticle();
                if (currIndex != 0) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.favorites:
                Intent toFavoriteActivityIntent = new Intent(this, FavoritesActivity.class);
                startActivity(toFavoriteActivityIntent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空缓存
        File[] files = photoCache.listFiles();
        for (File file : files) {
            file.delete();
        }
        photoCache.delete();
    }

}
