package com.syezon.note_xh.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syezon.note_xh.R;
import com.syezon.note_xh.activity.SortActivity;
import com.syezon.note_xh.db.DataUtils;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.utils.DbUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by June on 2018/3/7.
 */

public class SortViewPagerAdapter extends PagerAdapter {
    private List<NoteSortEntity> list = new ArrayList<>();
    private Context context;
    private PagerClickListener<NoteSortEntity> listener;

    public SortViewPagerAdapter(Context context, List<NoteSortEntity> list, PagerClickListener<NoteSortEntity> listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sort, null);
        TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final String name = list.get(position).getSortName();
        tvName.setText(name);
        if (position == 0) {
            String num = DbUtils.getCollectedNumber() + "";
            tvCount.setText(num + "" + "篇");
        }else{
            String num = DbUtils.getCategoryNumber(name) + "";
            if(TextUtils.equals(name,"未分类")){
                num = DbUtils.getCategoryNumber("") + "";
            }
            tvCount.setText(num + "" + "篇");
        }
        ImageView img = (ImageView) view.findViewById(R.id.img);
        List<DbModel> dataBySortName = DataUtils.getDataBySortName(name);
        String picPath = "";
        boolean setPic = false;
        for (DbModel dbModel : dataBySortName) {
            if(dbModel.getBoolean("hasimage")){
                picPath = dbModel.getString("imagepath");
                if (!TextUtils.isEmpty(picPath)) {
                    Uri uri=Uri.parse(picPath);
                    Glide.with(context).load(uri).crossFade() .into(img);
                    setPic = true;
                    break;
                }
            }
        }
        if(!setPic) img.setImageResource(R.mipmap.img_bg_sort_travel);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.click(list.get(position));
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface PagerClickListener<T>{
        void click(T t);
    }
}
