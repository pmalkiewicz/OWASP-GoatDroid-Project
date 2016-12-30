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
package org.owasp.goatdroid.fourgoats.base;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.activities.DestinationInfo;
import org.owasp.goatdroid.fourgoats.activities.Login;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseUnauthenticatedActivity extends FragmentActivity {

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.destination_info_menu, menu);
		context = this.getApplicationContext();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(BaseUnauthenticatedActivity.this,
				DestinationInfo.class);
		startActivity(intent);
		return true;
	}

	public void launchLogin() {
		Intent intent = new Intent(context, Login.class);
		startActivity(intent);
	}

}
