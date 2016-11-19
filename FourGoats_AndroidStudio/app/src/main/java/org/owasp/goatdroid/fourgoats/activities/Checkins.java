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
import org.owasp.goatdroid.fourgoats.base.BaseTabsViewPagerActivity;
import org.owasp.goatdroid.fourgoats.fragments.DoCheckin;
import org.owasp.goatdroid.fourgoats.fragments.HistoryFragment;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;

public class Checkins extends BaseTabsViewPagerActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		ActionBar bar = getSupportActionBar();
		mTabsAdapter.addTab(bar.newTab().setText(R.string.checkin_now),
				DoCheckin.class, null);
		mTabsAdapter.addTab(bar.newTab().setText(R.string.history),
				HistoryFragment.class, null);
	}
}