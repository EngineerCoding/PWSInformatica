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
