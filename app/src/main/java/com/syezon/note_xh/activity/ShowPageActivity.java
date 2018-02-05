package com.syezon.note_xh.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.event.BySortEvent;
import com.syezon.note_xh.event.ByTimeEvent;
import com.syezon.note_xh.event.ThemeChangeEvent;
import com.syezon.note_xh.fragment.SortFragment;
import com.syezon.note_xh.fragment.TimeFragment;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.MarketUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowPageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rl_showpage_time)
    RelativeLayout rlShowpageTime;
    @BindView(R.id.rl_showpage_sort)
    RelativeLayout rlShowpageSort;
    @BindView(R.id.showpage_container)
    RelativeLayout showpagecontainer;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_time_ima)
    TextView tvTimeIma;
    @BindView(R.id.tv_sort_ima)
    TextView tvSortIma;
    @BindView(R.id.tv_time_arrow)
    TextView tvTimeArrow;
    @BindView(R.id.tv_sort_arrow)
    TextView tvSortArrow;
    @BindView(R.id.tv_menu)
    TextView tvMenu;

    private boolean skinChange = false;

    private static final int BY_TIME = 1;
    private static final int BY_SORT = 2;
    private static final int DOWN = 1;
    private static final int UP = 2;

    private FragmentManager fragmentManager;
    private SortFragment sortFragment;
    private TimeFragment timeFragment;
    //BY_TIME.按时间显示的 BY_SORT.按分类显示的
    private int tag = BY_TIME;
    //判断时间箭头此时的方向
    private int timetag = DOWN;
    //判断分类箭头此时的方向
    private int sorttag = DOWN;

    private AlertDialog gradeDialog;

    private long currentBackPressedTime = 0;//退出时间
    private static final int BACK_PRESSED_INTERVAL = 2000;//退出间隔

    @Override
    public void onBackPressed() {
        if (!SharedPerferencesUtil.getBooleanData(this, PreferenceKeyUtils.SP_KEY_WHETHER_HAS_GRADE,false)) {
            popGradeDialog();
        } else {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showNewToast(this,"再按一次返回键退出程序");
            } else {
                super.onBackPressed();
            }
        }
    }

    private void popGradeDialog() {
        if(gradeDialog==null){
            gradeDialog = new AlertDialog.Builder(this).create();
            LinearLayout dialogView = (LinearLayout) View.inflate(this, R.layout.pop_grade, null);
            dialogView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gradeDialog.cancel();
                }
            });
            dialogView.findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gradeDialog.cancel();
                    SharedPerferencesUtil.saveBooleanData(ShowPageActivity.this, PreferenceKeyUtils.SP_KEY_WHETHER_HAS_GRADE,true);
                    finish();
                }
            });
            dialogView.findViewById(R.id.btn_encourage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gradeDialog.cancel();
                    SharedPerferencesUtil.saveBooleanData(ShowPageActivity.this, PreferenceKeyUtils.SP_KEY_WHETHER_HAS_GRADE,true);
                    finish();
                    MarketUtils.launchAppDetail2(ShowPageActivity.this);
                }
            });
            gradeDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            gradeDialog.setView(dialogView);
            gradeDialog.setCanceledOnTouchOutside(false);
        }

        gradeDialog.show();
        WindowManager.LayoutParams params = gradeDialog.getWindow().getAttributes();
        params.width = (int) (DisplayUtils.getScreenWidth(this)*0.85);
        params.height = (int) (DisplayUtils.getScreenHeight(this)*0.5);
        gradeDialog.getWindow().setAttributes(params);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_page);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setListener();

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        tvTimeIma.setTypeface(iconfont);
        tvSortIma.setTypeface(iconfont);
        tvTimeArrow.setTypeface(iconfont);
        tvSortArrow.setTypeface(iconfont);
        tvMenu.setTypeface(iconfont);
        //默认按时间排序
        fragmentManager = getSupportFragmentManager();
        timeFragment = new TimeFragment();
//        sortFragment=new SortFragment();
        fragmentManager.beginTransaction().add(R.id.showpage_container, timeFragment).commit();
        //如果是首次进入应用，就先往类别数据库中添加几个默认的类别
        if (SharedPerferencesUtil.getBooleanData(this, PreferenceKeyUtils.ISFIRST, true)) {
            try {
                NoteSortEntity noteSortEntity1 = new NoteSortEntity("生活");
                noteSortEntity1.setFirstLetterAsc(115);
                NoteSortEntity noteSortEntity2 = new NoteSortEntity("工作");
                noteSortEntity2.setFirstLetterAsc(103);
                NoteApplication.dbManager.saveBindingId(noteSortEntity1);
                NoteApplication.dbManager.saveBindingId(noteSortEntity2);
                SharedPerferencesUtil.saveBooleanData(this, PreferenceKeyUtils.ISFIRST, false);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        tvMenu.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (skinChange) {
            skinChange=false;
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(gradeDialog!=null){
            gradeDialog.dismiss();
            gradeDialog=null;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThemeChangeEvent(ThemeChangeEvent event) {
        skinChange = true;
    }

    private void setListener() {
        rlShowpageTime.setOnClickListener(this);
        rlShowpageSort.setOnClickListener(this);
        tvTimeArrow.setOnClickListener(this);
        tvSortArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_showpage_time:
                if (tag != BY_TIME) {
                    tag = BY_TIME;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.showpage_container,timeFragment).commit();
                    hideFragment(fragmentTransaction);
                    fragmentTransaction.show(timeFragment).commit();
                    //改变背景颜色
                    rlShowpageTime.setBackgroundColor(getResources().getColor(R.color.activity_backGround));
                    rlShowpageSort.setBackgroundColor(getResources().getColor(R.color.unselected_color));
                    tvTimeArrow.setVisibility(View.VISIBLE);
                    tvSortArrow.setVisibility(View.INVISIBLE);

                    tvTimeIma.setTextColor(getResources().getColor(customactionbarBackGroundSorceId));
                    tvTime.setTextColor(getResources().getColor(customactionbarBackGroundSorceId));
                    tvSortIma.setTextColor(getResources().getColor(themeColorSourceId));
                    tvSort.setTextColor(getResources().getColor(themeColorSourceId));

                } else {
                    timeOrderChange();
                }
                break;
            case R.id.rl_showpage_sort:
                if (tag != BY_SORT) {
                    tag = BY_SORT;
                    FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
//                    fragmentTransaction1.replace(R.id.showpage_container,sortFragment).commit();
                    hideFragment(fragmentTransaction1);
                    if (sortFragment != null && sortFragment.isAdded()) {
                        fragmentTransaction1.show(sortFragment).commit();
                    } else {
                        sortFragment = new SortFragment();
                        fragmentTransaction1.add(R.id.showpage_container, sortFragment).commit();
                    }
                    //改变背景颜色
                    rlShowpageSort.setBackgroundColor(getResources().getColor(R.color.activity_backGround));
                    rlShowpageTime.setBackgroundColor(getResources().getColor(R.color.unselected_color));
                    tvSortArrow.setVisibility(View.VISIBLE);
                    tvTimeArrow.setVisibility(View.INVISIBLE);

                    tvTimeIma.setTextColor(getResources().getColor(themeColorSourceId));
                    tvTime.setTextColor(getResources().getColor(themeColorSourceId));
                    tvSortIma.setTextColor(getResources().getColor(customactionbarBackGroundSorceId));
                    tvSort.setTextColor(getResources().getColor(customactionbarBackGroundSorceId));

                } else {
                    sortOrderChange();
                }
                break;
            case R.id.tv_time_arrow:
                timeOrderChange();
                break;
            case R.id.tv_sort_arrow:
                sortOrderChange();
                break;
            case R.id.tv_menu:
                Intent intent1=new Intent(this,SettingActivity.class);
                startActivity(intent1);
                this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    //按分类排的改变箭头指向
    private void sortOrderChange() {
        if (tag == BY_SORT) {
            //动画效果
            if (sorttag == DOWN) {
                tvSortArrow.setText(getResources().getString(R.string.up));
                sorttag = UP;
            } else {
                tvSortArrow.setText(getResources().getString(R.string.down));
                sorttag = DOWN;
            }
            //给sortfragment发送更新消息
            EventBus.getDefault().post(new BySortEvent());
        }
    }

    //按时间排的改变箭头指向
    private void timeOrderChange() {
        if (tag == BY_TIME) {
            if (timetag == DOWN) {
                tvTimeArrow.setText(getResources().getString(R.string.up));
                timetag = UP;
            } else {
                tvTimeArrow.setText(getResources().getString(R.string.down));
                timetag = DOWN;
            }
            EventBus.getDefault().post(new ByTimeEvent());
        }
    }


    //隐藏fragment
    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (timeFragment != null) {
            fragmentTransaction.hide(timeFragment);
        }
        if (sortFragment != null) {
            fragmentTransaction.hide(sortFragment);
        }

    }

    //防止主Activity销毁后，而Fragment给保存下来了，导致fragment重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
}
