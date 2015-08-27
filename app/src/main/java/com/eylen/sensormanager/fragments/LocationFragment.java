package com.eylen.sensormanager.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eylen.sensormanager.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Saioa on 12/08/2015.
 */
public class LocationFragment extends PlaceholderFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "LocationFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private WeakReference<Context> mContext;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;

    private boolean mRequestingLocationUpdates;

    private ToggleButton mToggleButton;
    private TextView mLastLatitude, mLastLongitude;
    private TextView mCurrentLatitude, mCurrentLongitude, mLastTimestamp;

    public LocationFragment(){
        super();
    }

    public void setContext(Context context){
        mContext = new WeakReference<Context>(context);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LocationFragment newInstance(int sectionNumber, Context context){
        LocationFragment fragment = new LocationFragment();
        fragment.setContext(context);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mLastLatitude = (TextView) getView().findViewById(R.id.last_known_latitude);
        mLastLongitude = (TextView) getView().findViewById(R.id.last_known_longitude);

        mCurrentLatitude = (TextView) getView().findViewById(R.id.latitude);
        mCurrentLongitude = (TextView) getView().findViewById(R.id.longitude);
        mLastTimestamp = (TextView) getView().findViewById(R.id.last_timestamp);

        mToggleButton = (ToggleButton) getView().findViewById(R.id.toggleLocationUpdates);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        Context context = mContext.get();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mLocationRequest == null) {
            createLocationRequest();
        }
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            mRequestingLocationUpdates = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLastLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            mLastLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI(){
        if (mCurrentLocation != null) {
            mCurrentLatitude.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mCurrentLongitude.setText(String.valueOf(mCurrentLocation.getLongitude()));
            mLastTimestamp.setText(mLastUpdateTime);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mGoogleApiClient == null){
            buildGoogleApiClient();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mToggleButton.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            //Esta otra version si se usan PendingIntents
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, pendingIntent);
        }
        mRequestingLocationUpdates = false;
    }


}
