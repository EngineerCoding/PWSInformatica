package com.ameling.grademanager.tree.subject;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.grademanager.tree.ITreeNode;
import com.ameling.parser.grade.Grade;

import java.util.List;

public class GradeNode implements ITreeNode {

	private final Grade grade;
	private final ITreeNode[] childNodes;

	protected GradeNode (final Grade grade) {
		if (grade == null)
			throw new NullPointerException();

		this.grade = grade;
		if (grade instanceof GradeWrapper && ((GradeWrapper) grade).calculator != null) {
			final List<Grade> grades = ((GradeWrapper) grade).calculator.grades;
			childNodes = new ITreeNode[grades.size()];
			for (int i = 0; i < childNodes.length; i++)
				childNodes[i] = new GradeNode(grades.get(i));
		} else {
			childNodes = new ITreeNode[0];
		}
	}

	@Override
	public boolean hasChildNodes () {
		return childNodes.length != 0;
	}

	@Override
	public ITreeNode[] getChildNodes () {
		return childNodes;
	}

	@Override
	public int getInflatableResource () {
		return hasChildNodes() ? R.layout.text_item_list : R.layout.grade_item_tree;
	}

	@Override
	public void populateView (final View view) {
		if (hasChildNodes()) {
			((TextView) view.findViewById(android.R.id.text1)).setText(grade.name);
		} else {
			((TextView) view.findViewById(R.id.grade_name)).setText(grade.name);
			if (grade.hasValue())
				((TextView) view.findViewById(R.id.grade_value)).setText(String.valueOf(grade.getValue()));
		}
	}
}
