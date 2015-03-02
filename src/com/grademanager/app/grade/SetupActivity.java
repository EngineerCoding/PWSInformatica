package com.grademanager.app.grade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.grademanager.app.BaseActivity;
import com.grademanager.app.MainActivity;
import com.grademanager.app.R;
import com.grademanager.app.SubjectConverter;
import com.grademanager.app.SubjectManager;
import com.grademanager.app.converter.ObjectAdapter;
import com.grademanager.app.school.IntegratedSchoolActivity;
import com.grademanager.parser.SyntaxException;
import com.grademanager.parser.grade.ExpressionCalculator;
import com.grademanager.parser.grade.Grade;
import com.grademanager.parser.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.grademanager.app.util.ConstantKeys.KEY_CALCULATOR;
import static com.grademanager.app.util.ConstantKeys.KEY_CLASSES;
import static com.grademanager.app.util.ConstantKeys.KEY_FORMULA;
import static com.grademanager.app.util.ConstantKeys.KEY_NAME;
import static com.grademanager.app.util.ConstantKeys.KEY_SUBJECT;
import static com.grademanager.app.util.ConstantKeys.KEY_WEIGHTING;

/**
 * This activity handles everything which contains input. The input stuff is formulas, grade values, sub formulas and pre-defined formulas. This is also used to edit already
 * existing formulas because this is the most simple solution for that.
 */
public class SetupActivity extends BaseActivity implements View.OnFocusChangeListener, TextView.OnEditorActionListener, ViewTreeObserver.OnGlobalLayoutListener {

	// The key which is used with recursive formulas
	private static final String RESULT_GRADEWRAPPER = "gradewrapper";

	// The values for making new requests
	private static final int REQUEST_INTEGRATED_FORMULA = 0;
	private static final int REQUEST_SUB_FORMULA = 1;

	// Keys for saving and loading a state
	private static String STATE_FORMULA = "formulaInput";
	private static String STATE_GRADE_INPUT = "gradeInputs";

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

	/**
	 * A flag to determine wheter this activity is used for editing purposes
	 */
	private boolean flagEditing = false;

	/**
	 * The name of the subject which is being edited
	 */
	private String editingSubject;

	/**
	 * The EditText field of the formula input
	 */
	private EditText inputFormula;

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
		// Add listeners
		inputFormula = (EditText) findViewById(R.id.subject_formula);
		inputFormula.setOnFocusChangeListener(this);
		inputFormula.setOnEditorActionListener(this);

		// Set a listener to know when the keyboard is down
		inputFormula.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);

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

			final View buttonIntegratedSchool = findViewById(R.id.select_integrated_school);
			buttonIntegratedSchool.setEnabled(false);
			buttonIntegratedSchool.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_disabled));

			// If there is a formula present, then show it
			if (intent.hasExtra(KEY_FORMULA)) {
				final GradeWrapper wrapper = (GradeWrapper) GradeConverter.instance.convert(new JSONObject(intent.getStringExtra(KEY_FORMULA)));
				inputFormula.setText(wrapper.calculator.expression);
				adapter.addAll(wrapper.calculator.grades);
				flagParsed = true;
			}
		}

		if (intent.hasExtra(MainActivity.FLAG_EDIT)) {
			flagEditing = intent.getBooleanExtra(MainActivity.FLAG_EDIT, false);
			if (flagEditing) {
				// Set the proper field name
				((TextView) findViewById(R.id.input_name)).setText(getString(R.string.subject_name));
				findViewById(R.id.new_subject_name).setEnabled(true);
				editingSubject = intent.getStringExtra(KEY_NAME);
				flagSubCalculator = false;

				final View buttonIntegratedSchool = findViewById(R.id.select_integrated_school);
				buttonIntegratedSchool.setEnabled(true);
				buttonIntegratedSchool.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
			}
		}
	}

	@Override
	protected void onSaveInstanceState (final Bundle outState) {
		super.onSaveInstanceState(outState);
		if (flagParsed) {
			// Store the formula
			outState.putString(STATE_FORMULA, inputFormula.getText().toString());

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
		inputFormula.setText(savedInstanceState.getString(STATE_FORMULA));

		// Load into the adapter
		final String[] inputStrings = savedInstanceState.getStringArray(STATE_GRADE_INPUT);
		if (inputStrings != null)
			for (final String grade : inputStrings)
				adapter.add(GradeConverter.instance.convert(new JSONObject(grade)));
	}
	// Implementation of the OnFocusActionListener

	/**
	 * A variable to check if parsing is necessary
	 */
	private String lastExpression = null;

	@Override
	public void onFocusChange (final View view, final boolean hasFocus) {
		if (hasFocus) {
			lastExpression = ((EditText) view).getText().toString();
		} else {
			final String newText = ((EditText) view).getText().toString();
			if (!lastExpression.equals(newText)) {
				// Text changed, try to parse the expression
				parseFromExpression(newText);
			}

		}
	}

	// Implementation of the OnGobalLayoutListener
	@Override
	public void onGlobalLayout () {
		final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (manager.isWatchingCursor(inputFormula)) {
			final String formula = inputFormula.getText().toString();
			if (lastExpression != null && !lastExpression.equals(formula)) {
				parseFromExpression(formula);
				lastExpression = formula;
				inputFormula.clearFocus();
			}
		}
	}

	// Implementation of the OnEditorActionListener
	@Override
	public boolean onEditorAction (final TextView textView, final int action, final KeyEvent keyEvent) {
		if (action == EditorInfo.IME_ACTION_SEND) {
			// Close the keyboard
			final InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

			// Check if the text is changed
			final String newText = textView.getText().toString();
			if (!lastExpression.equals(newText)) {
				// Text changed, try to parse the expression
				parseFromExpression(newText);
				lastExpression = newText;
			}
			return true;
		}
		return false;
	}

	@Override
	public void handleActivityResult (final int requestCode, final Intent data) {
		// Curly braces at the cases for readability purposes (no negative penalties because of that, only more compile time)
		switch (requestCode) {
			case REQUEST_INTEGRATED_FORMULA: {
				// Get the wrapper for it
				final CalculatorWrapper wrapper = CalculatorWrapper.converter.convert(new JSONObject(data.getStringExtra(KEY_CALCULATOR)));

				// Set the expression text and parse it
				inputFormula.setText(wrapper.expression);
				parseFromExpression(wrapper.expression);

				// Replace the grades if they have a GradeWrapper
				for (final Grade grade : wrapper.grades)
					if (grade instanceof GradeWrapper)
						replaceGrade(grade);

				// Set the subject name
				final TextView view = (TextView) findViewById(R.id.new_subject_name);
				if (view.getText().toString().trim().isEmpty()) {
					String new_subject = data.getStringExtra(KEY_SUBJECT);
					if (SubjectManager.instance.hasSubject(new_subject))
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

	/**
	 * Replaces the appropriate grade object in the {@link #adapter} for the given {@link GradeWrapper}
	 *
	 * @param grade The grade to replace the grade object for
	 */
	private void replaceGrade (final Grade grade) {
		if (grade != null) {
			final int index = getIndex(grade.name);
			if (index > -1) {
				adapter.remove(adapter.getItem(index));
				adapter.insert(grade, index);
			}
		}
	}

	/**
	 * Gets the index from the given grade name from the {@link #adapter}
	 *
	 * @param grade The name of the grade to search for
	 */
	private int getIndex (final String grade) {
		if (grade != null && !grade.trim().isEmpty())
			for (int i = 0; i < adapter.getCount(); i++)
			if (adapter.getItem(i).name.equals(grade))
				return i;
		return -1;
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

		// Get the grade by name and set the appropriate data to handle in #intialize
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
		if (!flagParsed || (lastExpression != null && !lastExpression.equals(inputFormula.getText().toString()))) {
			// Not parsed, try to parse the current input
			final InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
			parseFromExpression(inputFormula.getText().toString());

			// If still not parsed, exit
			if (!flagParsed) {
				Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		// Checks for the subject name
		final String subjectName = ((EditText) findViewById(R.id.new_subject_name)).getText().toString().trim();
		if (subjectName.isEmpty()) {
			Toast.makeText(this, R.string.toast_subject_required, Toast.LENGTH_SHORT).show();
			return;
		} else if (!flagSubCalculator) {
			if (SubjectManager.instance.hasSubject(subjectName) && !(flagEditing && subjectName.equals(editingSubject))) {
				Toast.makeText(this, R.string.toast_invalid_subject, Toast.LENGTH_SHORT).show();
				((TextView) findViewById(R.id.new_subject_name)).setText("");
				return;
			}
		}

		// Add double values to the grade objects
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
			if (flagEditing)
				resultIntent.putExtra(KEY_NAME, editingSubject);
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
	 * Creates a {@link com.grademanager.app.grade.CalculatorWrapper} from {@link #adapter}
	 *
	 * @return A proper CalculatorWrapper
	 */
	private CalculatorWrapper createCalculator () {
		final Grade[] grades = new Grade[adapter.getCount()];
		for (int i = 0; i < grades.length; i++)
			grades[i] = adapter.getItem(i);
		return new CalculatorWrapper(grades, inputFormula.getText().toString());
	}

	/**
	 * Parses the expression to a {@link ExpressionCalculator} and sets the ListView to that.
	 *
	 * @param expression The expression to parse as an {@link com.grademanager.parser.grade.ExpressionCalculator}
	 */
	private void parseFromExpression (String expression) {
		if (expression == null || (expression = expression.trim()).isEmpty())
			return;

		try {
			// Hard backup
			final List<Grade> grades = new ArrayList<>();
			for (int i = 0; i < adapter.getCount(); i++)
				grades.add(adapter.getItem(i).clone());

			final ExpressionCalculator calculator = new ExpressionCalculator(expression);
			// Parsed correctly, set the data
			adapter.clear();
			if (calculator.grades.size() > 0) {
				adapter.addAll(calculator.grades);
				flagParsed = true;

				// Restore the grades from the backup
				// Not using replaceGrade because the weighting could be changed
				if (grades.size() > 0) {
					for (final Grade grade : grades) {
						final int index = getIndex(grade.name);

						if (index > -1) {
							final Grade item = adapter.getItem(index);
							adapter.remove(item);

							if (item.weighting != grade.weighting) {
								// Recreate objects with the correct weightings
								if (grade instanceof GradeWrapper) {
									// This always needs to be done because a sub-calculator is available
									final GradeWrapper wrapper = (GradeWrapper) grade;

									final GradeWrapper replacement = new GradeWrapper(grade.name, item.weighting);
									replacement.setSubGrades(wrapper.calculator);
									adapter.insert(replacement, index);
								} else if (grade.hasValue()) {
									// This is only useful when the grade has a value
									final Grade replacement = new Grade(grade.name, item.weighting);
									replacement.setValue(grade.getValue());
									adapter.insert(replacement, index);
								}
							} else {
								// Just insert the grade
								adapter.insert(grade, index);
							}
						}
					}
				}
			} else {
				flagParsed = false;
				Toast.makeText(this, R.string.toast_no_grades, Toast.LENGTH_SHORT).show();
			}
		} catch (final SyntaxException e) {
			adapter.clear();
			flagParsed = false;
			Toast.makeText(this, R.string.toast_invalid_formula, Toast.LENGTH_SHORT).show();
		}
	}
}
