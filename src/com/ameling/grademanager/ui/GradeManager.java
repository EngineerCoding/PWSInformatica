package com.ameling.grademanager.ui;

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
import com.ameling.grademanager.R;
import com.ameling.grademanager.storage.StorageManager;
import com.ameling.grademanager.ui.adapter.SubjectConverter;
import com.ameling.grademanager.util.Subject;
import com.ameling.parser.json.JSONObject;

import java.util.List;

/**
 * This is the main activity for this app. It creates the FileManager and acts accordingly to display. It either setups all subjects or loads
 * it from the saved file and shows those.
 * When it does not setup the grades, it uses the internal adapter for the ListView
 *
 * @author Wesley A
 */
public class GradeManager extends Activity {

	// public for other classes to access
	public static StorageManager storageManager;

	// Request code
	private static final int REQUEST_CODE_SETUP = 0;

	// Strings which are is in onActivityResult
	public static final String RESULT_JSON = "jsonObject";

	// private data only for this activity
	private List<Subject> subjects;

	@Override
	public void onCreate (final Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);

		subjects = StorageManager.getInstance(this).getSubjects();
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
		if (subjects.size() > 0)
			StorageManager.getInstance(null).saveSubjects(subjects);
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
			final Subject subject = StorageManager.getInstance(null).format.decode(new JSONObject(data.getStringExtra(RESULT_JSON)));
			subjects.add(subject);
			// Update the ListView
			final ListView subjectList = (ListView) findViewById(R.id.subject_list);
			final ArrayAdapter<?> subjectAdapter = (ArrayAdapter) subjectList.getAdapter();
			if (subjectAdapter != null) {
				subjectAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Adds a new instance of {@link com.ameling.grademanager.ui.adapter.ObjectAdapter} to the ListView and gives it a proper ItemClickListener which opens new intents
	 */
	private void setupListView () {
		final ListView subjectList = (ListView) findViewById(R.id.subject_list);
		subjectList.setAdapter(SubjectConverter.instance.createAdapter(this, subjects));
		subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (final AdapterView<?> parent, final View view, final int position, final long id) {
				final Subject subject = subjects.get(position);
				Toast.makeText(GradeManager.this, subject.name, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
