package com.ameling.grademanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ameling.grademanager.R;
import com.ameling.grademanager.storage.StorageManager;
import com.ameling.grademanager.ui.adapter.GradeConverter;
import com.ameling.grademanager.ui.adapter.ObjectAdapter;
import com.ameling.grademanager.util.Subject;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;

import java.util.ArrayList;

public class SetupActivity extends BaseActivity implements View.OnFocusChangeListener, TextView.OnEditorActionListener {

	private static final int REQUEST_CODE = 1;

	public static final String REQUEST_SUBJECT_NAME = "name";
	public static final String REQUEST_SUBJECT_FORMULA = "formula";


	/**
	 * The adapter which is super important for this Activity
	 */
	private ObjectAdapter<Grade> adapter;

	/**
	 * The latest parsed calculator. Also serves as a flag, because if it is null it will not finish properly this Activity
	 */
	private Calculator calculator;

	// Keys for saving and loading a state
	private static String STATE_FORMULA = "formulaInput";
	private static String STATE_GRADE_INPUT = "gradeInputs";

	@Override
	public int getMainLayout () {
		return R.layout.setup;
	}

	@Override
	public int getMenuID () {
		return R.menu.setup_activity_actions;
	}

	@Override
	public void initialize () {
		// Set the back button on the toolbar
		getActionBar().setDisplayHomeAsUpEnabled(true);

		addEditTextListeners();

		// Add the adapter to the listview
		adapter = GradeConverter.instance.createAdapter(this, new ArrayList<Grade>());
		adapter.setNotifyOnChange(true);
		((ListView) findViewById(R.id.grade_list)).setAdapter(adapter);
	}

	@Override
	protected void onSaveInstanceState (final Bundle outState) {
		super.onSaveInstanceState(outState);
		if (calculator != null) {
			// Store the formula
			outState.putString(STATE_FORMULA, ((EditText) findViewById(R.id.subject_formula)).getText().toString());

			// Store the grade inputs
			final String[] inputStrings = new String[calculator.grades.size()];
			final ListView gradeList = (ListView) findViewById(R.id.grade_list);
			for (int i = 0; i < gradeList.getChildCount(); i++) {
				final View child = gradeList.getChildAt(i);
				inputStrings[i] = ((EditText) child.findViewById(R.id.grade_value)).getText().toString();
			}

			outState.putStringArray(STATE_GRADE_INPUT, inputStrings);
		}
	}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			// Reload the formula and show the grade list
			parseFromExpression(savedInstanceState.getString(STATE_FORMULA));

			// Reload the input of the grades
			final String[] inputStrings = savedInstanceState.getStringArray(STATE_GRADE_INPUT);
			if (inputStrings != null) {
				for (int i = 0; i < inputStrings.length; i++) {
					if (!inputStrings[i].isEmpty()) {
						final Grade grade = adapter.getItem(i);
						grade.setValue(Double.valueOf(inputStrings[i]));
					}
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed () {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		finish();
	}

	// Implementation of the OnFocusActionListener
	@Override
	public void onFocusChange (final View view, final boolean hasFocus) {
		if (!hasFocus) {
			final EditText editText = (EditText) view;
			parseFromExpression(editText.getText().toString());
		}
	}

	// Implementation of the OnEditorActionListener
	@Override
	public boolean onEditorAction (final TextView textView, final int action, final KeyEvent keyEvent) {
		if (action == EditorInfo.IME_ACTION_SEND) {
			// Close the keyboard
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

			// Try to parse the text
			parseFromExpression(textView.getText().toString());
			return true;
		}
		return false;
	}

	/**
	 * Click handler for the associated Button. This button handles to choose an integrated formula from a school_searchable.
	 *
	 * @param view The Button which got clicked
	 */
	public void selectFromIntegratedSchool (final View view) {
		startActivityForResult(new Intent(this, IntegratedSchoolActivity.class), REQUEST_CODE);
	}

	/**
	 * Click handler for the associated Button. This button handles to finish this setup
	 *
	 * @param view The Button which got clicked
	 */
	public void finishSetup (final View view) {
		// Check if the keyboard is up, and parse if necessary
		final InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
			parseFromExpression(((EditText) findViewById(R.id.subject_formula)).getText().toString());
		}

		final String subjectName = ((EditText) findViewById(R.id.new_subject_name)).getText().toString().trim();
		if (subjectName.isEmpty()) {
			Toast.makeText(this, R.string.toast_subject_required, Toast.LENGTH_SHORT).show();
			return;
		}

		if (calculator == null) {
			Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
		}

		final ListView gradeList = (ListView) findViewById(R.id.grade_list);
		for (int i = 0; i < gradeList.getChildCount(); i++) {
			final View child = gradeList.getChildAt(i);
			final String sValue = ((EditText) child.findViewById(R.id.grade_value)).getText().toString().trim();
			if (!sValue.isEmpty()) {
				final Grade grade = calculator.getGrade(((TextView) child.findViewById(R.id.grade_name)).getText().toString());
				grade.setValue(Double.valueOf(sValue));
			}
		}

		// We got all data, set the result and finish this
		final Subject subject = new Subject(subjectName, calculator);
		final Intent resultIntent = new Intent();
		resultIntent.putExtra(GradeManager.RESULT_JSON, StorageManager.getInstance(null).format.encode(subject).toString());
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	/**
	 * Sets the {@link TextView.OnEditorActionListener} and {@link View.OnFocusChangeListener} to always try to parse the text. Both listeners will try to parse the text via
	 * {@link #parseFromExpression}
	 */
	private void addEditTextListeners () {
		final EditText editText = (EditText) findViewById(R.id.subject_formula);
		editText.setOnFocusChangeListener(this);
		editText.setOnEditorActionListener(this);
	}

	/**
	 * Parses the expression to a {@link ExpressionCalculator} and sets the ListView to that. Also updates the field {@link #calculator}
	 *
	 * @param expression
	 */
	private void parseFromExpression (String expression) {
		if (expression == null || (expression = expression.trim()).isEmpty())
			return;

		try {
			final Calculator calculator = new ExpressionCalculator(expression);
			// Parsed correctly, set the data
			adapter.clear();
			if (calculator.grades.size() > 0) {
				adapter.addAll(calculator.grades);
				this.calculator = calculator;
			} else {
				this.calculator = null;
				Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
			}
		} catch (final SyntaxException e) {
			adapter.clear();
			calculator = null;
			Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
		}
	}
}
