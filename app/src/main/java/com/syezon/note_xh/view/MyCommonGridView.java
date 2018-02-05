package com.syezon.note_xh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyCommonGridView extends GridView {
    public MyCommonGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, h);
    }
}
