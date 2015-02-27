package com.ameling.grademanager.converter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * A generic Adapter which uses a {@link ViewConverter}
 *
 * @param <T> The object which gets converted
 */
public class ObjectAdapter<T> extends ArrayAdapter<T> {

	public static abstract class ViewConverter<T> {

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
			return new ObjectAdapter<T>(context, this, objects);
		}
	}

	/**
	 * The converter for this class
	 */
	private final ViewConverter<T> converter;

	/**
	 * Creates a new adapter which uses the {@link ViewConverter} to get the layout resource and populates that view
	 *
	 * @param context   The activity's Context
	 * @param converter The converter which converts T to a view
	 * @param objects   All objects to load initially
	 */
	public ObjectAdapter (final Context context, final ViewConverter<T> converter, final List<T> objects) {
		super(context, converter.getLayout(), objects);
		this.converter = converter;
		setNotifyOnChange(true);
	}

	@Override
	public View getView (final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			// Inflate the view from the converter
			final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			convertView = layoutInflater.inflate(converter.getLayout(), parent, false);
		}
		// Populate the view from the converter
		converter.populateInflatedView(convertView, getItem(position));
		return convertView;
	}
}
