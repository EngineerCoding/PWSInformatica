package com.ameling.grademanager.io;

import com.ameling.parser.grade.Calculator;
import com.ameling.parser.json.JSONObject;

/**
 * This class represents the sub class of all formats used through all versions. This way any format will work on any format
 *
 * @Author Wesley A
 */
public abstract class Format {

	/**
	 * A holder class which gets given at the main activity to expose all those subjects
	 */
	public static final class Subject {

		public final String name;
		public final Calculator calculator;

		private String[] names = null;

		public Subject(final String name, final Calculator calculator) {
			if (name == null || name.length() == 0 || calculator == null)
				throw new NullPointerException();
			this.name = name;
			this.calculator = calculator;
		}

		public String[] getGradeNames() {
			if (names == null) {
				names = new String[calculator.grades.size()];
				for (int i = 0; i < names.length; i++)
					names[i] = calculator.grades.get(i).name;
			}
			return names;
		}
	}

	/**
	 * All formats associated with a version
	 */
	protected static enum Version {
		V1_0(0, FormatV1_0.instance);

		private final int format_version;
		public Format format;

		private Version(final int format_version, final Format format) {
			this.format_version = format_version;
			this.format = format;
		}

		protected static Format getFormat(int format_version) {
			for (final Version version : values())
				if (format_version == version.format_version)
					return version.format;
			return null;
		}

		protected static int getLatestFormat() {
			return values().length - 1;
		}
	}

	/* end static */

	/**
	 * Creates a new instance of format with a new version. Each version represents a different format, when the version is not known then it will use the latest version if
	 * available. All versions can be found in {@link Format.Version}
	 */
	protected Format() {

	}

	/**
	 * This method decodes the read {@link JSONObject} and turns it into a {@link Subject}
	 *
	 * @param readObject The read object of the subject array, this is standard in all versions
	 * @return an associated {@link Subject} object
	 */
	public abstract Subject decode(final JSONObject readObject);

	/**
	 * Encodes a {@link Subject} to an associate {@link JSONObject}
	 *
	 * @param subject The {@link Subject} to convert
	 * @return An associated {@link JSONObject}
	 */
	public abstract JSONObject encode(final Subject subject);

}
