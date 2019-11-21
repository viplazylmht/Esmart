package com.viplazy.ez.esmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentInfo extends Fragment {

    private ImageView imageView;

    private View infoView;

    public FragmentInfo() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        infoView = inflater.inflate(R.layout.fragment_info, container, false);
        return  infoView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageView = infoView.findViewById(R.id.imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
