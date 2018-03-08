package com.syezon.note_xh.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.activity.AddNoteActivity;
import com.syezon.note_xh.activity.BaseActivity;
import com.syezon.note_xh.activity.SortActivity;
import com.syezon.note_xh.adapter.SortGridViewAdapter;
import com.syezon.note_xh.adapter.SortViewPagerAdapter;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.event.BySortEvent;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DateUtils;
import com.syezon.note_xh.utils.DisplayUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortFragment extends Fragment implements  View.OnClickListener{

//    @BindView(R.id.gv_showpage)
//    GridView gvShowpage;
//    @BindView(R.id.iv_add)
//    ImageView ivAdd;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.img_add)
    ImageView imgAdd;

    private List<NoteSortEntity> noteSortEntities;

    private SortGridViewAdapter sortGridViewAdapter;
    private static final int NON_EDIT = 1;//非编辑状态
    private static final int EDIT = 2;//编辑状态
    private PagerAdapter pagerAdapter;

    //编辑状态
    private int editStatus = NON_EDIT;

    private static final int ASCENDING = 1;
    private static final int DESCENDING = 2;
    //排序顺序ASCENDING.从小到大DESCENDING.从大到小,默认按首字母从小到大排
    private int orderTag=ASCENDING;

    public SortFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SortFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SortFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort2, container, false);
        ButterKnife.bind(this, view);
        init();
        initData();
        setListener();
        return view;
    }

    private void init() {
        noteSortEntities =new ArrayList<>();
//        sortGridViewAdapter = new SortGridViewAdapter(noteSortEntities, getActivity());
//        gvShowpage.setAdapter(sortGridViewAdapter);
        pagerAdapter = new SortViewPagerAdapter(getContext(), noteSortEntities
                , new SortViewPagerAdapter.PagerClickListener<NoteSortEntity>() {
            @Override
            public void click(NoteSortEntity noteSortEntity) {
                Intent intent=new Intent(getActivity(), SortActivity.class);
                intent.putExtra("sort_name", noteSortEntity.getSortName());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
        vp.setAdapter(pagerAdapter);
        vp.setOffscreenPageLimit(3);
        vp.setPageMargin(DisplayUtils.dip2px(getContext(), 30));
        tvTime.setText(DateUtils.getTimeByTimeStamp(String.valueOf(System.currentTimeMillis()), "M 月 dd 日 E"));
    }

    private void setListener() {
//        gvShowpage.setOnItemLongClickListener(this);
//        gvShowpage.setOnItemClickListener(this);
        imgAdd.setOnClickListener(this);
    }

    private void initData() {
        List<NoteSortEntity> sortEntityList=null;
        try {
            noteSortEntities.clear();
            sortEntityList= NoteApplication.dbManager.findAll(NoteSortEntity.class);
            for(int i=0;i<sortEntityList.size();i++){
                Log.d("asc2",sortEntityList.get(i).getFirstLetterAsc()+"");
            }
            noteSortEntities.addAll(sortEntityList);
            //默认是按升序排
            Collections.sort(noteSortEntities);
            if(orderTag==DESCENDING){
                Collections.reverse(noteSortEntities);
            }

            noteSortEntities.add(0,new NoteSortEntity("收藏"));
            List<DbModel> newDbModelList=NoteApplication.dbManager.findDbModelAll(new SqlInfo("select \"sortname\" from note"));
            if(newDbModelList!=null){
                //有未分类的就显示未分类文件夹
                b:
                for (DbModel dbModel:newDbModelList) {
                    if(TextUtils.isEmpty(dbModel.getString("sortname"))){
                        noteSortEntities.add(new NoteSortEntity("未分类"));
                        break b;
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (sortEntityList != null && noteSortEntities.size() == 2 && sortEntityList.size() == 0) {
//            ivAdd.setBackgroundResource(((BaseActivity) getActivity()).addBackGroundSourceId);
            editStatus = NON_EDIT;
            sortGridViewAdapter.setTag(NON_EDIT);
        }
//        sortGridViewAdapter.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBySortEvent(BySortEvent event) {
        orderTag=orderTag==ASCENDING?DESCENDING:ASCENDING;
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditEvent(EditEvent event) {
        initData();
    }

//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        sortGridViewAdapter.setTag(EDIT);
//        sortGridViewAdapter.notifyDataSetChanged();
////        ivAdd.setBackgroundResource(((BaseActivity)getActivity()).editcompleteBackGroundSorceId);
//        editStatus =EDIT;
//        return true;
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//        switch (sortGridViewAdapter.getTag()){
//            //非编辑状态下
//            case NON_EDIT:
//                Intent intent=new Intent(getActivity(), SortActivity.class);
//                intent.putExtra("sort_name", noteSortEntities.get(position).getSortName());
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                break;
//            //编辑状态下
//            case EDIT:
//                //点击收藏和分类时不出现弹框
//                if(position==0){
//                    return;
//                }
//
//                if(position==noteSortEntities.size()-1&&TextUtils.equals(noteSortEntities.get(position).getSortName(),"未分类")){
//                    return;
//                }
//                //"是否删除"对话框
//                final AlertDialog dialog=new AlertDialog.Builder(getActivity()).create();
//                LinearLayout dialogView= (LinearLayout) View.inflate(getActivity(), R.layout.dialog_delete_sort2,null);
//                dialogView.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String sortname = noteSortEntities.get(position).getSortName();
//                        try {
//                            NoteApplication.dbManager.delete(NoteSortEntity.class, WhereBuilder.b("sortname", "=", sortname));
//                            KeyValue valueUsername = new KeyValue("sortname", "");
//                            NoteApplication.dbManager.update(NoteEntity.class, WhereBuilder.b("sortname", "=", sortname), valueUsername);
//                            EventBus.getDefault().post(new EditEvent());
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//                        dialog.cancel();
//                    }
//                });
//                dialogView.findViewById(R.id.cancle_tv).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.cancel();
//                    }
//                });
//                dialog.setView(dialogView);
//                dialog.setCanceledOnTouchOutside(false);
//                Window window = dialog.getWindow();
//                if (window!=null) {
//                    window.getDecorView().setBackgroundResource(android.R.color.transparent);
//                    window.setLayout(DisplayUtils.dip2px(getActivity(), 300),DisplayUtils.dip2px(getActivity(), 150));
//                }
//                dialog.show();
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_add:
                //非编辑状态，跳转到AddNoteActivity；编辑状态将item都变为可编辑状态
                if(editStatus == NON_EDIT){
                    Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else {
//                    ivAdd.setBackgroundResource(((BaseActivity)getActivity()).addBackGroundSourceId);
                    sortGridViewAdapter.setTag(NON_EDIT);
                    sortGridViewAdapter.notifyDataSetChanged();
                    editStatus = NON_EDIT;
                }
                break;

        }
    }


}
