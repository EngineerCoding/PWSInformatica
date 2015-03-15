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
