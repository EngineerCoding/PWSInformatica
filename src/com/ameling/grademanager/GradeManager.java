package com.ameling.grademanager;

import android.content.Context;
import com.ameling.grademanager.grade.CalculatorWrapper;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONException;
import com.ameling.parser.json.JSONWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GradeManager {

	/**
	 * A holder class which gets given at the main activity to expose all those subjects
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
	}

	private static final String FILE_NAME = "subjects.json";

	protected static GradeManager instance;

	private final Context context;
	protected final List<Subject> subjects;

	protected GradeManager(final Context context) {
		this.context = context;
		subjects = getSubjects();
	}

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
		return new ArrayList<>();
	}

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
}

