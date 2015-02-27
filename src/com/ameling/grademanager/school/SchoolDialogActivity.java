package com.ameling.grademanager.school;

import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.grademanager.grade.CalculatorWrapper;

import java.util.Arrays;

import static com.ameling.grademanager.util.ConstantKeys.KEY_CALCULATOR;
import static com.ameling.grademanager.util.ConstantKeys.KEY_CLASSES;
import static com.ameling.grademanager.util.ConstantKeys.KEY_SUBJECT;

/**
 * This class is shown as a Dialog as the name suggests. It show all classes given by the {@link IntegratedSchool} which is received by
 * the intent that started this dialog. When the user selected a subject, the formula gets send back all the way to the {@link com.ameling.grademanager.grade.SetupActivity} so the
 * user can make the final edits and save the subject.
 */
public class SchoolDialogActivity extends BaseActivity {

	/**
	 * This converter converts a {@link String} to a simple {@link TextView}
	 */
	private static final ObjectAdapter.ViewConverter<String> StringConvert = new ObjectAdapter.ViewConverter<String>() {
		@Override
		public int getLayout () {
			return R.layout.text_item_list;
		}

		@Override
		public void populateInflatedView (final View view, final String from) {
			((TextView) view.findViewById(android.R.id.text1)).setText(from);
		}
	};

	@Override
	public int getMainLayout () {
		return R.layout.subject_dialog;
	}

	@Override
	public void initialize () {
		final Intent intent = getIntent();
		if (intent.hasExtra(IntegratedSchoolActivity.KEY_INDEX)) {
			// Set params to handle a touch outside this dialog
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

			// Get the school
			final IntegratedSchool school = IntegratedSchoolActivity.schoolCollection.get(intent.getIntExtra(IntegratedSchoolActivity.KEY_INDEX, 0));

			// sort the names for a more friendly view
			final String[] subjects = school.getClassLevelNames();
			Arrays.sort(subjects);

			// Set the adapter
			final ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, R.layout.text_item_spinner, subjects);
			classAdapter.setDropDownViewResource(R.layout.text_item_spinner);

			final Spinner spinner = (Spinner) findViewById(R.id.classes);
			spinner.setAdapter(classAdapter);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onNothingSelected (final AdapterView<?> adapterView) {}

				@Override
				public void onItemSelected (final AdapterView<?> adapterView, final View view, final int position, final long id) {
					// Set the proper list

					// Get the ClassLevel
					final String classLevel = classAdapter.getItem(position);
					final IntegratedSchool.ClassLevel level = school.getClassLevel(classLevel);

					// Set the adapter to the subject spinner
					final String[] subjects = level.getSupportedSubjects();
					Arrays.sort(subjects);

					final ArrayAdapter<String> classLevelAdapter = StringConvert.createAdapter(SchoolDialogActivity.this, Arrays.asList(subjects));

					final ListView subjectList = (ListView) findViewById(R.id.subjects);
					subjectList.setAdapter(classLevelAdapter);
					subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick (final AdapterView<?> adapterView, final View view, final int position, final long id) {
							// get the subject name
							final String subject = classLevelAdapter.getItem(position);
							final CalculatorWrapper wrapper = level.getFormula(classLevelAdapter.getItem(position));

							// send the result back
							final Intent intent = new Intent().putExtra(KEY_SUBJECT, subject).putExtra(KEY_CLASSES, classLevel)
									.putExtra(KEY_CALCULATOR, CalculatorWrapper.converter.convert(wrapper).toString());
							setResult(RESULT_OK, intent);
							finish();
						}
					});
				}
			});
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		// No action bar
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onTouchEvent (final MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			// A touch event happened so we cancel this activity
			onCancel(null);
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * A click handler for the cancel button
	 *
	 * @param view The view which got clicked
	 */
	public void onCancel (final View view) {
		// On back pressed is cancelling, see BaseActivity#onBackPressed()
		onBackPressed();
	}
}
