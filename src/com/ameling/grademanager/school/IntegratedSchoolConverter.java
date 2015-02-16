package com.ameling.grademanager.school;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.converter.ViewConverter;

public class IntegratedSchoolConverter extends ViewConverter<IntegratedSchool> implements AdapterView.OnItemClickListener {

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

	@Override
	public void onItemClick (final AdapterView<?> adapterView, final View view, final int position, final long id) {
		@SuppressWarnings ("unchecked") final ArrayAdapter<IntegratedSchool> schoolArrayAdapter = (ArrayAdapter<IntegratedSchool>) adapterView.getAdapter();
		showPopup(schoolArrayAdapter.getItem(position));
	}

	/**
	 * Shows the popup with the given school
	 *
	 * @param school The popup to show from
	 */
	public void showPopup (final IntegratedSchool school) {

	}
}
