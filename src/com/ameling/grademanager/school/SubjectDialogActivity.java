package com.ameling.grademanager.school;

import android.content.Intent;
import android.os.Bundle;
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

public class SubjectDialogActivity extends BaseActivity {

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

			final IntegratedSchool school = IntegratedSchoolActivity.schoolCollection.get(intent.getIntExtra(IntegratedSchoolActivity.KEY_INDEX, 0));

			// Set the adapter for the spinner (select class)
			final ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, R.layout.text_item_spinner, school.getClassLevelNames());
			classAdapter.setDropDownViewResource(R.layout.text_item_spinner);

			final Spinner spinner = (Spinner) findViewById(R.id.classes);
			spinner.setAdapter(classAdapter);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onNothingSelected (final AdapterView<?> adapterView) {}

				@Override
				public void onItemSelected (final AdapterView<?> adapterView, final View view, final int position, final long id) {
					// Show the subject spinner
					final ListView subjectList = (ListView) findViewById(R.id.subjects);
					if (subjectList.getVisibility() == View.GONE)
						subjectList.setVisibility(View.VISIBLE);

					// Get the ClassLevel
					final String classLevel = classAdapter.getItem(position);
					final IntegratedSchool.ClassLevel level = school.getClassLevel(classLevel);

					// Set the adapter to the subject spinner
					final ArrayAdapter<String> classLevelAdapter = StringConvert.createAdapter(SubjectDialogActivity.this, Arrays.asList(level.getSupportedSubjects()));

					subjectList.setAdapter(classLevelAdapter);
					subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick (final AdapterView<?> adapterView, final View view, final int position, final long id) {
							// get the subject name
							final String subject = classLevelAdapter.getItem(position);
							// send the result back
							final Intent intent = new Intent().putExtra(KEY_SUBJECT, subject).putExtra(KEY_CLASSES, classLevel).putExtra(KEY_CALCULATOR, CalculatorWrapper.converter.convert(level.getFormula(subject)).toString());
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
	public boolean onTouchEvent(final MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			onBackPressed();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed () {
		setResult(RESULT_CANCELED);
		finish();
	}
}
