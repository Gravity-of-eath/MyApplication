package com.hmj.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

    Calendar today = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.e("doDecorator", "" + new SimpleDateFormat("yyyy-MM-dd  HH").format(today.getTime()));
        MyCalendarView cardd = (MyCalendarView) findViewById(R.id.cardd);
        cardd.setDecorator(new MyCalendarView.Decorator() {
            @Override
            public MyCalendarView.CellView doDecorator(MyCalendarView.CellView cellView) {
                Log.e("doDecoratordddddd", today.get(Calendar.DAY_OF_MONTH) + "  ==  " + cellView.getDay());
                if (cellView.getDay() == today.get(Calendar.DAY_OF_MONTH) && cellView.getMonth() == today.get(Calendar.MONTH)) {
                    cellView.setCen("Today");
                    cellView.setToptext("结算");
                    cellView.setBtom("+200.00");
                    cellView.setSelected(true);
                }
                if (cellView.getWeek() == 7 || cellView.getWeek() == 6) {
                    cellView.cen.setTextColor(Color.RED);
                }
                return cellView;
            }
        });
        cardd.setDataSelectListener(new MyCalendarView.DataSelectListener() {
            @Override
            public void OnDateSelect(int year, int month, int dayOfmonth, int week) {
                Log.e("doDecorator", "OnDateSelect" + year + month + dayOfmonth + week);
            }

            @Override
            public void OnDataUnSelect(int year, int month, int dayOfmonth, int week) {
                Log.e("doDecorator", "OnDataUnSelect" + year + month + dayOfmonth + week);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
