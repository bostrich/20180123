package com.syezon.note_xh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.activity.BaseActivity;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.utils.DbUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/12.
 */
public class SortGridViewAdapter extends MyBaseAdapter<NoteSortEntity> {
    private static final int NON_EDIT = 1;//非编辑状态
    private static final int EDIT = 2;//编辑状态
    //flag为EDIT时就显示删除图标
    private int tag = NON_EDIT;

    public SortGridViewAdapter(List<NoteSortEntity> list, Context mContext) {
        super(list, mContext);
    }

    public SortGridViewAdapter(Context mContext) {
        super(mContext);
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_showpage_sort, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String name = mList.get(position).getSortName();
        viewHolder.sortNameTv.setText(name);
        if (position == 0) {
            String num = DbUtils.getCollectedNumber() + "";
            viewHolder.sortNumTv.setText("(" + num + ")");
            viewHolder.bgRl.setBackgroundResource(((BaseActivity)mContext).collectWenjianjiaBackGroundSorceId);
        } else {
            String num = DbUtils.getCategoryNumber(name) + "";
            if(TextUtils.equals(name,"未分类")){
                num = DbUtils.getCategoryNumber("") + "";
            }
            viewHolder.sortNumTv.setText("(" + num + ")");
            viewHolder.bgRl.setBackgroundResource(((BaseActivity)mContext).wenjianjiaBackGroundSorceId);
        }

        //根据tag判断是否显示删除图标
        if (tag == EDIT) {
            viewHolder.shadowRl.setVisibility(View.VISIBLE);
            if (position == 0) {
                viewHolder.shadowRl.setVisibility(View.GONE);
            }
            if(position==mList.size()-1&&TextUtils.equals(name,"未分类")){
                viewHolder.shadowRl.setVisibility(View.GONE);
            }
        } else {
            viewHolder.shadowRl.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.sort_name_tv)
        TextView sortNameTv;
        @BindView(R.id.sort_num_tv)
        TextView sortNumTv;
        @BindView(R.id.shadow_rl)
        RelativeLayout shadowRl;
        @BindView(R.id.bg_rl)
        RelativeLayout bgRl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
