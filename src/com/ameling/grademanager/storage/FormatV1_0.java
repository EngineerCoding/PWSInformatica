package com.ameling.grademanager.storage;

import com.ameling.grademanager.util.GradeWrapper;
import com.ameling.grademanager.util.Subject;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

/**
 * A {@link Format} which is used in version 1.0 of Grade Manager
 */
public class FormatV1_0 extends Format {

	// All used keys
	private static final String KEY_SUBJECT = "subject";
	private static final String KEY_GRADES = "grades";
	private static final String KEY_GRADENAME = "grade";
	private static final String KEY_VALUE = "value";
	private static final String KEY_WEIGHTING = "weighting";
	private static final String KEY_CHILDS = "childs";

	/**
	 * Singleton instance of this class
	 */
	public static final Format instance = new FormatV1_0();

	private FormatV1_0 () {}

	@Override
	public Subject decode (final JSONObject readObject) {
		final JSONArray gradeArray = readObject.getJSONArray(KEY_GRADES);
		final Grade[] grades = new Grade[gradeArray.getSize()];

		for (int i = 0; i < grades.length; i++)
			grades[i] = recurseJsonGrade(gradeArray.getJSONObject(i));
		return new Subject(readObject.getString(KEY_SUBJECT), new Calculator(grades));
	}

	@Override
	public JSONObject encode (final Subject subject) {
		final JSONArray grades = new JSONArray();
		for (final Grade grade : subject.calculator.grades) {
			final JSONObject object = new JSONObject();
			recurseGrade(object, grade);
			grades.add(object);
		}

		final JSONObject main = new JSONObject();
		main.set(KEY_SUBJECT, subject.name);
		main.set(KEY_GRADES, grades);
		return main;
	}

	/**
	 * Recurses into the {@link Grade} object that could be a {@link GradeWrapper}
	 *
	 * @param jsonObject The object to write to
	 * @param grade      The grade that gets written
	 */
	private void recurseGrade (final JSONObject jsonObject, final Grade grade) {
		jsonObject.set(KEY_GRADENAME, grade.name);
		jsonObject.set(KEY_WEIGHTING, grade.weighting);
		if (grade.hasValue()) {
			jsonObject.set(KEY_VALUE, grade.getValue());
		} else if (grade instanceof GradeWrapper) {
			final Calculator subCalculator = ((GradeWrapper) grade).calculator;

			final JSONArray childs = new JSONArray();
			for (final Grade _grade : subCalculator.grades) {
				final JSONObject object = new JSONObject();
				recurseGrade(object, _grade);
				childs.add(object);
			}
			jsonObject.set(KEY_CHILDS, childs);
		}
	}

	/**
	 * This recurses in the child of the {@link Grade} JSON objects. This is exactly the opposite of {@link #recurseGrade(JSONObject, Grade}}
	 *
	 * @param gradeObject The JSONObject to recurse into
	 * @return A corresponding Grade object
	 */
	private Grade recurseJsonGrade (final JSONObject gradeObject) {
		final String name = gradeObject.getString(KEY_GRADENAME);
		final int weighting = gradeObject.getInt(KEY_WEIGHTING);

		if (gradeObject.has(KEY_VALUE)) {
			final Grade grade = new Grade(name, weighting);
			grade.setValue(gradeObject.getDouble(KEY_VALUE));
			return grade;
		} else { // The childs key exists
			final JSONArray childArray = gradeObject.getJSONArray(KEY_CHILDS);
			final Grade[] childGrades = new Grade[childArray.getSize()];
			for (int i = 0; i < childGrades.length; i++)
				childGrades[i] = recurseJsonGrade(childArray.getJSONObject(i));

			final GradeWrapper wrapper = new GradeWrapper(name, weighting);
			wrapper.setSubGrades(new Calculator(childGrades));
			return wrapper;
		}
	}
}
