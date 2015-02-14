package com.ameling.grademanager.ui;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.school.IntegratedSchool;
import com.ameling.grademanager.school.JsonIntegratedSchool;
import com.ameling.grademanager.ui.adapter.IntegratedSchoolConverter;
import com.ameling.grademanager.ui.adapter.ObjectAdapter;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class IntegratedSchoolActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	private static final String FILE_DEFAULT_SCHOOLS = "schools.json";
	private static final String REGEX_SPLIT = "\\s+";
	private static final String[] COLUMN_SCHOOL = new String[]{ "_id", "school" };

	/**
	 * All integrated schools
	 */
	private static List<IntegratedSchool> schoolCollection;

	/**
	 * The adapter of the listview
	 */
	private ObjectAdapter<IntegratedSchool> schoolAdapter;

	/**
	 * The search view for the suggestions
	 */
	private SearchView searchView;

	@Override
	public int getMainLayout () {
		return R.layout.integrated_school;
	}

	@Override
	public int getMenuID () {
		return R.menu.school_activity_actions;
	}

	@Override
	public void initialize () {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (schoolCollection == null) {
			schoolCollection = new ArrayList<>();

			// Async task so it loads faster
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground (final Void... voids) {
					// Load default data
					JSONArray mainArray = null;
					try {
						final Reader reader = new InputStreamReader(getResources().getAssets().open(FILE_DEFAULT_SCHOOLS));
						mainArray = new JSONArray(reader);
					} catch (final IOException e) {
						e.printStackTrace();
					}

					if (mainArray != null) {
						for (int i = 0; i < mainArray.getSize(); i++) {
							final JSONObject parentJson = mainArray.getJSONObject(i);
							final JsonIntegratedSchool parent = new JsonIntegratedSchool(parentJson);
							schoolCollection.add(parent);

							final JSONArray arrayClasses = parentJson.getJSONArray(JsonIntegratedSchool.KEY_CLASSES);
							for (int j = 0; j < arrayClasses.getSize(); j++)
								new JsonIntegratedSchool.JsonClassLevel(arrayClasses.getJSONObject(j), parent, IntegratedSchoolActivity.this);
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute (Void aVoid) {
					schoolAdapter.addAll(schoolCollection);
				}
			}.execute();
		}

		final ListView schoolList = (ListView) findViewById(R.id.school_list);
		schoolAdapter = IntegratedSchoolConverter.instance.createAdapter(this, new ArrayList<IntegratedSchool>());
		schoolAdapter.addAll(schoolCollection);

		schoolList.setAdapter(schoolAdapter);
		schoolList.setOnItemClickListener(IntegratedSchoolConverter.instance);
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		final boolean returnVal = super.onCreateOptionsMenu(menu);

		this.searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		return returnVal;
	}

	@Override
	public boolean onOptionsItemSelected (final MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed () {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		finish();
	}

	// Implementation of OnQueryTextListener with helper methods

	@Override
	public boolean onQueryTextChange (final String query) {
		// Only set suggestions
		final List<IntegratedSchool> results = getResults(query);
		if (results != null) {
			final MatrixCursor cursor = new MatrixCursor(COLUMN_SCHOOL);
			for (int i = 0; i < results.size(); i++)
				cursor.addRow(new Object[]{ i, results.get(i) });

			final IntegratedSchoolAdapter adapter = new IntegratedSchoolAdapter(this, cursor, results);
			searchView.setSuggestionsAdapter(adapter);
			searchView.setOnSuggestionListener(adapter);
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit (final String query) {
		final List<IntegratedSchool> result = getResults(query);
		if (result != null) {
			schoolAdapter.clear();
			schoolAdapter.addAll(result);
		}
		return true;
	}

	/**
	 * Tries to get results from the query out of the {@link #schoolCollection} list
	 *
	 * @param query The query to search for
	 * @return A list of results or null when none are found
	 */
	private static List<IntegratedSchool> getResults (final String query) {
		final List<IntegratedSchool> results = new ArrayList<>();
		for (final IntegratedSchool school : schoolCollection)
			if (matchWord(query, school.name) || matchWord(query, school.country) || matchWord(query, school.city))
				results.add(school);
		return results.size() > 0 ? results : null;
	}

	/**
	 * Checks if every word in the searchable starts or ends with the prefix
	 *
	 * @param prefix     Where a word should start with
	 * @param searchable The sentence or collection of words which are in the search list
	 * @return Whether the prefix matches a word from searchable
	 */
	private static boolean matchWord (String prefix, String searchable) {
		if (prefix != null && !(prefix = prefix.trim()).isEmpty() && searchable != null && !(searchable = searchable.trim()).isEmpty()) {
			searchable = searchable.toLowerCase();
			prefix = prefix.toLowerCase();

			// easy checking
			if (searchable.equalsIgnoreCase(prefix) || searchable.startsWith(prefix) || searchable.endsWith(prefix))
				return true;

			// advanced checking
			final String[] searchable_words = searchable.split(REGEX_SPLIT);
			final String[] prefix_words = prefix.split(REGEX_SPLIT);

			for (String searchable_word : searchable_words)
				for (String prefix_word : prefix_words)
					if (searchable_word.startsWith(prefix_word) || searchable_word.endsWith(prefix_word))
						return true;
		}
		return false;
	}

	// implementation of OnCloseListener
	@Override
	public boolean onClose () {
		schoolAdapter.clear();
		schoolAdapter.addAll(schoolCollection);
		return false;
	}

	private class IntegratedSchoolAdapter extends CursorAdapter implements SearchView.OnSuggestionListener {

		private final List<IntegratedSchool> results;

		public IntegratedSchoolAdapter (final Context context, final Cursor cursor, final List<IntegratedSchool> results) {
			super(context, cursor, false);
			this.results = results;
		}

		@Override
		public View newView (final Context context, final Cursor cursor, final ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.school_listview, parent, false);
		}

		@Override
		public void bindView (final View view, final Context context, final Cursor cursor) {
			IntegratedSchoolConverter.instance.populateInflatedView(view, results.get(cursor.getPosition()));
		}

		// Implementation of OnSuggestionAdapter
		@Override
		public boolean onSuggestionSelect (final int position) {
			// Do nothing here
			return false;
		}

		@Override
		public boolean onSuggestionClick (final int position) {
			IntegratedSchoolConverter.instance.showPopup(results.get(position));
			return true;
		}
	}


}
