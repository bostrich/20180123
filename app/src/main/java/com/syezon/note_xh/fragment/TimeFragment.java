package com.syezon.note_xh.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.Config.AdConfig;
import com.syezon.note_xh.Config.AppConfig;
import com.syezon.note_xh.Config.AppSwitch;
import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.activity.AddNoteActivity;
import com.syezon.note_xh.activity.RecycleViewDivider;
import com.syezon.note_xh.adapter.TimeAndAdRecyclerViewAdapter;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.bean.AdInfo;
import com.syezon.note_xh.bean.BaseNoteBean;
import com.syezon.note_xh.bean.NewsNoteInfo;
import com.syezon.note_xh.bean.NormalNoteBean;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadManager;
import com.syezon.note_xh.download.feedback.NotificationDownloadFeedback;
import com.syezon.note_xh.event.ByTimeEvent;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.ParseUtil;
import com.syezon.note_xh.utils.ToastUtils;
import com.syezon.note_xh.utils.WebHelper;
import com.syezon.note_xh.view.MarkPopWindow;
import com.syezon.note_xh.view.MovePopWindow;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TimeFragment.class.getName();

    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.rv_showpage)
    RecyclerView showPageRv;
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

    private TimeAndAdRecyclerViewAdapter timeRecyclerViewAdapter;
    private List<BaseNoteBean> dbModelList;

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

    private List<Integer> listPosition = new ArrayList<>();


    public TimeFragment() {
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
        MobclickAgent.onPageStart("TimeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TimeFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //时间默认按从近到远排
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        ButterKnife.bind(this, view);
        init();
        initData();
        setListener();
        return view;
    }

    private void initData() {
        try {
            List<DbModel> newDbModelList = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note"));
            if (orderTag == DESCENDING) {
                Collections.reverse(newDbModelList);
            }
            dbModelList.clear();
            for (int i = 0; i < newDbModelList.size(); i++) {
                NormalNoteBean bean = new NormalNoteBean(newDbModelList.get(i));
                dbModelList.add(bean);
            }
            if(AppSwitch.showAdInNotes && dbModelList.size() > 0){
                addNoteAd();
            }

            LogUtil.e(TAG, "数据量：" + dbModelList.size());
            timeRecyclerViewAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void addNoteAd() {
        int totalPosition = 0;
        listPosition.clear();
        for (int i = 0; i < dbModelList.size(); i++) {
            if(dbModelList.get(i).hasImage()){
                totalPosition += 2;
            }else{
                totalPosition += 1;
            }
            if(totalPosition % 10 >= 5 ){
                listPosition.add(i);
                totalPosition = -3;
            }
        }

        if(listPosition.size() > 0){
            for (int i = 0; i < listPosition.size(); i++) {
                if(AppConfig.listAd.size() >= i + 1){
                    addAdInfo(AppConfig.listAd.get(i), 1, listPosition.get(i));
                }
            }
        }else{
            if(AppConfig.listAd.size() > 0){
                AdInfo adInfo = AppConfig.listAd.get(0);
                addAdInfo(adInfo, 0, 0);
            }
        }

    }

    private void addAdInfo(AdInfo adInfo, final int type, final int position){
        if(adInfo.getType().equals(AdConfig.TYPE_NEWS.getName())){
            if(adInfo.getUrl().equals(AdConfig.TYPE_NEWS_SOURCE_BL.getName())){//获取伴侣新闻
                LogUtil.e(TAG, "开始获取伴侣新闻");
                x.http().get(new RequestParams(Conts.URL_BL_NEWS_NORMAL), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            NewsNoteInfo newsAd = new NewsNoteInfo();
                            newsAd.setNews(ParseUtil.parseNews(result));
                            if(newsAd.isValid()){
                                if(type == 0){
                                    dbModelList.add(newsAd);
                                }else if(type ==1){
                                    dbModelList.add(position, newsAd);
                                }
                                timeRecyclerViewAdapter.notifyDataSetChanged();
                                LogUtil.e(TAG, "显示新闻广告");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        }else{
            if (type == 0) {
                dbModelList.add(adInfo);
            }else if(type ==1){
                dbModelList.add(position, adInfo);
            }
            timeRecyclerViewAdapter.notifyDataSetChanged();
        }


    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfont.ttf");
        tvMoveUp.setTypeface(iconfont);
        tvMarkUp.setTypeface(iconfont);

        movePopWindow = new MovePopWindow(getActivity(), new MovePopWindow.OnMovePopItemClick() {
            @Override
            public void onItemClick(String sortName) {
                List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();
                if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                    for (int selectedPosition : selectedPositionList) {
                        try {
                            DbModel dbModel = dbModelList.get(selectedPosition).getDbModel();
                            if(dbModel != null){
                                NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModel.getInt("_id"));
                                noteEntity.setSortName(sortName);
                                NoteApplication.dbManager.update(noteEntity, "sortname");
                            }

                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    ToastUtils.showUniqueToast(getActivity(),"修改成功");
                    selectedPositionList.clear();
                    EventBus.getDefault().post(new EditEvent());
                } else {
                    ToastUtils.showUniqueToast(getActivity(),"请先勾选");
                }
            }
        });

        markPopWindow=new MarkPopWindow(getActivity(), new MarkPopWindow.OnMarkPopItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case 0:
                        List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                            for (int selectedPosition : selectedPositionList) {
                                DbModel dbModel = dbModelList.get(selectedPosition).getDbModel();
                                if(dbModel != null){
                                    try {
                                        NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModel.getInt("_id"));
                                        noteEntity.setCollect(true);
                                        NoteApplication.dbManager.update(noteEntity, "iscollect");
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            ToastUtils.showUniqueToast(getActivity(),"收藏成功");
                            selectedPositionList.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(getActivity(),"请先勾选");
                        }
                        break;
                    case 1:
                        List<Integer> selectedPositionList1 = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList1!=null&&selectedPositionList1.size()>0) {
                            for (int selectedPosition : selectedPositionList1) {
                                DbModel dbModel = dbModelList.get(selectedPosition).getDbModel();
                                if(dbModel != null) {
                                    try {
                                        NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModel.getInt("_id"));
                                        noteEntity.setCollect(false);
                                        NoteApplication.dbManager.update(noteEntity, "iscollect");
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            ToastUtils.showUniqueToast(getActivity(),"取消收藏成功");
                            selectedPositionList1.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(getActivity(),"请先勾选");
                        }
                        break;
                    case 2:
                        List<Integer> selectedPositionList2 = timeRecyclerViewAdapter.getSelectedPositionList();
                        if (selectedPositionList2!=null&&selectedPositionList2.size()>0) {
                            for (int selectedPosition : selectedPositionList2) {
                                DbModel dbModel = dbModelList.get(selectedPosition).getDbModel();
                                if(dbModel != null) {
                                    try {
                                        NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModel.getInt("_id"));
                                        noteEntity.setComplete(true);
                                        NoteApplication.dbManager.update(noteEntity, "iscomplete");
                                    } catch (DbException e) {
                                        e.printStackTrace();
//                                    Log.d("==f==",e.getMessage());
                                    }
                                }
                            }
                            ToastUtils.showUniqueToast(getActivity(), "标记成功");
                            selectedPositionList2.clear();
                            EventBus.getDefault().post(new EditEvent());
                        } else {
                            ToastUtils.showUniqueToast(getActivity(),"请先勾选");
                        }
                        break;
                }
            }
        });

        dbModelList = new ArrayList<>();
        timeRecyclerViewAdapter = new TimeAndAdRecyclerViewAdapter(getActivity(), dbModelList);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        showPageRv.setLayoutManager(manager);
        showPageRv.setAdapter(timeRecyclerViewAdapter);

        showPageRv.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL
                , DisplayUtils.dip2px(getActivity(), 5), getResources().getColor(R.color.activity_backGround)));

        showPageRv.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL
                , DisplayUtils.dip2px(getActivity(), 5), getResources().getColor(R.color.activity_backGround)));

    }

    private void setListener() {
        ivAdd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        llMove.setOnClickListener(this);
        llMark.setOnClickListener(this);

        timeRecyclerViewAdapter.setmOnItemClickListener(new TimeAndAdRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (editState == UNEDITED) {
                    dbModelList.get(position).click(getContext());
                }
            }
        });

        timeRecyclerViewAdapter.setmOnItemLongClickListener(new TimeAndAdRecyclerViewAdapter.OnRecyclerViewItemLongClickListener() {
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

    private void adClick(final AdInfo info) {
        if(info.getType().equals(AdConfig.TYPE_NEWS.getName())){
            //待处理
        }else if(info.getType().equals(AdConfig.TYPE_URL.getName())){
            WebHelper.showAdDetail(getContext(), info.getName(), info.getUrl(), new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {

                }
            });
        }else if(info.getType().equals(AdConfig.TYPE_APK.getName())){
            DialogUtils.showDownloadHint(getContext(), info, new DialogUtils.DialogListener<AdInfo>() {
                @Override
                public void confirm(AdInfo info) {
                    DownloadBean bean = new DownloadBean();
                    bean.setUrl(info.getUrl());
                    bean.setAppName(info.getName());
                    DownloadManager.getInstance(getContext()).download(bean,DownloadManager.DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(getContext().getApplicationContext()));
                }

                @Override
                public void cancel() {

                }
            });

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onByTimeEvent(ByTimeEvent event) {
        Collections.reverse(dbModelList);
        timeRecyclerViewAdapter.notifyDataSetChanged();
        orderTag = orderTag == ASCENDING ? DESCENDING : ASCENDING;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditEvent(EditEvent event) {
        initData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.tv_delete:
                //"是否删除"对话框
                final List<Integer> selectedPositionList = timeRecyclerViewAdapter.getSelectedPositionList();

                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                LinearLayout dialogView = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_delete_one, null);
                dialogView.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int selectedPosition : selectedPositionList) {
                            DbModel dbModel = dbModelList.get(selectedPosition).getDbModel();
                            if(dbModel != null){
                                try {
                                    NoteApplication.dbManager.deleteById(NoteEntity.class, dbModel.getInt("_id"));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ToastUtils.showUniqueToast(getActivity(),"删除成功");
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
                dialog.setView(dialogView);
                dialog.setCanceledOnTouchOutside(false);
                Window window=dialog.getWindow();
                if (window!=null) {
                    window.getDecorView().setBackgroundResource(android.R.color.transparent);
                    window.setLayout(DisplayUtils.dip2px(getActivity(), 300),DisplayUtils.dip2px(getActivity(), 150));
                }
                if (selectedPositionList!=null&&selectedPositionList.size()>0) {
                    dialog.show();
                } else {
                    ToastUtils.showUniqueToast(getActivity(),"请先勾选");
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
                movePopWindow.showAtLocation(rlTimeMain,Gravity.BOTTOM|Gravity.START, (int) (DisplayUtils.getScreenWidth(getActivity())*0.25),0);
                break;
            case R.id.ll_mark:
                markPopWindow.showAtLocation(rlTimeMain,Gravity.BOTTOM|Gravity.START, (int) (DisplayUtils.getScreenWidth(getActivity())*0.5),0);
                break;
        }
    }


}
