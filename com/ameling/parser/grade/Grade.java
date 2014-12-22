package com.ameling.parser.grade;

import com.ameling.parser.json.IJSONFactory;
import com.ameling.parser.json.JSON;
import com.ameling.parser.json.JSONObject;

/**
 * Holds a grade and the value of it. This is based on weighting, and this is the object the
 * parser will parse into.
 *
 * @author Wesley A
 */
public class Grade {

	public static class JSONFactory implements IJSONFactory<Grade> {

		private JSONFactory () {}

		public static final JSONFactory instance = new JSONFactory();

		// Constants only used in this class
		private static final String NAME = "name";
		private static final String VALUE = "value";
		private static final String WEIGHTING = "weighting";

		@Override
		public JSON createJSON (final Grade object) {
			if (object != null) {
				final JSONObject jsonObject = new JSONObject();
				jsonObject.set(NAME, object.name);
				jsonObject.set(WEIGHTING, object.weighting);
				if (object.isSet) {
					jsonObject.set(VALUE, object.value);
				} else {
					jsonObject.setNull(VALUE);
				}

				return jsonObject;
			}
			return null;
		}

		@Override
		public Grade createInstance (final JSON json) {
			if (json != null && json instanceof JSONObject) {
				final JSONObject jsonObject = (JSONObject) json;

				final String name = jsonObject.getString(NAME);
				final int weighting = jsonObject.getInt(WEIGHTING);

				final Grade grade = new Grade(name, weighting);
				if (!jsonObject.isNull(VALUE))
					grade.setGrade(jsonObject.getDouble(VALUE));

				return grade;
			}
			return null;
		}
	}

	/**
	 * The name of this grade
	 */
	public final String name;

	/**
	 * The weighting of this grade
	 */
	public final int weighting;

	/**
	 * Creates a new grade with the name and weighting
	 *
	 * @param name      The name of this grade
	 * @param weighting The weigthing of this grade
	 */
	public Grade (final String name, final int weighting) {
		this.name = name;
		this.weighting = weighting;
	}

	/**
	 * This value is used {@link Calculator}
	 */
	protected double value;

	/**
	 * A boolean flag whether it is set or not. Used in {@link #reset}, {@link #setGrade(double)} and {@link Calculator}
	 */
	protected boolean isSet = false;

	/**
	 * Sets the grade value to this value.
	 *
	 * @param grade The value to set to
	 */
	public void setGrade (final double grade) {
		value = grade;
		if (!isSet)
			isSet = true;
	}

	/**
	 * Resets this grade's value (it is not set after calling this)
	 */
	public void reset () {
		isSet = false;
	}

	@Override
	public String toString () {
		return JSONFactory.instance.createJSON(this).toString();
	}
}
