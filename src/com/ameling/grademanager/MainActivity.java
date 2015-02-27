package com.ameling.grademanager;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.grademanager.grade.GradeConverter;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.grademanager.grade.SetupActivity;
import com.ameling.grademanager.tree.subject.SubjectTreeActivity;
import com.ameling.parser.json.JSONObject;

import static com.ameling.grademanager.util.ConstantKeys.KEY_FORMULA;
import static com.ameling.grademanager.util.ConstantKeys.KEY_NAME;

/**
 * This is the main activity for this app. It creates the FileManager and acts accordingly to display. It either setups all subjects or loads
 * it from the saved file and shows those.
 * When it does not setup the grades, it uses the internal adapter for the ListView
 *
 * @author Wesley A
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	// Strings which are used in onActivityResult
	public static final String RESULT_SUBJECT = "subject";

	// String used in this activity and SetupActivity
	public static final String FLAG_EDIT = "edit";

	// Request codes
	private static final int REQUEST_CODE_SETUP = 0;
	private static final int REQUEST_EDIT = 1;
	private static final int REQUEST_SHOW = 2;

	/**
	 * The adapter for this ListView
	 */
	private ObjectAdapter<SubjectManager.Subject> adapter;

	/**
	 * The options of the ContextMenu
	 */
	private String[] options;

	@Override
	public int getMenuID () {
		return R.menu.main_acitivity_actions;
	}

	@Override
	public int getMainLayout () {
		return R.layout.main;
	}

	@Override
	public void initialize () {
		if (SubjectManager.instance == null)
			SubjectManager.instance = new SubjectManager(this);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		final ListView subjectList = (ListView) findViewById(R.id.subject_list);
		adapter = SubjectConverter.instance.createAdapter(this, SubjectManager.instance.subjects);
		subjectList.setAdapter(adapter);
		subjectList.setOnItemClickListener(this);

		// Register for the default listeners
		registerForContextMenu(subjectList);
	}

	@Override
	public void handleActivityResult (final int requestCode, final Intent data) {
		// An extra check just in case
		if (requestCode == REQUEST_CODE_SETUP) {
			// Decode the JSON back to a subject object
			final SubjectManager.Subject subject = SubjectConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_SUBJECT)));
			adapter.add(subject); // Add it to the adapter (which adds it in GradeManager.subjects
		} else if (requestCode == REQUEST_EDIT) {
			// The name of the subject which got edited
			final String original = data.getStringExtra(KEY_NAME);
			for (int i = 0; i < adapter.getCount(); i++) {
				final SubjectManager.Subject originalSubject = adapter.getItem(i);
				if (originalSubject.name.equals(original)) {
					// Remove the original object
					adapter.remove(originalSubject);

					// Get the new subject and insert it into the old position
					final SubjectManager.Subject subject = SubjectConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_SUBJECT)));
					adapter.insert(subject, i);
					break;
				}
			}
		} else if (requestCode == REQUEST_SHOW) {
			final SubjectManager.Subject subject = SubjectConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_SUBJECT)));
			SubjectManager.instance.replaceSubject(subject);
			adapter.notifyDataSetChanged();
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

	@Override
	public void onCreateContextMenu (final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo) {
		// This method is called on a long-click of a view. We need to make sure it comes from our ListView
		if (view.getId() == R.id.subject_list) {
			// Cast it to the proper Menu Info and get the position
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

			// Set the header title
			menu.setHeaderTitle(adapter.getItem(position).name);

			// Set the options
			if (options == null)
				options = getResources().getStringArray(R.array.options_subject_menu);

			for (int i = 0; i < options.length; i++)
				menu.add(Menu.NONE, i, i, options[i]);
		}
	}

	@Override
	public boolean onContextItemSelected (final MenuItem item) {
		// Called when an option has been selected

		// Get the subject in question
		final SubjectManager.Subject subject = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);

		// Handle the proper index which is selected (0 for edit, 1 for delete)
		switch (item.getItemId()) {
			case 0:
				// Create a GradeWrapper to use the default behaviour from SetupActivity
				final GradeWrapper wrapper = new GradeWrapper("", 1);
				wrapper.setSubGrades(subject.calculator);

				// Create the intent to start SetupActivity
				final Intent intent = new Intent(this, SetupActivity.class);
				intent.putExtra(KEY_NAME, subject.name);
				intent.putExtra(KEY_FORMULA, GradeConverter.instance.convert(wrapper).toString());
				intent.putExtra(FLAG_EDIT, true);
				startActivityForResult(intent, REQUEST_EDIT);
				break;
			case 1:
				adapter.remove(subject);
				break;
			default:
				Toast.makeText(this, String.format("Unimplemented method: %s", options[item.getItemId()]), Toast.LENGTH_LONG).show();
		}

		return true;
	}

	// Implementation of AdapterView.OnItemClickListener (for the listview)
	@Override
	public void onItemClick (final AdapterView<?> parent, final View view, final int position, final long id) {
		final SubjectManager.Subject subject = SubjectManager.instance.subjects.get(position);
		final Intent intent = new Intent(this, SubjectTreeActivity.class);
		intent.putExtra(RESULT_SUBJECT, subject.name);
		startActivityForResult(intent, REQUEST_SHOW);
	}
}
