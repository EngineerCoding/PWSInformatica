package com.grademanager.app.tree.subject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.grademanager.app.MainActivity;
import com.grademanager.app.R;
import com.grademanager.app.SubjectManager;
import com.grademanager.app.grade.GradeWrapper;
import com.grademanager.app.tree.ITreeNode;
import com.grademanager.app.tree.TreeActivity;
import com.grademanager.parser.grade.Grade;

/**
 * This activity is based on {@link TreeActivity} because this class shows a tree from a {@link SubjectManager.Subject}. This activity
 * is used to modify all grades and show it correctly again in the {@link com.grademanager.app.MainActivity}
 */
public class SubjectTreeActivity extends TreeActivity implements View.OnLongClickListener {

	/**
	 * A key to save the state with
	 */
	private static final String STATE_INPUT = "input";

	/**
	 * The subject to show the tree of
	 */
	private SubjectManager.Subject subject;

	/**
	 * The node created from {@link #subject}
	 */
	private SubjectNode subjectNode;

	@Override
	public void initialize () {
		// Check if the activity got started with a subject name
		final Intent intent = getIntent();
		if (intent.hasExtra(MainActivity.RESULT_SUBJECT)) {
			// Retrieve the subject name
			subject = SubjectManager.instance.getSubject(intent.getStringExtra(MainActivity.RESULT_SUBJECT));
			subjectNode = new SubjectNode(subject, this);
		} else {
			finish();
		}

		// Let the tree activity do its work
		super.initialize();
	}

	@Override
	public ITreeNode getParentNode () {
		return subjectNode;
	}

	@Override
	protected void onSaveInstanceState (final Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the inputs
		outState.putStringArray(STATE_INPUT, getInputs());
	}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Set the inputs to the appropriate view
		final String[] input = savedInstanceState.getStringArray(STATE_INPUT);
		final View[] children = getParentGroup().getChildViews();
		for (int i = 0; i < children.length; i++)
			((TextView) children[i].findViewById(R.id.grade_value)).setText(input[i]);
	}

	/**
	 * Updates all grades with the given input
	 */
	protected void updateGrades () {
		// Set the grade values from the inputs
		final Grade[] grades = subject.getSubGrades();
		final String[] input = getInputs();
		for (int i = 0; i < grades.length; i++)
			if (!input[i].isEmpty())
				grades[i].setValue(Double.valueOf(input[i]));
	}

	@Override
	public void onBackPressed () {
		// Set the values of the grade
		updateGrades();

		// Set the result and finish this activity
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Retrieves the inputs from the views in the shown tree.
	 *
	 * @return An array of inputs in correct order.
	 */
	private String[] getInputs () {
		// Get the views
		final View[] children = getParentGroup().getChildViews();

		// Iterate over the views and store the inputs
		final String[] input = new String[children.length];
		for (int i = 0; i < children.length; i++)
			input[i] = ((TextView) children[i].findViewById(R.id.grade_value)).getText().toString().trim();
		return input;
	}

	@Override
	public boolean onLongClick (final View view) {
		final Grade grade = subject.calculator.getGrade(((TextView) view.findViewById(R.id.grade_name)).getText().toString());
		if (!(grade instanceof GradeWrapper)) {
			// Create a new dialog
			final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(getString(R.string.predict_grade_value));
			builder.setNeutralButton("Ok", null);

			// Create a custom view
			final View inflatedView = LayoutInflater.from(this).inflate(R.layout.predict_dialog, null, false);
			// Set default text
			final TextView inputCalc = (TextView) inflatedView.findViewById(R.id.input_calc);
			final String format = getString(R.string.format_grade_value);
			inputCalc.setText(String.format(format, grade.name, "-"));
			// When the text is changed the average calculation should appear
			((TextView) inflatedView.findViewById(R.id.input_average)).addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2) {}

				@Override
				public void onTextChanged (CharSequence charSequence, int i, int i1, int i2) {}

				@Override
				public void afterTextChanged (final Editable editable) {
					String input = editable.toString().trim();
					if (!input.isEmpty()) {
						if (input.charAt(input.length() - 1) == '.')
							input += "0";
						inputCalc.setText(String.format(format, grade.name, subject.calculator.calculateGrade(grade, Double.valueOf(input))));
					} else {
						inputCalc.setText(String.format(format, grade.name, "-"));
					}
				}
			});

			// Add the custom view to the dialog
			builder.setView(inflatedView);
			builder.show();
		}
		return false;
	}
}
