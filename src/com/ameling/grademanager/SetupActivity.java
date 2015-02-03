package com.ameling.grademanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;

import java.util.List;

public class SetupActivity extends Activity {

	@Override
	public void onCreate (final Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.setup);

		// Set the back button on the toolbar
		final ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		setFocusListener();
		setKeyboardListener();
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		getMenuInflater().inflate(R.menu.setup_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void finish () {
		super.finish();
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void setKeyboardListener () {
		((EditText) findViewById(R.id.subject_formula)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction (final TextView textView, final int action, final KeyEvent keyEvent) {
				if (action == EditorInfo.IME_ACTION_SEND) {
					// Close the keyboard
					InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
					// Try to parse the text
					try {
						final Calculator calc = new ExpressionCalculator(textView.getText().toString());
						final ListView gradeList = (ListView) findViewById(R.id.grade_list);
						gradeList.setAdapter(new GradeAdapter(calc.grades));
						//SetupActivity.this.gradeList = calc.grades;
						Toast.makeText(textView.getContext(), "Parsed!", Toast.LENGTH_SHORT).show();
					} catch (final SyntaxException e) {
						Toast.makeText(textView.getContext(), "Failed to parse", Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});
	}

	public void selectFromIntegratedSchool (final View view) {

	}

	/**
	 * Click handler for the associated Button. This Button can be found in setup.xml
	 *
	 * @param view The Button which got clicked
	 */
	public void finishSetup (final View view) {
		final ListView gradeList = (ListView) findViewById(R.id.grade_list);
		Log.i("CHECK", "childs");
		for (int i = 0; i < gradeList.getChildCount(); i++) {
			View child = gradeList.getChildAt(i);
			if (child != null) {
				Log.i("CHECK_FOUND", ((TextView) child.findViewById(R.id.grade_name)).getText().toString());
			}
			Log.i("CHECK", "LOOP" + i);
		}
	}

	private void setFocusListener () {
		final EditText text = (EditText) findViewById(R.id.subject_formula);
		text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange (final View view, boolean hasFocus) {
				if (!hasFocus) {
					try {
						final Calculator calc = new ExpressionCalculator(text.getText().toString());
						final ListView gradeList = (ListView) findViewById(R.id.grade_list);
						gradeList.setAdapter(new GradeAdapter(calc.grades));
						//SetupActivity.this.gradeList = calc.grades;

					} catch (final SyntaxException e) {
						Log.d("create", "FORMuLA", e);
					}
				}
			}
		});
	}

	/**
	 * An ArrayAdapter which inflates an item view to show in the parent ListView. This handles {@link Grade} objects.
	 */
	private class GradeAdapter extends ArrayAdapter<Grade> {

		private final List<Grade> gradeList;

		public GradeAdapter (final List<Grade> gradeList) {
			super(SetupActivity.this, R.layout.grade_listview, gradeList);
			this.gradeList = gradeList;
		}

		@Override
		public View getView (final int position, View convertView, final ViewGroup parent) {
			if (convertView == null)
				convertView = getLayoutInflater().inflate(R.layout.grade_listview, parent, false);
			final Grade grade = gradeList.get(position);

			final TextView gradeName = (TextView) convertView.findViewById(R.id.grade_name);
			gradeName.setText(grade.name);

			return convertView;
		}
	}


}
