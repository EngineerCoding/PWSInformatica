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

package com.grademanager.app.tree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.grademanager.app.BaseActivity;
import com.grademanager.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the base-activity of every activity that wants to show some kind of tree. For that too happen one has to only give a {@link ITreeNode}
 * which has child-nodes so acts like a parent-node. When the sub class implements {@link View.OnLongClickListener}, it will set that to the child
 * nodes which are not a group.
 */
public abstract class TreeActivity extends BaseActivity {

	// Flag to determine whether the width is set
	private static boolean flagWidthSet = false;

	// The width of both icons
	protected static int width;

	// State keys
	private static final String STATE_EXPANDED = "expanded";

	/**
	 * This is the main layout which functions as the main list
	 */
	private LinearLayout mainLayout;

	/**
	 * The parent group which is used to call the appropriate tree items
	 */
	private TreeGroup parentGroup;

	@Override
	public final int getMainLayout () {
		return R.layout.tree;
	}

	@Override
	public void initialize () {
		if (!flagWidthSet) {
			flagWidthSet = true;
			width = getResources().getDrawable(R.drawable.ic_collapsed).getIntrinsicWidth();
		}

		// Get the main layout everything attaches to and create TreeGroup node
		mainLayout = (LinearLayout) findViewById(R.id.tree_list);
		// Set the listener to the treegroup which sets it to the child nodes
		parentGroup = new TreeGroup(getParentNode(), false);

		// Create the layout
		parentGroup.createLayout(LayoutInflater.from(this), mainLayout);
	}

	/**
	 * Retrieves the parent group the view was created from.
	 *
	 * @return {@link #parentGroup}
	 */
	public TreeGroup getParentGroup () {
		return parentGroup;
	}

	/**
	 * Returns the parent node to show in this view. This should always be the same
	 *
	 * @return the parent node
	 */
	public abstract ITreeNode getParentNode ();

	@Override
	protected void onSaveInstanceState (final Bundle outState) {
		super.onSaveInstanceState(outState);

		// Collect if the children are collapsed (in order)
		final List<Boolean> collapsedList = new ArrayList<>();
		for (final TreeGroup group : parentGroup.childGroups)
			if (group != null)
				for (final boolean isCollapsed : collectCollapsedArray(group))
					collapsedList.add(isCollapsed);
		// Convert to regular boolean array
		final boolean[] collapsedArray = new boolean[collapsedList.size()];
		for (int i = 0; i < collapsedArray.length; i++)
			collapsedArray[i] = collapsedList.get(i);
		// Save the boolean array
		outState.putBooleanArray(STATE_EXPANDED, collapsedArray);
	}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Collect the array with booleans created in onSaveInstanceState(Bundle)
		final boolean[] collapsedArray = savedInstanceState.getBooleanArray(STATE_EXPANDED);
		int index = 0;
		// Set the appropriate the values and simulate a click (done in restoreCollapsedArray(TreeGroup, boolean[]))
		for (final TreeGroup group : parentGroup.childGroups)
			if (group != null)
				restoreCollapsedArray(group, Arrays.copyOfRange(collapsedArray, index, index += (group.countGroups() + 1)));
	}

	/**
	 * Collects recursively the booleans whether a child is collapsed or not
	 *
	 * @param group The group to start with
	 * @return A boolean-array containing whether a child is collapsed or not
	 */
	private static boolean[] collectCollapsedArray (final TreeGroup group) {
		if (group != null) {
			final boolean[] collapsedArray = new boolean[group.countGroups() + 1];
			int index = 0;
			collapsedArray[index++] = group.isCollapsed;

			for (final TreeGroup subGroup : group.childGroups) {
				if (subGroup != null)
					for (final boolean isCollapsed : collectCollapsedArray(subGroup))
						collapsedArray[index++] = isCollapsed;
			}
			return collapsedArray;
		}
		return null;
	}

	/**
	 * Sets the children to the correct stat with the given array. It does that by simulating a click ({@link android.view.View#performClick()})
	 *
	 * @param group          The group to collapse or not
	 * @param collapsedArray The array containing the data if it was collapsed or not
	 */
	private static void restoreCollapsedArray (final TreeGroup group, final boolean[] collapsedArray) {
		if (group != null && collapsedArray != null) {
			int index = 0;
			if (!collapsedArray[index++])
				group.treeLayout.findViewById(R.id.group_title).performClick();

			for (final TreeGroup subGroup : group.childGroups)
				if (subGroup != null)
					restoreCollapsedArray(subGroup, Arrays.copyOfRange(collapsedArray, index, (index += (subGroup.countGroups() + 1))));
		}
	}
}
