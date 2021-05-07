package com.spok.sensor.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spok.sensor.R;

import java.util.List;

public class FragmentListSensors extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_sensors, container, false);

        List<Sensor> sensor = ((SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE))
                .getSensorList(Sensor.TYPE_ALL);
        TextView sensors = v.findViewById(R.id.list_of_sensors);
        for (int i = 0; i < sensor.size(); i++)
            sensors.setText(sensors.getText() + sensor.get(i).getName() + "\n");

        return v;
    }
}
