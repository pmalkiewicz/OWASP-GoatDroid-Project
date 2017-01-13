package org.owasp.goatdroid.fourgoats.services;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.rest.peoplenearby.PeopleNearbyRequest;

import java.text.DateFormat;
import java.util.Date;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 300000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        mLastUpdateTime = "";

        UserInfoDBHelper uidh = new UserInfoDBHelper(getApplicationContext());
        String autoCheckin = uidh.getPreferences().get("autoCheckin");
        uidh.close();

        if (autoCheckin.equals("true")) {
            buildGoogleAPIClient();
            mGoogleApiClient.connect();
        } else
            stopSelf();
    }

    protected synchronized void buildGoogleAPIClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        new SendLocationAsyncTask().execute(null, null);
    }

    private class SendLocationAsyncTask extends
            AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            UserInfoDBHelper dbHelper = new UserInfoDBHelper(getApplicationContext());
            String sessionToken = dbHelper.getSessionToken();
            PeopleNearbyRequest rest =
                    new PeopleNearbyRequest(getApplicationContext());

            String latitude = Double.toString(mCurrentLocation.getLatitude());
            String longitude = Double.toString(mCurrentLocation.getLongitude());
            boolean success = false;
            try {
                success = rest.sendCurrentLocation(sessionToken, latitude, longitude);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                dbHelper.close();
            }
            return success;
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.i(TAG, "Location sent!");
            }
            else {
                Log.e(TAG, "Sending location failed!");
            }
        }
    }
}
