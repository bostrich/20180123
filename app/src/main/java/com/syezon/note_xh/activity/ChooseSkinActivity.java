package com.syezon.note_xh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.SkinAdapter;
import com.syezon.note_xh.event.ThemeChangeEvent;
import com.syezon.note_xh.utils.ColorUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.view.MyCommonGridView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseSkinActivity extends BaseActivity {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.gv_skin)
    MyCommonGridView gvSkin;
    @BindView(R.id.rl_skin_top)
    RelativeLayout rlSkinTop;

    private SkinAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_skin);
        ButterKnife.bind(this);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChooseSkinActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChooseSkinActivity");
        MobclickAgent.onPause(this);
    }


    private void setListener() {
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //选择相应颜色item做友盟后台自定义事件的统计，并改变应用皮肤
        gvSkin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        SharedPerferencesUtil.saveIntData(ChooseSkinActivity.this, PreferenceKeyUtils.THEME, R.style.PinkStyle);
                        doStatistics("pink");
                        ColorUtils.setStatusBarColor(ChooseSkinActivity.this, R.color.top_pink);
                        rlSkinTop.setBackgroundResource(R.color.top_pink);
                        break;
                    case 1:
                        SharedPerferencesUtil.saveIntData(ChooseSkinActivity.this, PreferenceKeyUtils.THEME, R.style.GreenStyle);
                        doStatistics("green");
                        ColorUtils.setStatusBarColor(ChooseSkinActivity.this, R.color.top_green);
                        rlSkinTop.setBackgroundResource(R.color.top_green);
                        break;
                    case 2:
                        SharedPerferencesUtil.saveIntData(ChooseSkinActivity.this, PreferenceKeyUtils.THEME, R.style.GrayStyle);
                        doStatistics("gray");
                        ColorUtils.setStatusBarColor(ChooseSkinActivity.this, R.color.top_gray);
                        rlSkinTop.setBackgroundResource(R.color.top_gray);
                        break;
                    case 3:
                        SharedPerferencesUtil.saveIntData(ChooseSkinActivity.this, PreferenceKeyUtils.THEME, R.style.BlueStyle);
                        doStatistics("blue");
                        ColorUtils.setStatusBarColor(ChooseSkinActivity.this, R.color.top_blue);
                        rlSkinTop.setBackgroundResource(R.color.top_blue);
                        break;
                }
                EventBus.getDefault().post(new ThemeChangeEvent());
            }
        });
    }

    /**
     * 向友盟后台发送自定义统计数据
     */
    private void doStatistics(String color) {
        HashMap<String, String> map = new HashMap<>();
        map.put("color", color);
        MobclickAgent.onEvent(ChooseSkinActivity.this, "choose_skin", map);
    }

    private void init() {
        adapter = new SkinAdapter(this, gvSkin);
        gvSkin.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gvSkin.setAdapter(adapter);

        int styleId = SharedPerferencesUtil.getIntData(this, PreferenceKeyUtils.THEME, R.style.GreenStyle);
        switch (styleId) {
            case R.style.PinkStyle:
                gvSkin.setItemChecked(0, true);
                adapter.notifyDataSetChanged();
                break;
            case R.style.GreenStyle:
                gvSkin.setItemChecked(1, true);
                adapter.notifyDataSetChanged();
                break;
            case R.style.GrayStyle:
                gvSkin.setItemChecked(2, true);
                adapter.notifyDataSetChanged();
                break;
            case R.style.BlueStyle:
                gvSkin.setItemChecked(3, true);
                adapter.notifyDataSetChanged();
                break;
        }
    }


}
