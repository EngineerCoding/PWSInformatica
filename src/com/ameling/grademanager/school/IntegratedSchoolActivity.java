package com.ameling.grademanager.school;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.converter.ObjectAdapter;
import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity is the main activity which takes cares of {@link IntegratedSchool} objects. For now it simply loads it from the assets folder but in the future and a server is
 * available this will also connect with that and build a cache of it. It is available to search for a school, city our even a country.
 */
public class IntegratedSchoolActivity extends BaseActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, TextWatcher {

	// Some static values used in this class
	private static final String FILE_DEFAULT_SCHOOLS = "schools.json";
	private static final String REGEX_SPLIT = "\\s+";
	private static final String[] COLUMN_SCHOOL = new String[]{ "_id", "school" };

	// Value used to set the intent
	public static final String KEY_INDEX = "index";

	// Request Code
	private static final int REQUEST_SUBJECT_CLASS = 0;

	/**
	 * All integrated schools
	 */
	protected static List<IntegratedSchool> schoolCollection;

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
		if (schoolCollection == null) {
			schoolCollection = new ArrayList<>();

			// This task which loads and parses the JSON from the assets folder is asynchronous, otherwise the the ui-thread will do too much work
			// Currently it onl loads schools from the assets, but this could be expanded to a server. This task will read the objects from the server
			// and store them into a cache for later use, so it also check there for integrated schools. This is a feature that can be implemented, but
			// since no server is available to us we cannot do that (yet)
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute () {
					findViewById(R.id.loading_schools).setVisibility(View.VISIBLE);
				}

				@Override
				protected Void doInBackground (final Void... voids) {
					// Load the array from the assets
					JSONArray mainArray = null;
					try {
						final Reader reader = new InputStreamReader(getResources().getAssets().open(FILE_DEFAULT_SCHOOLS));
						mainArray = new JSONArray(reader);
					} catch (final IOException e) {
						e.printStackTrace();
					}

					// Add the values to the collection
					if (mainArray != null) {
						for (int i = 0; i < mainArray.getSize(); i++) {
							final JSONObject parentJson = mainArray.getJSONObject(i);
							schoolCollection.add(JsonIntegratedSchool.create(parentJson, IntegratedSchoolActivity.this));
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute (final Void aVoid) {
					schoolAdapter.addAll(schoolCollection);
					findViewById(R.id.loading_schools).setVisibility(View.GONE);
				}
			}.execute();
		}

		// Set the adapter to the listview and all available schools to it
		final ListView schoolList = (ListView) findViewById(R.id.school_list);
		schoolAdapter = IntegratedSchoolConverter.instance.createAdapter(this, new ArrayList<IntegratedSchool>());
		schoolAdapter.addAll(schoolCollection);

		schoolList.setAdapter(schoolAdapter);
		schoolList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu (final Menu menu) {
		final boolean returnVal = super.onCreateOptionsMenu(menu);

		// Configure the search field
		this.searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		// Get the edit text of the search field to add a TextChangedListener
		final int resource_edit_text = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		((EditText) searchView.findViewById(resource_edit_text)).addTextChangedListener(this);

		return returnVal;
	}

	@Override
	public void handleActivityResult (final int requestCode, final Intent data) {
		if (requestCode == REQUEST_SUBJECT_CLASS) {
			// Proxy the data back
			setResult(RESULT_OK, data);
			finish();
		}
	}

	/**
	 * This starts the {@link SchoolDialogActivity} to select a formula from the selected school, class and subject.
	 *
	 * @param school The school which manages the classes
	 */
	private void showIntegratedSchool (final IntegratedSchool school) {
		final Intent intent = new Intent(this, SchoolDialogActivity.class);
		intent.putExtra(KEY_INDEX, schoolCollection.indexOf(school));
		startActivityForResult(intent, REQUEST_SUBJECT_CLASS);
	}

	// Implementation of AdapterView.OnItemClickListener
	@Override
	public void onItemClick (final AdapterView<?> adapterView, final View view, final int position, final long id) {
		// Show the clicked school
		final ArrayAdapter<IntegratedSchool> schoolArrayAdapter = (ArrayAdapter<IntegratedSchool>) adapterView.getAdapter();
		showIntegratedSchool(schoolArrayAdapter.getItem(position));
	}

	// Implementation of TextWatcher, used to have a proper onQueryTextChange (it doesn't update when the last character is removed)
	@Override
	public void beforeTextChanged (final CharSequence charSequence, final int start, final int count, final int after) {}

	@Override
	public void onTextChanged (final CharSequence charSequence, final int start, final int before, final int after) {}

	@Override
	public void afterTextChanged (final Editable editable) {
		if (editable.length() == 0)
			onQueryTextChange(editable.toString());
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
		} else {
			// Set an empty suggestions adapter
			searchView.setSuggestionsAdapter(new IntegratedSchoolAdapter(this, new MatrixCursor(COLUMN_SCHOOL), new ArrayList<IntegratedSchool>()));
		}

		return false;
	}

	@Override
	public boolean onQueryTextSubmit (final String query) {
		// Set the list to the results instead of a suggestion adapter
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
		if (!query.trim().isEmpty()) {
			final List<IntegratedSchool> results = new ArrayList<>();
			for (final IntegratedSchool school : schoolCollection)
				if (matchWord(query, school.name) || matchWord(query, school.country) || matchWord(query, school.city))
					results.add(school);
			return results.size() > 0 ? results : null;
		}
		return null;
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
		// The search is closed -> use the original list again
		schoolAdapter.clear();
		schoolAdapter.addAll(schoolCollection);
		return false;
	}

	/**
	 * This adapter simply binds the given position to a new view. This is also the suggestion listener, since it is set at pretty much the same time.
	 */
	private class IntegratedSchoolAdapter extends CursorAdapter implements SearchView.OnSuggestionListener {

		/**
		 * The given results
		 */
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
			IntegratedSchoolActivity.this.showIntegratedSchool(results.get(position));
			return true;
		}
	}
}
