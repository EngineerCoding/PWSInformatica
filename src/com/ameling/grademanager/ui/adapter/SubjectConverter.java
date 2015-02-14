package com.ameling.grademanager.ui.adapter;

import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.util.Subject;

import java.math.BigDecimal;

/**
 * A converter for {@link com.ameling.grademanager.util.Subject}
 */
public class SubjectConverter extends ViewConverter<Subject> {

	/**
	 * The one and only instance
	 */
	public static final ViewConverter<Subject> instance = new SubjectConverter();

	private SubjectConverter () {}

	@Override
	public int getLayout () {
		return R.layout.subject_listview;
	}

	@Override
	public void populateInflatedView (final View view, final Subject from) {
		// Round the number properly (the value for BigDecimal must be a String to be working properly)
		final String average = new BigDecimal(String.valueOf(from.calculator.calculateAverage())).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();

		final TextView averageGrade = (TextView) view.findViewById(R.id.averageGrade);
		averageGrade.setText(average);

		final TextView subjectName = (TextView) view.findViewById(R.id.subject_name);
		subjectName.setText(from.name);
	}
}
