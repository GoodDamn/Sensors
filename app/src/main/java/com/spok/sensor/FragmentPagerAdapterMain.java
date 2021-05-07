package com.spok.sensor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.spok.sensor.fragments.FragmentListSensors;
import com.spok.sensor.fragments.FragmentSensors;

public class FragmentPagerAdapterMain extends FragmentPagerAdapter {

    private Fragment[] fragments = {
            new FragmentSensors(),
            new FragmentListSensors()
    };

    public FragmentPagerAdapterMain(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
