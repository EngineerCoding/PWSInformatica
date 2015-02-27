package com.ameling.grademanager;

import android.content.Context;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.parser.grade.Grade;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONException;
import com.ameling.parser.json.JSONWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that takes care of all {@link Subject} objects. This will load them, save them and let other classes check if a subject is already taken.
 */
public class SubjectManager {

	/**
	 * A holder class which gets given at the main activity to expose all those subjects. The {@link com.ameling.grademanager.converter.JsonConverter} can be found in {@link
	 * com.ameling.grademanager.SubjectConverter}
	 */
	public static class Subject implements Cloneable {
		/**
		 * The id of the subject
		 */
		public final String name;

		/**
		 * The parsed formula of the subject
		 */
		public final CalculatorWrapper calculator;

		/**
		 * All {@link Grade} objects used in the {@link #calculator}
		 */
		private final Grade[] subGrades;

		/**
		 * Creates a Subject with a name and calculator
		 *
		 * @param name       The name of the subject
		 * @param calculator The calculator which is used in this Subject
		 */
		public Subject (final String name, final CalculatorWrapper calculator) {
			if (name == null || name.length() == 0 || calculator == null)
				throw new NullPointerException();
			this.name = name;
			this.calculator = calculator;

			// Get the sub grades from the calculator; all Grade objects get collected (NOT GradeWrapper objects)
			final List<Grade> subGrades = new ArrayList<>();
			for (final Grade grade : calculator.grades) {
				if (grade instanceof GradeWrapper) {
					subGrades.addAll(((GradeWrapper) grade).getChildren());
				} else {
					subGrades.add(grade);
				}
			}
			this.subGrades = subGrades.toArray(new Grade[subGrades.size()]);
		}

		public Grade[] getSubGrades () {
			return subGrades;
		}

		@Override
		public Subject clone () {
			return new Subject(name, calculator.clone());
		}
	}

	/**
	 * The file name of the internal storage subject file
	 */
	private static final String FILE_NAME = "subjects.json";

	/**
	 * The only instance created by {@link MainActivity}
	 */
	public static SubjectManager instance;

	/**
	 * The context to use
	 */
	private final Context context;

	/**
	 * All {@link Subject} objects
	 */
	protected final List<Subject> subjects;

	/**
	 * Creates the manager which uses the Context to load the internal storage
	 *
	 * @param context The activity's Context
	 */
	protected SubjectManager (final Context context) {
		this.context = context;
		subjects = getSubjects();
	}

	/**
	 * Loads the subjects from the internal file
	 *
	 * @return A list subjects
	 * @see #FILE_NAME
	 * @see #context
	 */
	public List<Subject> getSubjects () {
		try {
			final List<Subject> subjects = new ArrayList<>();

			// Read the file and turn it into a JSONArray
			final JSONArray subjectArray = new JSONArray(new InputStreamReader(context.openFileInput(FILE_NAME)));
			for (int i = 0; i < subjectArray.getSize(); i++)
				subjects.add(SubjectConverter.instance.convert(subjectArray.getJSONObject(i)));

			return subjects;
		} catch (final FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
		// Create an empty list
		return new ArrayList<>();
	}

	/**
	 * Saves all subjects in the internal storage file
	 *
	 * @see #FILE_NAME
	 * @see #context
	 */
	public void saveSubjects () {
		if (subjects.size() > 0) {
			final JSONArray subjectArray = new JSONArray();
			for (final Subject subject : subjects)
				subjectArray.add(SubjectConverter.instance.convert(subject));

			try {
				// Write the JSON to the file
				final JSONWriter writer = new JSONWriter(new OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)), false);
				writer.append(subjectArray);
				writer.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Replaces the subject with the same name in the list with the given subject
	 *
	 * @param subject The subject to replace with the name of this subject
	 */
	protected void replaceSubject (final Subject subject) {
		for (int i = 0; i < subjects.size(); i++) {
			final Subject subjectToReplace = subjects.get(i);
			if (subjectToReplace.name.equals(subject.name)) {
				subjects.set(i, subject);
				break;
			}
		}
	}

	/**
	 * Retrieves the {@link Subject} from {@link #subjects} with a given subject name
	 *
	 * @param subjectName The associated object to return
	 * @return The {@link Subject} or null when it doesn't exist
	 */
	public Subject getSubject (final String subjectName) {
		for (final Subject subject : subjects)
			if (subject.name.equals(subjectName))
				return subject;
		return null;
	}

	/**
	 * Checks if the name is already a {@link Subject} object
	 *
	 * @param name The name of the subject
	 * @return Whether it already exists or nots
	 */
	public boolean hasSubject (final String name) {
		for (final Subject subject : subjects)
			if (subject.name.equals(name))
				return true;
		return false;
	}
}

