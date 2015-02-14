package com.ameling.grademanager.ui.adapter;

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

	/**
	 * The converter for this class
	 */
	private final ViewConverter<T> converter;

	protected ObjectAdapter (final Context context, final ViewConverter<T> converter, final List<T> objects) {
		super(context, converter.getLayout(), objects);
		this.converter = converter;
		setNotifyOnChange(true);
	}

	@Override
	public View getView (final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			convertView = layoutInflater.inflate(converter.getLayout(), parent, false);
		}

		converter.populateInflatedView(convertView, getItem(position));
		return convertView;
	}
}
