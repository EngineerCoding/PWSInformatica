package com.ameling.grademanager.tree.subject;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.grademanager.tree.ITreeNode;
import com.ameling.parser.grade.Grade;

import java.util.List;

/**
 * This class is an {@link ITreeNode} which represents a {@link Grade} or {@link GradeWrapper} object. This is not done directly because
 * the parser is considered a library for this app, so it cannot be modified.
 */
public class GradeNode implements ITreeNode {

	/**
	 * The grade to represent
	 */
	private final Grade grade;

	/**
	 * The child nodes if the grade is an instance of {@link GradeWrapper}
	 */
	private final ITreeNode[] childNodes;

	/**
	 * Creates a node for the given {@link Grade}. If the grade is instance of {@link GradeWrapper}, then it will retrieve the child nodes.
	 *
	 * @param grade The grade to represent
	 */
	protected GradeNode (final Grade grade) {
		if (grade == null)
			throw new NullPointerException();

		this.grade = grade;

		// Set the child-nodes
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
			// instance of grade wrapper (so this is a group)
			((TextView) view.findViewById(android.R.id.text1)).setText(grade.name);
		} else {
			// instance of grade (so this is an item)
			((TextView) view.findViewById(R.id.grade_name)).setText(grade.name);
			if (grade.hasValue())
				((TextView) view.findViewById(R.id.grade_value)).setText(String.valueOf(grade.getValue()));
		}
	}
}
