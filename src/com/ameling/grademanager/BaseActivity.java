package com.ameling.grademanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public abstract class BaseActivity extends Activity {

	@Override
	public void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getMainLayout());
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		getMenuInflater().inflate(getMenuID(), menu);
		return super.onCreateOptionsMenu(menu);
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
	 * Initialize this activity. Called in {@link Activity#onCreate(Bundle)}
	 */
	public void initialize () {

	}

	@Override
	public final void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			handleActivityResult(requestCode, data);
		}
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
