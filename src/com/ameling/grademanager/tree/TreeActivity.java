package com.ameling.grademanager.tree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public int getMainLayout () {
		return R.layout.tree;
	}

	@Override
	public void initialize () {
		if (!flagWidthSet) {
			flagWidthSet = true;
			width = getResources().getDrawable(R.drawable.ic_collapsed).getIntrinsicWidth();
		}

		mainLayout = (LinearLayout) findViewById(R.id.tree_list);
		parentGroup = new TreeGroup(getParentNode(), false);

		parentGroup.isCollapsed = false;
		parentGroup.createLayout(LayoutInflater.from(this), mainLayout);
	}

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

		final List<Boolean> collapsedList = new ArrayList<>();
		for (final TreeGroup group : parentGroup.childGroups)
			if (group != null)
				for (final boolean isCollapsed : collectCollapsedArray(group))
					collapsedList.add(isCollapsed);

		final boolean[] collapsedArray = new boolean[collapsedList.size()];
		for (int i = 0; i < collapsedArray.length; i++)
			collapsedArray[i] = collapsedList.get(i);
		outState.putBooleanArray(STATE_EXPANDED, collapsedArray);
}

	@Override
	protected void onRestoreInstanceState (final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		final boolean[] collapsedArray = savedInstanceState.getBooleanArray(STATE_EXPANDED);
		int index = 0;

		for (final TreeGroup group : parentGroup.childGroups)
			if (group != null)
				restoreCollapsedArray(group, Arrays.copyOfRange(collapsedArray, index, index += (group.countGroups() + 1)));
	}

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
