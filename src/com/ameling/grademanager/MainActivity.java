package com.ameling.grademanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.ameling.grademanager.grade.SetupActivity;
import com.ameling.parser.json.JSONObject;

/**
 * This is the main activity for this app. It creates the FileManager and acts accordingly to display. It either setups all subjects or loads
 * it from the saved file and shows those.
 * When it does not setup the grades, it uses the internal adapter for the ListView
 *
 * @author Wesley A
 */
public class MainActivity extends Activity {

	// Request code
	private static final int REQUEST_CODE_SETUP = 0;

	// Strings which are is in onActivityResult
	public static final String RESULT_SUBJECT = "subject";


	@Override
	public void onCreate (final Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);

		if (GradeManager.instance == null)
			GradeManager.instance = new GradeManager(this);
		setupListView();
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		getMenuInflater().inflate(R.menu.main_acitivity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPause () {
		super.onPause();
		GradeManager.instance.saveSubjects();
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_subject:
				final Intent intent = new Intent(this, SetupActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETUP);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
		// A response of SetupActivity should only be here, just an extra check
		if (requestCode == REQUEST_CODE_SETUP && resultCode == RESULT_OK) {
			// Decode the JSON back to a subject object
			final GradeManager.Subject subject = SubjectConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_SUBJECT)));
			GradeManager.instance.subjects.add(subject);
			// Update the ListView
			final ListView subjectList = (ListView) findViewById(R.id.subject_list);
			final ArrayAdapter<?> subjectAdapter = (ArrayAdapter) subjectList.getAdapter();
			if (subjectAdapter != null) {
				subjectAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Adds a new instance of {@link com.ameling.grademanager.converter.ObjectAdapter} to the ListView and gives it a proper ItemClickListener which opens new intents
	 */
	private void setupListView () {
		final ListView subjectList = (ListView) findViewById(R.id.subject_list);
		subjectList.setAdapter(SubjectConverter.instance.createAdapter(this, GradeManager.instance.subjects));
		subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (final AdapterView<?> parent, final View view, final int position, final long id) {
				final GradeManager.Subject subject = GradeManager.instance.subjects.get(position);
				Toast.makeText(MainActivity.this, subject.name, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
