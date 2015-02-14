package com.ameling.grademanager.ui.adapter;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.parser.grade.Grade;

public class GradeConverter extends ViewConverter<Grade> implements View.OnFocusChangeListener {

	public static final ViewConverter<Grade> instance = new GradeConverter();

	private GradeConverter () {}

	@Override
	public int getLayout () {
		return R.layout.grade_listview;
	}

	@Override
	public void populateInflatedView (final View view, final Grade from) {
		if (from.hasValue()) {
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
}
