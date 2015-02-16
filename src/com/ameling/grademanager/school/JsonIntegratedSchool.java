package com.ameling.grademanager.school;

import android.content.Context;
import com.ameling.grademanager.R;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.parser.SyntaxException;
import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

import static com.ameling.grademanager.util.ConstantKeys.*;

/**
 * An implementation of {@link IntegratedSchool} which reads it from a {@link JSONObject}. This is going to throw {@link com.ameling.parser.json.JSONException} when the given
 * object is not an actual implementation of this object. Currently this is the only implementation of {@link IntegratedSchool} but there are plans in the future to use this more
 * often once a server is in the air where this app can pull of schools. It will probably build a cache in the form of a database, but for now this is just here to load it from
 * the assets.
 */
public class JsonIntegratedSchool extends IntegratedSchool {

	/**
	 * Implementation of {@link IntegratedSchool.ClassLevel} to return it in the parent object
	 */
	private static class JsonClassLevel extends ClassLevel {

		/**
		 * The name of the classLevel
		 */
		public final String name;

		/**
		 * All stored subjects
		 */
		private final String[] subjects;

		/**
		 * All corresponding calculators
		 */
		private final Calculator[] calculators;

		/**
		 * Creates a new ClassLevel
		 * @param object The object to read from
		 * @param parentSchool The parent object to get a possible class parent, should never be null
		 * @param context The context to try to localize from, should not be null
		 */
		public JsonClassLevel (final JSONObject object, final JsonIntegratedSchool parentSchool, final Context context) {
			if (object == null)
				throw new NullPointerException();

			ClassLevel parentClassLevel = null;
			if (object.has(KEY_PARENT))
				parentClassLevel = parentSchool.getClassLevel(object.getString(KEY_PARENT));

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

				recurseChildren(subject, formula);
				calculators[i] = formula;
				parentSchool.add(name, this); // Add this object to the parent
			}
		}

		/**
		 * Recurses into the child object to set the parent calculator properly
		 *
		 * @param object The object which can have child objects
		 * @param parent The parent calculator
		 */
		private void recurseChildren (final JSONObject object, final Calculator parent) {
			if (object.has(KEY_CHILDREN)) {
				final JSONArray array = object.getJSONArray(KEY_CHILDREN);

				for (int i = 0; i < array.getSize(); i++) {
					final JSONObject child = array.getJSONObject(i);
					final String childName = child.getString(KEY_NAME);

					for (int j = 0; j < parent.grades.size(); j++) {
						final Grade grade = parent.grades.get(j);
						if (grade.name.equals(childName)) {
							final GradeWrapper wrapper = new GradeWrapper(grade);
							wrapper.setSubGrades(new CalculatorWrapper(child.getString(KEY_FORMULA)));
							parent.grades.set(j, wrapper);

							recurseChildren(child, wrapper.calculator);
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

	/**
	 * All stored classNames, a corresponding ClassLevel can be found in {@link #classLevels} with the same index
	 */
	private final String[] classNames;

	/**
	 * All stored classLevels, a corresponding class name can be found in ({@link #classNames} with the same index
	 */
	private final JsonClassLevel[] classLevels;

	/**
	 * Creates a plain IntegratedSchool object. This does not parse ClassLevels by itself!
	 * @param object The json to read from
	 */
	private JsonIntegratedSchool (final JSONObject object) {
		super(object.getString(KEY_NAME), object.getString(KEY_COUNTRY), object.getString(KEY_CITY));
		classNames = new String[object.getJSONArray(KEY_CLASSES).getSize()];
		classLevels = new JsonClassLevel[classNames.length];
	}

	/**
	 * Adds a classLevel to this school. Only used internally
	 * @param name The name of the {@link ClassLevel}
	 * @param classLevel The actual {@link JsonClassLevel}
	 */
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

	/**
	 * Creates a functional object of {@link JsonIntegratedSchool}
	 * @param object The object to read the values from
	 * @param context The context to localize the keys with
	 * @return A functional {@link JsonIntegratedSchool}
	 */
	public static JsonIntegratedSchool create (final JSONObject object, final Context context) {
		final JsonIntegratedSchool parent = new JsonIntegratedSchool(object);
		final JSONArray arrayClasses = object.getJSONArray(KEY_CLASSES);
		for (int j = 0; j < arrayClasses.getSize(); j++)
			new JsonIntegratedSchool.JsonClassLevel(arrayClasses.getJSONObject(j), parent, context);
		return parent;
	}
}
