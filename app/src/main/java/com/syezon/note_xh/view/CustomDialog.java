package com.syezon.note_xh.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 *
 */
public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
        initDialog(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public CustomDialog(Context context, int width, int heigh) {
        super(context);
        initDialog(width, heigh);

    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
        initDialog(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public CustomDialog(Context context, int themeResId, int width, int heigh) {
        super(context, themeResId);
        initDialog(width, heigh);

    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initDialog(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void initDialog(int width, int heigh) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = width;
        attributes.width = heigh;
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
    }
}
