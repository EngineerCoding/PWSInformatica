package com.ameling.grademanager.io;

import android.content.Context;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONException;
import com.ameling.parser.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class FileManager {

	private static final String KEY_VERSION = "version";
	private static final String KEY_SUBJECTS = "subjects";

	private static final String FILE_NAME = "gradeStorage.json";

	private final Context context;

	public FileManager(final Context context) {
		if (context == null)
			throw new NullPointerException();
		this.context = context;
	}

	public boolean isFileAvailable() {
		final String[] files = context.fileList();
		for (String name : files)
			if (FILE_NAME.equals(name))
				return true;
		return false;
	}

	public Format.Subject[] getSubjects() {
		if (isFileAvailable()) {
			try {
				final JSONObject mainObject = new JSONObject(new InputStreamReader(context.openFileInput(FILE_NAME)));
				final Format format = Format.Version.getFormat(mainObject.getInt(KEY_VERSION));
				if (format != null) {
					final List<Format.Subject> subjects = new ArrayList<Format.Subject>();
					final JSONArray jsonSubjects = mainObject.getJSONArray(KEY_SUBJECTS);
					for (int i = 0; i < jsonSubjects.getSize(); i++)
						subjects.add(format.decode(jsonSubjects.getJSONObject(i)));

					return subjects.toArray(new Format.Subject[subjects.size()]);
				}
			} catch (final FileNotFoundException | JSONException e) {
				return null;
			}
		}
		return null;
	}


}
