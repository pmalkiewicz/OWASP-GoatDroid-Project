/**
 * OWASP GoatDroid Project
 *
 * This file is part of the Open Web Application Security Project (OWASP)
 * GoatDroid project. For details, please see
 * https://www.owasp.org/index.php/Projects/OWASP_GoatDroid_Project
 *
 * Copyright (c) 2012 - The OWASP Foundation
 *
 * GoatDroid is published by OWASP under the GPLv3 license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 *
 * @author Jack Mannino (Jack.Mannino@owasp.org https://www.owasp.org/index.php/User:Jack_Mannino)
 * @author Walter Tighzert
 * @created 2012
 */
package org.owasp.goatdroid.fourgoats.fragments;

import java.util.HashMap;
import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.activities.AddVenue;
import org.owasp.goatdroid.fourgoats.activities.Login;
import org.owasp.goatdroid.fourgoats.activities.ViewCheckin;
import org.owasp.goatdroid.fourgoats.db.CheckinDBHelper;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.misc.Constants;
import org.owasp.goatdroid.fourgoats.misc.Utils;
import org.owasp.goatdroid.fourgoats.rest.checkin.CheckinRequest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class DoCheckin extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

	Context context;
	TextView gpsCoordsText;
	String latitude;
	String longitude;
	Button sendCheckin;
    GoogleApiClient mGoogleApiClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
		//getLocation();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
	}

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.do_checkin, container, false);
		gpsCoordsText = (TextView) v.findViewById(R.id.gpsCoords);
		sendCheckin = (Button) v.findViewById(R.id.button1);
		sendCheckin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendCheckin(v);
			}
		});

		return v;
	}

	public void sendCheckin(View v) {
		if (gpsCoordsText.getText().toString()
				.startsWith("Getting your location")) {
			Utils.makeToast(context, Constants.NO_LOCATION, Toast.LENGTH_LONG);
		} else {
			DoCheckinAsyncTask checkin = new DoCheckinAsyncTask();
			checkin.execute(null, null);
		}

	}

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
			latitude = Double.toString(lastLocation.getLatitude());
			longitude = Double.toString(lastLocation.getLongitude());
			gpsCoordsText.setText("Latitude: " + latitude + "\n\nLongitude: "
					+ longitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

	private class DoCheckinAsyncTask extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			UserInfoDBHelper dbHelper = new UserInfoDBHelper(context);
			HashMap<String, String> checkinInfo = new HashMap<String, String>();
			String sessionToken = dbHelper.getSessionToken();
			CheckinRequest rest = new CheckinRequest(context);

			try {
				checkinInfo = rest.doCheckin(sessionToken, latitude, longitude);

				if (checkinInfo.get("success").equals("true")) {
					CheckinDBHelper db = new CheckinDBHelper(context);
					checkinInfo.put("latitude", latitude);
					checkinInfo.put("longitude", longitude);
					db.insertCheckin(checkinInfo);
				}
			} catch (Exception e) {
				checkinInfo.put("errors", e.getMessage());
			} finally {
				dbHelper.close();
			}
			return checkinInfo;
		}

		protected void onPostExecute(HashMap<String, String> results) {
			if (results.get("success").equals("true")) {
				if (results.size() == 4)
					Utils.makeToast(context, Constants.CHECKIN_GREAT_SUCCESS,
							Toast.LENGTH_LONG);
				else {
					String reward = Constants.REWARD_EARNED + " "
							+ results.get("rewardName");
					Utils.makeToast(context, reward, Toast.LENGTH_LONG);
				}

				Bundle bundle = new Bundle();
				bundle.putString("checkinID", results.get("checkinID"));
				bundle.putString("venueName", results.get("venueName"));
				bundle.putString("venueWebsite", results.get("venueWebsite"));
				bundle.putString("dateTime", results.get("dateTime"));
				bundle.putString("latitude", latitude);
				bundle.putString("longitude", longitude);
				launchViewCheckin(bundle);
				CheckinDBHelper db = new CheckinDBHelper(context);
				results.put("latitude", latitude);
				results.put("longitude", longitude);
				db.insertCheckin(results);
			} else if (results.get("errors").equals(
					Constants.VENUE_DOESNT_EXIST)) {
				Utils.makeToast(context, Constants.VENUE_DOESNT_EXIST,
						Toast.LENGTH_LONG);
				Bundle bundle = new Bundle();
				bundle.putString("latitude", latitude);
				bundle.putString("longitude", longitude);
				launchAddVenue(bundle);
			} else if (results.get("errors").equals(Constants.INVALID_SESSION)) {
				Utils.makeToast(context, Constants.INVALID_SESSION,
						Toast.LENGTH_LONG);
				Intent intent = new Intent(context, Login.class);
				startActivity(intent);
			} else {
				Utils.makeToast(context, results.get("errors"),
						Toast.LENGTH_LONG);
			}
		}

		public void launchViewCheckin(Bundle bundle) {
			Intent intent = new Intent(context, ViewCheckin.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}

		public void launchAddVenue(Bundle bundle) {
			Intent intent = new Intent(context, AddVenue.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
}