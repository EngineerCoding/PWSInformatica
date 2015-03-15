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

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.grademanager.app.R;
import com.grademanager.app.grade.GradeWrapper;
import com.grademanager.app.tree.ITreeNode;
import com.grademanager.parser.grade.Grade;

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
	 * The listener to use on objects
	 */
	private final View.OnLongClickListener longClickListener;

	/**
	 * Creates a node for the given {@link Grade}. If the grade is instance of {@link GradeWrapper}, then it will retrieve the child nodes.
	 *
	 * @param grade 			The grade to represent
	 * @param longClickListener The listener to add to the view
	 */
	protected GradeNode (final Grade grade, final View.OnLongClickListener longClickListener) {
		if (grade == null)
			throw new NullPointerException();
		this.grade = grade;
		this.longClickListener = longClickListener;

		// Set the child-nodes
		if (grade instanceof GradeWrapper && ((GradeWrapper) grade).calculator != null) {
			final List<Grade> grades = ((GradeWrapper) grade).calculator.grades;
			childNodes = new ITreeNode[grades.size()];
			for (int i = 0; i < childNodes.length; i++)
				childNodes[i] = new GradeNode(grades.get(i), longClickListener);
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

			// Set a text watcher to update the parent
			final EditText gradeInput = (EditText) view.findViewById(R.id.grade_value);
			if (grade.hasValue())
				gradeInput.setText(String.valueOf(grade.getValue()));
		}
		view.setOnLongClickListener(longClickListener);
	}
}
