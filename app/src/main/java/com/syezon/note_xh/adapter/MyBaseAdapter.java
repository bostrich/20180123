package com.syezon.note_xh.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by admin on 2016/6/8.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> mList;
    public Context mContext;
    public MyBaseAdapter(List<T> list, Context mContext) {
        this.mList=list;
        this.mContext=mContext;
    }

    public MyBaseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmList(List<T> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
