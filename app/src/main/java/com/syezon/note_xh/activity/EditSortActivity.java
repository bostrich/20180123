package com.syezon.note_xh.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.EditSortAdapter;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.event.ChooseSortEvent;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditSortActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.cb_edit)
    CheckBox cbEdit;
    @BindView(R.id.lv_showsort)
    ListView lvShowsort;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    private List<NoteSortEntity> sortEntityList;
    private EditSortAdapter editSortAdapter;

    //编辑状态
    private static final int EDIT = 2;
    //非编辑状态
    private static final int NON_EDIT = 1;

    private static final int FROM_EDITNOTE = 1;
    private static final int FROM_SETTING = -1;
    //用以标记是从那个activit跳过来的，为FROM_EDITNOTE的话表示从AddNoteActivity跳转过来，为FROM_SETTING的话表示从SettingActivity跳转过来
    private int tag = FROM_SETTING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sort);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("EditSortActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("EditSortActivity");
        MobclickAgent.onPause(this);
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        tvAdd.setTypeface(iconfont);

        tag = getIntent().getIntExtra("from_where", FROM_SETTING);
        sortEntityList = new ArrayList<>();
        editSortAdapter = new EditSortAdapter(sortEntityList, this, new EditSortAdapter.OnSortDeleteListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void OnSortDelete(final int pos) {
                //"删除"对话框
                final String sortname = sortEntityList.get(pos).getSortName();
                final AlertDialog alertDialog = new AlertDialog.Builder(EditSortActivity.this).create();
                LinearLayout linearLayout = (LinearLayout) View.inflate(EditSortActivity.this, R.layout.dialog_delete_sort, null);

                linearLayout.findViewById(R.id.cancle_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                linearLayout.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            NoteApplication.dbManager.delete(NoteSortEntity.class, WhereBuilder.b("sortname", "=", sortname));
                            KeyValue valueUsername = new KeyValue("sortname", "");
                            NoteApplication.dbManager.update(NoteEntity.class, WhereBuilder.b("sortname", "=", sortname), valueUsername);
                            EventBus.getDefault().post(new EditEvent());
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        alertDialog.cancel();
                    }
                });
                TextView textView = (TextView) linearLayout.findViewById(R.id.sort_delete_tv);
                textView.setText("是否确定删除" + "“" + sortname + "“" + "分类?");
                alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                alertDialog.setView(linearLayout);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                params.width = DisplayUtils.dip2px(EditSortActivity.this, 300);
                params.height = DisplayUtils.dip2px(EditSortActivity.this, 160);
                alertDialog.getWindow().setAttributes(params);
            }
        });
        lvShowsort.setAdapter(editSortAdapter);

        if (tag==FROM_SETTING) {
            cbEdit.toggle();
            cbEdit.setText("完成");
            editSortAdapter.setTag(EDIT);
            editSortAdapter.notifyDataSetChanged();
            tvAdd.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        try {
            sortEntityList.clear();
            sortEntityList.addAll(NoteApplication.dbManager.findAll(NoteSortEntity.class));
            editSortAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (sortEntityList.size() == 0) {
            tvAdd.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        lvShowsort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (cbEdit.isChecked()) {
                    //"修改"对话框
                    final AlertDialog dialog = new AlertDialog.Builder(EditSortActivity.this).create();
                    LinearLayout dialogView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_change_sort_name, null);
                    final EditText newname = (EditText) dialogView.findViewById(R.id.change_name_et);
                    TextView cancle = (TextView) dialogView.findViewById(R.id.cancle_tv);
                    TextView confirm = (TextView) dialogView.findViewById(R.id.confirm_tv);
                    cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String oldname = sortEntityList.get(position).getSortName();
                            String name = newname.getText().toString();
                            if (!TextUtils.isEmpty(name)) {
                                //判断是否该分类已经存在
                                for (int i = 0; i < sortEntityList.size(); i++) {
                                    if (TextUtils.equals(name, sortEntityList.get(i).getSortName())) {
                                        Toast.makeText(EditSortActivity.this, "该分类已经存在", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                //修改数据库数据
                                try {
                                    KeyValue valueUsername = new KeyValue("sortname", name);
                                    KeyValue valueUsername1 = new KeyValue("sortname", name);
                                    NoteApplication.dbManager.update(NoteSortEntity.class, WhereBuilder.b("sortname", "=", oldname), valueUsername);
                                    NoteApplication.dbManager.update(NoteEntity.class, WhereBuilder.b("sortname", "=", oldname), valueUsername1);
                                    EventBus.getDefault().post(new EditEvent());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                    dialog.setView(dialogView);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = DisplayUtils.dip2px(EditSortActivity.this, 300);
                    params.height = DisplayUtils.dip2px(EditSortActivity.this, 160);
                    dialog.getWindow().setAttributes(params);
                } else {
                    if (tag == FROM_EDITNOTE) {
                        EventBus.getDefault().post(new ChooseSortEvent(sortEntityList.get(position).getSortName()));
                        finish();
                    }
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cbEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbEdit.setText("完成");
                    editSortAdapter.setTag(EDIT);
                    editSortAdapter.notifyDataSetChanged();
                    tvAdd.setVisibility(View.VISIBLE);
                } else {
                    if (tag == FROM_EDITNOTE) {
                        cbEdit.setText("编辑");
                        editSortAdapter.setTag(NON_EDIT);
                        editSortAdapter.notifyDataSetChanged();
                        tvAdd.setVisibility(View.GONE);
                    } else {
                        finish();
                    }

                }
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //"添加"对话框
                final AlertDialog dialoga = new AlertDialog.Builder(EditSortActivity.this).create();
                LinearLayout dialogView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_sort_name, null);
                final EditText nameEt = (EditText) dialogView.findViewById(R.id.add_name_et);
                TextView cancel = (TextView) dialogView.findViewById(R.id.cancle_tv);
                TextView confirm = (TextView) dialogView.findViewById(R.id.confirm_tv);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialoga.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameEt.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            try {
                                //判断是否该分类已经存在
                                for (int i = 0; i < sortEntityList.size(); i++) {
                                    if (TextUtils.equals(name, sortEntityList.get(i).getSortName())) {
                                        Toast.makeText(EditSortActivity.this, "该分类已经存在", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                //将分类名称存入数据库
                                NoteSortEntity noteSortEntity = new NoteSortEntity(name);
                                char firstChar = name.charAt(0);
                                String firstLetter = StringUtils.converterToFirstSpell(String.valueOf(firstChar));
                                noteSortEntity.setFirstLetterAsc(StringUtils.getAsc(firstLetter));
                                NoteApplication.dbManager.saveBindingId(noteSortEntity);
                                //通知相应界面进行更新数据
                                EventBus.getDefault().post(new EditEvent());
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            dialoga.dismiss();
                        } else {
                            Toast.makeText(EditSortActivity.this, "分类名称不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialoga.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                dialoga.setView(dialogView);
                dialoga.setCanceledOnTouchOutside(false);
                dialoga.show();
                WindowManager.LayoutParams params = dialoga.getWindow().getAttributes();
                params.width = DisplayUtils.dip2px(EditSortActivity.this, 300);
                params.height = DisplayUtils.dip2px(EditSortActivity.this, 160);
                dialoga.getWindow().setAttributes(params);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditEvent(EditEvent event)  {
        initData();
    }

}
