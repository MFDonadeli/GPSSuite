package br.com.mfdonadeli.gpssuite;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by mfdonadeli on 11/4/15.
 */
public class GPSClass implements GpsStatus.Listener, LocationListener, SensorEventListener {

    private static final String TAG = "GPSClass";

    private Context context;

    private LocationManager mLocationManager;
    private boolean mRecordLocation;
    private FillListener listener;

    private String[] mProvider = new String[3];
    private Location[] mLastLocation = new Location[3];
    private Location mLocation;
    private boolean mValid = false;

    private ArrayList<String> coordList = new ArrayList<String>();

    private float speed;
    private float speed_max;
    private double speed_avg;
    private double speedTotal;
    private int speedCount;
    private float distance;
    private long timebegin;
    private float altitude;
    private float altitude_max;
    private boolean tracking = false;
    private boolean miles = false;

    private final double TO_MI = 0.62137119224;
    private final double TO_FT = 3.28083989501;

    //Sensors
    private SensorManager sensorManager;
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;

    private GeomagneticField geomagneticField;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] rotation = new float[9];
    private float[] orientation = new float[3];
    private float[] smoothed = new float[3];
    private float bearing;

    public GPSClass(String provider, FillListener listener) {
        this.listener = listener;
    }

    public GPSClass(FillListener listener)
    {
        this.listener = listener;
    }

    public GPSClass(Context context, FillListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    public interface FillListener
    {
        void fillItem(int nItem, String sValor);
        void fillLatLong(double latitude, double longitude);
        void putList(ArrayList<String> list);

        final int GPS_ACCURACY = 1;
        final int GPS_ALTITUDE = 2;
        final int GPS_SPEED = 3;
        final int GPS_HEADING = 4;
        final int GPS_MAXSPEED = 5;
        final int GPS_AVGSPEED = 6;
        final int GPS_DISTANCE = 7;
        final int GPS_MAXALTITUDE = 8;
        final int GPS_TIME = 9;

        int SAVE_GPX = -1;
        int SAVE_KML = -2;
    }

    public void setMiles(boolean miles)
    {
        this.miles = miles;
    }

    public Location getCurrentLocation() {
        if (!mRecordLocation) return null;
        // go in best to worst order
        for (int i = 0; i < mLastLocation.length; i++) {
            Location l = this.current(i);
            if (l != null && l.getLatitude() != 0.00) return l;
            l = mLocationManager.getLastKnownLocation(this.mProvider[i]);
            if (l != null) return l;
        }
        Log.d(TAG, "No location received yet.");
        return null;
    }

    public String getCurrentAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        Location loc = getCurrentLocation();

        String sRet = "";

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            sRet = addresses.get(0).getAddressLine(0) + " - ";
            sRet += addresses.get(0).getLocality() + " - ";
            //sRet += addresses.get(0).getAdminArea() + " - ";
            sRet += addresses.get(0).getCountryName();

            if(sRet == "")
                sRet = addresses.get(0).getFeatureName();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sRet;
    }

    public void recordLocation(boolean recordLocation) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    public void beginRecordTrack()
    {
        if(tracking == true) {
            stopRecordTrack();
            Log.d(TAG, "Stopped Record Track.");
        }
        else {
            timebegin = System.currentTimeMillis();
            tracking = true;
            Log.d(TAG, "Started Record Track.");
        }
    }

    public void stopRecordTrack()
    {
        tracking = false;
    }

    public void resetRecordTrack()
    {
        speed = 0;
        speed_avg = 0;
        speed_max = 0;
        speedCount = 0;
        speedTotal = 0;

        altitude = 0;
        altitude_max = 0;

        coordList.clear();
    }

    public Location getLocation()
    {
        return mLastLocation[0];
    }

    public void startSensor()
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startReceivingLocationUpdates() {
        mProvider[0] = LocationManager.GPS_PROVIDER;
        mLastLocation[0] = new Location(mProvider[0]);
        mProvider[1] = LocationManager.NETWORK_PROVIDER;
        mLastLocation[1] = new Location(mProvider[1]);
        mProvider[2] = LocationManager.PASSIVE_PROVIDER;
        mLastLocation[2] = new Location(mProvider[2]);


        List<String> aaa;
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            aaa  = mLocationManager.getAllProviders();
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(
                        mProvider[0],
                        1000,
                        0F,
                        this);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        mProvider[1],
                        1000,
                        0F,
                        this);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        mProvider[2],
                        1000,
                        0F,
                        this);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");

            mLocationManager.addGpsStatusListener(this);
        }
    }

    public void stopSensor()
    {
        sensorManager.unregisterListener(this, sensorMagnetic);
        sensorManager.unregisterListener(this, sensorGravity);
    }

    public void stopReceivingLocationUpdates() {
        if (mLocationManager != null) {
            //for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(this);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            //}
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
    }

    public void setLastLocation(Location location)
    {
        for(int i = 0; i < mProvider.length; i++)
            if(location.getProvider() == mProvider[i])
                mLastLocation[i].set(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() == 0.0
                && location.getLongitude() == 0.0) {
            // Hack to filter out 0.0,0.0 locations
            return;
        }
        // If GPS is available before start camera, we won't get status
        // update so update GPS indicator when we receive data.
        if (//mListener != null && mRecordLocation &&
                android.location.LocationManager.GPS_PROVIDER.equals(mProvider)) {
            //mListener.showGpsOnScreenIndicator(true);
        }
        if (!mValid) {
            Log.d(TAG, "Got first location.");
        }

        setLastLocation(location);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        listener.fillLatLong(latitude, longitude);
        listener.fillItem(FillListener.GPS_ACCURACY, String.valueOf(location.getAccuracy()));
        listener.fillItem(FillListener.GPS_TIME, String.valueOf(location.getTime()));

        if(tracking) {

            geomagneticField = new GeomagneticField((float) latitude, (float) longitude,
                    (float) location.getAltitude(), System.currentTimeMillis());


            altitude = (float) location.getAltitude();

            speed = location.getSpeed() * 3.6f;

            if(miles)
            {
                speed *= TO_MI;
                altitude *= TO_FT;
            }

            speedTotal += speed;
            speedCount++;

            speed_avg = speedTotal / speedCount;

            if (speed_max < speed) speed_max = speed;
            if (altitude_max < altitude) altitude_max = altitude;

            long timeAtual = System.currentTimeMillis();
            float dif = (timeAtual - timebegin) / (1000f * 3600f);

            Log.d("TAG", dif + " e " + timeAtual + " e " + timebegin);


            distance = (float) speed_avg * dif;

            coordList.add(latitude + ";" + longitude);

            listener.fillItem(FillListener.GPS_ALTITUDE, String.valueOf(altitude));
            listener.fillItem(FillListener.GPS_MAXALTITUDE, String.valueOf(altitude_max));

            listener.fillItem(FillListener.GPS_SPEED, String.valueOf(speed));
            listener.fillItem(FillListener.GPS_MAXSPEED, String.valueOf(speed_max));
            listener.fillItem(FillListener.GPS_AVGSPEED, String.valueOf(speed_avg));
            listener.fillItem(FillListener.GPS_DISTANCE, String.valueOf(distance));
        }
        mValid = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch(status) {
            case LocationProvider.OUT_OF_SERVICE: {
                mValid = false;
                //MainActivity.ChangeStatus("OUT_OF_SERVICE");
                break;
            }
            case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                mValid = false;
                //MainActivity.ChangeStatus("UNAVAILABLE");
                //if (//mListener != null && mRecordLocation &&
                //        android.location.LocationManager.GPS_PROVIDER.equals(provider)) {
                //mListener.showGpsOnScreenIndicator(false);
                //}
                break;
            }
            case LocationProvider.AVAILABLE: {
                mValid = false;
                //MainActivity.ChangeStatus("AVAILABLE");
                break;
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        mValid = false;
    }

    public Location current(int i) {
        return mValid ? mLastLocation[i] : null;
    }

    @Override
    public void onGpsStatusChanged(int i) {
        switch (i){
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                GpsStatus status = mLocationManager.getGpsStatus(null);

                Iterator<GpsSatellite> it = status.getSatellites().iterator();

                ArrayList<String> list = new ArrayList<String>();


                while(it.hasNext()){
                    GpsSatellite sat = it.next();
                    if(sat.getSnr()>0)
                        list.add(sat.getPrn() + "-" + sat.getSnr() + "-" +
                            sat.getElevation() + "-" + sat.getAzimuth());
                }

                listener.putList(list);
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }


    //Sensors
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            smoothed = LowPassFilter.filter(sensorEvent.values, gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            smoothed = LowPassFilter.filter(sensorEvent.values, geomagnetic);
            geomagnetic[0] = smoothed[0];
            geomagnetic[1] = smoothed[1];
            geomagnetic[2] = smoothed[2];
        }

        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
        SensorManager.getOrientation(rotation, orientation);

        bearing = orientation[0];
        bearing = (float)Math.toDegrees(bearing);

        if(geomagneticField != null) {
            bearing += geomagneticField.getDeclination();
        }

        if(bearing < 0)
            bearing += 360;

        String dirText = "";
        int range = (int) (bearing / (360 / 16f));

        if(range == 15 || range == 0) dirText = context.getResources().getString(R.string.N);
        if(range == 1 || range == 2) dirText = context.getResources().getString(R.string.NE);
        if(range == 3 || range == 4) dirText = context.getResources().getString(R.string.E);
        if(range == 5 || range == 6) dirText = context.getResources().getString(R.string.SE);
        if(range == 7 || range == 8) dirText = context.getResources().getString(R.string.S);
        if(range == 9 || range == 10) dirText = context.getResources().getString(R.string.SW);
        if(range == 11 || range == 12) dirText = context.getResources().getString(R.string.W);
        if(range == 13 || range == 14) dirText = context.getResources().getString(R.string.NW);

        listener.fillItem(4, dirText);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && i == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            //Unreliable
        }
    }

    public void SaveAs(int type)
    {
        if(type == listener.SAVE_GPX){
            GPXFile file = new GPXFile(coordList, context);
            file.CreateGPXFile();
        }
        else if(type == listener.SAVE_KML)
        {
            KMLFile file = new KMLFile(coordList, context);
            file.CreateKMLFile();
        }
    }
}
