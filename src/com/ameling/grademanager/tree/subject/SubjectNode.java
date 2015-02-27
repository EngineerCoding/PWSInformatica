package com.ameling.grademanager.tree.subject;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.tree.ITreeNode;

public class SubjectNode implements ITreeNode {

	private final String name;

	private final ITreeNode[] childNodes;

	protected SubjectNode (final SubjectManager.Subject subject) {
		name = subject.name;
		childNodes = new ITreeNode[subject.calculator.grades.size()];
		for (int i = 0; i < childNodes.length; i++)
			childNodes[i] = new GradeNode(subject.calculator.grades.get(i));
	}

	@Override
	public boolean hasChildNodes () {
		return true;
	}

	@Override
	public ITreeNode[] getChildNodes () {
		return childNodes;
	}

	@Override
	public int getInflatableResource () {
		return R.layout.text_item_list;
	}

	@Override
	public void populateView (final View view) {
		((TextView) view.findViewById(android.R.id.text1)).setText(name);
	}
}
