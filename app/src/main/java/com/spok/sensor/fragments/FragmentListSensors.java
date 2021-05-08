package com.spok.sensor.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spok.sensor.R;

import java.util.List;

import co.gofynd.gravityview.GravityView;

public class FragmentListSensors extends Fragment {


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_sensors, container, false);

        List<Sensor> sensor = ((SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE))
                .getSensorList(Sensor.TYPE_ALL);
        TextView sensors = v.findViewById(R.id.list_of_sensors);
        for (int i = 0; i < sensor.size(); i++)
            sensors.setText(sensors.getText() + String.valueOf(i + 1) + ") " + sensor.get(i).getName() + "\n");

        return v;
    }

}
