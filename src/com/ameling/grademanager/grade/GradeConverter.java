package com.ameling.grademanager.grade;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.converter.IConverterJson;
import com.ameling.grademanager.converter.ViewConverter;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONObject;

import static com.ameling.grademanager.util.ConstantKeys.KEY_CALCULATOR;
import static com.ameling.grademanager.util.ConstantKeys.KEY_NAME;
import static com.ameling.grademanager.util.ConstantKeys.KEY_VALUE;
import static com.ameling.grademanager.util.ConstantKeys.KEY_WEIGHTING;

public class GradeConverter extends ViewConverter<Grade> implements IConverterJson<Grade, JSONObject>, View.OnFocusChangeListener {

	public static final GradeConverter instance = new GradeConverter();

	private GradeConverter () {}

	@Override
	public int getLayout () {
		return R.layout.grade_listview;
	}

	@Override
	public void populateInflatedView (final View view, final Grade from) {
		if (from instanceof GradeWrapper) {
			((Button) view.findViewById(R.id.button_add_formula)).setText(view.getContext().getString(R.string.edit_formula));
			view.findViewById(R.id.grade_value).setEnabled(false);
		} else if (from.hasValue()) {
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
