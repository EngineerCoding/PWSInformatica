package com.ameling.grademanager.grade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.MainActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.SubjectConverter;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.grademanager.school.IntegratedSchoolActivity;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONObject;

import java.util.ArrayList;

import static com.ameling.grademanager.util.ConstantKeys.KEY_CALCULATOR;
import static com.ameling.grademanager.util.ConstantKeys.KEY_CLASSES;
import static com.ameling.grademanager.util.ConstantKeys.KEY_FORMULA;
import static com.ameling.grademanager.util.ConstantKeys.KEY_NAME;
import static com.ameling.grademanager.util.ConstantKeys.KEY_SUBJECT;
import static com.ameling.grademanager.util.ConstantKeys.KEY_WEIGHTING;

/**
 * This activity handles everything which contains input. The input stuff is formulas, grade values, sub formulas and pre-defined formulas. This is also used to edit already
 * existing formulas because this is the most simple solution for that.
 */
public class SetupActivity extends BaseActivity implements View.OnFocusChangeListener, TextView.OnEditorActionListener {

	// The key which is used with recursive formulas
	private static final String RESULT_GRADEWRAPPER = "gradewrapper";

	// The values for making new requests
	private static final int REQUEST_INTEGRATED_FORMULA = 0;
	private static final int REQUEST_SUB_FORMULA = 1;

	// Keys for saving and loading a state
	private static String STATE_FORMULA = "formulaInput";
	private static String STATE_GRADE_INPUT = "gradeInputs";

	//public static final String REQUEST_SUBJECT_NAME = "name";
	//public static final String REQUEST_SUBJECT_FORMULA = "formula";

	/**
	 * The adapter which is super important for this Activity
	 */
	private ObjectAdapter<Grade> adapter;

	/**
	 * The ListView of this object
	 */
	private ListView gradeList;

	/**
	 * A flag to determine whether the formula has been flagParsed correctly
	 */
	private boolean flagParsed = false;

	/**
	 * The weighting of the parent grade. This is only set when it is a recursive call to this activity
	 */
	private int gradeWeighting;

	/**
	 * A flag to determine whether this is a sub calculator or the main calculator
	 */
	private boolean flagSubCalculator = false;

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

		// Add listeners
		final EditText editText = (EditText) findViewById(R.id.subject_formula);
		editText.setOnFocusChangeListener(this);
		editText.setOnEditorActionListener(this);

		// Add the adapter to the listview
		adapter = GradeConverter.instance.createAdapter(this, new ArrayList<Grade>());
		gradeList = ((ListView) findViewById(R.id.grade_list));
		gradeList.setAdapter(adapter);

		// Check if the intent is for recursive sub formula
		final Intent intent = getIntent();
		if (intent.hasExtra(KEY_NAME)) {
			flagSubCalculator = true;
			gradeWeighting = intent.getIntExtra(KEY_WEIGHTING, -1);

			// Set better texts and disable the subject name TextEdit and the ability to choose from a integrated school
			((TextView) findViewById(R.id.input_name)).setText(getString(R.string.grade_name));
			final EditText inputFieldName = (EditText) findViewById(R.id.new_subject_name);
			inputFieldName.setText(intent.getStringExtra(KEY_NAME));
			inputFieldName.setEnabled(false);
			findViewById(R.id.select_integrated_school).setEnabled(false);

			// If there is a formula present, then show it
			if (intent.hasExtra(KEY_FORMULA)) {
				final GradeWrapper wrapper = (GradeWrapper) GradeConverter.instance.convert(new JSONObject(intent.getStringExtra(KEY_FORMULA)));
				((EditText) findViewById(R.id.subject_formula)).setText(wrapper.calculator.expression);
				adapter.addAll(wrapper.calculator.grades);
				flagParsed = true;
			}
		}
	}

	@Override
	protected void onSaveInstanceState (final Bundle outState) {
		super.onSaveInstanceState(outState);
		if (flagParsed) {
			// Store the formula
			outState.putString(STATE_FORMULA, ((EditText) findViewById(R.id.subject_formula)).getText().toString());

			// Store the grade inputs
			final String[] inputStrings = new String[adapter.getCount()];
			for (int i = 0; i < inputStrings.length; i++)
				inputStrings[i] = GradeConverter.instance.convert(adapter.getItem(i)).toString();
			outState.putStringArray(STATE_GRADE_INPUT, inputStrings);
		}
	}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Set the formula
		((EditText) findViewById(R.id.subject_formula)).setText(savedInstanceState.getString(STATE_FORMULA));

		// Load into the adapter
		final String[] inputStrings = savedInstanceState.getStringArray(STATE_GRADE_INPUT);
		if (inputStrings != null)
			for (final String grade : inputStrings)
				adapter.add(GradeConverter.instance.convert(new JSONObject(grade)));
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed () {
		// Result failed
		setResult(RESULT_CANCELED);
		finish();
	}

	// Implementation of the OnFocusActionListener
	@Override
	public void onFocusChange (final View view, final boolean hasFocus) {
		if (!hasFocus) {
			// Try to parse the expression
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
				final CalculatorWrapper wrapper = CalculatorWrapper.converter.convert(new JSONObject(data.getStringExtra(KEY_CALCULATOR)));

				((EditText) findViewById(R.id.subject_formula)).setText(wrapper.expression);
				parseFromExpression(wrapper.expression);

				for (final Grade grade : wrapper.grades) {
					if (grade instanceof GradeWrapper)
						replaceGrade(grade);
				}

				final TextView view = (TextView) findViewById(R.id.new_subject_name);
				if (view.getText().toString().trim().isEmpty()) {
					String new_subject = data.getStringExtra(KEY_SUBJECT);
					if (MainActivity.hasSubject(new_subject))
						new_subject = data.getStringExtra(KEY_CLASSES) + ":" + new_subject;
					// Don't double check, the user gets notified when it is already in use
					view.setText(new_subject);
				}
				break;
			}
			case REQUEST_SUB_FORMULA: {
				// Get the wrapper for a grade in this adapter
				final GradeWrapper wrapper = (GradeWrapper) GradeConverter.instance.convert(new JSONObject(data.getStringExtra(RESULT_GRADEWRAPPER)));
				replaceGrade(wrapper);
				break;
			}
		}
	}

	private void replaceGrade (final Grade grade) {
		for (int i = 0; i < adapter.getCount(); i++) {
			final Grade toReplaceGrade = adapter.getItem(i);
			if (grade.name.equals(grade.name)) {
				// Remove the original object and insert our wrapper
				adapter.remove(toReplaceGrade);
				adapter.insert(grade, i);
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

		// Create the intent with information for the sub activity
		final Intent intent = new Intent(this, SetupActivity.class);
		intent.putExtra(KEY_NAME, name);

		for (int i = 0; i < adapter.getCount(); i++) {
			final Grade grade = adapter.getItem(i);
			if (grade.name.equals(name)) {
				intent.putExtra(KEY_WEIGHTING, grade.weighting);
				if (grade instanceof GradeWrapper)
					intent.putExtra(KEY_FORMULA, GradeConverter.instance.convert(grade).toString());
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
		if (!flagParsed) {
			// Not parsed, try to parse the current input
			final InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
			parseFromExpression(((EditText) findViewById(R.id.subject_formula)).getText().toString());

			// If still not parsed, exit
			if (!flagParsed) {
				Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		final String subjectName = ((EditText) findViewById(R.id.new_subject_name)).getText().toString().trim();
		if (subjectName.isEmpty()) {
			Toast.makeText(this, R.string.toast_subject_required, Toast.LENGTH_SHORT).show();
			return;
		} else if (!flagSubCalculator) {
			if (MainActivity.hasSubject(subjectName)) {
				Toast.makeText(this, R.string.toast_invalid_subject, Toast.LENGTH_SHORT).show();
				((TextView) findViewById(R.id.new_subject_name)).setText("");
				return;
			}
		}

		// Add double values to the grade object
		for (int i = 0; i < gradeList.getChildCount(); i++) {
			final View child = gradeList.getChildAt(i);
			final Grade grade = adapter.getItem(i);
			if (!(grade instanceof GradeWrapper)) {
				final String sValue = ((EditText) child.findViewById(R.id.grade_value)).getText().toString().trim();
				if (!sValue.isEmpty())
					grade.setValue(Double.valueOf(sValue));
			}
		}

		// We got all data, set the result and finish this
		final Intent resultIntent = new Intent();
		if (!flagSubCalculator) {
			final SubjectManager.Subject object = new SubjectManager.Subject(subjectName, createCalculator());
			resultIntent.putExtra(MainActivity.RESULT_SUBJECT, SubjectConverter.instance.convert(object).toString());
		} else {
			// Create a grade wrapper of this activity
			final GradeWrapper wrapper = new GradeWrapper(subjectName, gradeWeighting);
			wrapper.setSubGrades(createCalculator());
			resultIntent.putExtra(RESULT_GRADEWRAPPER, GradeConverter.instance.convert(wrapper).toString());
		}
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	/**
	 * Creates a {@link com.ameling.grademanager.grade.CalculatorWrapper} from {@link #adapter}
	 *
	 * @return A proper CalculatorWrapper
	 */
	private CalculatorWrapper createCalculator () {
		final Grade[] grades = new Grade[adapter.getCount()];
		for (int i = 0; i < grades.length; i++)
			grades[i] = adapter.getItem(i);
		return new CalculatorWrapper(grades, ((EditText) findViewById(R.id.subject_formula)).getText().toString());
	}

	/**
	 * Parses the expression to a {@link ExpressionCalculator} and sets the ListView to that.
	 *
	 * @param expression The expression to parse as an {@link com.ameling.parser.grade.ExpressionCalculator}
	 */
	private void parseFromExpression (String expression) {
		if (expression == null || (expression = expression.trim()).isEmpty())
			return;

		try {
			final ExpressionCalculator calculator = new ExpressionCalculator(expression);
			// Parsed correctly, set the data
			adapter.clear();
			if (calculator.grades.size() > 0) {
				adapter.addAll(calculator.grades);
				flagParsed = true;
			} else {
				flagParsed = false;
				Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
			}
		} catch (final SyntaxException e) {
			adapter.clear();
			flagParsed = false;
			Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
		}
	}
}



