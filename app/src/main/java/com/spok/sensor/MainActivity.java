package com.spok.sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spok.sensor.fragments.FragmentListSensors;
import com.spok.sensor.fragments.FragmentSensors;

import java.util.List;

import co.gofynd.gravityview.GravityView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ViewPager) findViewById(R.id.viewPager_main)).setAdapter(new FragmentPagerAdapterMain(getSupportFragmentManager()));

        GravityView.getInstance(MainActivity.this)
                .setImage((ImageView) findViewById(R.id.imageViewGravity), R.drawable.background)
                .center().registerListener();
    }
}
