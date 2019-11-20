package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.ArcProgress;

public class FragmentAnalyst extends Fragment {


    private ArcProgress arcProgressDay, arcProgressWeek, arcProgressMonth;

    private View layoutView;

    public FragmentAnalyst() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutView = inflater.inflate(R.layout.fragment_analyst, container, false);


        arcProgressDay = layoutView.findViewById(R.id.arc_progress);
        arcProgressWeek = layoutView.findViewById(R.id.arc_progress1);
        arcProgressMonth = layoutView.findViewById(R.id.arc_progress2);

        arcProgressDay.setProgress(50);
        arcProgressMonth.setBottomText("Good");
        arcProgressWeek.setArcAngle(360);

        arcProgressDay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int curProgress = arcProgressDay.getProgress();
                if (curProgress >= 100) arcProgressDay.setProgress(0);
                else arcProgressDay.setProgress(curProgress+1);
                return false;
            }
        });
        return layoutView;
    }
}
