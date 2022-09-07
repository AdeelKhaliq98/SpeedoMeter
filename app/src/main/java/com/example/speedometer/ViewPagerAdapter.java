package com.example.speedometer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.Fragments.AnalogFragment;
import com.example.Fragments.DigitalFragment;
import com.example.Fragments.MapFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new AnalogFragment();
        }
        else if (position == 1)
        {
            fragment = new DigitalFragment();
        }
        else if (position == 2)
        {
            fragment = new MapFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Analog";
        }
        else if (position == 1)
        {
            title = "Digital";
        }
        else if (position == 2)
        {
            title = "Map";
        }
        return title;
    }
}
