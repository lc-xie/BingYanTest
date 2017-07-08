package com.example.stephen.bingyantest.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.bean.Books;

import java.util.List;

/**
 * Created by stephen on 17-7-5.
 */

public class BookShelfAdapter  extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder> {

    private List<Books> booksList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImage;
        TextView bookName;
        TextView bookAuthor;

        public ViewHolder(View view){
            super(view);
            bookAuthor=(TextView)view.findViewById(R.id.book_author);
            bookName=(TextView)view.findViewById(R.id.book_name);
            bookImage=(ImageView)view.findViewById(R.id.book_image);
        }
    }

    public BookShelfAdapter(List<Books> booksList1){
        this.booksList=booksList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Books books=booksList.get(position);
        holder.bookAuthor.setText(books.getBookAuthor());
        holder.bookName.setText(books.getBookName());
        holder.bookImage.setImageResource(books.getImageId());
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }
}
