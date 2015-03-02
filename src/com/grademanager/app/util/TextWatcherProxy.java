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
