package com.syezon.note_xh.activity;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.TouchImageAdapter;
import com.syezon.note_xh.utils.PhotoUtils;
import com.syezon.note_xh.view.ExtendedViewPager;
import com.syezon.note_xh.view.TouchImageView;
import com.umeng.analytics.MobclickAgent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowContentPicActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.viewPager)
    ExtendedViewPager viewPager;

    private TouchImageAdapter adapter;
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content_pic);
        ButterKnife.bind(this);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ShowContentPicActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ShowContentPicActivity");
        MobclickAgent.onPause(this);
    }

    private void init() {
        viewList=new ArrayList<>();
        Uri uri=getIntent().getParcelableExtra("bitmap");
        ContentResolver contentResolver = ShowContentPicActivity.this.getContentResolver();
        Bitmap bitmap=null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=1;
        try {
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap!=null) {
            long bitmapSize= PhotoUtils.getBitmapSize(bitmap);
            Log.d("ssssss",""+bitmapSize);
            //图片过大就二次采样，否则无法显示
            if(bitmapSize>20000000){
                options.inSampleSize=2;
                try {
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null,options);
                    Log.d("ssssss",""+bitmapSize);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
//        Drawable drawable= PhotoUtils.getMyDrawable(bitmap);
            TouchImageView touchImageView = new TouchImageView(this);
            touchImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            touchImageView.setImageBitmap(bitmap);
//        touchImageView.setImageDrawable(drawable);
            viewList.add(touchImageView);
            adapter=new TouchImageAdapter(this,viewList);
            viewPager.setAdapter(adapter);
        }
    }

    private void setListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
