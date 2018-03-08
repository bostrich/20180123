package com.syezon.note_xh.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.TimeRecyclerViewAdapter;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.ToastUtils;
import com.syezon.note_xh.view.MarkPopWindow;
import com.syezon.note_xh.view.MovePopWindow;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_showpage)
    RecyclerView rvShowPage;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_move_up)
    TextView tvMoveUp;
    @BindView(R.id.ll_move)
    LinearLayout llMove;
    @BindView(R.id.tv_mark_up)
    TextView tvMarkUp;
    @BindView(R.id.ll_mark)
    LinearLayout llMark;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.ll_edit)
    LinearLayout llEdit;
    @BindView(R.id.rl_time_main)
    RelativeLayout rlTimeMain;

    private String sortName;

    private TimeRecyclerViewAdapter timeRecyclerViewAdapter;
    private List<DbModel> dbModelList;

    private MovePopWindow movePopWindow;
    private MarkPopWindow markPopWindow;

    private static final int ASCENDING = 1;
    private static final int DESCENDING = 2;
    //排序顺序ASCENDING.从小到大DESCENDING.从大到小,默认按首字母从小到大排
    private int orderTag = DESCENDING;

    private static final int UNEDITED = 1;//非编辑状态
    private static final int EDITED = 2;//编辑状态
    //"添加"图标状态ADD.添加状态EDIT.编辑状态
    private int editState = UNEDITED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        init();
        initData();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SortActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SortActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(SortActivity.this.getAssets(), "iconfont.ttf");
        tvMoveUp.setTypeface(iconfont);
        tvMarkUp.setTypeface(iconfont);

        Intent intent = getIntent();
        sortName = intent.getStringExtra("sort_name");
        tvTitle.setText(sortName);

        movePopWindow = new MovePopWindow(SortActivity.this, new MovePopWindow.OnMovePopItemClick() {
            @Override
            public void onItemClick(String sortName) {
                List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();
                if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                    for (int selectedPosition : selectedPositionList) {
                        try {
                            NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                            noteEntity.setSortName(sortName);
                            NoteApplication.dbManager.update(noteEntity, "sortname");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    ToastUtils.showUniqueToast(SortActivity.this,"修改成功");
                    selectedPositionList.clear();
                    EventBus.getDefault().post(new EditEvent());
                } else {
                    ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                }
            }
        });

        //popupwindow 中listView 无法响应点击事件
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) movePopWindow.setFocusable(true);

        markPopWindow=new MarkPopWindow(SortActivity.this, new MarkPopWindow.OnMarkPopItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case 0:
                        List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                            for (int selectedPosition : selectedPositionList) {
                                try {
                                    NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                                    noteEntity.setCollect(true);
                                    NoteApplication.dbManager.update(noteEntity, "iscollect");
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                            ToastUtils.showUniqueToast(SortActivity.this,"收藏成功");
                            selectedPositionList.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                        }
                        break;
                    case 1:
                        List<Integer> selectedPositionList1 = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList1!=null&&selectedPositionList1.size()>0) {
                            for (int selectedPosition : selectedPositionList1) {
                                try {
                                    NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                                    noteEntity.setCollect(false);
                                    NoteApplication.dbManager.update(noteEntity, "iscollect");
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                            ToastUtils.showUniqueToast(SortActivity.this,"取消收藏成功");
                            selectedPositionList1.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                        }
                        break;
                    case 2:
                        List<Integer> selectedPositionList2 = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList2!=null&&selectedPositionList2.size()>0) {
                            for (int selectedPosition : selectedPositionList2) {
                                try {
                                    NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                                    noteEntity.setComplete(true);
                                    NoteApplication.dbManager.update(noteEntity, "iscomplete");
                                } catch (DbException e) {
                                    e.printStackTrace();
//                                    Log.d("==f==",e.getMessage());
                                }
                            }
                            ToastUtils.showUniqueToast(SortActivity.this, "取消标记成功");
                            selectedPositionList2.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                        }
                        break;
                    case 3:
                        List<Integer> selectedPositionList3 = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList3!=null&&selectedPositionList3.size()>0) {
                            for (int selectedPosition : selectedPositionList3) {
                                try {
                                    NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                                    noteEntity.setComplete(false);
                                    NoteApplication.dbManager.update(noteEntity, "iscomplete");
                                } catch (DbException e) {
                                    e.printStackTrace();
//                                    Log.d("==f==",e.getMessage());
                                }
                            }
                            ToastUtils.showUniqueToast(SortActivity.this, "标记成功");
                            selectedPositionList3.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                        }
                        break;
                }
            }
        });
        //popupwindow 中listView 无法响应点击事件
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) markPopWindow.setFocusable(true);


        dbModelList = new ArrayList<>();
        timeRecyclerViewAdapter = new TimeRecyclerViewAdapter(SortActivity.this, dbModelList);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvShowPage.setLayoutManager(manager);
        rvShowPage.setAdapter(timeRecyclerViewAdapter);

        rvShowPage.addItemDecoration(new RecycleViewDivider(SortActivity.this, LinearLayoutManager.HORIZONTAL
                , DisplayUtils.dip2px(SortActivity.this, 5), getResources().getColor(R.color.activity_backGround)));

        rvShowPage.addItemDecoration(new RecycleViewDivider(SortActivity.this, LinearLayoutManager.VERTICAL
                , DisplayUtils.dip2px(SortActivity.this, 5), getResources().getColor(R.color.activity_backGround)));
    }

    private void initData() {
        try {
            switch (sortName){
                case "收藏":
                    List<DbModel> newDbModelList = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where iscollect = 1"));
                    Collections.sort(newDbModelList, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    if (orderTag == DESCENDING) {
                        Collections.reverse(newDbModelList);
                    }
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList);
                    timeRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case "未分类":
                    List<DbModel> newDbModelList1 = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where sortname = ''"));
                    Collections.sort(newDbModelList1, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    if (orderTag == DESCENDING) {
                        Collections.reverse(newDbModelList1);
                    }
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList1);
                    timeRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                default:
                    List<DbModel> newDbModelList2 = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where sortname ='"+sortName+"'"));
                    Collections.sort(newDbModelList2, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    if (orderTag == DESCENDING) {
                        Collections.reverse(newDbModelList2);
                    }
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList2);
                    timeRecyclerViewAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (dbModelList.size() == 0) {
//
//        }
    }

    private void setListener() {
        ivAdd.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        llMove.setOnClickListener(this);
        llMark.setOnClickListener(this);

        timeRecyclerViewAdapter.setmOnItemClickListener(new TimeRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (editState == UNEDITED) {
                    NoteEntity noteEntity = null;
                    try {
                        noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModelList.get(position).getInt("_id"));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(SortActivity.this, AddNoteActivity.class);
                    intent.putExtra("note_entity", noteEntity);
                    startActivity(intent);
                }
            }
        });

        timeRecyclerViewAdapter.setmOnItemLongClickListener(new TimeRecyclerViewAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                timeRecyclerViewAdapter.setEditState(EDITED);
                timeRecyclerViewAdapter.notifyDataSetChanged();

                ivAdd.setVisibility(View.GONE);
                llEdit.setVisibility(View.VISIBLE);

                editState = EDITED;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(SortActivity.this, AddNoteActivity.class);
                if (!TextUtils.equals(sortName,"收藏")&&!TextUtils.equals(sortName,"未分类")) {
                    intent.putExtra("name",sortName);
                }
                SortActivity.this.startActivity(intent);
                SortActivity.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.tv_delete:
                //"是否删除"对话框
                final List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();
                final AlertDialog dialog = new AlertDialog.Builder(SortActivity.this).create();
                LinearLayout dialogView = (LinearLayout) View.inflate(SortActivity.this, R.layout.dialog_delete_one, null);
                dialogView.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int selectedPosition : selectedPositionList) {
                            try {
                                NoteApplication.dbManager.deleteById(NoteEntity.class, dbModelList.get(selectedPosition).getInt("_id"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        ToastUtils.showUniqueToast(SortActivity.this,"删除成功");
                        selectedPositionList.clear();
                        EventBus.getDefault().post(new EditEvent());
                        dialog.cancel();
                    }
                });
                dialogView.findViewById(R.id.cancle_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                Window window = dialog.getWindow();
                dialog.setView(dialogView);
                dialog.setCanceledOnTouchOutside(false);
                if (window!=null) {
                    window.getDecorView().setBackgroundResource(android.R.color.transparent);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = DisplayUtils.dip2px(SortActivity.this, 300);
                    params.height = DisplayUtils.dip2px(SortActivity.this, 150);
                    window.setAttributes(params);
                }


                if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                    dialog.show();
                } else {
                    ToastUtils.showUniqueToast(SortActivity.this,"请先勾选");
                }
                break;
            case R.id.tv_cancel:
                llEdit.setVisibility(View.GONE);
                ivAdd.setVisibility(View.VISIBLE);

                timeRecyclerViewAdapter.setEditState(UNEDITED);
                timeRecyclerViewAdapter.notifyDataSetChanged();

                editState = UNEDITED;
                break;
            case R.id.ll_move:
//                int navigationBarHeight=new SystemBarTintManager(SortActivity.this).getConfig().getNavigationBarHeight();
//                int upHeight=navigationBarHeight+ DisplayUtils.dip2px(SortActivity.this,50);
                movePopWindow.showAtLocation(rlTimeMain,Gravity.BOTTOM|Gravity.START, (int) (DisplayUtils.getScreenWidth(SortActivity.this)*0.25),0);
                break;
            case R.id.ll_mark:
                markPopWindow.showAtLocation(rlTimeMain,Gravity.BOTTOM|Gravity.START, (int) (DisplayUtils.getScreenWidth(SortActivity.this)*0.5),0);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditEvent(EditEvent event) {
        initData();
    }
}
