/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Wesley "EngineerCoding" Ameling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.grademanager.app.tree.subject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.grademanager.app.MainActivity;
import com.grademanager.app.R;
import com.grademanager.app.SubjectConverter;
import com.grademanager.app.SubjectManager;
import com.grademanager.app.tree.ITreeNode;
import com.grademanager.app.tree.TreeActivity;
import com.grademanager.app.util.TextWatcherProxy;
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

	@Override
	protected void onPause () {
		super.onPause();
		// Save the inputs
		updateGrades();
		// Also save the subjects here because changes could have been made
		SubjectManager.instance.saveSubjects();
	}

	@Override
	public void onBackPressed () {
		// Set the result and finish this activity
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Updates all grades with the given input
	 */
	private void updateGrades () {
		// Set the grade values from the inputs
		final Grade[] grades = subject.getSubGrades();
		final String[] input = getInputs();
		for (int i = 0; i < grades.length; i++) {
			if (!input[i].isEmpty())
				grades[i].setValue(Double.valueOf(input[i]));
			else if(grades[i].hasValue())
				grades[i].reset();
		}
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
		// Update the states of all grades (it might not have saved so this a double check) for a more accurate value
		updateGrades();

		// Get the proper textview (from R.layout.text_item_list or R.layout.grade_item_tree)
		TextView textView = (TextView) view.findViewById(R.id.grade_name);
		if (textView == null)
			textView = (TextView) view.findViewById(android.R.id.text1);

		final Grade grade = subject.calculator.getGrade(textView.getText().toString());
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
		((TextView) inflatedView.findViewById(R.id.input_average)).addTextChangedListener(new TextWatcherProxy(new TextWatcherProxy.ITextWatcher() {
			@Override
			public void afterTextChanged (final Editable editable) {
				final String input = editable.toString().trim();
				if (!input.isEmpty()) {
					inputCalc.setText(String.format(format, grade.name, SubjectConverter.formatAverage(subject.calculator.calculateGrade(grade, Double.valueOf(input)))));
				} else {
					inputCalc.setText(String.format(format, grade.name, "-"));
				}
			}
		}));

		// Add the custom view to the dialog
		builder.setView(inflatedView);
		builder.show();
		return false;
	}
}
