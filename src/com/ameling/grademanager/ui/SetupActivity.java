package com.ameling.grademanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ameling.grademanager.R;
import com.ameling.grademanager.storage.StorageManager;
import com.ameling.grademanager.ui.adapter.GradeConverter;
import com.ameling.grademanager.ui.adapter.ObjectAdapter;
import com.ameling.grademanager.util.CalculatorWrapperFactory;
import com.ameling.grademanager.util.GradeWrapper;
import com.ameling.grademanager.util.Subject;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends BaseActivity implements View.OnFocusChangeListener, TextView.OnEditorActionListener {

	private static final int REQUEST_INTEGRATED_FORMULA = 0;
	private static final int REQUEST_SUB_FORMULA = 1;

	public static final String REQUEST_SUBJECT_NAME = "name";
	public static final String REQUEST_SUBJECT_FORMULA = "formula";

	private static final String REQUEST_SUB_GRADE = "grade";
	private static final String REQUEST_EDIT_FORMULA = "formula";


	/**
	 * The adapter which is super important for this Activity
	 */
	private ObjectAdapter<Grade> adapter;

	/**
	 * The ListView of this object
	 */
	private ListView gradeList;

	/**
	 * The latest parsed calculator. Also serves as a flag, because if it is null it will not finish properly this Activity
	 */
	private CalculatorWrapperFactory.CalculatorProxy calculator;

	/**
	 * An {@link ArrayList} with {@link com.ameling.grademanager.util.Subject} which get set by {@link #handleActivityResult(int, Intent)} and represent a grade
	 */
	private List<Subject> wrappers = new ArrayList<>();

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
		gradeList = ((ListView) findViewById(R.id.grade_list));
		gradeList.setAdapter(adapter);

		// Check if the intent is for recursive sub formula
		final Intent intent = getIntent();
		if (intent.hasExtra(REQUEST_SUB_GRADE)) {
			((TextView) findViewById(R.id.input_name)).setText(getString(R.string.grade_name));
			final EditText inputFieldName = (EditText) findViewById(R.id.new_subject_name);
			inputFieldName.setText(intent.getStringExtra(REQUEST_SUB_GRADE));
			inputFieldName.setEnabled(false);

			findViewById(R.id.select_integrated_school).setEnabled(false);
			if (intent.hasExtra(REQUEST_EDIT_FORMULA)) {
				final Subject subject = StorageManager.instance(null).format.decode(new JSONObject(intent.getStringExtra(REQUEST_EDIT_FORMULA)));
				((TextView) findViewById(R.id.subject_formula)).setText(subject.calculator.expression);
				adapter.addAll(subject.calculator.grades);
			}
		}
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

	@Override
	public void handleActivityResult (final int requestCode, final Intent data) {
		// Curly braces at the cases for readability purposes (no negative penalties because of that, only more compile time)
		switch (requestCode) {
			case REQUEST_INTEGRATED_FORMULA: {

				break;
			}
			case REQUEST_SUB_FORMULA: {
				final JSONObject object = new JSONObject(data.getStringExtra(GradeManager.RESULT_JSON));
				final Subject subject = StorageManager.instance(null).format.decode(object);

				boolean set = false;
				for (int i = 0; i < wrappers.size(); i++) {
					final Subject wrapper = wrappers.get(i);
					if (wrapper.name.equals(subject.name)) {
						wrappers.set(i, subject);
						set = true;
						break;
					}
				}

				if (!set)
					wrappers.add(subject);

				// Disable the value field of this grade
				for (int i = 0; i < gradeList.getChildCount(); i++) {
					final View child = gradeList.getChildAt(i);
					if (((TextView) child.findViewById(R.id.grade_name)).getText().toString().equals(subject.name)) {
						child.findViewById(R.id.grade_value).setEnabled(false);
						((Button) child.findViewById(R.id.button_add_formula)).setText(getString(R.string.edit_formula));
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * Click handler for the associated Button. This button handles to choose an integrated formula from a school.
	 *
	 * @param view The Button which got clicked
	 */
	public void selectFromIntegratedSchool (final View view) {
		startActivityForResult(new Intent(this, IntegratedSchoolActivity.class), REQUEST_INTEGRATED_FORMULA);
		// TODO: handle this
	}

	/**
	 * Click handler for the associated button. This button allows one to input a formula for a grade, recursively
	 *
	 * @param view The button which was clicked
	 */
	public void recurseFormula (final View view) {
		// Button has a parent (LinearLayout) which has a parent (FrameLayout), see grade_listview.xml
		final ViewGroup parent = (ViewGroup) view.getParent().getParent();
		final String name = ((TextView) parent.findViewById(R.id.grade_name)).getText().toString();

		final Intent intent = new Intent(this, SetupActivity.class);
		intent.putExtra(REQUEST_SUB_GRADE, name);
		for (int i = 0; i < wrappers.size(); i++) {
			final Subject subject = wrappers.get(i);
			if (subject.name.equals(name)) {
				intent.putExtra(REQUEST_EDIT_FORMULA, StorageManager.instance(null).format.encode(subject).toString());
				break;
			}
		}
		startActivityForResult(intent, REQUEST_SUB_FORMULA);
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

		for (int i = 0; i < gradeList.getChildCount(); i++) {
			final View child = gradeList.getChildAt(i);

			final Grade grade = calculator.getGrade(((TextView) child.findViewById(R.id.grade_name)).getText().toString());
			final String sValue = ((EditText) child.findViewById(R.id.grade_value)).getText().toString().trim();

			if (!sValue.isEmpty()) {
				grade.setValue(Double.valueOf(sValue));
			} else if (wrappers.size() > 0) {
				final GradeWrapper wrapper = new GradeWrapper(grade);
				for (int j = 0; j < wrappers.size(); j++) {
					final Subject subjectWrapper = wrappers.get(j);
					if (subjectWrapper.name.equals(wrapper.name)) {
						wrapper.setSubGrades(subjectWrapper.calculator);
						wrappers.remove(j);
						calculator.grades.set(calculator.grades.indexOf(grade), wrapper);
						break;
					}
				}
			}
		}


		// We got all data, set the result and finish this
		final Intent resultIntent = new Intent();
		final Subject object = new Subject(subjectName, calculator);
		resultIntent.putExtra(GradeManager.RESULT_JSON, StorageManager.instance(null).format.encode(object).toString());
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
			final CalculatorWrapperFactory.CalculatorProxy calculator = CalculatorWrapperFactory.createCalculator(null, expression);
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
