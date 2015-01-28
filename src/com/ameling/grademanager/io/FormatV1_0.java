package com.ameling.grademanager.io;

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


	protected FormatV1_0() {}

	@Override
	public Subject decode(final JSONObject readObject) {
		return null;
	}

	@Override
	public JSONObject encode(final Subject subject) {
		final JSONArray gradeArray = new JSONArray(); // Array for all the grades
		for (final String gradeName : subject.getGradeNames()) {
			final JSONObject gradeObject = new JSONObject();
			gradeObject.set(KEY_GRADENAME, gradeName);

			final Grade grade = subject.calculator.getGrade(gradeName);
			gradeObject.set(KEY_WEIGHTING, grade.weighting);
			if (grade.hasValue())
				gradeObject.set(KEY_VALUE, 0);

			gradeArray.add(gradeObject);
		}

		final JSONObject subjectObject = new JSONObject();
		subjectObject.set(KEY_SUBJECT, subject.name);
		subjectObject.set(KEY_GRADES, gradeArray);
		return subjectObject;
	}
}
