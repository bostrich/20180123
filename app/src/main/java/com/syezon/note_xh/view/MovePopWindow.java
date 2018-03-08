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
import com.syezon.note_xh.adapter.MoveAdapter;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.utils.DisplayUtils;

import org.xutils.ex.DbException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/10/27.
 */
public class MovePopWindow extends PopupWindow {
    @BindView(R.id.lv_classify)
    ListView lvClassify;

    List<NoteSortEntity > sortEntityList;
    private MoveAdapter moveAdapter;

    public MovePopWindow(Context mContext, final OnMovePopItemClick onMovePopItemClick) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_move, null);
        ButterKnife.bind(this, view);

        try {
            sortEntityList= NoteApplication.dbManager.findAll(NoteSortEntity.class);
            moveAdapter=new MoveAdapter(sortEntityList,mContext);
            lvClassify.setAdapter(moveAdapter);

            lvClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String sortName=sortEntityList.get(position).getSortName();
                    onMovePopItemClick.onItemClick(sortName);
                }
            });
        } catch (DbException e) {
            e.printStackTrace();
        }


        // 设置视图
        this.setContentView(view);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth((int) (DisplayUtils.getScreenWidth(mContext)*0.25));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_move_window_anim);
    }

    public void updateSort(){
        try {
            sortEntityList.clear();
            sortEntityList.addAll(NoteApplication.dbManager.findAll(NoteSortEntity.class));
            moveAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public interface OnMovePopItemClick{
        void onItemClick(String sortName);
    }

}
