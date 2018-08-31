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
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.activity.VoicePlayActivity;
import com.example.stephen.bingyantest.bean.Voice;
import com.example.stephen.bingyantest.imageThreeCache.ImageCallBack;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 17-7-15.
 */

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder> {
    private List<Voice> voicesList;
    ImageTool imageTool;

    static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView voiceImage;
        View voiceView;
        TextView voiceName;
        TextView voiceAuthor;
        TextView voiceTag;
        final Context context;

        public ViewHolder(View view){
            super(view);
            voiceView=view;
            voiceAuthor=(TextView)view.findViewById(R.id.text_author_voice);
            voiceTag=(TextView)view.findViewById(R.id.text_tag_voice);
            voiceName=(TextView)view.findViewById(R.id.text_name_voice);
            voiceImage=(ImageView)view.findViewById(R.id.image_voice);
            context=view.getContext();
        }
    }

    public VoiceAdapter(List<Voice> voicesList,ImageTool imageTool){
        this.voicesList=voicesList;
        this.imageTool=imageTool;
    }

    @Override
    public VoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_voice, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.voiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Voice voice = voicesList.get(position);
                Gson gson=new Gson();
                String voiceJsonStr= gson.toJson(voice);
                Intent intent = new Intent(view.getContext(), VoicePlayActivity.class);
                intent.putExtra("voiceJsonStr",voiceJsonStr);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder mHolder, int position) {
        Voice voice=voicesList.get(position);
        mHolder.voiceAuthor.setText(voice.getVoiceAuthor());
        mHolder.voiceName.setText(voice.getVoiceName());
        mHolder.voiceTag.setText(voice.getVoiceNumber());
        /*try {
            Bitmap bitmap=imageTool.downloadBitmapByImageRequest(voice.getVoiceImageUrl(), new ImageCallBack() {
                @Override
                public void imageLoadded(Bitmap bitmap, String tag) {
                    if (bitmap!=null&&mHolder.voiceImage!=null){
                        mHolder.voiceImage.setImageBitmap(bitmap);
                        //Log.d("ImageAdapter","setImage!!!!");
                    }
                }
            });
            if (bitmap!=null){
                mHolder.voiceImage.setImageBitmap(bitmap);
                //Log.d("ImageAdapter","setImage!!!!");
            }else {
                mHolder.voiceImage.setImageResource(R.drawable.book_default);
                Log.d("ImageAdapter","setImage failed!!!!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }*/
        //通过imageLoader加载图片
        imageTool.downloadBitmapByImageLoader(voice.getVoiceImageUrl(), new
                ImageTool.bookImageListener(mHolder.voiceImage, voice.getVoiceImageUrl()));
    }

    @Override
    public int getItemCount() {
        return voicesList.size();
    }

}
