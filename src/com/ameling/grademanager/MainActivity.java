package com.ameling.grademanager;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.grademanager.grade.SetupActivity;
import com.ameling.parser.json.JSONObject;

/**
 * This is the main activity for this app. It creates the FileManager and acts accordingly to display. It either setups all subjects or loads
 * it from the saved file and shows those.
 * When it does not setup the grades, it uses the internal adapter for the ListView
 *
 * @author Wesley A
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	// Request code
	private static final int REQUEST_CODE_SETUP = 0;

	// Strings which are is in onActivityResult
	public static final String RESULT_SUBJECT = "subject";

	private ObjectAdapter<SubjectManager.Subject> adapter;

	@Override
	public int getMainLayout () {
		return R.layout.main;
	}

	@Override
	public int getMenuID () {
		return R.menu.main_acitivity_actions;
	}

	@Override
	public void initialize () {
		if (SubjectManager.instance == null)
			SubjectManager.instance = new SubjectManager(this);

		final ListView subjectList = (ListView) findViewById(R.id.subject_list);
		adapter = SubjectConverter.instance.createAdapter(this, SubjectManager.instance.subjects);
		subjectList.setAdapter(adapter);
		subjectList.setOnItemClickListener(this);
	}

	@Override
	public void handleActivityResult (final int requestCode, final Intent data) {
		// An extra check just in case
		if (requestCode == REQUEST_CODE_SETUP) {
			// Decode the JSON back to a subject object
			final SubjectManager.Subject subject = SubjectConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_SUBJECT)));
			adapter.add(subject); // Add it to the adapter (which adds it in GradeManager.subjects
		}
	}

	@Override
	protected void onPause () {
		super.onPause();
		SubjectManager.instance.saveSubjects();
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

	// Implementation of AdapterView.OnItemClickListener (for the listview)
	@Override
	public void onItemClick (final AdapterView<?> parent, final View view, final int position, final long id) {
		final SubjectManager.Subject subject = SubjectManager.instance.subjects.get(position);
		Toast.makeText(this, subject.name, Toast.LENGTH_SHORT).show();
	}

	/**
	 * A proxy to {@link SubjectManager#hasSubject(String)} because only this class should have direct access to the SubjectManager
	 *
	 * @param name The name of the *new* subject
	 * @return Whether it already exists or not
	 */
	public static boolean hasSubject (final String name) {
		if (name != null || !name.isEmpty())
			// no null check for the instance because this is the first activity to ever live
			return SubjectManager.instance.hasSubject(name);
		return false;
	}
}
