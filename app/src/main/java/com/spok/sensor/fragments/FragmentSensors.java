package com.spok.sensor.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spok.sensor.R;

import co.gofynd.gravityview.GravityView;

public class FragmentSensors extends Fragment implements SensorEventListener {

    private TextView text_gravity, text_distance, text_temperature,
            text_light, text_acceleration, text_humidity,
            text_pressure, text_proximity;
    private SeekBar sb_temperature, sb_gravity, sb_proximity;
    private ImageView image_lamp, image_smartphone,
            image_humidity, image_stickPressure,
            image_compass;
    private SensorManager sensorManager;

    private float vectorLength(float X, float Y, float Z) // Vector length
    {
        return (float) Math.sqrt(X * X + Y * Y + Z * Z);
    }

    private GravityView gravityView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensors, container, false);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        text_gravity = v.findViewById(R.id.textView_gravity);
        text_distance = v.findViewById(R.id.textView_steps_counter);
        text_temperature = v.findViewById(R.id.textView_temperature);
        text_light = v.findViewById(R.id.textView_light);
        text_acceleration = v.findViewById(R.id.textView_Acceleration);
        text_humidity = v.findViewById(R.id.textView_humidity);
        text_pressure = v.findViewById(R.id.textView_pressure);
        text_proximity = v.findViewById(R.id.textView_proximity);
        image_lamp = v.findViewById(R.id.imageView_lamp);
        image_smartphone = v.findViewById(R.id.imageView_smartphone);
        image_compass = v.findViewById(R.id.imageView_compass);
        image_humidity = v.findViewById(R.id.imageView_humidity);
        image_stickPressure = v.findViewById(R.id.imageView_stickPressure);
        sb_temperature = v.findViewById(R.id.seekbar_temperature);
        sb_proximity = v.findViewById(R.id.seekbar_proximity);
        sb_gravity = v.findViewById(R.id.seekbar_gravity);
        sb_gravity.setEnabled(false);
        sb_proximity.setEnabled(false);
        sb_temperature.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb_proximity.setSplitTrack(false);
            sb_gravity.setSplitTrack(false);
            sb_gravity.setSplitTrack(false);
        }

        final ImageView gravityWoodenBox = v.findViewById(R.id.imageView_flyingBox);
        final Animation gravity_up = AnimationUtils.loadAnimation(getContext(), R.anim.gravity_up),
                gravity_down = AnimationUtils.loadAnimation(getContext(), R.anim.gravity_down);

        gravity_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gravityWoodenBox.startAnimation(gravity_down);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        gravity_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gravityWoodenBox.startAnimation(gravity_up);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        gravityWoodenBox.startAnimation(gravity_up);

        gravityView = GravityView.getInstance(getContext());
        gravityView.setImage((ImageView) v.findViewById(R.id.imageViewBackground), R.drawable.background)
                .center();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        int delay = SensorManager.SENSOR_DELAY_NORMAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), delay);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), delay);
        gravityView.registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private float[] aAccelerometer = new float[3],
            aGeomagnetic = new float[3];
    private float azimuth = 0f,
            currentAzimuth = 0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float smooth = 0.97f;
        synchronized (this) // Async execution
        {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    image_smartphone.setRotation(event.values[0] * -9); // Set Rotation on Z axis
                    image_smartphone.setRotationX(event.values[2] * 9); // Set Rotation on X axis
                    aAccelerometer[0] = smooth * aAccelerometer[0] + (1 - smooth) * event.values[0];
                    aAccelerometer[1] = smooth * aAccelerometer[1] + (1 - smooth) * event.values[1];
                    aAccelerometer[2] = smooth * aAccelerometer[2] + (1 - smooth) * event.values[2];
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    text_distance.setText(event.values[0] + " m");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    image_smartphone.setRotationY((float) (event.values[1] * 180 / Math.PI)); // Set Rotation on Y axis
                    break;
                case Sensor.TYPE_PROXIMITY:
                    text_proximity.setText(event.values[0] + " cm");
                    if (event.values[0] > sb_proximity.getMax())
                        sb_proximity.setMax((int) event.values[0]);
                    sb_proximity.setProgress((int) event.values[0]);
                    break;
                case Sensor.TYPE_PRESSURE:
                    text_pressure.setText(event.values[0] + " hPa");
                    image_stickPressure.setRotation(event.values[0] * 180 / 1100 + 90);
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    text_humidity.setText(event.values[0] + "%");
                    image_humidity.setAlpha(event.values[0] / 100);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    text_acceleration.setText("Linear Acceleration: " + vectorLength(event.values[0], event.values[1], event.values[2]) + " m/s2");
                    break;
                case Sensor.TYPE_LIGHT:
                    image_lamp.setAlpha(event.values[0] / 100);
                    text_light.setText(event.values[0] + " lx");
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    text_temperature.setText(event.values[0] + "°C");
                    sb_temperature.setProgress((int) (sb_temperature.getMax() / 2 + event.values[0]));
                    break;
                case Sensor.TYPE_GRAVITY:
                    float value = vectorLength(event.values[0], event.values[1], event.values[2]);
                    text_gravity.setText(value + " m/s2");
                    sb_gravity.setProgress((short) value);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    aGeomagnetic[0] = smooth * aGeomagnetic[0] + (1 - smooth) * event.values[0];
                    aGeomagnetic[1] = smooth * aGeomagnetic[1] + (1 - smooth) * event.values[1];
                    aGeomagnetic[2] = smooth * aGeomagnetic[2] + (1 - smooth) * event.values[2];
                    break;
            }

            float[] W = new float[9],
                    E = new float[9];
            boolean isSuccess = SensorManager.getRotationMatrix(W, E, aAccelerometer, aGeomagnetic);
            if (isSuccess) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(W, orientation);
                azimuth = ((float) Math.toDegrees(orientation[0]) + 360) % 360;
                Animation animation = new RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                currentAzimuth = azimuth;
                animation.setDuration(500);
                animation.setFillAfter(true);
                image_compass.startAnimation(animation);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}