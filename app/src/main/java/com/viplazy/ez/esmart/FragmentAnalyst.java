package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

public class FragmentAnalyst extends Fragment {

    static public int DAY = 1;
    static public int WEEK = 2;
    static public int MONTH = 3;

    //private ArcProgress arcProgressDay, arcProgressWeek, arcProgressMonth;
    private BarChart  barChart;
    private View layoutView;

    public int targetOfBar;

    private List<BarEntry> barEntriesDay;
    private BarData barDataDay ;
    private BarDataSet barDataSetDay;

    private List<BarEntry> barEntriesWeek;
    private BarData barDataWeek ;
    private BarDataSet barDataSetWeek;

    private List<BarEntry> barEntriesMonth;
    private BarData barDataMonth ;
    private BarDataSet barDataSetMonth;

    //private HorizontalScrollView scrollView;



    public FragmentAnalyst() {
        targetOfBar = DAY;
        barEntriesDay = new ArrayList<>();
        barEntriesWeek = new ArrayList<>();
        barEntriesMonth = new ArrayList<>();
    }

    public void AddDataDay(float th, float value){
        barEntriesDay.add(new BarEntry(th,value));
    }

    public void AddDataWeek(float th, float value){
        barEntriesWeek.add(new BarEntry(th,value));
    }

    public void AddDataMonth(float th, float value){
        barEntriesMonth.add(new BarEntry(th,value));
    }

    public void UpdateChart(){
        // Settings Bar Chart
        barChart.setVisibility(View.VISIBLE);
        barChart.animateY(4000);
        barChart.setFitBars(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        //Update
        barChart.invalidate();
    }


    public void GetDayChartData(){
        AddDataDay(1f,1f);
        AddDataDay(2f,2f);
        AddDataDay(4f,6f);
        AddDataDay(7f,7f);
    }

    public void GetWeekChartData(){
        AddDataWeek(3,3);
        AddDataWeek(7,8);
        AddDataWeek(2,3);
        AddDataWeek(1,9);
    }

    public void GetMonthChartData(){
        AddDataMonth(1f,1f);
        AddDataMonth(2f,2f);
        AddDataMonth(4f,6f);
        AddDataMonth(7f,7f);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutView = inflater.inflate(R.layout.fragment_analyst, container, false);
        Description description = new Description();
        description.setText("");


        barChart = layoutView.findViewById(R.id.barChartDays);
        GetDayChartData();

        // Set Data
        barDataSetDay = new BarDataSet(barEntriesDay, "Correct");
        barDataDay = new BarData(barDataSetDay);
        barDataDay.setBarWidth(0.9f);
        barChart.setData(barDataDay);
        barChart.setDescription(description);
        UpdateChart();


        barChart = layoutView.findViewById(R.id.barChartWeeks);
        GetWeekChartData();

        // Set Data
        barDataSetWeek = new BarDataSet(barEntriesWeek, "Correct");
        barDataWeek = new BarData(barDataSetWeek);
        barDataWeek.setBarWidth(0.9f);
        barChart.setData(barDataWeek);
        barChart.setDescription(description);
        UpdateChart();



        barChart = layoutView.findViewById(R.id.barChartMonths);
        GetMonthChartData();


        // Set Data
        barDataSetMonth = new BarDataSet(barEntriesMonth, "Correct");
        barDataMonth = new BarData(barDataSetMonth);
        barDataMonth.setBarWidth(0.9f);
        barChart.setData(barDataMonth);
        barChart.setDescription(description);
        UpdateChart();

        return layoutView;
    }
}
