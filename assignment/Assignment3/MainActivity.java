package com.example.yukesh_raghavan_assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView tv = findViewById(R.id.output);
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        int orientation = getResources().getConfiguration().orientation;
        double magnitude = Math.sqrt(x * x + y * y + z * z);

        if (magnitude > 3 && magnitude <= 10 && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tv.setText("Watching videos/movies on the phone while walking");
        } else if (magnitude > 5 && magnitude <= 10 && orientation == Configuration.ORIENTATION_PORTRAIT) {
            tv.setText("Walking");
        } else if (magnitude > 10 && orientation == Configuration.ORIENTATION_PORTRAIT) {
            tv.setText("Running");
        } else if (magnitude > 0 && magnitude <= 5 && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tv.setText("Watching videos while sitting/standing");
        } else if (magnitude > 0 && magnitude <= 5 && orientation == Configuration.ORIENTATION_PORTRAIT) {
            tv.setText("Standing/Sitting");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
