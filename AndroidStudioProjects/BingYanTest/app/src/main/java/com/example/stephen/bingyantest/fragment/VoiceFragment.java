package com.example.stephen.bingyantest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stephen.bingyantest.R;

/**
 * Created by stephen on 17-7-2.
 */

public class VoiceFragment extends Fragment {
    private View view;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_voice,container,false);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_voice);

        return view;
    }
}
