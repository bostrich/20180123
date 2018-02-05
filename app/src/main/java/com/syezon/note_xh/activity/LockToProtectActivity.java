package com.syezon.note_xh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syezon.note_xh.R;
import com.syezon.note_xh.event.ProtectionActivated;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.view.PasswordImageView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockToProtectActivity extends BaseActivity implements View.OnClickListener {

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
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.password_ll)
    LinearLayout passwordLl;
    @BindView(R.id.num_delete_rl)
    RelativeLayout numDeleteRl;

    private static final int FIRST_SETTING = 1;
    private static final int SECOND_SETTING = 2;

    //第一次设置密码tag
    private int type = FIRST_SETTING;

    private String firstPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_to_protect);
        ButterKnife.bind(this);
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LockToProtectActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LockToProtectActivity");
        MobclickAgent.onPause(this);
    }

    private void setListener() {
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
        ivBack.setOnClickListener(this);
        numDeleteRl.setOnClickListener(this);
        passwordTv4.setOnTextChangedListener(new PasswordImageView.OnTextChangedListener() {
            @Override
            public void textChanged(String content) {
                String input = passwordTv1.getTextContent() + passwordTv2.getTextContent() +
                        passwordTv3.getTextContent() + passwordTv4.getTextContent();
                if (type == FIRST_SETTING) {
                    firstPassword = input;
                    tvInfo.setText("请再次输入密码");
                    type = SECOND_SETTING;
                    doAnimation();
                    clearText();
                } else if (type == SECOND_SETTING) {
                    //前后两次输入密码一致则弹出密码提示对话框
                    if (TextUtils.equals(input, firstPassword)) {
                        clearText();
                        //"是否设置密码提示"对话框
                        final AlertDialog alertDialog = new AlertDialog.Builder(LockToProtectActivity.this).create();
                        LinearLayout linearLayout = (LinearLayout) View.inflate(LockToProtectActivity.this, R.layout.dialog_confirm_set_password, null);
                        linearLayout.findViewById(R.id.cancle_tv).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPerferencesUtil.saveStringData(LockToProtectActivity.this, PreferenceKeyUtils.PASSWORD, firstPassword);
                                alertDialog.cancel();
                                EventBus.getDefault().post(new ProtectionActivated());
                                finish();
                            }
                        });
                        linearLayout.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPerferencesUtil.saveStringData(LockToProtectActivity.this, PreferenceKeyUtils.PASSWORD, firstPassword);
                                alertDialog.cancel();
                                EventBus.getDefault().post(new ProtectionActivated());
                                Intent intent = new Intent(LockToProtectActivity.this, QuestionActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                finish();
                            }
                        });
                        alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                        alertDialog.setView(linearLayout);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                        params.width = DisplayUtils.dip2px(LockToProtectActivity.this, 320);
                        params.height = DisplayUtils.dip2px(LockToProtectActivity.this, 160);
                        alertDialog.getWindow().setAttributes(params);

                    } else {
                        Toast.makeText(LockToProtectActivity.this, "两次密码不匹配，请重新设置", Toast.LENGTH_SHORT).show();
                        //重置密码
                        clearText();
                        tvInfo.setText("请输入密码");
                        type = FIRST_SETTING;
                        firstPassword = "";
                        doAnimation();
                    }
                }
            }
        });
    }


    //密码框弹出动画
    private void doAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2
                , Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0);

        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(800);
        passwordLl.startAnimation(animation);
    }

    //输入密码
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

    //删除密码
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

    //清空密码
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
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

}
