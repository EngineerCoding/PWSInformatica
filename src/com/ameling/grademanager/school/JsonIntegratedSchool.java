package com.ameling.grademanager.school;

import android.content.Context;
import com.ameling.grademanager.R;
import com.ameling.grademanager.util.GradeWrapper;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

public class JsonIntegratedSchool extends IntegratedSchool {

	private static final String KEY_PARENT = "parent";
	private static final String KEY_SUBJECT = "subject";
	private static final String KEY_FORMULA = "formula";
	private static final String KEY_CHILDS = "childs";
	private static final String KEY_SUBJECTS = KEY_SUBJECT + "s";
	private static final String PREFIX_SUBJECT = KEY_SUBJECT + "_";

	public static class JsonClassLevel extends ClassLevel {

		public final String name;
		private ClassLevel parentClassLevel;

		private final String[] subjects;
		private final Calculator[] calculators;

		public JsonClassLevel (final JSONObject object, final JsonIntegratedSchool parentSchool, final Context context) {
			if (object == null)
				throw new NullPointerException();

			if (object.has(KEY_PARENT))
				this.parentClassLevel = parentSchool.getClassLevel(object.getString(KEY_PARENT));

			name = object.getString(KEY_NAME);
			final JSONArray subjectArray = object.getJSONArray(KEY_SUBJECTS);

			subjects = new String[subjectArray.getSize()];
			calculators = new Calculator[subjectArray.getSize()];

			// Decode the subjects
			for (int i = 0; i < subjectArray.getSize(); i++) {
				final JSONObject subject = subjectArray.getJSONObject(i);
				subjects[i] = subject.getString(KEY_SUBJECT);

				// Try to localize the subject name
				try {
					int id = R.string.class.getField(PREFIX_SUBJECT + subjects[i]).getInt(null);
					subjects[i] = context.getString(id);
				} catch (final NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}

				// Check if the parentClassLevel has it, then try to grab it from the actual key
				Calculator formula = null;
				if (parentClassLevel != null && parentClassLevel.hasSubject(subjects[i]))
					formula = parentClassLevel.getFormula(subjects[i]);

				if (subject.has(KEY_FORMULA))
					formula = new ExpressionCalculator(subject.getString(KEY_FORMULA));

				// If the formula is null, then formula must be defined -> exception
				if (formula == null)
					throw new SyntaxException("Key formula must be defined!");

				recurseChilds(subject, formula);
				calculators[i] = formula;
				parentSchool.add(name, this);
			}
		}

		/**
		 * Recurses into the child object to set the parent calculator properly
		 *
		 * @param object The object which can have child objects
		 * @param parent The parent calculator
		 */
		private void recurseChilds (final JSONObject object, final Calculator parent) {
			if (object.has(KEY_CHILDS)) {
				final JSONArray array = object.getJSONArray(KEY_CHILDS);

				for (int i = 0; i < array.getSize(); i++) {
					final JSONObject child = array.getJSONObject(i);
					final String childName = child.getString(KEY_NAME);

					for (int j = 0; j < parent.grades.size(); j++) {
						final Grade grade = parent.grades.get(j);
						if (grade.name.equals(childName)) {
							final GradeWrapper wrapper = new GradeWrapper(grade);
							wrapper.setSubGrades(new ExpressionCalculator(child.getString(KEY_FORMULA)));
							parent.grades.set(j, wrapper);

							recurseChilds(child, wrapper.calculator);
							break;
						}
					}
				}
			}
		}


		@Override
		public String[] getSupportedSubjects () {
			return subjects;
		}

		@Override
		public Calculator getFormula (final String subject) {
			for (int i = 0; i < subjects.length; i++)
				if (subjects[i].equals(subject))
					return calculators[i].clone();
			return null;
		}
	}

	private static final String KEY_NAME = "name";
	private static final String KEY_COUNTRY = "country";
	private static final String KEY_CITY = "city";
	public static final String KEY_CLASSES = "classes";

	private final String[] classNames;
	private final JsonClassLevel[] classLevels;

	public JsonIntegratedSchool (final JSONObject object) {
		super(object.getString(KEY_NAME), object.getString(KEY_COUNTRY), object.getString(KEY_CITY));
		classNames = new String[object.getJSONArray(KEY_CLASSES).getSize()];
		classLevels = new JsonClassLevel[classNames.length];
	}

	private void add (final String name, final JsonClassLevel classLevel) {
		for (int i = 0; i < classNames.length; i++) {
			if (classNames[i] == null) {
				classNames[i] = name;
				classLevels[i] = classLevel;
				return;
			}
		}
	}

	@Override
	public String[] getClassLevelNames () {
		return classNames;
	}

	@Override
	public ClassLevel getClassLevel (final String identifier) {
		for (int i = 0; i < classNames.length; i++)
			if (classNames[i].equalsIgnoreCase(identifier))
				return classLevels[i];
		return null;
	}
}
