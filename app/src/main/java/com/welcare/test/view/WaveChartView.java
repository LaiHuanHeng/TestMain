package com.welcare.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/22.
 */

public class WaveChartView extends SurfaceView {

    private int width,height;//控件大小
    private static final int defHeight = 50,defWidth = defHeight*2;//默认控件大小
    private Paint gridPaint;//格子的画笔
    private int gridColor;//格子的颜色
    private int gridWidth = 15;//格子的宽度
    private int gridHeight = 15;//格子的高度
    private int gridLineWidth = 1;//格子的线的宽度

    private Paint waveChartLinePaint;//波动线的画笔
    private int waveChartLineColor;//波动线的颜色
    private int waveChartLineWidth = 2;//波动线的宽度

    private int hCenterLine = 0;

    private List<XY> xyList = new ArrayList<>();


    private Handler handler = new Handler(){//
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           for(int i= 0;i<xyList.size();i++)
           {
               xyList.get(i).y -= gridWidth;
           }
           invalidate();
           handler.sendEmptyMessageDelayed(0,2000);
        }
    };
    private Handler addDatahandler = new Handler(){//
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setTestData();
            addDatahandler.sendEmptyMessageDelayed(0,3000);
        }
    };



    public WaveChartView(Context context) {
        this(context,null);
    }

    public WaveChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gridPaint = new Paint();
        gridColor = Color.rgb(255,181,197);
        gridPaint.setColor(gridColor);
        gridPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(gridLineWidth);
        waveChartLinePaint = new Paint();
        waveChartLineColor = Color.rgb(0,205,0);
        waveChartLinePaint.setColor(waveChartLineColor);
        waveChartLinePaint.setStrokeWidth(waveChartLineWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {
         paintGrid(canvas);
        drawWaveLine(canvas);


    }

    /**绘画背景格子
     * @param canvas
     */
    private void paintGrid(Canvas canvas)
    {

        canvas.drawLine(width/2,0,width/2,height,gridPaint);
        int totalGridWidth = width/2;
        while (totalGridWidth >= 0)
        {
            totalGridWidth -= gridWidth;
            canvas.drawLine(totalGridWidth,0,totalGridWidth,height,gridPaint);
        }
        totalGridWidth = width/2;
        while (totalGridWidth <= width)
        {
            totalGridWidth += gridWidth;
            canvas.drawLine(totalGridWidth,0,totalGridWidth,height,gridPaint);
        }

        canvas.drawLine(0,height/2,width,height/2,gridPaint);
        int totalGridHeight = height/2;
        while (totalGridHeight >= 0)
        {
            totalGridHeight -= gridHeight;
            canvas.drawLine(0,totalGridHeight,width,totalGridHeight,gridPaint);
        }
        totalGridHeight = height/2;
        while (totalGridHeight <= height)
        {
            totalGridHeight += gridHeight;
            canvas.drawLine(0,totalGridHeight,width,totalGridHeight,gridPaint);
        }
    }
    //绘制波动线
    private void drawWaveLine(Canvas canvas)
    {
        if(xyList.size() == 0)
        {
           canvas.drawLine(0,hCenterLine,width,hCenterLine,waveChartLinePaint);
       }else
        {
            canvas.drawLine(0,hCenterLine,xyList.get(0).x,hCenterLine,waveChartLinePaint);
            for(int i =1;i<xyList.size();)
            {
                canvas.drawLine(xyList.get(i-1).x,xyList.get(i-1).y,xyList.get(i).x,xyList.get(i).y,waveChartLinePaint);
            }
        }

    }
    private void setTestData()
    {
        addData(hCenterLine);
        addData(hCenterLine);
        addData(hCenterLine+30);
        addData(hCenterLine-90);
        addData(hCenterLine+90);
        addData(hCenterLine-30);
        addData(hCenterLine);

    }

    /**添加数据
     * @param y
     */
    public void addData(int y)
    {
        XY xy = new XY();
        xy.y = y;
        if(xyList.size() <= 0)
        {
            xy.x = width;
        }else
        {
            xy.x = xyList.get(xyList.size()-1).x+3*gridWidth;
        }
        xyList.add(xy);
        if(xyList.size()> width/(3*gridWidth))
        {
            xyList.remove(0);
//            for(int i = 0;i<width/(3*gridWidth);i++)
//            {
//                xyList.remove(0);
//            }
        }

        invalidate();
    }

    public  void startRun()
    {
//        handler.sendEmptyMessage(0);
//        addDatahandler.sendEmptyMessage(0);
        setTestData();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        if(getLayoutParams()!= null) {

            if (getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {
                width = defWidth;
            }
            if (getLayoutParams().height == FrameLayout.LayoutParams.WRAP_CONTENT) {
                height = defHeight;
            }
            setMeasuredDimension(width, height);
        }
        if(height!= 0)
        hCenterLine = height/2;
    }

//    public static void RePaint()
//    {
//        Canvas canvas = null;
//        try {
//            canvas = getHolder().lockCanvas();
//            Paints(canvas);
//        } finally{
//            if (canvas != null) {
//                holder.unlockCanvasAndPost(canvas);
//            }
//        }
    public class XY
    {
        int x;
        int y;
    }
}
