package com.ameling.grademanager.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

public abstract class ViewConverter<T> {

	/**
	 * Gets the layout for one single object
	 *
	 * @return The layout
	 */
	public abstract int getLayout ();

	/**
	 * Populates the view with data from the object
	 *
	 * @param view The view to be populated
	 * @param from The data which the view gets populated with
	 */
	public abstract void populateInflatedView (final View view, final T from);

	/**
	 * Creates an {@link android.widget.ArrayAdapter} with this IViewConverter
	 *
	 * @param context The context of the app
	 * @param objects The objects to create the adapter with
	 * @return A simple adapter
	 */
	public ObjectAdapter<T> createAdapter (final Context context, final List<T> objects) {
		return new ObjectAdapter<>(context, this, objects);
	}
}
