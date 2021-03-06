/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Wesley "EngineerCoding" Ameling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.grademanager.app.school;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import com.grademanager.app.R;
import com.grademanager.app.converter.ObjectAdapter;
import com.grademanager.parser.json.JSONArray;
import com.grademanager.parser.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/** This task which loads and parses the JSON from the assets folder is asynchronous, otherwise the the ui-thread will do too much work
 * Currently it onl loads schools from the assets, but this could be expanded to a server. This task will read the objects from the server
 * and store them into a cache for later use, so it also check there for integrated schools. This is a feature that can be implemented, but
 * since no server is available to us we cannot do that (yet)
 */
 public class AsyncAssetLoad extends AsyncTask<Void, Void, Void> {

	/**
	 * The asset file
	 */
	private static final String FILE_DEFAULT_SCHOOLS = "schools.json";

	/**
	 * The activity which is loading
	 */
	private final Activity activity;

	/**
	 * The adapter that will load
	 */
	private final ObjectAdapter<IntegratedSchool> adapter;

	/**
	 * Creates a new {@link AsyncTask} which loads the JSON from the assets folder
	 *
	 * @param activity The activity which called this
	 * @param adapter  The adapter from said activity
	 */
	private AsyncAssetLoad (final Activity activity, final ObjectAdapter<IntegratedSchool> adapter) {
		this.activity = activity;
		this.adapter = adapter;
	}

	@Override
	protected void onPreExecute () {
		activity.findViewById(R.id.loading_schools).setVisibility(View.VISIBLE);
		flagLoading = true;
	}

	@Override
	protected Void doInBackground (final Void... voids) {
		// Load the array from the assets
		JSONArray mainArray = null;
		try {
			final Reader reader = new InputStreamReader(activity.getResources().getAssets().open(FILE_DEFAULT_SCHOOLS));
			mainArray = new JSONArray(reader);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Add the values to the collection
		if (mainArray != null) {
			for (int i = 0; i < mainArray.getSize(); i++) {
				final JSONObject parentJson = mainArray.getJSONObject(i);
				IntegratedSchoolActivity.schoolCollection.add(JsonIntegratedSchool.create(parentJson, activity));
			}
		}
		flagLoading = false;
		return null;
	}

	@Override
	protected void onPostExecute (final Void aVoid) {
		adapter.addAll(IntegratedSchoolActivity.schoolCollection);
		activity.findViewById(R.id.loading_schools).setVisibility(View.GONE);
	}

	/**
	 * A flag whether the task has ran or not
	 */
	private static boolean flagRun = false;

	/**
	 * A flag which is true while the task is loading
	 */
	protected static boolean flagLoading = false;

	/**
	 * Runs this task if necessary
	 *
	 * @param activity The activity which called this
	 * @param adapter  The adapter from said activity
	 */
	protected static void runTask (final IntegratedSchoolActivity activity, final ObjectAdapter<IntegratedSchool> adapter) {
		if (!flagRun && activity != null && adapter != null) {
			flagRun = true;
			new AsyncAssetLoad(activity, adapter).execute();
		}
	}

}
