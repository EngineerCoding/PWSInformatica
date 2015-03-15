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

package com.grademanager.app;

import android.view.View;
import android.widget.TextView;
import com.grademanager.app.converter.JsonConverter;
import com.grademanager.app.converter.ObjectAdapter;
import com.grademanager.app.grade.CalculatorWrapper;
import com.grademanager.parser.json.JSONObject;

import java.math.BigDecimal;

import static com.grademanager.app.util.ConstantKeys.KEY_CALCULATOR;
import static com.grademanager.app.util.ConstantKeys.KEY_NAME;

/**
 * A converter for {@link SubjectManager.Subject}
 */
public class SubjectConverter extends ObjectAdapter.ViewConverter<SubjectManager.Subject> implements JsonConverter<SubjectManager.Subject, JSONObject> {

	/**
	 * The one and only instance
	 */
	public static final SubjectConverter instance = new SubjectConverter();

	private SubjectConverter () {}

	@Override
	public int getLayout () {
		return R.layout.subject_listview;
	}

	@Override
	public void populateInflatedView (final View view, final SubjectManager.Subject from) {
		// Set the average grade
		final TextView averageGrade = (TextView) view.findViewById(R.id.average_grade);
		averageGrade.setText(formatAverage(from.calculator.calculateAverage()));
		// Set the subject name
		final TextView subjectName = (TextView) view.findViewById(R.id.subject_name);
		subjectName.setText(from.name);
	}

	@Override
	public SubjectManager.Subject convert (final JSONObject json) {
		return new SubjectManager.Subject(json.getString(KEY_NAME), CalculatorWrapper.converter.convert(json.getJSONObject(KEY_CALCULATOR)));
	}

	@Override
	public JSONObject convert (final SubjectManager.Subject object) {
		final JSONObject json = new JSONObject();
		json.set(KEY_NAME, object.name);
		json.set(KEY_CALCULATOR, CalculatorWrapper.converter.convert(object.calculator));
		return json;
	}

	/**
	 * Creates a properly rounded {@link String} of the given value
	 *
	 * @param average The average to round
	 * @return A {@link String} representing this average
	 */
	public static String formatAverage (final double average) {
		final BigDecimal bigAverage = new BigDecimal(String.valueOf(average));
		return bigAverage.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
	}
}
