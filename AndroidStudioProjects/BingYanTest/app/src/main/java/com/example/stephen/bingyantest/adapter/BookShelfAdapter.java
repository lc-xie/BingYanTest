package com.example.stephen.bingyantest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stephen.bingyantest.activity.BookReadingActivity;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.bean.Book;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;

import java.util.List;

/**
 * Created by stephen on 17-7-5.
 */

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder> {

    private static final String TAG = BookShelfAdapter.class.getSimpleName();

    public static enum TYPE_BOOK_ITEM {
        TYPE1, TYPE2, TYPE_FOOTER
    }

    private List<Book> booksList;
    ImageTool imageTool;

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView bookImage;
        View bookView;
        TextView bookName;
        TextView bookAuthor;
        final Context context;

        public ViewHolder(View view) {
            super(view);
            bookView = view;
            bookAuthor = (TextView) view.findViewById(R.id.book_author);
            bookName = (TextView) view.findViewById(R.id.book_name);
            bookImage = (ImageView) view.findViewById(R.id.book_image);
            context = view.getContext();
        }
    }

    public BookShelfAdapter(List<Book> booksList1) {
        this.booksList = booksList1;
        this.imageTool = ImageTool.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_BOOK_ITEM.TYPE1.ordinal() : TYPE_BOOK_ITEM.TYPE2.ordinal();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        if (viewType == TYPE_BOOK_ITEM.TYPE1.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book_1, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book_2, parent, false);
        }
        final ViewHolder holder = new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Book books = booksList.get(position);
                String url = books.getUrl();
                Intent intent = new Intent(view.getContext(), BookReadingActivity.class);
                intent.putExtra("url_data", url);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder mHolder, int position) {
        Book book = booksList.get(position);
        mHolder.bookAuthor.setText(book.getBookAuthor());
        mHolder.bookName.setText(book.getBookName());
        imageTool.getBitmap(mHolder.bookImage, book.getBookImageUrl());
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

}
