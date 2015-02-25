package com.ameling.grademanager.tree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ameling.grademanager.R;

public final class TreeGroup {

	private final ITreeNode parentNode;
	protected boolean isCollapsed = true;

	protected final ITreeNode[] childNodes;
	protected final TreeGroup[] childGroups;

	protected TreeGroup (final ITreeNode parentNode) {
		this.parentNode = parentNode;
		final ITreeNode[] subNodes = parentNode.getChildNodes();

		childNodes = new ITreeNode[subNodes.length];
		childGroups = new TreeGroup[subNodes.length];

		for (int i = 0; i < subNodes.length; i++) {
			if (subNodes[i].hasChildNodes()) {
				childGroups[i] = new TreeGroup(subNodes[i]);
			} else {
				childNodes[i] = subNodes[i];
			}
		}
	}

	protected void createLayout (final LayoutInflater inflater, final ViewGroup parent) {
		// The main layout for this group
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.treegroup, null, false);

		// Set the correct image
		((ImageView) layout.findViewById(R.id.group_icon)).setImageResource(isCollapsed ? R.drawable.ic_collapsed : R.drawable.ic_expanded);

		// Add the title for this group view
		final View titleView = inflater.inflate(parentNode.getInflatableResource(), null, false);
		parentNode.populateView(titleView);
		((LinearLayout) layout.findViewById(R.id.inflatable_content)).addView(titleView);

		// Add the children views
		final LinearLayout childrenList = (LinearLayout) layout.findViewById(R.id.inflatable_children);
		childrenList.setPadding(50, 0, 0, 0);

		for (int i = 0; i < childNodes.length; i++) {
			final ITreeNode childNode = childNodes[i];
			if (childNode != null) {
				final View view = inflater.inflate(childNode.getInflatableResource(), null, false);
				childNode.populateView(view);
				view.setPadding(view.getPaddingLeft() + TreeActivity.width + 20, view.getPaddingTop(), view.getPaddingBottom(), view.getPaddingRight());
				childrenList.addView(view);
			} else {
				childGroups[i].createLayout(inflater, childrenList);
			}
		}

		// Set the proper visibility
		if (isCollapsed)
			childrenList.setVisibility(View.GONE);

		// Add the click listener to the title
		layout.findViewById(R.id.group_title).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (final View view) {
				isCollapsed = !isCollapsed;
				((ImageView) layout.findViewById(R.id.group_icon)).setImageResource(isCollapsed ? R.drawable.ic_collapsed : R.drawable.ic_expanded);

				// Set the proper visibility
				childrenList.setVisibility(isCollapsed ? View.GONE : View.VISIBLE);
			}
		});

		parent.addView(layout);
	}
}
