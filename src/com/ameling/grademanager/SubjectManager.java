package com.ameling.grademanager;

import android.content.Context;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.grademanager.grade.GradeWrapper;
import com.ameling.grademanager.grade.tree.ITreeNode;
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
	public static class Subject {

		/**
		 * The id of the subject
		 */
		public final String name;

		/**
		 * The parsed formula of the subject
		 */
		public final CalculatorWrapper calculator;

		public Subject (final String name, final CalculatorWrapper calculator) {
			if (name == null || name.length() == 0 || calculator == null)
				throw new NullPointerException();
			this.name = name;
			this.calculator = calculator;
		}

		public ITreeNode createTreeNode () {
			// Use the wrapper because it is a perfect data structure for this
			final GradeWrapper wrapper = new GradeWrapper(name, 1);
			wrapper.setSubGrades(calculator);
			return wrapper;
		}
	}

	// The file name of the internal storage subject file
	private static final String FILE_NAME = "subjects.json";

	/**
	 * The only instance created by {@link com.ameling.grademanager.MainActivity}
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
}

