package com.spok.sensor.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spok.sensor.R;

import java.text.DecimalFormat;


public class FragmentSensors extends Fragment implements SensorEventListener {

    private TextView text_gravity, text_distance, text_temperature,
            text_light, text_acceleration, text_humidity,
            text_pressure, text_proximity;
    private SeekBar sb_temperature, sb_gravity, sb_linearAcceleration;
    private ImageView image_lamp, image_smartphone,
            image_stickHumidity, image_stickPressure,
            image_compass, image_proximityGrad;
    private SensorManager sensorManager;

    private float vectorLength(float X, float Y, float Z) // Vector length
    {
        return (float) Math.sqrt(X * X + Y * Y + Z * Z);
    }

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
        image_stickHumidity = v.findViewById(R.id.imageView_stickHumidity);
        image_stickPressure = v.findViewById(R.id.imageView_stickPressure);
        image_proximityGrad = v.findViewById(R.id.imageView_proximityGrad);

        sb_temperature = v.findViewById(R.id.seekbar_temperature);
        sb_gravity = v.findViewById(R.id.seekbar_gravity);
        sb_linearAcceleration = v.findViewById(R.id.seekbar_linearAcceleration);
        sb_linearAcceleration.setEnabled(false);
        sb_gravity.setEnabled(false);
        sb_temperature.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb_linearAcceleration.setSplitTrack(false);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private final float[] accelerometer = new float[3],
            geomagnetic = new float[3];
    private float newAzimuth = 0f;
    private int maxProximity = 1;
    private boolean isExecutingLAcceleration = false;
    private float oldValueLight = 0f;

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(final SensorEvent event) {
        synchronized (this) // Async execution
        {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            float smooth = 0.95f;
            switch (event.sensor.getType()) {
                case Sensor.TYPE_STEP_COUNTER: // Steps counter
                    text_distance.setText((int) event.values[0] + " m");
                    break;
                case Sensor.TYPE_GRAVITY: // Gravity
                    float value = vectorLength(event.values[0], event.values[1], event.values[2]);
                    text_gravity.setText(decimalFormat.format(value) + " m/s2");
                    sb_gravity.setProgress((short) value);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE: // Temperature
                    text_temperature.setText(event.values[0] + "°C");
                    sb_temperature.setProgress((int) (sb_temperature.getMax() / 2 + event.values[0]));
                    break;
                case Sensor.TYPE_LIGHT: // Light
                    if ((int) oldValueLight != (int) event.values[0]) {
                        float startAlpha = oldValueLight / 100;
                        if (startAlpha > 1.0f) startAlpha = 1.0f;
                        float endAlpha = event.values[0] / 100;
                        Animation alpha = new AlphaAnimation(startAlpha, endAlpha);
                        alpha.setDuration(650);
                        alpha.setFillAfter(true);
                        image_lamp.startAnimation(alpha);
                        oldValueLight = event.values[0];
                    }
                    text_light.setText((int) event.values[0] + " lx");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION: // Linear Acceleration
                    float acceleration = vectorLength(event.values[0], event.values[1], event.values[2]) * 10f;
                    text_acceleration.setText(decimalFormat.format(acceleration / 10f) + " m/s2");
                    if (!isExecutingLAcceleration) new SeekBarAnimation(sb_linearAcceleration)
                            .execute((int) acceleration);
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY: // Relative Humidity
                    text_humidity.setText(event.values[0] + "%");
                    image_stickHumidity.setRotation(164 * event.values[0] / 100 + 140);
                    break;
                case Sensor.TYPE_PRESSURE: // Pressure
                    text_pressure.setText(event.values[0] + " hPa");
                    image_stickPressure.setRotation(event.values[0] * 180 / 1100 + 90);
                    break;
                case Sensor.TYPE_PROXIMITY: // Proximity
                    text_proximity.setText(event.values[0] + " cm");
                    if (event.values[0] > maxProximity) maxProximity = (int) event.values[0];
                    image_proximityGrad.setAlpha(event.values[0] / 10);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD: // Magnetic field
                    geomagnetic[0] = smooth * (geomagnetic[0] + event.values[0]); // X axis
                    geomagnetic[1] = smooth * (geomagnetic[1] + event.values[1]); // Y axis
                    geomagnetic[2] = smooth * (geomagnetic[2] + event.values[2]); // Z axis
                    break;
                case Sensor.TYPE_GYROSCOPE: // Gyroscope
                    image_smartphone.setRotationY((float) (event.values[1] * 180 / Math.PI)); // Set Rotation on Y axis
                    break;
                case Sensor.TYPE_ACCELEROMETER: // Accelerometer
                    image_smartphone.setRotation(event.values[0] * -9); // Set Rotation on Z axis
                    image_smartphone.setRotationX(event.values[2] * 9); // Set Rotation on X axis
                    accelerometer[0] = smooth * (accelerometer[0] + event.values[0]); // X axis
                    accelerometer[1] = smooth * (accelerometer[1] + event.values[1]); // Y axis
                    accelerometer[2] = smooth * (accelerometer[2] + event.values[2]); // Z axis
                    break;
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                float[] R = new float[9];
                if (SensorManager.getRotationMatrix(R, null, accelerometer, geomagnetic)) {
                    // If rotation matrix possibly
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation); // Convert values to orientation array.
                    float azimuth = ((float) Math.toDegrees(orientation[0]) + 360) % 360; // Set azimuth from X axis orientation
                    RotateAnimation animation = new RotateAnimation(newAzimuth, azimuth,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f); // Create animation with center pivot to self
                    newAzimuth = azimuth;
                    animation.setDuration(700);
                    animation.setFillAfter(true); // Save position after finishing animation
                    image_compass.startAnimation(animation);
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @SuppressLint("StaticFieldLeak")
    public class SeekBarAnimation extends AsyncTask<Integer, Integer, Void> {

        private final SeekBar seekBar;

        public SeekBarAnimation(SeekBar seekBar) {
            this.seekBar = seekBar;
        }

        private void changeProgress(int newValue) {
            int i = seekBar.getProgress();
            while (i != newValue) {
                publishProgress(i);
                if (newValue > i) i++;
                else i--;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            isExecutingLAcceleration = true;
            changeProgress(integers[0]);
            isExecutingLAcceleration = false;

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            seekBar.setProgress(values[0]);
        }
    }

}
