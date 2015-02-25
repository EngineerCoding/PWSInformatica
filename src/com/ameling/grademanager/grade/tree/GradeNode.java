package com.ameling.grademanager.grade.tree;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.parser.grade.Grade;

public class GradeNode implements ITreeNode {

	public final Grade grade;

	public GradeNode(final Grade grade) {
		this.grade = grade;
	}

	@Override
	public boolean hasChildNodes () {
		return false;
	}

	@Override
	public ITreeNode[] getChildNodes () {
		return new ITreeNode[0];
	}

	@Override
	public int getInflatableResource () {
		return R.layout.grade_item_tree;
	}

	@Override
	public void populateView (final View view) {
		((TextView) view.findViewById(R.id.grade_name)).setText(grade.name);
		if (grade.hasValue())
			((TextView) view.findViewById(R.id.grade_value)).setText(String.valueOf(grade.getValue()));
	}
}
