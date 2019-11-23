package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class FragmentAchievement extends Fragment {

    private ImageView rankImage;
    private View layoutView;
    public FragmentAchievement() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        layoutView = inflater.inflate(R.layout.fragment_achievement, container, false);

        rankImage = layoutView.findViewById(R.id.rank_image);
        rankImage.setVisibility(View.VISIBLE);
        rankImage.setImageResource(R.drawable.notification_icon);

        //rankImage.setImageResource(R.drawable.notification_icon);
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }
}
