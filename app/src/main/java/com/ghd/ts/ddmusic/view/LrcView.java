package com.ghd.ts.ddmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import com.ghd.ts.ddmusic.R;
import com.ghd.ts.ddmusic.entity.LrcBean;
import com.ghd.ts.ddmusic.utils.LrcUtil;

import java.util.List;

public class LrcView extends View {

    private List<LrcBean> mList;
    private Paint mgPaint;
    private Paint mhPaint;
    private int mWidth = 0, mHeight = 0;
    private int currentPosition = 0;
    private MediaPlayer mPlayer;
    private int mLastPosition = 0;
    private int mLighLineColor;
    private int mLrcColor;
    private int mMode = 0;
    public final static int KARAOKE = 1;

    public void setHighLineColor(int highLineColor) {
        this.mLighLineColor = highLineColor;
    }

    public void setLrcColor(int lrcColor) {
        this.mLrcColor = lrcColor;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public void setPlayer(MediaPlayer player) {
        this.mPlayer = player;
    }

    /**
     * 标准歌词字符串
     *
     * @param lrc
     */
    public void setLrc(String lrc) {
        mList = LrcUtil.parseStr2List(lrc);
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
        mLighLineColor = ta.getColor(R.styleable.LrcView_hignLineColor, getResources().getColor(R.color.green));
        mLrcColor = ta.getColor(R.styleable.LrcView_lrcColor, getResources().getColor(android.R.color.darker_gray));
        mMode = ta.getInt(R.styleable.LrcView_lrcMode, mMode);
        ta.recycle();
        mgPaint = new Paint();
        mgPaint.setAntiAlias(true);
        mgPaint.setColor(mLrcColor);
        mgPaint.setTextSize(36);
        mgPaint.setTextAlign(Paint.Align.CENTER);
        mhPaint = new Paint();
        mhPaint.setAntiAlias(true);
        mhPaint.setColor(mLighLineColor);
        mhPaint.setTextSize(36);
        mhPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }
        if (mList == null || mList.size() == 0) {
            canvas.drawText("暂无歌词", mWidth / 2, mHeight / 2, mgPaint);
            return;
        }

        getCurrentPosition();

//        drawLrc1(canvas);
        int currentMillis = mPlayer.getCurrentPosition();
        drawLrc2(canvas, currentMillis);
        long start = mList.get(currentPosition).getStart();
        float v = (currentMillis - start) > 500 ? currentPosition * 80 : mLastPosition * 80 + (currentPosition - mLastPosition) * 80 * ((currentMillis - start) / 500f);
        setScrollY((int) v);
        if (getScrollY() == currentPosition * 80) {
            mLastPosition = currentPosition;
        }
        postInvalidateDelayed(100);
    }

    private void drawLrc2(Canvas canvas, int currentMillis) {
        if (mMode == 0) {
            for (int i = 0; i < mList.size(); i++) {
                if (i == currentPosition) {
                    canvas.drawText(mList.get(i).getLrc(), mWidth / 2, mHeight / 2 + 80 * i, mhPaint);
                } else {
                    canvas.drawText(mList.get(i).getLrc(), mWidth / 2, mHeight / 2 + 80 * i, mgPaint);
                }
            }
        } else {
            for (int i = 0; i < mList.size(); i++) {
                canvas.drawText(mList.get(i).getLrc(), mWidth / 2, mHeight / 2 + 80 * i, mgPaint);
            }
            String highLineLrc = mList.get(currentPosition).getLrc();
            int highLineWidth = (int) mgPaint.measureText(highLineLrc);
            int leftOffset = (mWidth - highLineWidth) / 2;
            LrcBean lrcBean = mList.get(currentPosition);
            long start = lrcBean.getStart();
            long end = lrcBean.getEnd();
            int i = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
            if (i > 0) {
                Bitmap textBitmap = Bitmap.createBitmap(i, 80, Bitmap.Config.ARGB_8888);
                Canvas textCanvas = new Canvas(textBitmap);
                textCanvas.drawText(highLineLrc, highLineWidth / 2, 80, mhPaint);
                canvas.drawBitmap(textBitmap, leftOffset, mHeight / 2 + 80 * (currentPosition - 1), null);
            }
        }
    }

    public void init() {
        currentPosition = 0;
        mLastPosition = 0;
        setScrollY(0);
        invalidate();
    }

    private void drawLrc1(Canvas canvas) {
        String text = mList.get(currentPosition).getLrc();
        canvas.drawText(text, mWidth / 2, mHeight / 2, mhPaint);

        for (int i = 1; i < 10; i++) {
            int index = currentPosition - i;
            if (index > -1) {
                canvas.drawText(mList.get(index).getLrc(), mWidth / 2, mHeight / 2 - 80 * i, mgPaint);
            }
        }
        for (int i = 1; i < 10; i++) {
            int index = currentPosition + i;
            if (index < mList.size()) {
                canvas.drawText(mList.get(index).getLrc(), mWidth / 2, mHeight / 2 + 80 * i, mgPaint);
            }
        }
    }

    private void getCurrentPosition() {
        try {
            int currentMillis = mPlayer.getCurrentPosition();
            if (currentMillis < mList.get(0).getStart()) {
                currentPosition = 0;
                return;
            }
            if (currentMillis > mList.get(mList.size() - 1).getStart()) {
                currentPosition = mList.size() - 1;
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                if (currentMillis >= mList.get(i).getStart() && currentMillis < mList.get(i).getEnd()) {
                    currentPosition = i;
                    return;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            postInvalidateDelayed(100);
        }
    }





}
