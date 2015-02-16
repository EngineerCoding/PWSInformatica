package com.ameling.grademanager;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.converter.IConverterJson;
import com.ameling.grademanager.converter.ViewConverter;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.parser.json.JSONObject;

import java.math.BigDecimal;

import static com.ameling.grademanager.util.ConstantKeys.KEY_CALCULATOR;
import static com.ameling.grademanager.util.ConstantKeys.KEY_NAME;

/**
 * A converter for {@link com.ameling.grademanager.GradeManager.Subject}
 */
public class SubjectConverter extends ViewConverter<GradeManager.Subject> implements IConverterJson<GradeManager.Subject, JSONObject> {

	/**
	 * The one and only instance
	 */
	protected static final SubjectConverter instance = new SubjectConverter();

	private SubjectConverter () {}

	@Override
	public int getLayout () {
		return R.layout.subject_listview;
	}

	@Override
	public void populateInflatedView (final View view, final GradeManager.Subject from) {
		// Round the number properly (the value for BigDecimal must be a String to be working properly)
		final String average = new BigDecimal(String.valueOf(from.calculator.calculateAverage())).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();

		final TextView averageGrade = (TextView) view.findViewById(R.id.averageGrade);
		averageGrade.setText(average);

		final TextView subjectName = (TextView) view.findViewById(R.id.subject_name);
		subjectName.setText(from.name);
	}

	@Override
	public GradeManager.Subject convert (final JSONObject json) {
		return new GradeManager.Subject(json.getString(KEY_NAME), CalculatorWrapper.converter.convert(json.getJSONObject(KEY_CALCULATOR)));
	}

	@Override
	public JSONObject convert (final GradeManager.Subject object) {
		final JSONObject json = new JSONObject();
		json.set(KEY_NAME, object.name);
		json.set(KEY_CALCULATOR, CalculatorWrapper.converter.convert(object.calculator));
		return json;
	}
}
