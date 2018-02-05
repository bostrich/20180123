package com.syezon.note_xh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.view.PasswordImageView;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.password_tv_1)
    PasswordImageView passwordTv1;
    @BindView(R.id.password_tv_2)
    PasswordImageView passwordTv2;
    @BindView(R.id.password_tv_3)
    PasswordImageView passwordTv3;
    @BindView(R.id.password_tv_4)
    PasswordImageView passwordTv4;
    @BindView(R.id.number1_iv)
    ImageView number1Iv;
    @BindView(R.id.number2_iv)
    ImageView number2Iv;
    @BindView(R.id.number3_iv)
    ImageView number3Iv;
    @BindView(R.id.number4_iv)
    ImageView number4Iv;
    @BindView(R.id.number5_iv)
    ImageView number5Iv;
    @BindView(R.id.number6_iv)
    ImageView number6Iv;
    @BindView(R.id.number7_iv)
    ImageView number7Iv;
    @BindView(R.id.number8_iv)
    ImageView number8Iv;
    @BindView(R.id.number9_iv)
    ImageView number9Iv;
    @BindView(R.id.number0_iv)
    ImageView number0Iv;
    @BindView(R.id.password_rl)
    RelativeLayout passwordRl;
    @BindView(R.id.promot_tv)
    TextView promotTv;
    @BindView(R.id.password_ll)
    LinearLayout passwordLl;
    @BindView(R.id.num_delete_rl)
    RelativeLayout numDeleteRl;

    //密码提示
    String prompt;

    Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        init();
        setListener();
        //没设置密码则直接finish掉
        if (TextUtils.isEmpty(SharedPerferencesUtil.getStringData(this, PreferenceKeyUtils.PASSWORD, ""))) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PasswordActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MobclickAgent.onPageEnd("PasswordActivity");
        MobclickAgent.onPause(this);
    }

    private void init() {
        //如果设置了密码提示则出现密码提示
        prompt = SharedPerferencesUtil.getStringData(this, PreferenceKeyUtils.QUESTION, "");
        if (!TextUtils.isEmpty(prompt)) {
            promotTv.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        promotTv.setOnClickListener(this);
        number0Iv.setOnClickListener(this);
        number1Iv.setOnClickListener(this);
        number2Iv.setOnClickListener(this);
        number3Iv.setOnClickListener(this);
        number4Iv.setOnClickListener(this);
        number5Iv.setOnClickListener(this);
        number6Iv.setOnClickListener(this);
        number7Iv.setOnClickListener(this);
        number8Iv.setOnClickListener(this);
        number9Iv.setOnClickListener(this);
        numDeleteRl.setOnClickListener(this);
        passwordTv4.setOnTextChangedListener(new PasswordImageView.OnTextChangedListener() {
            @Override
            public void textChanged(String content) {
                String input = passwordTv1.getTextContent() + passwordTv2.getTextContent() + passwordTv3.getTextContent() + passwordTv4.getTextContent();
                String password = SharedPerferencesUtil.getStringData(PasswordActivity.this, PreferenceKeyUtils.PASSWORD, "");
                if (TextUtils.equals(input, password)) {
                    if (getIntent().getBooleanExtra("islock", false)) {
                        finish();
                    } else {
                        Intent intent = new Intent(PasswordActivity.this, ShowPageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    //优化频繁出现Toast
                    if (toast == null) {
                        toast = Toast.makeText(PasswordActivity.this, "密码错误，请重新键入", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast.setText("密码错误，请重新键入");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2
                            , Animation.RELATIVE_TO_SELF, 0
                            , Animation.RELATIVE_TO_SELF, 0
                            , Animation.RELATIVE_TO_SELF, 0);

                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setDuration(800);

                    passwordLl.startAnimation(animation);
                    clearText();
                }
            }
        });
    }

    private void setText(String text) {
        if (TextUtils.isEmpty(passwordTv1.getTextContent())) {
            passwordTv1.setTextContent(text);
        } else if (TextUtils.isEmpty(passwordTv2.getTextContent())) {
            passwordTv2.setTextContent(text);
        } else if (TextUtils.isEmpty(passwordTv3.getTextContent())) {
            passwordTv3.setTextContent(text);
        } else if (TextUtils.isEmpty(passwordTv4.getTextContent())) {
            passwordTv4.setTextContent(text);
        }
    }

    private void deleteText() {
        if (!TextUtils.isEmpty(passwordTv4.getTextContent())) {
            passwordTv4.setTextContent("");
        } else if (!TextUtils.isEmpty(passwordTv3.getTextContent())) {
            passwordTv3.setTextContent("");
        } else if (!TextUtils.isEmpty(passwordTv2.getTextContent())) {
            passwordTv2.setTextContent("");
        } else if (!TextUtils.isEmpty(passwordTv1.getTextContent())) {
            passwordTv1.setTextContent("");
        }
    }

    private void clearText() {
        passwordTv1.setTextContent("");
        passwordTv2.setTextContent("");
        passwordTv3.setTextContent("");
        passwordTv4.setTextContent("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number0_iv:
                setText("0");
                break;
            case R.id.number1_iv:
                setText("1");
                break;
            case R.id.number2_iv:
                setText("2");
                break;
            case R.id.number3_iv:
                setText("3");
                break;
            case R.id.number4_iv:
                setText("4");
                break;
            case R.id.number5_iv:
                setText("5");
                break;
            case R.id.number6_iv:
                setText("6");
                break;
            case R.id.number7_iv:
                setText("7");
                break;
            case R.id.number8_iv:
                setText("8");
                break;
            case R.id.number9_iv:
                setText("9");
                break;
            case R.id.num_delete_rl:
                deleteText();
                break;
            case R.id.promot_tv:
                promotTv.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(passwordRl, "密码提示：" + prompt, Snackbar.LENGTH_SHORT);
                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        promotTv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                        .show();
                snackbar.getView().setBackgroundResource(R.color.mygray);
                break;
        }
    }
}
