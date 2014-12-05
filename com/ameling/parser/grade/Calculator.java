package com.ameling.parser.grade;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the core of the app which calculates the grades. The weighting of all the grades is 
 * used to calculate the average. This all is based on {@link Grade} objects.
 *
 * @author Wesley A
 */
public final class Calculator {

	/**
	 * The grades this object uses and knows
	 */
    public final Grade[] grades;

    /**
     * Creates a new object using the given grades
     * @param grades Thee grades to use
     */
    public Calculator(final Grade[] grades) {
        this.grades = grades;
    }

    /**
     * Calculates the given grade when the average is given. This method will find the {@link Grade}
     * object for you and calls {@link #calculateGrade(Grade, double)}
     * @param name The name of the grade to calculate the value of
     * @param average What the average should be
     * @return the given grade's value to achieve the average
     */
    public double calculateGrade(final String name, final double average) {
        if (name != null) {
            for(final Grade grade : grades) {
                if (grade.name.equals(name)) {
                    return calculateGrade(grade, average);
                }
            }
        }

        // TODO: throw exception
        return 0D;
    }

    /**
     * Calculates the grade object's value to get the given average. Takes into account for other 
     * set grades.
     * @param grade The grade to calculate the value of
     * @param average The average to achieve
     * @return the grade's value
     * @see Grade#setValue(double)
     * @see Grade#reset()
     */
    public double calculateGrade(final Grade grade, double average) {
        if (grade != null && !grade.isSet) {
            List<Grade> setGrades = new ArrayList<Grade>();
            int totalWeighting = grade.weighting;

            for(final Grade _grade : grades) {
                if (_grade.isSet) {
                    setGrades.add(_grade);
                    totalWeighting += _grade.weighting;
                }
            }

            average *= totalWeighting;
            for(final Grade _grade : setGrades) {
                average -= (_grade.weighting * _grade.value);
            }
            return average / grade.weighting;
        }

        // TODO: throw exception
        return 0.0D;
    }

    /**
     * Calculates the average of all set grades
     * @return The average of all set {@link Grade} objects
     * @see Grade#isSet
     */
    public double calculateAverage() {
        double total = 0.0D; // The total of all grades
        int totalWeighting = 0; // Total weighting

        for(final Grade grade : grades) {
            if (grade.isSet) { // when the grade is set, it is valid
                totalWeighting += grade.weighting; // add to the weighting
                total += grade.value * grade.weighting; // add the grade times the weighting (otherwise you get odd values)
            }
        }

        if(totalWeighting != 0) // If the totalWeighting is 0, that means that there are no value found
            return total / totalWeighting;
        return 0.0D;
     }

}
