package com.swufestu.vocabulary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SentenceListAdapter extends BaseAdapter {
    //仿照SimpleAdapter的形参列表
    private Context context=null;
    private int resources;
    private ArrayList<HashMap<String,Object>> list=null;
    private String[] from;
    private int[] to;

    public SentenceListAdapter(Context context, int resources, ArrayList<HashMap<String, Object>> list, String[] from, int[] to) {
        super();
        this.context = context;
        this.resources = resources;
        this.list = list;
        this.from = from;
        this.to = to;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) {
        LayoutInflater inflater=LayoutInflater.from(context);
        contentView=inflater.inflate(resources, null);
        TextView text=(TextView)contentView.findViewById(to[0]);
        text.setText((String)(list.get(position).get(from[0])));
        return contentView;
    }
}
