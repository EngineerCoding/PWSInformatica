package com.grademanager.app.tree;

import android.view.View;

/**
 * This interface indicates that an object is a node of a tree. This tree is created in the {@link com.grademanager.app.tree.TreeActivity} and when this
 * object {@link #hasChildNodes()}, it will be turned into a {@link com.grademanager.app.tree.TreeGroup}
 */
public interface ITreeNode {

	/**
	 * Checks if this object has nodes to show or is a node itself
	 *
	 * @return True if this object has child nodes or false if it is a child node
	 */
	public boolean hasChildNodes ();

	/**
	 * Only gets called when {@link #hasChildNodes()} returns true
	 *
	 * @return The child-nodes of this object
	 */
	public ITreeNode[] getChildNodes ();

	/**
	 * Gets the inflatable resource for this object, whether it is or has child-nodes
	 *
	 * @return The inflatable resource
	 */
	public int getInflatableResource ();

	/**
	 * Populate the view of the inflatable resource
	 *
	 * @param view The view which is created from {@link #getInflatableResource()}
	 */
	public void populateView (final View view);

}
