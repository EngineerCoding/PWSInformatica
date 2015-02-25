package com.ameling.grademanager.tree;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.R;

public abstract class TreeActivity extends BaseActivity {

	// Flag to determine whether the width is set
	private static boolean flagWidthSet = false;

	// The width of both icons
	protected static int width;

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
		return R.layout.tree_view;
	}

	@Override
	public void initialize () {
		if (!flagWidthSet) {
			flagWidthSet = true;
			width = getResources().getDrawable(R.drawable.ic_collapsed).getIntrinsicWidth();
		}

		mainLayout = (LinearLayout) findViewById(R.id.tree_list);
		parentGroup = new TreeGroup(getParentNode());

		parentGroup.isCollapsed = false;
		parentGroup.createLayout(LayoutInflater.from(this), mainLayout);
	}

	/**
	 * Returns the parent node to show in this view
	 * @return the parent node
	 */
	public abstract ITreeNode getParentNode ();
}
