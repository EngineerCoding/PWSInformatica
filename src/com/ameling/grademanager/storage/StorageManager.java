package com.ameling.grademanager.storage;

import android.content.Context;
import com.ameling.grademanager.util.Subject;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONException;
import com.ameling.parser.json.JSONObject;
import com.ameling.parser.json.JSONWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the main file of all the file IO happening. In the current case of the app it only will save grades and load them into objects using
 * the {@link Format} class. This class is i place just if the file structure changes for some reason it will be backwards compatible.
 *
 * @Author Wesley A
 */
public final class StorageManager {

	// All static strings
	private static final String KEY_VERSION = "version";
	private static final String KEY_SUBJECTS = "subjects";
	private static final String FILE_NAME = "gradeStorage.json";

	/**
	 * The only instance of this FileManager
	 */
	private static StorageManager instance;

	/**
	 * Creates a new instance or returns the already created instance.
	 *
	 * @param context The context which will supply the file
	 * @return an instance of FileManager
	 * @throws NullPointerException when the given context is null
	 */
	public static StorageManager instance (final Context context) throws NullPointerException {
		if (instance == null)
			instance = new StorageManager(context);
		return instance;
	}

	/**
	 * The file supplier for this FileManager
	 */
	private final Context context;

	/**
	 * The format which is used for the file IO
	 */
	public final Format format;

	/**
	 * The format version of {@link #format}
	 */
	private final int format_version;

	/**
	 * Creates a new instance or returns the already created instance. An instance can be acquired from {@link #instance(Context)}
	 *
	 * @param context The context which will supply the file
	 * @return an instance of FileManager
	 * @throws NullPointerException when the given context is null
	 */
	private StorageManager (final Context context) throws NullPointerException {
		if (context == null)
			throw new NullPointerException();
		this.context = context;
		format_version = Format.Version.getLatestFormat();
		format = Format.Version.getFormat(format_version);
	}

	/**
	 * Retrieves the Subjects from the file decoded with the {@link #format}
	 *
	 * @return an array of {@link Subject} when there is a file or null when there is no file
	 * @see Format#decode(JSONObject)
	 */
	public List<Subject> getSubjects () {
		try {
			// Read the file and turn it into a JSONObject
			final JSONObject mainObject = new JSONObject(new InputStreamReader(context.openFileInput(FILE_NAME)));
			final Format format = Format.Version.getFormat(mainObject.getInt(KEY_VERSION));
			if (format != null) {
				// Decode it with the format and transform it into a Subject object
				final List<Subject> subjects = new ArrayList<Subject>();
				final JSONArray jsonSubjects = mainObject.getJSONArray(KEY_SUBJECTS);
				for (int i = 0; i < jsonSubjects.getSize(); i++)
					subjects.add(format.decode(jsonSubjects.getJSONObject(i)));

				return subjects;
			}
		} catch (final FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<Subject>();
	}

	/**
	 * This method writes the subjects to the file using the {@link #format}
	 *
	 * @param subjects Subjects to save
	 * @return whether it was successful or not
	 */
	public boolean saveSubjects (final List<Subject> subjects) {
		// First transform the subjects to the appropriate JSONObject
		final JSONArray subjectArray = new JSONArray();
		for (final Subject subject : subjects)
			subjectArray.add(format.encode(subject));

		final JSONObject mainObject = new JSONObject();
		mainObject.set(KEY_VERSION, format_version);
		mainObject.set(KEY_SUBJECTS, subjectArray);

		try {
			// Write the JSON to the file
			final JSONWriter writer = new JSONWriter(new OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)), false);
			writer.append(mainObject);
			writer.close();
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}











