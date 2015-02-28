package com.grademanager.app.tree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.grademanager.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This object represents a {@link ITreeNode} as a group. That means that {@link ITreeNode#hasChildNodes()} was true.
 * This class collects all the children nodes and can create a {@link LinearLayout} for this group. This also handles the click
 * events so that the the group can be collapsed and expanded.
 */
public final class TreeGroup implements View.OnClickListener {

	/**
	 * The tag of the LinearLayout to recognise it
	 */
	private static final String TAG = TreeGroup.class.toString();

	/**
	 * The parent node where this class is based on
	 */
	private final ITreeNode parentNode;

	/**
	 * A flag to determine if the collapsible/expandable image should be drawn
	 */
	private final boolean isCollapsible;

	/**
	 * A flag to determine whether this group is collapsed or not
	 */
	protected boolean isCollapsed = true;

	/**
	 * All child-nodes from the {@link #parentNode}. When a node is null in this array it can be found in {@link #childGroups}
	 */
	protected final ITreeNode[] childNodes;

	/**
	 * All child-nodes which area group from the {@link #parentNode}. When a node is null in this array it can be found in {@link #childNodes}
	 */
	protected final TreeGroup[] childGroups;

	/**
	 * A listener for the child items (not group, just regular nodes)
	 */
	private final View.OnLongClickListener childLongClickListener;

	/**
	 * The latest layout that got created in {@link #createLayout(LayoutInflater, ViewGroup)}
	 */
	protected LinearLayout treeLayout = null;

	/**
	 * Creates a group from the parent node
	 *
	 * @param parentNode             The parent node to create the group from
	 * @param childLongClickListener This listener is used on child nodes, can be null
	 * @param isCollapsible          Sets the {@link #isCollapsible} flag
	 */
	protected TreeGroup (final ITreeNode parentNode, final View.OnLongClickListener childLongClickListener, boolean isCollapsible) {
		this.parentNode = parentNode;
		this.childLongClickListener = childLongClickListener;
		this.isCollapsible = isCollapsible;

		// Get the child nodes
		final ITreeNode[] subNodes = parentNode.getChildNodes();

		// Put the nodes in the appropriate array
		childNodes = new ITreeNode[subNodes.length];
		childGroups = new TreeGroup[subNodes.length];
		for (int i = 0; i < subNodes.length; i++) {
			if (subNodes[i].hasChildNodes()) {
				childGroups[i] = new TreeGroup(subNodes[i], childLongClickListener);
			} else {
				childNodes[i] = subNodes[i];
			}
		}
	}

	/**
	 * Creates a new group from the parent node, but the {@link #isCollapsible} flag is always set to true
	 *
	 * @param parentNode             The parent node to create the group from
	 * @param childLongClickListener This listener is used on child nodes, can be null
	 */
	protected TreeGroup (final ITreeNode parentNode, final View.OnLongClickListener childLongClickListener) {
		this(parentNode, childLongClickListener, true);
	}

	/**
	 * Creates a new layout from this group. The basic layout can be found in res/layout/treegroup.xml, this has 2 elements:
	 * <ul>
	 * <li>The title: this is the image and the inflatable resource</li>
	 * <li>The children-layout: All children get added here</li>
	 * </ul>
	 * This also attaches to the parent.
	 *
	 * @param inflater An inflater to inflate resources
	 * @param parent   The parent to attach to
	 */
	protected void createLayout (final LayoutInflater inflater, final ViewGroup parent) {
		// The main layout for this group
		treeLayout = (LinearLayout) inflater.inflate(R.layout.treegroup, null, false);
		treeLayout.setTag(TAG);

		// Add the title for this group view
		final View titleView = inflater.inflate(parentNode.getInflatableResource(), null, false);
		parentNode.populateView(titleView);
		((LinearLayout) treeLayout.findViewById(R.id.inflatable_content)).addView(titleView);

		// Add the children views
		final LinearLayout childrenList = (LinearLayout) treeLayout.findViewById(R.id.inflatable_children);
		childrenList.setPadding(50, 0, 0, 0);

		for (int i = 0; i < childNodes.length; i++) {
			final ITreeNode childNode = childNodes[i];
			if (childNode != null) {
				final View view = inflater.inflate(childNode.getInflatableResource(), null, false);
				childNode.populateView(view);
				view.setPadding(view.getPaddingLeft() + TreeActivity.width + 20, view.getPaddingTop(), view.getPaddingBottom(), view.getPaddingRight());
				childrenList.addView(view);

				if (childLongClickListener != null)
					view.setOnLongClickListener(childLongClickListener);
			} else {
				childGroups[i].createLayout(inflater, childrenList);
			}
		}

		// Set the proper visibility
		if (isCollapsed && isCollapsible)
			childrenList.setVisibility(View.GONE);

		if (isCollapsible) {
			// Add the click listener to the title
			treeLayout.findViewById(R.id.group_title).setOnClickListener(this);

			// Set the correct image
			((ImageView) treeLayout.findViewById(R.id.group_icon)).setImageResource(isCollapsed ? R.drawable.ic_collapsed : R.drawable.ic_expanded);
		} else {
			// Remove the collapsible image
			final LinearLayout titleLayout = (LinearLayout) treeLayout.findViewById(R.id.group_title);
			titleLayout.removeView(titleLayout.findViewById(R.id.group_icon));
		}

		parent.addView(treeLayout);
	}

	/**
	 * This is a proxy to {@link #getChildNodes(LinearLayout)} to make the recursion properly work
	 *
	 * @return All child views which are not a group
	 */
	public View[] getChildViews () {
		final List<View> children = getChildNodes(treeLayout);
		return children.toArray(new View[children.size()]);
	}

	/**
	 * Retrieves all child elements from the given Layout, this uses recursion to achieve that.
	 *
	 * @param treeGroup The group to check in
	 * @return A List of child-views
	 */
	private static List<View> getChildNodes (final LinearLayout treeGroup) {
		final LinearLayout childrenList = (LinearLayout) treeGroup.findViewById(R.id.inflatable_children);
		final List<View> childViews = new ArrayList<>();

		for (int i = 0; i < childrenList.getChildCount(); i++) {
			final View child = childrenList.getChildAt(i);
			if (child instanceof LinearLayout && child.getTag().equals(TAG)) {
				// This our tag so we need to get the children of this view
				childViews.addAll(getChildNodes((LinearLayout) child));
			} else {
				// Simply add the child
				childViews.add(child);
			}
		}
		return childViews;
	}

	/**
	 * Counts the groups in this group, also includes sub-sub-groups ;)
	 *
	 * @return the amount of groups stored in all groups in this group
	 */
	public int countGroups () {
		int amount = 0;
		for (final TreeGroup group : childGroups) {
			if (group != null) {
				amount += 1;
				amount += group.countGroups();
			}
		}
		return amount;
	}

	@Override
	public void onClick (final View view) {
		isCollapsed = !isCollapsed;
		((ImageView) view.findViewById(R.id.group_icon)).setImageResource(isCollapsed ? R.drawable.ic_collapsed : R.drawable.ic_expanded);

		// Set the proper visibility
		treeLayout.findViewById(R.id.inflatable_children).setVisibility(isCollapsed ? View.GONE : View.VISIBLE);
	}
}
