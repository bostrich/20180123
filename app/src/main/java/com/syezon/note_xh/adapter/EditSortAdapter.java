package com.syezon.note_xh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.utils.DbUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 编辑分类adapter
 */
public class EditSortAdapter extends MyBaseAdapter<NoteSortEntity> {

    private static final int NON_EDIT = 1;
    private static final int EDIT = 2;
    private int tag=NON_EDIT;
    private OnSortDeleteListener onSortDeleteListener;

    public EditSortAdapter(List<NoteSortEntity> list, Context mContext,OnSortDeleteListener onSortDeleteListener) {
        super(list, mContext);
        this.onSortDeleteListener=onSortDeleteListener;
    }

    public EditSortAdapter(Context mContext) {
        super(mContext);
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NoteSortEntity noteSortEntity=mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_edit_sort, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int num= DbUtils.getCategoryNumber(noteSortEntity.getSortName());

            viewHolder.editSortnameTv.setText(noteSortEntity.getSortName()+"("+num+"篇"+")");


        viewHolder.editDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortDeleteListener.OnSortDelete(position);
            }
        });

        if (tag==EDIT) {
            viewHolder.editDeleteIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editDeleteIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.edit_delete_iv)
        ImageView editDeleteIv;
        @BindView(R.id.edit_sortname_tv)
        TextView editSortnameTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnSortDeleteListener{
        void OnSortDelete(int pos);
    }
}
