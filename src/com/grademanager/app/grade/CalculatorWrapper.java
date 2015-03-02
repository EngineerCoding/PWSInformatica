package com.grademanager.app.grade;

import com.grademanager.app.converter.JsonConverter;
import com.grademanager.parser.grade.Calculator;
import com.grademanager.parser.grade.ExpressionCalculator;
import com.grademanager.parser.grade.Grade;
import com.grademanager.parser.json.JSONArray;
import com.grademanager.parser.json.JSONObject;

import static com.grademanager.app.util.ConstantKeys.KEY_EXPRESSION;
import static com.grademanager.app.util.ConstantKeys.KEY_GRADES;

/**
 * A wrapper for all currently implemented {@link Calculator} objects. This simply holds the expression which is saved when the subjects get written to an internal private file.
 * To make it more organized, this class also contains its {@link JsonConverter} to convert from and to a {@link JSONObject}
 */
public class CalculatorWrapper extends Calculator implements Cloneable {

	/**
	 * The converter for this class
	 */
	public static final JsonConverter<CalculatorWrapper, JSONObject> converter = new JsonConverter<CalculatorWrapper, JSONObject>() {
		@Override
		public CalculatorWrapper convert (final JSONObject json) {
			final JSONArray array = json.getJSONArray(KEY_GRADES);

			// Collect all grades
			final Grade[] grades = new Grade[array.getSize()];
			for (int i = 0; i < array.getSize(); i++)
				grades[i] = GradeConverter.instance.convert(array.getJSONObject(i));
			// Create a calculator with those grades
			return new CalculatorWrapper(grades, json.getString(KEY_EXPRESSION));
		}

		@Override
		public JSONObject convert (final CalculatorWrapper object) {
			// Convert all grades and put them in an array
			final JSONArray array = new JSONArray();
			for (final Grade grade : object.grades)
				array.add(GradeConverter.instance.convert(grade));

			// Set the array and its expression to a JSONObject
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

	@Override
	public double calculateGrade (final Grade gradeToCalculate, final double average) {
		// Check if the grade is in a wrapper
		if (super.getGrade(gradeToCalculate.name) == null) {
			// This grade is in a grade wrapper, lets find the grade
			for (final Grade grade : grades) {
				if (grade instanceof GradeWrapper) {
					// Found the grade, calculate the average of this wrapper
					final GradeWrapper wrapper = (GradeWrapper) grade;
					if (wrapper.hasGrade(gradeToCalculate))
						return wrapper.calculator.calculateGrade(gradeToCalculate, super.calculateGrade(wrapper, average));
				}
			}
		}
		// Simply let the super class do the work, since it is the default
		return super.calculateGrade(gradeToCalculate, average);
	}

	@Override
	public Grade getGrade (final String name) {
		final Grade grade = super.getGrade(name);
		if (grade != null)
			return grade; // Just simply return it because it has been found

		// Do a custom search on the wrappers, because the regular ones should have been found
		for (final Grade subGrade : grades) {
			if (subGrade instanceof GradeWrapper) {
				final CalculatorWrapper subCalculator = ((GradeWrapper) subGrade).calculator;
				if (subCalculator != null) {
					final Grade foundGrade = subCalculator.getGrade(name);
					if (foundGrade != null)
						return foundGrade;
				}
			}
		}
		return grade;
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

	@Override
	public CalculatorWrapper clone () {
		final Calculator calculator = super.clone();
		return new CalculatorWrapper(calculator.grades.toArray(new Grade[calculator.grades.size()]), expression);
	}
}
