package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentAchievement extends Fragment {

    private ImageView rankImage;
    private View layoutView;

    private String email;
    private DatabaseReference dataRef;
    public FragmentAchievement(String email) {
        this.email = email;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        layoutView = inflater.inflate(R.layout.fragment_achievement, container, false);



        email = email.replace('.',',');
        dataRef = FirebaseDatabase.getInstance().getReference("User");
        dataRef.child(email).child("Month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Getting Data", "Running2");
                ArrayList<String> idPassed;
                float value = 0;
                if (!dataSnapshot.hasChildren())
                    Log.d("size", "nochild");
                else Log.d("size", "haschild");
                for (DataSnapshot dts : dataSnapshot.getChildren()) {
                    Log.d("dts", dts.getValue(User.class).toString());
                    value = dts.getValue(User.class).getPercent();
                    value = value*100f;
                    Log.d("Getting Data", "Running in");
                }


                rankImage = layoutView.findViewById(R.id.rank_image);

                if (value >= 0f && value <= 6.6f)  rankImage.setImageResource(R.drawable.ic_rank15_bronze3);
                if (value >= 6.6f && value <= 13.3f)  rankImage.setImageResource(R.drawable.ic_rank14_bronze2);
                if (value >= 13.3f && value <= 20f)  rankImage.setImageResource(R.drawable.ic_rank13_bronze1);
                if (value >= 20f && value <= 26.6)  rankImage.setImageResource(R.drawable.ic_rank12_silver3);
                if (value >= 26.6f && value <= 33.3f)  rankImage.setImageResource(R.drawable.ic_rank11_silver2);
                if (value >= 33.3f && value <= 40f)  rankImage.setImageResource(R.drawable.ic_rank10_silver1);
                if (value >= 40f && value <= 46.6f)  rankImage.setImageResource(R.drawable.ic_rank09_gold3);
                if (value >= 46.6f && value <= 53.3f)  rankImage.setImageResource(R.drawable.ic_rank08_gold2);
                if (value >= 53.3f && value <= 60f)  rankImage.setImageResource(R.drawable.ic_rank07_gold1);
                if (value >= 60f && value <= 66.6f)  rankImage.setImageResource(R.drawable.ic_rank06_diamond3);
                if (value >= 66.6f && value <= 73.3f)  rankImage.setImageResource(R.drawable.ic_rank05_diamond2);
                if (value >= 73.3f && value <= 80f)  rankImage.setImageResource(R.drawable.ic_rank04_diamond1);
                if (value >= 80f && value <= 86.6f)  rankImage.setImageResource(R.drawable.ic_rank03_master3);
                if (value >= 86.6f && value <= 93.3f)  rankImage.setImageResource(R.drawable.ic_rank02_master2);
                if (value >= 93.3f && value <= 100f)  rankImage.setImageResource(R.drawable.ic_rank01_master1);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //rankImage.setImageResource(R.drawable.notification_icon);
        return layoutView;
    }
}
