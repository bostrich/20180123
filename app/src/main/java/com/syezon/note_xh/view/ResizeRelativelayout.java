package com.syezon.note_xh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by admin on 2016/7/21.
 */
public class ResizeRelativelayout extends RelativeLayout {
    private OnResizeListener mListener;

    public void setOnResizeListener(OnResizeListener mListener) {
        this.mListener = mListener;
    }

    public ResizeRelativelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null) {
           mListener.OnResize(w, h, oldw, oldh);
        }
    }


    public interface OnResizeListener {
        void OnResize(int w, int h, int oldw, int oldh);
    }
}
