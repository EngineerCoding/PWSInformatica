package com.ameling;

import com.ameling.parser.json.JSONArray;
import com.ameling.parser.json.JSONObject;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Wesley A on 13-12-2014.
 */
public class CommitGenerator {

	private static final String ASK_KEY = "Please enter your basic key:";
	private static final String REQUEST_METHOD = "GET";
	private static final String KEY_AUTHORIZATION = "Authorization";
	private static final String FORMAT_LINE = "[%s] +%04d -%04d %s: %s";
	private static final String VALUES = "values";
	private static final String FORMAT_AUTHORIZATION = "Basic %s";
	private static final File file = new File("C:\\Users\\Wesley A\\Dropbox\\Presentaties en verslagen\\PWS\\Logboek\\commits.txt");
	private static final URL URL_COMMITS;
	private static final String HASH = "hash";
	private static final String DATE = "date";
	private static final String URL_FORMAT_DIFF = "https://bitbucket.org/api/2.0/repositories/EngineerCoding/pws-informatica/diff/%s";
	private static final String MESSAGE = "message";

	static {
		URL placeholder;
		try {
			placeholder = new URL("https://bitbucket.org/api/2.0/repositories/EngineerCoding/pws-informatica/commits/");
		} catch (MalformedURLException e) {
			placeholder = null;
		}
		URL_COMMITS = placeholder;
	}

	private final String key;

	protected CommitGenerator () {
		System.out.println(ASK_KEY);
		final Scanner scanner = new Scanner(System.in);
		key = scanner.nextLine();
		scanner.close();
		getCommits();
	}

	private void getCommits () {
		try {
			final JSONArray values = new JSONObject(readURL(URL_COMMITS)).getJSONArray(VALUES);
			if (file.exists())
				file.delete();
			file.createNewFile();

			final Writer writer = new FileWriter(file);
			for (int i = 0; i < values.getSize(); i++) {
				final JSONObject object = values.getJSONObject(i);

				System.out.printf("Retrieving commit: %s\n", object.getString(HASH));
				URL url = null;
				try {
					url = new URL(String.format(URL_FORMAT_DIFF, object.getString(HASH)));
				} catch (final MalformedURLException e) {
					e.printStackTrace();
				}

				final String content = readURL(url);
				writer.write(String.format(FORMAT_LINE,
						object.getString(DATE),
						countDiff(content, MODE.ADD),
						countDiff(content, MODE.MIN),
						object.getString(HASH),
						object.getString(MESSAGE)));
				writer.write(System.lineSeparator());
				System.out.println(String.format("%s +%04d -%04d", object.getString(DATE),
						countDiff(content, MODE.ADD),
						countDiff(content, MODE.MIN)));
			}
			writer.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static enum MODE {
		ADD("+", "+++"),
		MIN("-", "---");

		public final String finder;
		public final String watcher;

		private MODE (final String finder, final String watcher) {
			this.finder = finder;
			this.watcher = watcher;
		}
	}

	private static int countDiff (final String content, final MODE mode) {
		int counter = 0;
		if (content != null && mode != null) {
			final BufferedReader reader = new BufferedReader(new StringReader(content));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(mode.finder) && !line.startsWith(mode.watcher))
						counter += 1;
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return counter;
	}

	private String readURL (final URL url) throws IOException {
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(REQUEST_METHOD);
		connection.setRequestProperty(KEY_AUTHORIZATION, String.format(FORMAT_AUTHORIZATION, key));

		final Reader reader = new InputStreamReader(connection.getInputStream());
		final StringBuilder builder = new StringBuilder();
		int next;
		while ((next = reader.read()) != -1) {
			builder.append((char) next);
		}
		reader.close();
		return builder.toString();
	}

	public static void main(String[] args) {
		new CommitGenerator();
	}
}
