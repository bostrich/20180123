package com.syezon.note_xh.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syezon.note_xh.R;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.event.ChooseSortEvent;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.PhotoUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.utils.StringUtils;
import com.syezon.note_xh.utils.ToastUtils;
import com.syezon.note_xh.view.AddPicPopWindow;
import com.syezon.note_xh.view.BackPopWindow;
import com.syezon.note_xh.view.CopyPopWindow;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xml.sax.XMLReader;
import org.xutils.ex.DbException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;//返回键
    @BindView(R.id.btn_save)
    Button btnSave;//保存键
    @BindView(R.id.tv_choose_sort)
    TextView tvChooseSort;//选择分类textview
    @BindView(R.id.et_addnote_title)
    EditText etAddnoteTitle;//添加title
    @BindView(R.id.tv_weather)
    TextView tvWeather;//天气小图标
    @BindView(R.id.tv_leidian)
    TextView tvLeidian;
    @BindView(R.id.tv_xiaoyu)
    TextView tvXiaoyu;
    @BindView(R.id.tv_xue)
    TextView tvXue;
    @BindView(R.id.tv_duoyun)
    TextView tvDuoyun;
    @BindView(R.id.tv_qingtian)
    TextView tvQingtian;
    @BindView(R.id.ll_weather)
    LinearLayout llWeather;//天气图标 LinearLayout
    @BindView(R.id.et_addnote_content)
    EditText etAddnoteContent;//添加类容
    @BindView(R.id.tv_addpic)
    TextView tvAddpic;//添加图片
    @BindView(R.id.tv_edit)
    TextView tvEdit;//编辑
    @BindView(R.id.tv_collect)
    TextView tvCollect;//收藏
    @BindView(R.id.ll_weather_describe)
    LinearLayout llWeatherDescribe;//对天气图标的描述
    @BindView(R.id.rl_addnote_top)
    RelativeLayout rlAddnoteTop;
    @BindView(R.id.rl_add_note)
    RelativeLayout rlAddNote;

    private int screeWidth;
    private int screeHeight;

    private int x;//触摸点的x坐标
    private int y;//触摸点的y坐标

    private int weatherTvTag = 1;//天气图标 LinearLayout的出现与隐藏
    private File imageFile = null;//拍照获取的图片经储存在sd卡中后新的file
    private boolean isCollect = false;//是否被收藏
    private boolean isUpdate = false;//确定是添加还是更新
    private boolean whetherBlockTouch = false;//是否屏蔽触摸事件(当popWindow弹出时让主界面不可点击)

    private static final int FROM_EDITNOTE = 1;

    private PopupWindow addPicPop;
    private PopupWindow backPop;

    private boolean mIsDoubleClick;

    //进来时的默认初始内容
    private String defaultSortName = "选择分类";
    private String defaultTitle = "";
    private String defaultContent = "";
    private String defaultWeather = "tianqi";
    private boolean defaultCollect = false;

    private CopyPopWindow copyPopWindow;

    /**
     * 点击回退键执行退出逻辑
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backLogic(ivCancel);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setListener();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddNoteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddNoteActivity");
        MobclickAgent.onPause(this);
    }

    //html图片标签与图片之间的转化
    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            String f = source.substring(0, 1);
            String url = source.substring(2);
            if (f.equals("1")) {
                try {
                    ContentResolver contentResolver = AddNoteActivity.this.getContentResolver();
                    Uri uri = Uri.parse(url);
                    Bitmap bitmap = PhotoUtils.getScaledBitmap(contentResolver, uri, screeWidth, screeHeight);
                    long bitmapSize = PhotoUtils.getBitmapSize(bitmap);/////////////////
                    LogUtil.d("ssssss", "" + bitmapSize);////////////////
                    return PhotoUtils.getMyDrawable(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d("ssssss", e.getMessage());////////////////
                    return null;
                }
            } else {
                return null;
            }
        }
    };

    //使图片可点击
    private Html.TagHandler tagHandler = new Html.TagHandler() {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                int len = output.length();
//                 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                String url = imgURL.substring(2);
//                 使图片可点击并监听点击事件
                output.setSpan(new ClickableImage(url), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    };

    private class ClickableImage extends ClickableSpan {
        private String url = "";

        public ClickableImage(String url) {
            this.url = url;
        }

        //图片点击事件的处理逻辑
        @Override
        public void onClick(View widget) {
            Uri uri = Uri.parse(url);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                Intent intent = new Intent(AddNoteActivity.this, ShowContentPicActivity.class);
                intent.putExtra("bitmap", uri);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //popWindow弹出后则屏蔽MainActivity的触摸事件
        if (whetherBlockTouch) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (addPicPop != null) {
            addPicPop.dismiss();
            addPicPop = null;
        }
        if (backPop != null) {
            backPop.dismiss();
            backPop = null;
        }

        if (copyPopWindow != null) {
            copyPopWindow.dismiss();
            copyPopWindow = null;
        }
    }

    /**
     * 使页面不可编辑
     */
    private void disAllowEdit() {
        tvWeather.setEnabled(false);
        etAddnoteTitle.setFocusableInTouchMode(false);
        etAddnoteTitle.clearFocus();
        etAddnoteContent.setFocusableInTouchMode(false);
        etAddnoteContent.clearFocus();

    }

    /**
     * 使页面可编辑
     */
    private void allowEdit() {
        tvWeather.setEnabled(true);
        etAddnoteTitle.setFocusableInTouchMode(true);
//        etAddnoteTitle.requestFocus();
        etAddnoteContent.setFocusableInTouchMode(true);
//        etAddnoteContent.requestFocus();

    }

    private void init() {
        etAddnoteContent.setMovementMethod(LinkMovementMethod.getInstance());
        addPicPop = new AddPicPopWindow(this, this);
        backPop = new BackPopWindow(this, this);
        /** DisplayMetrics获取屏幕信息 */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screeWidth = displayMetrics.widthPixels;
        screeHeight = displayMetrics.heightPixels;

        //使用矢量图
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");

        tvWeather.setTypeface(iconfont);
        tvLeidian.setTypeface(iconfont);
        tvXiaoyu.setTypeface(iconfont);
        tvXue.setTypeface(iconfont);
        tvDuoyun.setTypeface(iconfont);
        tvQingtian.setTypeface(iconfont);
        tvAddpic.setTypeface(iconfont);
        tvEdit.setTypeface(iconfont);
        tvCollect.setTypeface(iconfont);

        int textSize = SharedPerferencesUtil.getIntData(this, PreferenceKeyUtils.SP_KEY_NOTE_TEXT_SIZE, 18);
        etAddnoteContent.setTextSize(textSize);

        setDefaultName();//进来先添加默认分类名

        Intent intent = getIntent();
        NoteEntity noteEntity = intent.getParcelableExtra("note_entity");

        //点击便签item进来时的情况(记录原信息并填写原便签内容)
        if (noteEntity != null) {
            defaultSortName = TextUtils.isEmpty(noteEntity.getSortName()) ? "选择分类" : noteEntity.getSortName();
            defaultTitle = noteEntity.getTitle();
            defaultContent = Html.fromHtml(noteEntity.getContent(), imageGetter, tagHandler).toString();
            defaultWeather = noteEntity.getWeatherStr();
            defaultCollect = noteEntity.isCollect();

            tvChooseSort.setText(TextUtils.isEmpty(noteEntity.getSortName()) ? "选择分类" : noteEntity.getSortName());
            etAddnoteTitle.setText(noteEntity.getTitle());
            etAddnoteContent.setText(Html.fromHtml(noteEntity.getContent(), imageGetter, tagHandler));
            tvWeather.setText(StringUtils.strToIconStr(this, noteEntity.getWeatherStr()));
            isCollect = noteEntity.isCollect();
            if (isCollect) {
                tvCollect.setText(R.string.collect);
                tvCollect.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            }
            disAllowEdit();//不允许编辑
            isUpdate = true;//处于更新便签状态
        } else {
            //新建便签
            tvEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }

        //从相应分类跳转进来时自动添加相应的分类名，从收藏页跳进来时自动添加收藏
        String name = intent.getStringExtra("name");
        boolean collection = intent.getBooleanExtra("isCollect", false);

        if (name != null) {
            tvChooseSort.setText(name);
            defaultSortName = name;
            tvChooseSort.setText(name);/////////////////////////
        }

        if (collection) {
            defaultCollect = true;
            tvCollect.setText(R.string.collect);
            tvCollect.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            isCollect = true;
        }
    }

    /**
     * 添加默认分类名，即类名列表第一个，为空的话则不添加
     */
    private void setDefaultName() {
        try {
            List<NoteSortEntity> noteSortEntityList = NoteApplication.dbManager.findAll(NoteSortEntity.class);
            if (noteSortEntityList != null && noteSortEntityList.size() > 0) {
                defaultSortName = noteSortEntityList.get(0).getSortName();
                tvChooseSort.setText(defaultSortName);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        ivCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        tvAddpic.setOnClickListener(this);
        tvChooseSort.setOnClickListener(this);
        tvWeather.setOnClickListener(this);
        tvLeidian.setOnClickListener(this);
        tvXiaoyu.setOnClickListener(this);
        tvXue.setOnClickListener(this);
        tvDuoyun.setOnClickListener(this);
        tvQingtian.setOnClickListener(this);

        tvEdit.setOnClickListener(this);
        tvCollect.setOnClickListener(this);

        /**
         * 双击允许页面可编辑
         */
        etAddnoteTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mIsDoubleClick) {
                        allowEdit();
                        tvEdit.setVisibility(View.GONE);
                        if (btnSave.getVisibility() == View.GONE) {
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mIsDoubleClick = true;
                        etAddnoteTitle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsDoubleClick = false;
                            }
                        }, 150);
                    }

                }
                return false;
            }
        });

        /**
         * 双击允许页面可编辑
         */
        etAddnoteContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = (int) event.getX();
                    y = (int) event.getY();

                    if (mIsDoubleClick) {
                        if (copyPopWindow != null) {
                            copyPopWindow.dismiss();
                        }
                        allowEdit();
                        tvEdit.setVisibility(View.GONE);
                        if (btnSave.getVisibility() == View.GONE) {
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mIsDoubleClick = true;
                        etAddnoteContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsDoubleClick = false;
                            }
                        }, 150);
                    }

                }
                return false;
            }
        });

        /**
         * 弹出复制、编辑选项弹框
         */
        etAddnoteContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etAddnoteContent.isFocusableInTouchMode()) {
//                    Log.d("location",x+"     "+y);/////////////
                    if (copyPopWindow == null) {
                        copyPopWindow = new CopyPopWindow(AddNoteActivity.this, etAddnoteContent.getText().toString(), tvEdit);
                    }
                    if (!copyPopWindow.isShowing()) {
                        copyPopWindow.showAsDropDown(findViewById(R.id.line), x - DisplayUtils.dip2px(AddNoteActivity.this, 50), y);
                    } else {
                        copyPopWindow.dismiss();
                    }
                }
            }
        });

        /**
         * 根据内容编辑框是否有焦点来控制添加图片视图的隐藏与出现
         */
        etAddnoteContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(etAddnoteContent.getText())) {
                        etAddnoteContent.append(" ");
                    }
                    tvAddpic.setVisibility(View.VISIBLE);
                } else {
                    tvAddpic.setVisibility(View.GONE);
                }
            }
        });


        etAddnoteTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.equals(etAddnoteContent.getText(), " ")) {
                        etAddnoteContent.setText("");
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                backLogic(v);
                break;
            case R.id.btn_save:
                saveLogic();
                break;
            case R.id.tv_choose_sort:
                //通过weatherTv的可编辑状态来判断整个页面的编辑状态
                if (tvWeather.isEnabled()) {
                    //跳转到EditSortActivity选择分类名
                    Intent intent = new Intent(this, EditSortActivity.class);
                    //表示从AddNoteEntityActivity跳转过去
                    intent.putExtra("from_where", FROM_EDITNOTE);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else {
                    ToastUtils.showUniqueToast(mContext, "请按右下角编辑按钮切换到编辑状态");
                }
                break;
            case R.id.tv_addpic:
                //添加图片
                int startPosition = etAddnoteContent.getSelectionStart();
                if (startPosition != -1) {
                    addPicPop.showAtLocation(rlAddNote, Gravity.BOTTOM, 0, 0);
                    popupAlpha(0.4f);
                    whetherBlockTouch = true;
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                } else {
                    ToastUtils.showUniqueToast(this, "请选取图片插入位置");
                }
                break;
            case R.id.tv_edit:
                if (copyPopWindow != null) {
                    copyPopWindow.dismiss();
                }
                //切换编辑状态
                allowEdit();
                tvEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_collect:
                //改变收藏状态
                if (isCollect) {
                    tvCollect.setText(R.string.collect_null);
                    tvCollect.setTextColor(getResources().getColor(themeColorSourceId));
                    isCollect = false;
                    ToastUtils.showUniqueToast(mContext, "已移除收藏");
                } else {
                    tvCollect.setText(R.string.collect);
                    tvCollect.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                    isCollect = true;
                    ToastUtils.showUniqueToast(mContext, "已添加收藏");
                }

                if (isCollect == defaultCollect) {
                    btnSave.setVisibility(View.GONE);
                } else {
                    btnSave.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_weather:
                //控制天气选择栏的出现与隐藏
                if (weatherTvTag == 1) {
                    llWeather.setVisibility(View.VISIBLE);
                    llWeatherDescribe.setVisibility(View.VISIBLE);
                    weatherTvTag = 2;
                } else {
                    llWeather.setVisibility(View.GONE);
                    llWeatherDescribe.setVisibility(View.GONE);
                    weatherTvTag = 1;
                }
                break;
            case R.id.camera:
                addPicPop.dismiss();
                popupAlpha(1f);
                whetherBlockTouch = false;
                //相机拍照
//                File tmpDir = this.getFilesDir();
//                LogUtil.d("ssssss",""+tmpDir);/////////////////
//                if (tmpDir != null && !tmpDir.exists()) {
//                    tmpDir.mkdir();
//                }

                //文件夹note_pic
                String path = Environment.getExternalStorageDirectory().toString() + "/note_pic";
                File path1 = new File(path);
                if (!path1.exists()) {
                    path1.mkdirs();
                }
                imageFile = new File(path1, System.currentTimeMillis() + ".png");

//                imageFile = new File(file, System.currentTimeMillis() + ".png");
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent1, PhotoUtils.REQUESTCODE_CAMAR);
                break;
            case R.id.local:
                addPicPop.dismiss();
                popupAlpha(1f);
                whetherBlockTouch = false;
                //从本地图库选择照片
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType("image/*");
                startActivityForResult(intent2, PhotoUtils.REQUESTCODE_PHOTO);
                break;
            case R.id.cancel:
                //取消添加图片
                addPicPop.dismiss();
                popupAlpha(1f);
                whetherBlockTouch = false;
                break;
            case R.id.quit_edit:
                //放弃编辑
                finish();
                break;
            case R.id.save_edit:
                //保存编辑退出
                saveLogic();
                break;
            case R.id.tv_leidian:
                tvWeather.setText(tvLeidian.getText());
                llWeather.setVisibility(View.GONE);
                llWeatherDescribe.setVisibility(View.GONE);
                break;
            case R.id.tv_xiaoyu:
                tvWeather.setText(tvXiaoyu.getText());
                llWeather.setVisibility(View.GONE);
                llWeatherDescribe.setVisibility(View.GONE);
                break;
            case R.id.tv_xue:
                tvWeather.setText(tvXue.getText());
                llWeather.setVisibility(View.GONE);
                llWeatherDescribe.setVisibility(View.GONE);
                break;
            case R.id.tv_duoyun:
                tvWeather.setText(tvDuoyun.getText());
                llWeather.setVisibility(View.GONE);
                llWeatherDescribe.setVisibility(View.GONE);
                break;
            case R.id.tv_qingtian:
                tvWeather.setText(tvQingtian.getText());
                llWeather.setVisibility(View.GONE);
                llWeatherDescribe.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 保存的逻辑
     */
    private void saveLogic() {
        if (isUpdate) {
            //编辑原有便签内容
            NoteEntity noteEntity = getIntent().getParcelableExtra("note_entity");
            //天气
            noteEntity.setWeatherStr(StringUtils.iconStrToStr(AddNoteActivity.this, tvWeather.getText().toString()));
            //分类名
            String sortName = tvChooseSort.getText().toString();
            if (TextUtils.equals(sortName, "选择分类")) {
                noteEntity.setSortName("");
            } else {
                noteEntity.setSortName(sortName);
            }
            //标题
            String title = "";
            if (etAddnoteTitle.getText() != null) {
                title = etAddnoteTitle.getText().toString();
            }
            noteEntity.setTitle(title);
            //内容
            String content = Html.toHtml(etAddnoteContent.getEditableText());
            noteEntity.setContent(content);
            //是否有图片
            boolean hasImage = content.contains(".png") || content.contains(".jpg");
            noteEntity.setHasImage(hasImage);
            //简略内容
            String[] strings = etAddnoteContent.getText().toString().split("\n");
            String briefContent = "";
            if (strings.length > 0) {
                briefContent += strings[0];
            }
            if (strings.length > 1) {
                briefContent += strings[1];
            }
            //内容前两行中如果有图片标签则将其去掉
            if (hasImage) {
                briefContent = briefContent.substring(0, briefContent.length() - 1);
            }
            noteEntity.setBriefContent(briefContent);
            //图片路径
            String imagePath = "";
            if (hasImage) {
                //如果内容中包含图片则取出第一张图片
                final List<String> imaPaths = new ArrayList<>();
                Html.fromHtml(content, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        String tag = source.substring(0, 1);
                        String imgUrl = source.substring(2);
                        if (tag.equals("1")) {
                            imaPaths.add(imgUrl);
                        }
                        return null;
//                            return AddNoteActivity.this.getResources().getDrawable(R.drawable.dian);
                    }
                }, null);
                if (imaPaths.size() > 0) {
                    imagePath = imaPaths.get(0);
                }
            }
            noteEntity.setImagePath(imagePath);

            //是否收藏
            noteEntity.setCollect(isCollect);
            //添加版本号
            noteEntity.setVersion(noteEntity.getVersion() + 1);

            try {
                NoteApplication.dbManager.update(noteEntity, "weather", "sortname", "title", "content", "hasimage", "iscollect", "imagepath", "briefcontent", "version");
                EventBus.getDefault().post(new EditEvent());
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                disAllowEdit();
                tvEdit.setVisibility(View.VISIBLE);
            } catch (DbException e) {
                e.printStackTrace();
            }
            defaultSortName = tvChooseSort.getText().toString();
            defaultTitle = etAddnoteTitle.getText().toString();
            defaultContent = etAddnoteContent.getText().toString();
            defaultWeather = StringUtils.iconStrToStr(AddNoteActivity.this, tvWeather.getText().toString());
            defaultCollect = isCollect;

            finish();
        } else {
            //新建便签的逻辑
            if (TextUtils.isEmpty(etAddnoteContent.getText()) || TextUtils.equals(etAddnoteContent.getText(), " ") || TextUtils.equals(tvChooseSort.getText(), "选择分类")) {
                if (TextUtils.isEmpty(etAddnoteContent.getText()) || TextUtils.equals(etAddnoteContent.getText(), " ")) {
                    ToastUtils.showUniqueToast(mContext, "请输入内容");
                } else if (TextUtils.equals(tvChooseSort.getText(), "选择分类")) {
                    ToastUtils.showUniqueToast(mContext, "请选择分类");
                }
            } else {
                @SuppressLint("SimpleDateFormat")
                String time = System.currentTimeMillis() + "";
                String sortName = tvChooseSort.getText().toString();
                String title = "";
                if (etAddnoteTitle.getText() != null) {
                    title = etAddnoteTitle.getText().toString();
                }

                //简略内容
                String[] strings = etAddnoteContent.getText().toString().split("\n");
                String briefContent = "";
                if (strings.length > 0) {
                    briefContent += strings[0];
                }
                if (strings.length > 1) {
                    briefContent += strings[1];
                }

                String htmlText = Html.toHtml(etAddnoteContent.getEditableText());
                //是否包含图片
                boolean hasImage = htmlText.contains(".png");

                //内容前两行中如果有图片标签则使其失效(将标签删除部分弄得不完整)
                if (hasImage) {
                    briefContent = briefContent.substring(0, briefContent.length() - 1);
                }

                //图片路径
                String imagePath = "";
                if (hasImage) {
                    //如果内容中包含图片则取出第一张图片
                    final List<String> imaPaths = new ArrayList<>();
                    Html.fromHtml(htmlText, new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            String tag = source.substring(0, 1);
                            String url = source.substring(2);
                            if (tag.equals("1")) {
                                imaPaths.add(url);
                            }
                            return null;
                        }
                    }, null);
                    if (imaPaths.size() > 0) {
                        imagePath = imaPaths.get(0);
                    }
                }

                NoteEntity noteEntity = new NoteEntity();
                noteEntity.setHasImage(hasImage);
                noteEntity.setWeatherStr(StringUtils.iconStrToStr(this, tvWeather.getText().toString()));
                noteEntity.setSortName(sortName);
                noteEntity.setTime(time);
                noteEntity.setTitle(title);
                noteEntity.setContent(htmlText);
                noteEntity.setCollect(isCollect);
                noteEntity.setBriefContent(briefContent);
                noteEntity.setImagePath(imagePath);
                noteEntity.setVersion(1);
                try {
                    NoteApplication.dbManager.saveBindingId(noteEntity);
                    EventBus.getDefault().post(new EditEvent());
                    ToastUtils.showUniqueToast(mContext, "创建成功");
                    finish();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
//        //隐藏键盘
//        InputMethodManager imm2 = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm2.isActive()) {
//            imm2.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//        }
    }

    /**
     * 退出的逻辑,未做改变直接退出,否则会弹出确认退出弹框
     */
    private void backLogic(View v) {
        if (TextUtils.equals(defaultSortName, tvChooseSort.getText()) &&
                TextUtils.equals(defaultTitle, etAddnoteTitle.getText()) &&
                (TextUtils.equals(defaultContent, etAddnoteContent.getText()) || (TextUtils.isEmpty(defaultContent) && TextUtils.equals(etAddnoteContent.getText(), " "))) &&
                TextUtils.equals(defaultWeather, StringUtils.iconStrToStr(this, tvWeather.getText().toString())) && defaultCollect == isCollect) {
            onBackPressed();
        } else {
            //"是否退出编辑"弹框
//            final AlertDialog dialog = new AlertDialog.Builder(AddNoteActivity.this).create();
//            LinearLayout dialogView = (LinearLayout) View.inflate(AddNoteActivity.this, R.layout.dialog_is_back, null);
//            dialogView.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                    finish();
//                }
//            });
//            dialogView.findViewById(R.id.cancle_tv).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
//            dialog.setView(dialogView);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//            params.width = DisplayUtils.dip2px(this, 300);
//            params.height = DisplayUtils.dip2px(this, 150);
//            dialog.getWindow().setAttributes(params);

            backPop.showAtLocation(rlAddNote, Gravity.BOTTOM, 0, 0);
            popupAlpha(0.4f);
            whetherBlockTouch = true;
            //隐藏键盘
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        }
    }

    //控制界面的透明度
    private void popupAlpha(float f) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = f;
        this.getWindow().setAttributes(params);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseSortEvent(ChooseSortEvent event) {
        tvChooseSort.setText(event.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.REQUESTCODE_CAMAR:
                    Uri uri2 = Uri.fromFile(imageFile);
//                    LogUtil.d("ssssss",uri2.toString());//////////////
                    Editable eb2 = etAddnoteContent.getEditableText();
                    // 获得光标所在位置
                    int startPosition2 = etAddnoteContent.getSelectionStart();
                    eb2.insert(
                            startPosition2,
                            Html.fromHtml("<br/><img src='1:" + uri2.toString()
                                    + "'/><br/>", imageGetter, tagHandler));
                    break;

                case PhotoUtils.REQUESTCODE_PHOTO:
                    if (data == null) {
                        return;
                    } else {
                        Uri uri = PhotoUtils.convertUri(this, data.getData());
                        Editable eb = etAddnoteContent.getEditableText();
                        // 获得光标所在位置
                        int startPosition = etAddnoteContent.getSelectionStart();
                        if (uri != null) {
                            eb.insert(
                                    startPosition,
                                    Html.fromHtml("<br/><img src='1:" + uri.toString()
                                            + "'/><br/>", imageGetter, tagHandler));
                        } else {
                            Toast.makeText(AddNoteActivity.this, "图片不存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

}