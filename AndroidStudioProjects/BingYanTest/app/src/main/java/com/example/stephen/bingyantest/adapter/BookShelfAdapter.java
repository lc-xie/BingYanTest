package com.example.stephen.bingyantest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.stephen.bingyantest.activity.BookReadingActivity;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.bean.Books;
import com.example.stephen.bingyantest.imageThreeCache.ImageCallBack;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;

import java.io.IOException;
import java.util.List;
import java.util.logging.MemoryHandler;

/**
 * Created by stephen on 17-7-5.
 */

public class BookShelfAdapter  extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder> {

    private static final String TAG = BookShelfAdapter.class.getSimpleName();
    public static enum TYPE_BOOK_ITEM{
        TYPE1,TYPE2,TYPE_FOOTER
    }

    private List<Books> booksList;
    ImageTool imageTool;

    static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView bookImage;
        View bookView;
        TextView bookName;
        TextView bookAuthor;
        final Context context;

        public ViewHolder(View view){
            super(view);
            bookView=view;
            bookAuthor=(TextView)view.findViewById(R.id.book_author);
            bookName=(TextView)view.findViewById(R.id.book_name);
            bookImage=(ImageView)view.findViewById(R.id.book_image);
            context=view.getContext();
        }
    }

    public BookShelfAdapter(List<Books> booksList1,ImageTool imageTool){
        this.booksList=booksList1;
        this.imageTool=imageTool;
    }

    @Override
    public int getItemViewType(int position) {
        return position%2==0?TYPE_BOOK_ITEM.TYPE1.ordinal():TYPE_BOOK_ITEM.TYPE2.ordinal();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        if (viewType==TYPE_BOOK_ITEM.TYPE1.ordinal()){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book_1,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book_2,parent,false);
        }
        final ViewHolder holder=new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Books books=booksList.get(position);
                String url=books.getUrl();
                Intent intent=new Intent(view.getContext(),BookReadingActivity.class);
                intent.putExtra("url_data",url);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder mHolder, int position) {
        final Books book=booksList.get(position);
        mHolder.bookAuthor.setText(book.getBookAuthor());
        mHolder.bookName.setText(book.getBookName());
        Bitmap bitmap;
        bitmap = imageTool.getBitmapFromFileOrCache(book.getBookImageUrl());
        if (bitmap == null) {   //缓存和本地都没有该图片
            //通过ImageRequest加载图片
            /*try {
                imageTool.downloadBitmapByImageRequest(book.getBookImageUrl(), new ImageCallBack() {
                    @Override
                    public void imageLoadded(Bitmap bitmap, String tag) {
                        if (bitmap!=null&&mHolder.bookImage!=null){
                            mHolder.bookImage.setImageBitmap(bitmap);
                            Log.d("ImageAdapter","setImage!!!!==========" + book.getBookName());
                        } else {
                            mHolder.bookImage.setImageDrawable(mHolder.context.getResources().getDrawable(R.drawable.book_default,null));
                            Log.d("ImageAdapter","setImage failed!!!!----------" + book.getBookName());
                        }
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }*/

            //通过imageLoader加载图片
            imageTool.downloadBitmapByImageLoader(book.getBookImageUrl(), new
                    ImageTool.bookImageListener(mHolder.bookImage, book.getBookImageUrl()));
        } else {
            mHolder.bookImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

}
