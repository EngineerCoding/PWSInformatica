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

package com.grademanager.app.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * This TextWatcherProxy is used when a class is only interested in the {@link TextWatcher#afterTextChanged} method
 */
public class TextWatcherProxy implements TextWatcher {

	/**
	 * The interface which the parent will called from
	 */
	public static interface ITextWatcher {
		public void afterTextChanged(final Editable editable);
	}

	/**
	 * The parent
	 */
	private final ITextWatcher parent;

	/**
	 * This constructor takes a parent to call to
	 *
	 * @param parent The parent which will actually do something
	 */
	public TextWatcherProxy (final ITextWatcher parent) {
		this.parent = parent;
		if (parent == null)
			throw new NullPointerException();
	}

	@Override
	public void beforeTextChanged (final CharSequence charSequence, final int i, final int i1, final int i2) {

	}

	@Override
	public void onTextChanged (final CharSequence charSequence, final int i, final int i1, final int i2) {

	}

	@Override
	public void afterTextChanged (final Editable editable) {
		parent.afterTextChanged(editable);
	}
}
