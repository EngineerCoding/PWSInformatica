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

package com.grademanager.app.grade;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.grademanager.app.R;
import com.grademanager.app.converter.JsonConverter;
import com.grademanager.app.converter.ObjectAdapter;
import com.grademanager.parser.grade.Grade;
import com.grademanager.parser.json.JSONObject;

import static com.grademanager.app.util.ConstantKeys.KEY_CALCULATOR;
import static com.grademanager.app.util.ConstantKeys.KEY_NAME;
import static com.grademanager.app.util.ConstantKeys.KEY_VALUE;
import static com.grademanager.app.util.ConstantKeys.KEY_WEIGHTING;

/**
 * This is converter is not just a simple {@link com.grademanager.app.converter.ObjectAdapter.ViewConverter}, but also takes care of the Json converting with
 * {@link JsonConverter}
 */
public class GradeConverter extends ObjectAdapter.ViewConverter<Grade> implements JsonConverter<Grade, JSONObject>, View.OnFocusChangeListener {

	/**
	 * The one and only instance
	 */
	public static final GradeConverter instance = new GradeConverter();

	/**
	 * This is a singleton class, so a private constructor
	 */
	private GradeConverter () {}

	@Override
	public int getLayout () {
		return R.layout.grade_listview;
	}

	@Override
	public void populateInflatedView (final View view, final Grade from) {
		if (from instanceof GradeWrapper) {
			// Set the appropriate button title and remove the value field
			((Button) view.findViewById(R.id.button_add_formula)).setText(view.getContext().getString(R.string.edit_formula));
			view.findViewById(R.id.grade_value).setVisibility(View.GONE);
		} else if (from.hasValue()) {
			// Set the value of the grade and the focus-listener
			final EditText gradeValue = (EditText) view.findViewById(R.id.grade_value);
			gradeValue.setText(String.valueOf(from.getValue()));
			gradeValue.setOnFocusChangeListener(this);
		}

		final TextView gradeName = (TextView) view.findViewById(R.id.grade_name);
		gradeName.setText(from.name);
	}

	@Override
	public void onFocusChange (final View view, final boolean hasFocus) {
		if (hasFocus && view instanceof EditText) {
			final EditText edit = (EditText) view;
			edit.setCursorVisible(true);
		}
	}

	@Override
	public Grade convert (final JSONObject json) {
		if (json.has(KEY_CALCULATOR)) {
			final GradeWrapper wrapper = new GradeWrapper(json.getString(KEY_NAME), json.getInt(KEY_WEIGHTING));
			wrapper.setSubGrades(CalculatorWrapper.converter.convert(json.getJSONObject(KEY_CALCULATOR)));
			return wrapper;
		} else {
			final Grade grade = new Grade(json.getString(KEY_NAME), json.getInt(KEY_WEIGHTING));
			if (json.has(KEY_VALUE))
				grade.setValue(json.getDouble(KEY_VALUE));
			return grade;
		}
	}

	@Override
	public JSONObject convert (final Grade object) {
		final JSONObject json = new JSONObject();
		if (object instanceof GradeWrapper) {
			json.set(KEY_NAME, object.name);
			json.set(KEY_WEIGHTING, object.weighting);
			json.set(KEY_CALCULATOR, CalculatorWrapper.converter.convert(((GradeWrapper) object).calculator));
		} else {
			json.set(KEY_NAME, object.name);
			json.set(KEY_WEIGHTING, object.weighting);
			if (object.hasValue())
				json.set(KEY_VALUE, object.getValue());
		}
		return json;
	}
}
