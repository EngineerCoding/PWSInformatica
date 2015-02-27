package com.ameling.grademanager.tree.subject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.MainActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.tree.ITreeNode;
import com.ameling.grademanager.tree.TreeActivity;
import com.ameling.parser.grade.Grade;

/**
 * This activity is based on {@link TreeActivity} because this class shows a tree from a {@link SubjectManager.Subject}. This activity
 * is used to modify all grades and show it correctly again in the {@link com.ameling.grademanager.MainActivity}
 */
public class SubjectTreeActivity extends TreeActivity {

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
			subjectNode = new SubjectNode(subject);
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

	@Override
	public void onBackPressed () {
		// We are going back to the main screen, so save the inputs
		// Set the grade values from the inputs
		final Grade[] grades = subject.getSubGrades();
		final String[] input = getInputs();
		for (int i = 0; i < grades.length; i++)
			if (!input[i].isEmpty())
				grades[i].setValue(Double.valueOf(input[i]));

		// Set the result and finish this activity
		setResult(RESULT_OK, new Intent());
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
}
