package com.syezon.note_xh.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by admin on 2016/6/13.
 */
public class MyBaseViewPageAdapter extends PagerAdapter {
    public Context mContext;
    public List<View> mList;
    public onItemClickListener onItemClickListener1;

    public MyBaseViewPageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public MyBaseViewPageAdapter(Context mContext, List<View> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setmList(List<View> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener1(onItemClickListener onItemClickListener1) {
        this.onItemClickListener1 = onItemClickListener1;
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mList.get(position));
        mList.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener1!=null){
                    onItemClickListener1.onItemClickListener();
                }
            }
        });
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(mList.get(position));
    }

    public interface onItemClickListener{
        void onItemClickListener();

    }
}
