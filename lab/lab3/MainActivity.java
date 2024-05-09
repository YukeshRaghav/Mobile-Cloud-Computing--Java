package com.example.yukeshraghavanlab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public EditText editText;
    private SensorManager sensorManager;
    private Sensor light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
    @Override
    public  final void onSensorChanged(SensorEvent event)
    {
        float illuminance= event.values[0];
        //Display this illuminance on the app
        String data= Float.toString(illuminance);
        editText= findViewById(R.id.edit_text);
        editText.setText(data);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this,light, 5000000);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

}