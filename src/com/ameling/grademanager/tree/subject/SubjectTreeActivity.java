package com.ameling.grademanager.tree.subject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.MainActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.SubjectConverter;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.tree.ITreeNode;
import com.ameling.grademanager.tree.TreeActivity;
import com.ameling.parser.grade.Grade;

public class SubjectTreeActivity extends TreeActivity {

	private static final String STATE_INPUT = "input";

	private SubjectManager.Subject subject;

	private SubjectNode subjectNode;

	@Override
	public int getMainLayout () {
		return R.layout.subject_tree;
	}

	@Override
	public void initialize () {
		final Intent intent = getIntent();
		if (intent.hasExtra(MainActivity.RESULT_SUBJECT)) {
			subject = SubjectManager.instance.getSubject(intent.getStringExtra(MainActivity.RESULT_SUBJECT)).clone();
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
		outState.putStringArray(STATE_INPUT, getInputs());
	}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		final String[] input = savedInstanceState.getStringArray(STATE_INPUT);
		final View[] children = getParentGroup().getChildViews();
		for (int i = 0; i < children.length; i++)
			((TextView) children[i].findViewById(R.id.grade_value)).setText(input[i]);
	}

	private String[] getInputs () {
		final View[] children = getParentGroup().getChildViews();

		final String[] input = new String[children.length];
		for (int i = 0; i < children.length; i++)
			input[i] = ((TextView) children[i].findViewById(R.id.grade_value)).getText().toString().trim();
		return input;
	}

	public void finishSubject (final View view) {
		final Grade[] grades = subject.getSubGrades();
		final String[] input = getInputs();
		for (int i = 0; i < grades.length; i++)
			if (!input[i].isEmpty())
				grades[i].setValue(Double.valueOf(input[i]));

		final Intent intent = new Intent();
		intent.putExtra(MainActivity.RESULT_SUBJECT, SubjectConverter.instance.convert(subject).toString());
		setResult(RESULT_OK, intent);
		finish();
	}
}
