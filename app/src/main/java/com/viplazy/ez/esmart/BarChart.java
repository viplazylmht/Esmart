package com.viplazy.ez.esmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BarChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        FragmentAnalyst charts = new FragmentAnalyst();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, charts).commit();
    }
}
