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
package org.owasp.goatdroid.fourgoats.activities;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.services.LocationService;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AdminHome extends BaseActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 0;

    @Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getActionBar().setTitle("Home");
		getActionBar().setDisplayHomeAsUpEnabled(false);

		setContentView(R.layout.admin_home);

		UserInfoDBHelper uidh = new UserInfoDBHelper(getApplicationContext());
		String autoCheckin = uidh.getPreferences().get("autoCheckin");
		uidh.close();
		if (autoCheckin.equals("false")) {
			Button button = (Button) findViewById(R.id.people_nearby_button);
			button.setVisibility(View.INVISIBLE);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            } else {
                Intent locationServiceIntent = new Intent(AdminHome.this,
                        LocationService.class);
                startService(locationServiceIntent);
            }
        }
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent locationServiceIntent = new Intent(AdminHome.this,
                            LocationService.class);
                    startService(locationServiceIntent);
                } else {
                    Toast.makeText(this, R.string.people_neraby_location_warning, Toast.LENGTH_LONG).show();
                }
            }

        }
    }

	public void launchCheckins(View v) {
		Intent intent = new Intent(AdminHome.this, Checkins.class);
		startActivity(intent);
	}

	public void launchFriends(View v) {
		Intent intent = new Intent(AdminHome.this, Friends.class);
		startActivity(intent);
	}

	public void launchRewards(View v) {
		Intent intent = new Intent(AdminHome.this, Rewards.class);
		startActivity(intent);
	}

	public void launchManageUsers(View v) {
		Intent intent = new Intent(AdminHome.this, AdminOptions.class);
		startActivity(intent);
	}

	public void launchPeopleNearby(View v) {
		Intent intent = new Intent(AdminHome.this, PeopleNearby.class);
		startActivity(intent);
	}
}
