/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Wesley "EngineerCoding" Ameling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.grademanager.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This class is the base of every activity because it is supposed to save a lot of typing. This activity does the basic stuff that
 * almost every activity has, so it is more of an utility than a real feature.
 */
public abstract class BaseActivity extends Activity {

	@Override
	public void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getMainLayout());

		if (isSubActivity()) {
			final ActionBar bar = getActionBar();
			if (bar != null)
				bar.setDisplayHomeAsUpEnabled(true);
		}

		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		getMenuInflater().inflate(getMenuID(), menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed () {
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Returns the resource ID for the action bar. Returns by default the default menu
	 *
	 * @return A menu Id
	 */
	public int getMenuID () {
		return R.menu.setup_activity_actions;
	}

	/**
	 * Returns the resource ID of the main layout
	 *
	 * @return the resource ID
	 */
	public abstract int getMainLayout ();

	/**
	 * A boolean to indicate whether this activity is a child activity (true) or the main activity
	 *
	 * @return Whether this activity is a child activity or not
	 */
	public boolean isSubActivity() {
		return true;
	}

	/**
	 * Initialize this activity. Called in {@link Activity#onCreate(Bundle)}
	 */
	public void initialize () {

	}

	@Override
	public final void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK)
			handleActivityResult(requestCode, data);
	}

	/**
	 * This method handles a request code with its data. All checks are already executed so this is only called when it is worth it to call
	 *
	 * @param requestCode The requestcode the activity has been started with
	 * @param data        The data which got sent back
	 */
	public void handleActivityResult (final int requestCode, final Intent data) {

	}

}
