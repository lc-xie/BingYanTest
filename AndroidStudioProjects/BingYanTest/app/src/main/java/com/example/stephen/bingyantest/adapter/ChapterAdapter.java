package com.example.stephen.bingyantest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stephen.bingyantest.activity.ChapterReadingActivity;
import com.example.stephen.bingyantest.R;

import java.util.List;

/**
 * Created by stephen on 17-7-14.
 */

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {
    private List<String> chapterList;
    private List<String> chapterContentList;

    public ChapterAdapter(List<String> list,List<String> contentList){
        chapterList=list;
        chapterContentList=contentList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bookView;
        TextView chapterName;
        final Context context;

        public ViewHolder(View view){
            super(view);
            bookView=view;
            chapterName=(TextView)view.findViewById(R.id.chapter_name);
            context=view.getContext();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_chapter,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                String chapterName=chapterList.get(position);
                String chapterContent=chapterContentList.get(position);
                Intent intent=new Intent(v.getContext(),ChapterReadingActivity.class);
                intent.putExtra("chapter_name",chapterName);
                intent.putExtra("chapter_content",chapterContent);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String chapterName=chapterList.get(position);
        holder.chapterName.setText(chapterName);
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }
}
