package com.syezon.note_xh.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.MarkAdapter;
import com.syezon.note_xh.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/10/27.
 */
public class MarkPopWindow extends PopupWindow {

    @BindView(R.id.lv_mark)
    ListView lvMark;

    public MarkPopWindow(Context mContext, final OnMarkPopItemClick onMarkPopItemClick) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_mark, null);
        ButterKnife.bind(this, view);
        MarkAdapter markAdapter=new MarkAdapter(mContext);
        lvMark.setAdapter(markAdapter);

        lvMark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMarkPopItemClick.onItemClick(position);
            }
        });


        // 设置视图
        this.setContentView(view);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth((int) (DisplayUtils.getScreenWidth(mContext) * 0.25));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_move_window_anim);
    }


    public interface OnMarkPopItemClick {
        void onItemClick(int position);
    }
}
