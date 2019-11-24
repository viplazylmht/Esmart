package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    private DatabaseReference dataRef;

    //private HorizontalScrollView scrollView;

    private String email;

    public FragmentAnalyst(String email) {

        this.email = email;
        targetOfBar = DAY;
        barEntriesDay = new ArrayList<>();
        barEntriesWeek = new ArrayList<>();
        barEntriesMonth = new ArrayList<>();


    }

    public void GetChartData(){
        Log.d("Getting Data", "Running");
        email = email.replace('.',',');
        dataRef.child(email).child("Day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barEntriesDay.clear();
                Log.d("Getting Data", "Running2");
                ArrayList<String> idPassed;
                float value = 0;
                float count = 0;
                if (!dataSnapshot.hasChildren())
                    Log.d("size", "nochild");
                else Log.d("size", "haschild");
                for (DataSnapshot dts : dataSnapshot.getChildren()) {
                    Log.d("dts", dts.getValue(User.class).toString());
                    count = count + 1;
                    value = dts.getValue(User.class).getPercent();
                    Log.d("Getting Data", "Running in");
                    AddDataDay(1f*count, value*100f);
                }

                Log.d("day chart", "Read");
                // Set Data
                if(barEntriesDay.size() >= 1){
                    barDataSetDay = new BarDataSet(barEntriesDay, "Correct %");
                    barDataDay = new BarData(barDataSetDay);
                    barDataDay.setBarWidth(0.9f);
                    barChart = layoutView.findViewById(R.id.barChartDays);
                    barChart.setData(barDataDay);
                    barChart.setFitBars(true);
                    barChart.getAxisLeft().setDrawLabels(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getXAxis().setDrawLabels(false);
                    Description description = new Description();
                    description.setText("");
                    barChart.setDescription(description);
                    UpdateChart();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child(email).child("Week").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Getting Data", "Running2");
                ArrayList<String> idPassed;
                float value = 0;
                float count = 0;
                if (!dataSnapshot.hasChildren())
                    Log.d("size", "nochild");
                else Log.d("size", "haschild");
                for (DataSnapshot dts : dataSnapshot.getChildren()) {
                    Log.d("dts", dts.getValue(User.class).toString());
                    count = count + 1;
                    value = dts.getValue(User.class).getPercent();
                    Log.d("Getting Data", "Running in");
                    AddDataWeek(1f*count, value*100f);
                }

                Log.d("week chart", "Read");
                // Set Data
                if(barEntriesWeek.size() >= 1){

                    barDataSetWeek = new BarDataSet(barEntriesWeek, "Correct %");
                    barDataWeek = new BarData(barDataSetWeek);
                    barDataWeek.setBarWidth(0.9f);
                    barChart = layoutView.findViewById(R.id.barChartWeeks);
                    barChart.setData(barDataWeek);
                    barChart.setFitBars(true);
                    barChart.getAxisLeft().setDrawLabels(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getXAxis().setDrawLabels(false);
                    Description description = new Description();
                    description.setText("");
                    barChart.setDescription(description);
                    UpdateChart();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        dataRef.child(email).child("Month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Getting Data", "Running2");
                ArrayList<String> idPassed;
                float value = 0;
                float count = 0;
                if (!dataSnapshot.hasChildren())
                    Log.d("size", "nochild");
                else Log.d("size", "haschild");
                for (DataSnapshot dts : dataSnapshot.getChildren()) {
                    Log.d("dts", dts.getValue(User.class).toString());
                    count = count + 1;
                    value = dts.getValue(User.class).getPercent();
                    Log.d("Getting Data", "Running in");
                    AddDataMonth(1f*count, value*100f);
                }

                Log.d("month chart", "Read");
                // Set Data
                if(barEntriesMonth.size() >= 1){
                    barDataSetMonth = new BarDataSet(barEntriesMonth, "Correct %");
                    barDataMonth= new BarData(barDataSetMonth);
                    barDataMonth.setBarWidth(0.9f);
                    barChart = layoutView.findViewById(R.id.barChartMonths);
                    barChart.setData(barDataMonth);
                    barChart.setFitBars(true);
                    barChart.getAxisLeft().setDrawLabels(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getXAxis().setDrawLabels(false);
                    Description description = new Description();
                    description.setText("");
                    barChart.setDescription(description);
                    UpdateChart();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutView = inflater.inflate(R.layout.fragment_analyst, container, false);

        dataRef = FirebaseDatabase.getInstance().getReference("User");
        GetChartData();

        return layoutView;
    }
}
