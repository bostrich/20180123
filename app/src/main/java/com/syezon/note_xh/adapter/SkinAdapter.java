package com.syezon.note_xh.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.syezon.note_xh.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/29.
 */
public class SkinAdapter extends MyBaseAdapter<String> {
    GridView gridView;
    public SkinAdapter(List<String> list, Context mContext) {
        super(list, mContext);
    }

    public SkinAdapter(Context mContext,GridView gridView) {
        super(mContext);
        this.gridView=gridView;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_skin, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        int checkedPosition=gridView.getCheckedItemPosition();
        switch (position){
            case 0:
                viewHolder.bgIv.setBackgroundResource(R.drawable.pink_skin);
                viewHolder.checkIv.setBackgroundResource(R.mipmap.check_pink);
                if(0==checkedPosition){
                    viewHolder.checkIv.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.checkIv.setVisibility(View.GONE);
                }
                break;
            case 1:
                viewHolder.bgIv.setBackgroundResource(R.drawable.green_skin);
                viewHolder.checkIv.setBackgroundResource(R.mipmap.check_green);
                if(1==checkedPosition){
                    viewHolder.checkIv.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.checkIv.setVisibility(View.GONE);
                }
                break;
            case 2:
                viewHolder.bgIv.setBackgroundResource(R.drawable.gray_skin);
                viewHolder.checkIv.setBackgroundResource(R.mipmap.check_gray);
                if(2==checkedPosition){
                    viewHolder.checkIv.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.checkIv.setVisibility(View.GONE);
                }
                break;
            case 3:
                viewHolder.bgIv.setBackgroundResource(R.drawable.blue_skin);
                viewHolder.checkIv.setBackgroundResource(R.mipmap.check_blue);
                if(3==checkedPosition){
                    viewHolder.checkIv.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.checkIv.setVisibility(View.GONE);
                }
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.bg_iv)
        ImageView bgIv;
        @BindView(R.id.check_iv)
        ImageView checkIv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
