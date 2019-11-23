package com.viplazy.ez.esmart;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    DatabaseRawData data;

    PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.data = new DatabaseRawData();
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new FragmentAchievement();
                break;
            case 1:
                frag = new FragmentAnalyst(data);
                break;
            case 2:
                frag = new FragmentInfo();
                break;
        }
        return frag;
    }

    PagerAdapter(FragmentManager fragmentManager, DatabaseRawData data) {
        super(fragmentManager);

        this.data = data;
    }

    public FragmentInfo getFragmentInfo() {
        return new FragmentInfo();
    }

    @Override
    public int getCount() {
        return 3;
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
            case 2:
                title = "Info";
                break;
        }
        return title;
    }
}