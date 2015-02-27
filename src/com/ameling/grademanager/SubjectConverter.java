package com.ameling.grademanager;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.converter.JsonConverter;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.parser.json.JSONObject;

import java.math.BigDecimal;

import static com.ameling.grademanager.util.ConstantKeys.KEY_CALCULATOR;
import static com.ameling.grademanager.util.ConstantKeys.KEY_NAME;

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
		// Round the number properly (the value for BigDecimal must be a String to be working properly)
		final String average = new BigDecimal(String.valueOf(from.calculator.calculateAverage())).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
		// Set the average grade
		final TextView averageGrade = (TextView) view.findViewById(R.id.averageGrade);
		averageGrade.setText(average);
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
}
