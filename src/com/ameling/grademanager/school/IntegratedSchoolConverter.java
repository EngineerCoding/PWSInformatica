package com.ameling.grademanager.school;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.converter.ObjectAdapter;

public class IntegratedSchoolConverter extends ObjectAdapter.ViewConverter<IntegratedSchool> {

	public static final IntegratedSchoolConverter instance = new IntegratedSchoolConverter();

	private IntegratedSchoolConverter () {}

	@Override
	public int getLayout () {
		return R.layout.school_listview;
	}

	@Override
	public void populateInflatedView (final View view, final IntegratedSchool from) {
		final TextView school_name = (TextView) view.findViewById(R.id.school_name);
		school_name.setText(from.name);

		final TextView school_country = (TextView) view.findViewById(R.id.school_country);
		school_country.setText(from.country);

		final TextView school_city = (TextView) view.findViewById(R.id.school_city);
		school_city.setText(from.city);
	}
}
