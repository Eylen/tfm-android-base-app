package com.eylen.sensormanager.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.eylen.sensormanager.R;


import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * A placeholder fragment containing the accelerometer and gyroscope controls
 */
public class SensorsFragment extends PlaceholderFragment implements SensorEventListener {
    private static final String TAG = "SensorsFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private Sensor mAccelerometer;

    private float[] gravity;
    private float[] linear_acceleration;
    private float[] gyroscope;

    private WeakReference<Context> mContext;

    private TextView mGravityX;
    private TextView mGravityY;
    private TextView mGravityZ;

    private TextView mGyroscopeX;
    private TextView mGyroscopeY;
    private TextView mGyroscopeZ;

    private TextView mLinearAccelerationX;
    private TextView mLinearAccelerationY;
    private TextView mLinearAccelerationZ;

    private DecimalFormat decimalFormat;

    public SensorsFragment(){
        super();
        gravity = new float[3];
        linear_acceleration = new float[3];
        gyroscope = new float[3];
        decimalFormat = new DecimalFormat("0.00");
    }

    public void setContext(Context context){
        mContext = new WeakReference<Context>(context);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SensorsFragment newInstance(int sectionNumber, Context context){
        SensorsFragment fragment = new SensorsFragment();
        fragment.setContext(context);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.d(TAG, "SensorsFragment onCreate");

        if (mContext != null && mContext.get() != null) {
            //Retrieve sensorManager
            mSensorManager = (SensorManager) mContext.get().getSystemService(Context.SENSOR_SERVICE);
            //Retrieve gyroscope and acceleration sensors
            mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "SensorsFragment onCreateView");
        View rootView = inflater.inflate(R.layout.sensors_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "SensorsFragment onActivityCreated");

        mLinearAccelerationX = (TextView) getView().findViewById(R.id.accelerometer_x);
        mLinearAccelerationY = (TextView) getView().findViewById(R.id.accelerometer_y);
        mLinearAccelerationZ = (TextView) getView().findViewById(R.id.accelerometer_z);

        mGyroscopeX = (TextView) getView().findViewById(R.id.gyroscope_x);
        mGyroscopeY = (TextView) getView().findViewById(R.id.gyroscope_y);
        mGyroscopeZ = (TextView) getView().findViewById(R.id.gyroscope_z);

        mGravityX = (TextView) getView().findViewById(R.id.gravity_x);
        mGravityY = (TextView) getView().findViewById(R.id.gravity_y);
        mGravityZ = (TextView) getView().findViewById(R.id.gravity_z);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SensorsFragment onResume");
        if (mGyroscope != null) {
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SensorsFragment onPause");
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                final float alpha = 0.2f;

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                mGravityX.setText(decimalFormat.format(gravity[0]));
                mGravityY.setText(decimalFormat.format(gravity[1]));
                mGravityZ.setText(decimalFormat.format(gravity[2]));

                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                mLinearAccelerationX.setText(decimalFormat.format(linear_acceleration[0]));
                mLinearAccelerationY.setText(decimalFormat.format(linear_acceleration[1]));
                mLinearAccelerationZ.setText(decimalFormat.format(linear_acceleration[2]));

                break;
            case Sensor.TYPE_GYROSCOPE:
                // Axis of the rotation sample, not normalized
                gyroscope[0] = event.values[0];
                gyroscope[1] = event.values[1];
                gyroscope[2] = event.values[2];

                mGyroscopeX.setText(decimalFormat.format(gyroscope[0]));
                mGyroscopeY.setText(decimalFormat.format(gyroscope[1]));
                mGyroscopeZ.setText(decimalFormat.format(gyroscope[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO??
    }
}
