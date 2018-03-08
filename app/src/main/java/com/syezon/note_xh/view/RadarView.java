package com.syezon.note_xh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/4/27.
 */

public class RadarView extends FrameLayout {
    private Context mContext;
    private int viewSize = 360;
    private Paint mPaintLine;
    private Paint mPaintCircle;
    private Paint mPaintSector;
    public boolean isStart = false;
    private Paint mPaintPoint;
    //旋转效果起始角度
    private int start = 0;

    private List<Dot> dots = new ArrayList<>();

    private Shader mShader;

    private Matrix matrix;

    public final static int CLOCK_WISE=1;
    public final static int ANTI_CLOCK_WISE=-1;

    @IntDef({ CLOCK_WISE, ANTI_CLOCK_WISE })
    public @interface RADAR_DIRECTION {

    }
    //默认为顺时针呢
    private final static int DEFAULT_DIERCTION=CLOCK_WISE;

    //设定雷达扫描方向
    private int direction=DEFAULT_DIERCTION;

    private boolean threadRunning = false;

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    public RadarView(Context context) {
        super(context);
        mContext = context;
        initPaint();
    }

    private void initPaint() {
        setBackgroundColor(Color.TRANSPARENT);

        //宽度=5，抗锯齿，描边效果的白色画笔
        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(2);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setColor(getResources().getColor(R.color.scan));

        //宽度=5，抗锯齿，描边效果的浅绿色画笔
        mPaintCircle = new Paint();
        mPaintCircle.setStrokeWidth(5f);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setColor(0x99000000);

        //白色实心画笔
        mPaintPoint=new Paint();
        mPaintPoint.setColor(0x9a3E9E90);
        mPaintPoint.setStyle(Paint.Style.FILL);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.viewSize= MeasureSpec.getSize(widthMeasureSpec);

        if (mPaintSector==null) {
            //暗绿色的画笔
            mPaintSector = new Paint();
            mPaintSector.setColor(0x9Debfaf8);
            mPaintSector.setAntiAlias(true);
            mShader = new SweepGradient(viewSize / 2, viewSize / 2, Color.TRANSPARENT, 0x9D95e2d7);
            mPaintSector.setShader(mShader);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void start() {
        if(!threadRunning){
            ScanThread thread = new ScanThread(this);
            thread.setName("radar");
            thread.start();
            threadRunning = true;
            isStart = true;
        }
    }

    public void stop() {
        if (isStart) {
            isStart = false;
        }
        threadRunning = false;
        start = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(viewSize / 2, viewSize / 2, viewSize / 6, mPaintLine);
        canvas.drawCircle(viewSize / 2, viewSize / 2, viewSize / 3, mPaintLine);
        canvas.drawCircle(viewSize / 2, viewSize / 2, viewSize / 2-1, mPaintLine);
        //绘制两条十字线
        canvas.drawLine(viewSize / 2, 0, viewSize / 2, viewSize, mPaintLine);
        canvas.drawLine(0, viewSize / 2, viewSize, viewSize / 2, mPaintLine);


        //这里在雷达扫描过制定圆周度数后
        for (int i = 0; i < dots.size(); i++) {
            Dot dot = dots.get(i);
            float radio = (3 + Math.abs((start / 40) % 4 - 2)) * DisplayUtils.getScreenDensity(mContext);
            if(dot.isGig) radio = (5 + Math.abs((start / 40) % 6 - 3)) * DisplayUtils.getScreenDensity(mContext);
            canvas.drawCircle(viewSize / 2 + dot.getX(), viewSize / 2 + dot.getY(), radio, mPaintPoint);
        }

        //根据matrix中设定角度，不断绘制shader,呈现出一种扇形扫描效果
        try {
            canvas.concat(matrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        canvas.drawCircle(viewSize / 2, viewSize / 2, viewSize / 2, mPaintSector);
        super.onDraw(canvas);
    }

    public void setDirection(@RADAR_DIRECTION int direction) {
        if (direction != CLOCK_WISE && direction != ANTI_CLOCK_WISE) {
            throw new IllegalArgumentException("Use @RADAR_DIRECTION constants only!");
        }
        this.direction = direction;
    }

    private class ScanThread extends Thread {

        private RadarView view;

        public ScanThread(RadarView view) {
            this.view = view;
        }

        @Override
        public void run() {
            while (threadRunning) {
                if (isStart) {
                    view.post(new Runnable() {
                        public void run() {
                            start += 1;
                            if(start % 300 == 0) addRandomDot(false);
                            matrix = new Matrix();
                            //设定旋转角度,制定进行转转操作的圆心
//                            matrix.postRotate(start, viewSize / 2, viewSize / 2);
//                            matrix.setRotate(start,viewSize/2,viewSize/2);
                            matrix.preRotate(direction*start,viewSize/2,viewSize/2);
                            view.invalidate();
                        }
                    });
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 生成随机点
     */
    public void addRandomDot(boolean isBig) {
        Dot dot = new Dot();
        int randomX = (int) (viewSize / 2 * (2 * Math.random() - 1) * 0.8);
        dot.setX(randomX);
        int maxY = (int) Math.sqrt(Math.pow(viewSize / 2, 2) - Math.pow(randomX, 2));
        int randomY = (int) (maxY * (2 * Math.random() - 1) * 0.8);
        dot.setY(randomY);
        dot.setGig(isBig);
        if(dots.size() < 3){
            dots.add(dot);
        }else{
            Iterator<Dot> iterator = dots.iterator();
            while(iterator.hasNext()){
                Dot next = iterator.next();
                if(next.isGig == dot.isGig()){
                    iterator.remove();
                    break;
                }
            }
            dots.add(dot);
        }

    }



    class Dot{
        private int x;
        private int y;
        private boolean isGig;

        public Dot(){}

        public Dot(int x, int y, boolean isGig) {
            this.x = x;
            this.y = y;
            this.isGig = isGig;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isGig() {
            return isGig;
        }

        public void setGig(boolean gig) {
            isGig = gig;
        }
    }
}
