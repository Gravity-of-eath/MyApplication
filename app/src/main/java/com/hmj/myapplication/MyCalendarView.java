package com.hmj.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.hmj.myapplication.MyCalendarView.ID.LEFT_ARROW;

/**
 * Created by Administrator on 2018/1/9.
 */

public class MyCalendarView extends LinearLayout {
    private final String TAG = "yaokun_calendar";
    private int mWidth;
    private int mHight;
    private int cellhight;
    private int rowhight = 35;
    private Context context;
    int colorTrans = Color.parseColor("#00ffffff");
    Calendar calendar = Calendar.getInstance();
    List<CellView> cellViews = new ArrayList<>();
    int monthMaxday = 31;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
    int monthStart = 0;
    int selectedbackground, cellbackground, titleTextColor, weekTextColor, dayTextColor, topTextColor, btomTextColor;
    int titleTextSize, daytextsize;
    Drawable leftArrow, rightArrow;
    boolean showTitle = true, showEn = true, multipleSelect, canChouse, boldface;
    private String[] WeekTitelsSC = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",};
    private String[] WeekTitelsEN = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT",};
    private GradientDrawable selectedDrawable;
    private LinearLayout monthView;
    Decorator decorator;
    DataSelectListener dataSelectListener;
    private TextView timeTitle;

    public DataSelectListener getDataSelectListener() {
        return dataSelectListener;
    }

    public void setDataSelectListener(DataSelectListener dataSelectListener) {
        this.dataSelectListener = dataSelectListener;
    }

    public Decorator getDecorator() {
        return decorator;
    }

    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
        initMonthView(calendar);
    }

    public MyCalendarView(Context context) {
        this(context, null);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(VERTICAL);
        this.context = context;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        UpdataView(calendar);
        initArgument(attrs);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                InitView();
            }
        }, 30);
    }

    private void initArgument(AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.MyCalendarView, 0, 0);
        if (a.getIndexCount() > 0) {
            showEn = a.getBoolean(R.styleable.MyCalendarView_showEn, true);
            multipleSelect = a.getBoolean(R.styleable.MyCalendarView_multipleSelect, true);
            showTitle = a.getBoolean(R.styleable.MyCalendarView_showTitle, true);
            canChouse = a.getBoolean(R.styleable.MyCalendarView_canChouse, true);
            boldface = a.getBoolean(R.styleable.MyCalendarView_boldface, false);
            selectedbackground = a.getColor(R.styleable.MyCalendarView_selectedbackground, Color.BLUE);
            cellbackground = a.getColor(R.styleable.MyCalendarView_cellbackground, Color.WHITE);
            titleTextSize = a.getDimensionPixelSize(R.styleable.MyCalendarView_titleTextSize, 20);
            titleTextColor = a.getColor(R.styleable.MyCalendarView_titleTextColor, Color.BLACK);
            weekTextColor = a.getColor(R.styleable.MyCalendarView_weekTextColor, Color.BLACK);
            leftArrow = a.getDrawable(R.styleable.MyCalendarView_leftArrow);
            rightArrow = a.getDrawable(R.styleable.MyCalendarView_rightArrow);
            dayTextColor = a.getColor(R.styleable.MyCalendarView_dayTextColor, Color.BLACK);
            topTextColor = a.getColor(R.styleable.MyCalendarView_topTextColor, Color.BLACK);
            btomTextColor = a.getColor(R.styleable.MyCalendarView_btomTextColor, Color.BLACK);
            daytextsize = px2dip(a.getDimensionPixelSize(R.styleable.MyCalendarView_dayTextSize, 12));
        }
        selectedDrawable = new GradientDrawable();
        selectedDrawable.setShape(GradientDrawable.OVAL);
        selectedDrawable.setColor(selectedbackground);
        selectedDrawable.setStroke(2, Color.BLUE, 1, 0);
    }


    //初始化标题
    private void initTitel(Context context) {
        LinearLayout titel = getRowView(rowhight, HORIZONTAL);
        titel.setGravity(Gravity.CENTER);
        ImageView imgview = getImgview(leftArrow);
        imgview.setId(LEFT_ARROW);
        imgview.setOnClickListener(listener);
        titel.addView(imgview);
        timeTitle = new TextView(context);
        timeTitle.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        timeTitle.setLayoutParams(layoutParams);
        timeTitle.setBackgroundColor(colorTrans);
        timeTitle.setTextSize(titleTextSize);
        timeTitle.setTextColor(titleTextColor);
        timeTitle.setText(format.format(calendar.getTime()));
        titel.addView(timeTitle);
        ImageView imgview1 = getImgview(rightArrow);
        imgview1.setId(ID.RIGHT_ARROW);
        imgview1.setOnClickListener(listener);
        titel.addView(imgview1);
        this.addView(titel);
        LinearLayout week = getRowView(rowhight, HORIZONTAL);
        measureView(week);
        String[] weeks = showEn ? WeekTitelsEN : WeekTitelsSC;
        for (String s : weeks) {
            week.addView(getTextCell(s, 12, weekTextColor));
        }
        this.addView(week);
        monthView = getRowView(0, VERTICAL);
        this.addView(monthView);

    }

    private LinearLayout getRowView(int hight, int Orientation) {
        LinearLayout titel = new LinearLayout(context);
        titel.setOrientation(Orientation);
        if (Orientation == HORIZONTAL) {
            titel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, rowhight));
            titel.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            titel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            titel.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        }
        return titel;
    }

    //测量视图
    private void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
        v.measure(w, h);
    }

    private TextView getTextCell(String s, int textSize, int textcolor) {
        if (null == context) {
            return null;
        }
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(colorTrans);
        textView.setTextSize(textSize);
        textView.setTextColor(textcolor);
        if (!TextUtils.isEmpty(s))
            textView.setText(s);
        measureView(textView);
        return textView;
    }

    private ImageView getImgview(Drawable drawable) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams((rowhight), (rowhight)));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageDrawable(drawable);
        int px5 = dip2px(15);
        imageView.setPadding(px5, px5, px5, px5);
        return imageView;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mWidth = r - l;
        mHight = b - t;
        rowhight = mWidth / 7;
    }


    private void InitView() {
        this.removeAllViews();
        initTitel(context);
        initMonthView(calendar);
    }

    View.OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case ID.LEFT_ARROW:
                    int i = calendar.get(Calendar.MONTH);
                    Log.e(TAG, "RIGHT_ARROW = " + i);
                    calendar.set(Calendar.MONTH, i - 1);
                    timeTitle.setText(format.format(calendar.getTime()));
                    UpdataView(calendar);
                    Log.e(TAG, "LEFT_ARROW = " + format.format(calendar.getTime()));
                    break;
                case ID.RIGHT_ARROW:
                    int i1 = calendar.get(Calendar.MONTH);
                    Log.e(TAG, "RIGHT_ARROW = " + i1);
                    calendar.set(Calendar.MONTH, i1 + 1);
                    timeTitle.setText(format.format(calendar.getTime()));
                    UpdataView(calendar);
                    Log.e(TAG, "RIGHT_ARROW = " + format.format(calendar.getTime()));
                    break;
                default:
                    Log.e(TAG, "Day = " + v.getId());
                    selectItem((v.getId()) - 1);
                    break;
            }
        }
    };


    private void UpdataView(Calendar calendar) {
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String strDay;
        switch (firstDayOfWeek) {
            case 1:
                strDay = "星期天";
                monthStart = 0;
                break;
            case 2:
                strDay = "星期一";
                monthStart = 1;
                break;
            case 3:
                strDay = "星期二";
                monthStart = 2;
                break;
            case 4:
                strDay = "星期三";
                monthStart = 3;
                break;
            case 5:
                strDay = "星期四";
                monthStart = 4;
                break;
            case 6:
                strDay = "星期五";
                monthStart = 5;
                break;
            default:
                monthStart = 6;
                strDay = "星期六";
        }
        Log.e(TAG, "dddddddas  " + strDay);
        initMonthView(calendar);
    }

    GestureDetector detector = new GestureDetector(new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.e(TAG, "onShowPress");

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e(TAG, "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e(TAG, "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "onFling");
            return false;
        }
    });

    private void selectItem(int pos) {
        if (pos >= 42 || pos < 0) {
            return;
        }
        if (!multipleSelect) {
            for (CellView cellView : cellViews) {
                cellView.setSelected(false);
            }
        }
        cellViews.get(pos).setSelected(!cellViews.get(pos).isSelected());
        if (canChouse && dataSelectListener != null) {
            if (cellViews.get(pos).isSelected()) {
                dataSelectListener.OnDateSelect(cellViews.get(pos).getYear(), cellViews.get(pos).getMonth(), cellViews.get(pos).getDay(), cellViews.get(pos).getWeek());
            } else {
                dataSelectListener.OnDataUnSelect(cellViews.get(pos).getYear(), cellViews.get(pos).getMonth(), cellViews.get(pos).getDay(), cellViews.get(pos).getWeek());
            }
        }

    }

    //初始化月视图
    private void initMonthView(Calendar calendar) {
        cellViews.clear();
        if (null == monthView) {
            monthView = getRowView(0, VERTICAL);
            this.addView(monthView);
        }
        monthView.removeAllViews();
        LinearLayout rowView = getRowView(rowhight, HORIZONTAL);
        int index = 1;
        Calendar clone = (Calendar) calendar.clone();
        clone.add(Calendar.MONTH, -1);
        int maximum = clone.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.e(TAG, "DAY_OF_MONTH d    ----------- " + format.format(clone.getTime()) + "  --" + maximum + "  -  " + clone.get(Calendar.MONTH));
        int star = (maximum - monthStart);
        for (int i = star; i <= maximum; i++) {
            index++;
            int i1 = clone.get(Calendar.DAY_OF_WEEK);
            if (i1 - 1 == 0) {
                i1 = 7;
            } else {
                i1 -= 1;
            }
            CellView cell = getCell(i + "", clone.get(Calendar.YEAR), clone.get(Calendar.MONTH), i, i1);
            cell.setCanSelect(false);
            cell.cen.setTextColor(Color.GRAY);
            rowView.addView(cell);
        }
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            index++;
            CellView cell;
            int s = i;
            if (i > monthMaxday) {
                s = i - monthMaxday;
                int abs = Math.abs(index - 2);
                cell = getCell(s + "", year, month, s, abs == 0 ? 7 : abs);
                cell.cen.setTextColor(Color.GRAY);
                cell.setCanSelect(false);
            } else {
                int abs = Math.abs(index - 2);
                cell = getCell(s + "", year, month, s, abs == 0 ? 7 : abs);
            }
            cell.setCanSelect(canChouse);
            cell.setId(i);
            cell.setOnClickListener(listener);
            cellViews.add(cell);
            if (index == 8) {
                index = 1;
                if (decorator != null) {
                    rowView.addView(decorator.doDecorator(cell));
                } else {
                    rowView.addView(cell);
                }
                monthView.addView(rowView);
                rowView = getRowView(rowhight, HORIZONTAL);
            } else {
                if (decorator != null) {
                    rowView.addView(decorator.doDecorator(cell));
                } else {
                    rowView.addView(cell);
                }
            }
        }
        for (int j = 0; j <= 7 - index; j++) {
            CellView cell = getCell("" + (j + 1), 2018, 01, 10, 3);
            cell.setCanSelect(false);
            cell.cen.setTextColor(Color.GRAY);
            rowView.addView(cell);
        }
        monthView.addView(rowView);


    }


    public int dip2px(float dpValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density);
    }

    public int px2dip(float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);

    }

    private CellView getCell(String text, int year, int month, int day, int week) {
        CellView cellView = new CellView(context, text, daytextsize, dayTextColor,
                "", daytextsize - 5, topTextColor, "",
                daytextsize - 5, btomTextColor,
                year, month, day, week);
        return cellView;
    }


    class CellView extends LinearLayout {
        public TextView top, btom;
        TextView cen;
        Object mTag;
        private int year, month, day, week;
        boolean selected = false;

        public boolean isCanSelect() {
            return canSelect;
        }

        public void setCanSelect(boolean canSelect) {
            this.canSelect = canSelect;
        }

        boolean canSelect = true;

        public int getWeek() {
            return week;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            if (!canSelect) {
                return;
            }
            this.selected = selected;
            if (selected) {
                this.setBackgroundDrawable(selectedDrawable);
            } else {
                this.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public Object getmTag() {
            return mTag;
        }

        public void setmTag(Object mTag) {
            this.mTag = mTag;
        }

        public CellView(Context context, String text, int textsize, int textColor,
                        String toptext, int topsize, int topColor,
                        String btomtext, int btomsize, int btomColor,
                        int year, int month, int day, int week) {
            super(context);
            this.setOrientation(VERTICAL);
            if (selected) {
                this.setBackgroundDrawable(selectedDrawable);
            } else {
                this.setBackgroundColor(Color.TRANSPARENT);
            }
            this.year = year;
            this.month = month;
            this.day = day;
            this.week = week;
            LayoutParams thispar = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            this.setLayoutParams(thispar);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 3);
            top = new TextView(context);
            top.setLayoutParams(layoutParams);
            top.setSingleLine(true);
            top.setText(toptext);
            top.setTextColor(topColor);
            top.setTextSize(topsize);
            top.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            top.setBackgroundColor(Color.TRANSPARENT);

            btom = new TextView(context);
            btom.setLayoutParams(layoutParams);
            btom.setSingleLine(true);
            btom.setBackgroundColor(Color.TRANSPARENT);
            btom.setText(btomtext);
            btom.setTextColor(btomColor);
            btom.setTextSize(btomsize);
            btom.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

            LayoutParams cL = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 4);
            cen = new TextView(context);
            cen.setLayoutParams(cL);
            cen.setSingleLine(true);
            cen.setText(text);
            cen.setTextColor(textColor);
            cen.setTextSize(textsize);
            cen.setBackgroundColor(Color.TRANSPARENT);
            cen.setGravity(Gravity.CENTER);
            if (boldface) {
                cen.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            measureView(top);
            measureView(cen);
            measureView(btom);
            this.addView(top);
            this.addView(cen);
            this.addView(btom);
        }


        public CharSequence getToptext() {
            return top.getText();
        }

        public void setToptext(CharSequence top) {
            this.top.setText(top);
        }

        public CharSequence getBtom() {
            return btom.getText();
        }

        public void setBtom(CharSequence btom) {
            this.btom.setText(btom);
        }

        public CharSequence getCen() {
            return cen.getText();
        }

        public void setCen(CharSequence cen) {
            this.cen.setText(cen);
        }
    }

    public interface Decorator {
        CellView doDecorator(CellView cellView);
    }

    public static class ID {
        public static final int LEFT_ARROW = 110;
        public static final int RIGHT_ARROW = 112;
        public static final int DAY_1 = 1;
        public static final int DAY_2 = 2;
        public static final int DAY_3 = 3;
        public static final int DAY_4 = 4;
        public static final int DAY_5 = 5;
        public static final int DAY_6 = 6;
        public static final int DAY_7 = 7;
        public static final int DAY_8 = 8;
        public static final int DAY_9 = 9;
        public static final int DAY_10 = 10;
        public static final int DAY_11 = 11;
        public static final int DAY_12 = 12;
        public static final int DAY_13 = 13;
        public static final int DAY_14 = 14;
        public static final int DAY_15 = 15;
        public static final int DAY_16 = 16;
        public static final int DAY_17 = 17;
        public static final int DAY_18 = 18;
        public static final int DAY_19 = 19;
        public static final int DAY_20 = 20;
        public static final int DAY_21 = 21;
        public static final int DAY_22 = 22;
        public static final int DAY_23 = 23;
        public static final int DAY_24 = 24;
        public static final int DAY_25 = 25;
        public static final int DAY_26 = 26;
        public static final int DAY_27 = 27;
        public static final int DAY_28 = 28;
        public static final int DAY_29 = 29;
        public static final int DAY_30 = 30;
        public static final int DAY_31 = 31;
    }


    public interface DataSelectListener {
        void OnDateSelect(int year, int month, int dayOfmonth, int week);

        void OnDataUnSelect(int year, int month, int dayOfmonth, int week);
    }
}
