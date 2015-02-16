package com.ameling.grademanager.grade;

import com.ameling.grademanager.converter.IConverterJson;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

import static com.ameling.grademanager.util.ConstantKeys.KEY_EXPRESSION;
import static com.ameling.grademanager.util.ConstantKeys.KEY_GRADES;

public class CalculatorWrapper extends Calculator {

	/**
	 * The converter for this class
	 */
	public static final IConverterJson<CalculatorWrapper, JSONObject> converter = new IConverterJson<CalculatorWrapper, JSONObject>() {
		@Override
		public CalculatorWrapper convert (final JSONObject json) {
			final JSONArray array = json.getJSONArray(KEY_GRADES);
			final Grade[] grades = new Grade[array.getSize()];

			for (int i = 0; i < array.getSize(); i++)
				grades[i] = GradeConverter.instance.convert(array.getJSONObject(i));

			return new CalculatorWrapper(grades, json.getString(KEY_EXPRESSION));
		}

		@Override
		public JSONObject convert (final CalculatorWrapper object) {
			final JSONArray array = new JSONArray();
			for (final Grade grade : object.grades)
				array.add(GradeConverter.instance.convert(grade));

			final JSONObject json = new JSONObject();
			json.set(KEY_GRADES, array);
			json.set(KEY_EXPRESSION, object.expression);
			return json;
		}
	};

	/**
	 * The expression used
	 */
	public final String expression;

	/**
	 * Creates a normal {@link }Calculator}
	 *
	 * @param grades     The grades for the Calculator
	 * @param expression The expression where the calculator is made from (and thus the grades)
	 */
	public CalculatorWrapper (final Grade[] grades, final String expression) {
		super(grades);
		this.expression = expression;
	}

	/**
	 * Creates a {@link ExpressionCalculator}
	 *
	 * @param expression The expression to use
	 */
	public CalculatorWrapper (final String expression) {
		this(getGrades(expression), expression);
	}

	/**
	 * Creates the actual {@link ExpressionCalculator}
	 *
	 * @param expression The expression to use
	 * @return A list of {@link Grade} objects to create a normal calculator with
	 */
	private static Grade[] getGrades (final String expression) {
		final Calculator calculator = new ExpressionCalculator(expression);
		return calculator.grades.toArray(new Grade[calculator.grades.size()]);
	}
}
