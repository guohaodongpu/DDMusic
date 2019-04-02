package com.ghd.ts.ddmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class QuickIndexBar extends View {
    private static final String TAG = "TAG";
    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};
    private Paint mPaint;

    public interface OnLetterChangeListener{
        void OnLetterChange(String letter);
    }
    public QuickIndexBar(Context context) {
        this(context, null);
    }
    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public QuickIndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mCellHeight = mHeight * 1.0f / LETTERS.length;
        mCellWidth = getMeasuredWidth();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制字母
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            // 求x坐标
            int x = (int) (mCellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
            // 求y坐标
            // 格子高度的一半 + 文字高度的一半 + 其上边所有格子高度
            Rect bounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            int y = (int) (mCellHeight / 2.0f + bounds.height() / 2.0f + mCellHeight * i);
            canvas.drawText(text, x, y, mPaint);
        }
    }



















}
