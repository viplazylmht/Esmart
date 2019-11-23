package com.viplazy.ez.esmart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

   String email;

    PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        //   this.data = new DatabaseRawData();
        email = "";
    }
    PagerAdapter(FragmentManager fragmentManager, String email) {
        super(fragmentManager);
        this.email = email;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new FragmentAchievement(email);
                break;
            case 1:
                frag = new FragmentAnalyst(email);
                break;
        }
        return frag;
    }

    PagerAdapter(FragmentManager fragmentManager, DatabaseRawData data) {
        super(fragmentManager);

       // this.data = data;
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Achievement";
                break;
            case 1:
                title = "Analyst";
                break;
        }
        return title;
    }
}