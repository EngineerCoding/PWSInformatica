package com.ameling.grademanager.io;

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
	private static final String KEY_GRADENAME = "gradeName";
	private static final String KEY_VALUE = "value";
	private static final String KEY_WEIGHTING = "weighting";

	/**
	 * Singleton instance of this class
	 */
	public static final Format instance = new FormatV1_0();

	private FormatV1_0 () {}

	@Override
	public Subject decode (final JSONObject readObject) {
		final JSONArray gradeArray = readObject.getJSONArray(KEY_GRADES);
		final Grade[] grades = new Grade[gradeArray.getSize()];
		for (int i = 0; i < grades.length; i++) {
			final JSONObject jsonGrade = gradeArray.getJSONObject(i);
			grades[i] = new Grade(jsonGrade.getString(KEY_GRADENAME), jsonGrade.getInt(KEY_WEIGHTING));
			if (jsonGrade.has(KEY_VALUE))
				grades[i].setValue(jsonGrade.getDouble(KEY_VALUE));
		}

		return new Subject(readObject.getString(KEY_SUBJECT), new Calculator(grades));
	}

	@Override
	public JSONObject encode (final Subject subject) {
		final JSONArray gradeArray = new JSONArray();
		for (final String gradeName : subject.getGradeNames()) {
			final JSONObject jsonGrade = new JSONObject();
			jsonGrade.set(KEY_GRADENAME, gradeName);

			final Grade grade = subject.calculator.getGrade(gradeName);
			jsonGrade.set(KEY_WEIGHTING, grade.weighting);
			if (grade.hasValue())
				jsonGrade.set(KEY_VALUE, grade.getValue());

			gradeArray.add(jsonGrade);
		}

		final JSONObject subjectObject = new JSONObject();
		subjectObject.set(KEY_SUBJECT, subject.name);
		subjectObject.set(KEY_GRADES, gradeArray);
		return subjectObject;
	}
}
