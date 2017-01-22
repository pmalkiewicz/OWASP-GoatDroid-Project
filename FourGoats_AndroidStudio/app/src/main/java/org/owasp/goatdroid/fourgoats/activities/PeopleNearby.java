package org.owasp.goatdroid.fourgoats.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.adapter.PeopleNearbyAdapter;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.misc.Profile;
import org.owasp.goatdroid.fourgoats.misc.Utils;
import org.owasp.goatdroid.fourgoats.rest.peoplenearby.PeopleNearbyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.view.View.GONE;

public class PeopleNearby extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = PeopleNearby.class.getSimpleName();
    ListView mListView;
    TextView mTextView;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.people_nearby_layout);
        mListView = (ListView) findViewById(R.id.people_nearby_listview);
        mListView.setVisibility(View.INVISIBLE);
        mTextView = (TextView) findViewById(R.id.people_nearby_textview);
        mTextView.setVisibility(View.VISIBLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView,
                                    int myItemInt, long mylng) {
                String selectedFromList = (String) (mListView
                        .getItemAtPosition(myItemInt));
                String[] splitList = selectedFromList.split("\n");
                String userName = splitList[0];
                Intent intent = new Intent(PeopleNearby.this, ViewProfile.class);
                Bundle profileBundle = new Bundle();
                profileBundle.putString("userName", userName);
                intent.putExtras(profileBundle);
                startActivity(intent);
            }
        });
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public String[] bindListView(ArrayList<Profile> userData) {
        ArrayList<String> userArray = new ArrayList<String>();
        for (Profile p : userData) {
                userArray.add(p.getUserName() + "\n" + p.getFirstName() + " "
                        + p.getLastName() + "\n" + p.getDistance() + "\n" + p.getTimestamp());
        }
        String[] users = new String[userArray.size()];
        users = userArray.toArray(users);
        return users;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            GetUsersNearby search = new GetUsersNearby(this);
            search.execute(null, null);
        }
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

    private class GetUsersNearby extends AsyncTask<Void, Void, ArrayList<Profile>> {

        Activity activity;

        public GetUsersNearby(Activity a) {
            activity = a;
        }

        protected ArrayList<Profile> doInBackground(Void... params) {

            ArrayList<Profile> userData;
            UserInfoDBHelper uidh = new UserInfoDBHelper(context);
            String sessionToken = uidh.getSessionToken();
            uidh.close();
            PeopleNearbyRequest rest = new PeopleNearbyRequest(context);
            try {
                if (sessionToken.equals("")) {
                    Intent intent = new Intent(context, Login.class);
                    startActivity(intent);
                    return null;

                } else {
                    if (mLocation != null) {
                        userData = rest.getProfiles(sessionToken,
                                Double.toString(mLocation.getLatitude()),
                                Double.toString(mLocation.getLongitude()));
                        if (userData != null) {
                            if (userData.size() > 0) {
                                sortByDistance(userData);
                                return userData;
                            } else {
                                return userData;
                            }
                        } else {
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(ArrayList<Profile> users) {
            if (context != null && users != null) {
                String[] array = bindListView(users);
                mListView.setAdapter(new PeopleNearbyAdapter(activity, array));
                mListView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(GONE);
            }
            if (users.size() == 0) {
                Utils.makeToast(context, "There are no users nearby!", Toast.LENGTH_LONG);
            }
            if (mLocation == null) {
                mTextView.setVisibility(GONE);
                Utils.makeToast(context, "Could not get location, try again later!", Toast.LENGTH_LONG);
            }
        }
    }


    private ArrayList<Profile> sortByDistance(ArrayList<Profile> data){
        Collections.sort(data, new Comparator<Profile>() {
            @Override
            public int compare(Profile profile1, Profile profile2) {
                return Double.compare(profile1.getDistance(), profile2.getDistance());
            }
        });
        return data;
    }

}
