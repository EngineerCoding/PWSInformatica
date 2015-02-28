package com.grademanager.app.school;

import com.grademanager.app.grade.CalculatorWrapper;

/**
 * This class represents a school with its formulas. To formulas are arranged with their classes, as a school can have multiple
 * classes with the same subjects. Those classes are represented by the {@link ClassLevel} class, which return the supported subjects
 * and the formulas for them. The school itself only manages those classes by giving their names and returning the appropriate objects
 * for the name.
 */
public abstract class IntegratedSchool {

	/**
	 * This is also a holder class which hold the actual formulas. This is so because a school_searchable can have multiple classes with different levels, but the average grade does not have
	 * to be the same because it is a different level after all. The point of this is to distinguish between levels and even years.
	 */
	public abstract static class ClassLevel {

		/**
		 * Returns the supported subjects in a EnumSet. This exact EnumSet is used to get parameters from
		 * {@link #getFormula(String))
		 *
		 * @return An {@link java.util.EnumSet } with the supported subjects
		 */
		public abstract String[] getSupportedSubjects ();

		/**
		 * Returns the formula for the given subject
		 *
		 * @param subject The subject which we need the formula from
		 * @return A valid average expression
		 */
		public abstract CalculatorWrapper getFormula (final String subject);

		/**
		 * Checks if subject is in the {@link #getSupportedSubjects()} array
		 *
		 * @param subject The subject to check for
		 * @return Whether the subject exists
		 */
		public final boolean hasSubject (final String subject) {
			for (final String s : getSupportedSubjects())
				if (s.equalsIgnoreCase(subject))
					return true;
			return false;
		}
	}

	/**
	 * The id of the school_searchable
	 */
	public final String name;

	/**
	 * The id of the country. Could be possibly changed to enumerations in the future
	 */
	public final String country;

	/**
	 * The city where this school_searchable is
	 */
	public final String city;

	public IntegratedSchool (final String name, final String country, final String city) {
		this.name = name;
		this.country = country;
		this.city = city;
	}

	/**
	 * Describes all classes with the associated level. It is simply an identifier and has to make sense for people who are on this actual school_searchable
	 *
	 * @return An array with all identifiers
	 */
	public abstract String[] getClassLevelNames ();

	/**
	 * Has to return a {@link ClassLevel} object associated with the given identifier. This is only called once for each identifier given at {@link #getClassLevelNames()} so you
	 * don't have to worry about multiple instances of a class level. When done correctly, all classes are in an enumeration but that is not required at all
	 *
	 * @param identifier The identifier associated with the ClassLevel object
	 * @return A ClassLevel object for the given identifier
	 */
	public abstract ClassLevel getClassLevel (final String identifier);

	@Override
	public final boolean equals (final Object object) {
		if (object instanceof IntegratedSchool) {
			final IntegratedSchool school = (IntegratedSchool) object;
			return school.name.equals(name) && school.country.equals(country) && school.city.equals(city);
		}
		return false;
	}

}
