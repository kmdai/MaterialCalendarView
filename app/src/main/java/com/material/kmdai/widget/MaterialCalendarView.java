package com.material.kmdai.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.material.kmdai.materialcalendarview.R;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by kmdai on 16-6-15.
 */
public class MaterialCalendarView extends View {
    private final static int MAIN_COLOR = Color.parseColor("#ff69be40");
    /**
     * 星期
     */
    private String[] mWeek = {"日", "一", "二", "三", "四", "五", "六"};
    private int mWidth;
    private int mHight;
    private float mHorizontalAV;
    private float mVerticalAV;
    /**
     * 当月有多少天
     */
    private int mCurrentMonthDay;
    /**
     * 本月
     */
    private int mMonth;
    /**
     * 本年
     */
    private int mYear;
    /**
     * 当天是几号
     */
    private int mDayOfMonth;
    /**
     * 今天是星期几
     */
    private int mDayOfWeek;
    private int mFirstWeek;
    private Paint mPaint;
    private TextPaint mTitlePT;
    private Paint.FontMetrics mFontMetrics;
    private float mTextHight;
    private RectF mToday;
    /**
     * 选择圆圈半径
     */
    private float mSelectRadius;
    private int mTextColor;
    private int mActivePointerId;
    private float mPressX;
    private float mPressY;
    private float mStartX;
    private float mStartY;
    private int[][] mDate;
    private OnDateSelect mOnDateSelect;

    public MaterialCalendarView(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float textsize = 150;
        mTextColor = Color.BLACK;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
            textsize = attributes.getDimension(R.styleable.CalendarView_textSize, 15);
            mTextColor = attributes.getColor(R.styleable.CalendarView_textColor, Color.BLACK);
        }
        mPressX = mPressY = -1;
        mPaint = new Paint();
        mPaint.setColor(MAIN_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mTitlePT = new TextPaint();
        mTitlePT.setStyle(Paint.Style.FILL);
        mTitlePT.setTextSize(textsize);
        mTitlePT.setAntiAlias(true);
        mTitlePT.setTextAlign(Paint.Align.CENTER);
        mTitlePT.setColor(mTextColor);
        mFontMetrics = mTitlePT.getFontMetrics();
        mTextHight = mFontMetrics.bottom - mFontMetrics.top;
        mSelectRadius = mTextHight / 2 + dip2px(5);
        mToday = new RectF();
        mDate = new int[5][7];
        initDate();
    }

    /**
     * 日期点击坚挺
     *
     * @param onDateSelect
     */
    public void setOnDateSelect(OnDateSelect onDateSelect) {
        mOnDateSelect = onDateSelect;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWeek(canvas);
        drawDay(canvas);
    }

    /**
     * 画星期
     *
     * @param canvas
     */
    private void drawWeek(Canvas canvas) {
        mTitlePT.setColor(mTextColor);
        for (int i = 0; i < mWeek.length; i++) {
            float dx = i * mHorizontalAV + mHorizontalAV / 2;
            StaticLayout staticLayout = new StaticLayout(mWeek[i], mTitlePT, (int) mHorizontalAV, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
            int h = staticLayout.getHeight();
            int dy = ((int) mVerticalAV - h) / 2;
            canvas.translate(dx, dy);
            staticLayout.draw(canvas);
            canvas.translate(-(dx), -dy);
        }
    }

    /**
     * 画日期
     *
     * @param canvas
     */
    private void drawDay(Canvas canvas) {
        int colum = mFirstWeek;
        int row = 1;
        float width = mVerticalAV;
        float textBaseLine = mVerticalAV - (mVerticalAV - mTextHight) / 2 - mFontMetrics.bottom;
        for (int i = 1; i <= mCurrentMonthDay; i++) {
            mDate[row - 1][colum] = i;
            mTitlePT.setColor(mTextColor);
            if ((row + 1) == mPressY && (colum + 1) == mPressX && mPressY > 1) {
                float x, y;
                x = (mPressX - 1) * mHorizontalAV + mHorizontalAV / 2;
                y = (mPressY - 1) * mVerticalAV + mVerticalAV / 2;
                mToday.set(x - mSelectRadius, y - mSelectRadius, x + mSelectRadius, y + mSelectRadius);
                canvas.drawArc(mToday, 0, 360, true, mPaint);
                mTitlePT.setColor(Color.WHITE);
            } else if (i == mDayOfMonth) {
                float x, y;
                x = colum * mHorizontalAV + mHorizontalAV / 2;
                y = row * mVerticalAV + mVerticalAV / 2;
                mToday.set(x - mSelectRadius, y - mSelectRadius, x + mSelectRadius, y + mSelectRadius);
                canvas.drawArc(mToday, 0, 360, true, mPaint);
                mTitlePT.setColor(Color.WHITE);
            }
            canvas.drawText(String.valueOf(i), mHorizontalAV * colum + mHorizontalAV / 2, textBaseLine + row * mVerticalAV, mTitlePT);
            if (colum++ == 6) {
                row++;
                colum = 0;
                System.out.println("row------" + row);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mActivePointerId = event.getPointerId(0);
                mPressX = (int) Math.ceil((mStartX) / mHorizontalAV);
                mPressY = (int) Math.ceil((mStartY) / mVerticalAV);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                int pressX = (int) Math.ceil((x) / mHorizontalAV);
                int pressY = (int) Math.ceil((y) / mVerticalAV);
                if (pressX == mPressX && pressY == mPressY && mOnDateSelect != null) {
//                    if(){
//
//                    }
//                    mOnDateSelect.onDateSelect(mYear,mMonth,mPressX,);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                mPressX = mPressY = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHight = h;
        mHorizontalAV = mWidth / 7;
        mVerticalAV = mHight / 6;
        invalidate();
    }

    /**
     * 取得当月天数
     */
    public int getCurrentMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(Calendar.MONTH, 4);
        calendar.set(Calendar.DATE, 1);//把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return calendar.get(Calendar.DATE);
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(Calendar.MONTH, 4);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mYear = calendar.get(Calendar.YEAR);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mCurrentMonthDay = getCurrentMonthLastDay();
        if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY) {
            mDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
        calendar.set(Calendar.DATE, 1);
        mFirstWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * dp转化为 px
     *
     * @param dpValue
     * @return
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    interface OnDateSelect {
        void onDateSelect(int year, int month, int day, int week);
    }
}
