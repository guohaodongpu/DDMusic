package com.ghd.ts.ddmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ghd.ts.ddmusic.OnTouchingLetterChangedListener;

public class RulerWidget  extends View {

    public static String[] indexStr = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    public static int INDEX_LENGTH = indexStr.length;

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    Paint mPaint = new Paint();
    boolean showBkg = false;
    int choose = -1;

    public RulerWidget(Context context) {
        super(context);
    }

    public RulerWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RulerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//		if(showBkg){
        canvas.drawColor(Color.parseColor("#40000000"));
//		 }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / indexStr.length;
        for(int i=0;i<indexStr.length;i++){
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(24);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            if(i == choose){
                mPaint.setColor(Color.parseColor("#3399ff"));
                mPaint.setFakeBoldText(true);
            }
            float xPos = width/2  - mPaint.measureText(indexStr[i])/2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(indexStr[i], xPos, yPos, mPaint);
            mPaint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y/getHeight()*indexStr.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if(oldChoose != c && listener != null){
                    if(c > 0 && c< indexStr.length){
                        listener.onTouchingLetterChanged(indexStr[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(oldChoose != c && listener != null){
                    if(c > 0 && c< indexStr.length){
                        listener.onTouchingLetterChanged(indexStr[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//	                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

}