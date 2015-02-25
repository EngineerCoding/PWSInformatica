package com.ameling.grademanager.tree;

import android.view.View;

public interface ITreeNode {

	public boolean hasChildNodes ();

	public ITreeNode[] getChildNodes ();

	public int getInflatableResource ();

	public void populateView (final View view);

}
