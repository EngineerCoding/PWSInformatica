package com.grademanager.app.school;

import android.view.View;
import android.widget.TextView;
import com.grademanager.app.R;
import com.grademanager.app.converter.ObjectAdapter;

/**
 * This class is a {@link ObjectAdapter.ViewConverter} to convert an {@link IntegratedSchool} object to a listview View.
 */
public class IntegratedSchoolConverter extends ObjectAdapter.ViewConverter<IntegratedSchool> {

	/**
	 * The one and only instance of this singleton
	 */
	public static final IntegratedSchoolConverter instance = new IntegratedSchoolConverter();

	/**
	 * A private constructor because this is a singleton
	 */
	private IntegratedSchoolConverter () {}

	@Override
	public int getLayout () {
		return R.layout.school_listview;
	}

	@Override
	public void populateInflatedView (final View view, final IntegratedSchool from) {
		// Set the school name
		final TextView school_name = (TextView) view.findViewById(R.id.school_name);
		school_name.setText(from.name);
		// Set the school's country
		final TextView school_country = (TextView) view.findViewById(R.id.school_country);
		school_country.setText(from.country);
		// Set the school's city
		final TextView school_city = (TextView) view.findViewById(R.id.school_city);
		school_city.setText(from.city);
	}
}
