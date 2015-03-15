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
import android.widget.TextView;
import com.grademanager.app.R;
import com.grademanager.app.SubjectManager;
import com.grademanager.app.tree.ITreeNode;

/**
 * This {@link ITreeNode} represents a {@link SubjectManager.Subject} object. This is not directly on that class because it doesn't make much
 * sense, the {@link ITreeNode} part is only used in this package.
 */
public class SubjectNode implements ITreeNode {

	/**
	 * The name of the subject
	 */
	private final String name;

	/**
	 * The child-nodes which are actually the {@link GradeNode} objects
	 */
	private final ITreeNode[] childNodes;

	/**
	 * Creates child nodes from the subject's grades
	 *
	 * @param subject 			The subject to create a node from
	 * @param longClickListener The listener for {@link GradeNode}
	 */
	protected SubjectNode (final SubjectManager.Subject subject, final View.OnLongClickListener longClickListener) {
		name = subject.name;
		childNodes = new ITreeNode[subject.calculator.grades.size()];
		for (int i = 0; i < childNodes.length; i++)
			childNodes[i] = new GradeNode(subject.calculator.grades.get(i), longClickListener);
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
		// This always is a group item
		((TextView) view.findViewById(android.R.id.text1)).setText(name);
	}
}
