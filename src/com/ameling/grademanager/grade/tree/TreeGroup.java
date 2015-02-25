package com.ameling.grademanager.grade.tree;

import android.view.View;

public final class TreeGroup {

	private final ITreeNode parentNode;
	protected boolean isCollapsed = true;

	protected final ITreeNode[] childNodes;
	protected final TreeGroup[] childGroups;
	protected final int[] childIDs;

	protected final int indentation;

	protected TreeGroup (final ITreeNode parentNode, final TreeAdapter.IdCounter idCounter) {
		this.parentNode = parentNode;
		final ITreeNode[] subNodes = parentNode.getChildNodes();

		childNodes = new ITreeNode[subNodes.length];
		childGroups = new TreeGroup[subNodes.length];
		childIDs = new int[subNodes.length];

		indentation = idCounter.nextIndent();

		for (int i = 0; i < subNodes.length; i++) {
			childIDs[i] = idCounter.nextID();
			if (subNodes[i].hasChildNodes()) {
				childGroups[i] = new TreeGroup(subNodes[i], idCounter);
			} else {
				childNodes[i] = subNodes[i];
			}
		}
	}

	public Object getItem (final int id) {
		if (containsID(id)) {
			for (int i = 0; i < childIDs.length; i++) {
				final TreeGroup group = childGroups[i];
				if (id == childIDs[i])
					return group != null ? group : childNodes[i];

				if (group != null && group.containsID(id))
					return group.getItem(id);
			}
		}
		return null;
	}

	private boolean containsHardID (final int id) {
		for (final int childID : childIDs)
			if (childID == id)
				return true;
		return false;
	}

	/**
	 * Checks if any child contains this id or this group has the id
	 *
	 * @param id The id to check for
	 * @return Whether it contains the id or not
	 */
	private boolean containsID(final int id) {
		for (final TreeGroup group : childGroups)
			if (group != null && group.containsID(id))
				return true;
		return containsHardID(id);
	}

	public TreeGroup getParent (final int id) {
		if (containsID(id)) {
			if (containsHardID(id))
				return this;
			for (final TreeGroup group : childGroups)
				if (group != null && group.containsID(id))
					return group;
		}
		return null;
	}

	protected int getIndentation(final int id) {
		return getParent(id).indentation;
	}

	public int getInflatableResource () {
		return parentNode.getInflatableResource();
	}

	public void populateView (final View view) {
		parentNode.populateView(view);
	}


}
