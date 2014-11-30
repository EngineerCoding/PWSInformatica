package com.ameling.parser.grade.weighting;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the core of the app where this is about. This calculates a future grade with the given Grades. Those grades all of a weighting, which is used for calculating.
 *
 * @author Wesley A
 */
public class GradeCalculator {

    public final Grade[] grades;

    public GradeCalculator(final Grade[] grades) {
        this.grades = grades;
    }

    public double calculateGrade(final String name, final double target) {
        if (name != null) {
            for(final Grade grade : grades) {
                if (grade.name.equals(name)) {
                    return calculateGrade(grade, target);
                }
            }
        }

        // TODO: throw exception
        return 0D;
    }

    public double calculateGrade(final Grade grade, double target) {
        if (grade != null && !grade.isSet) {
            List<Grade> setGrades = new ArrayList<Grade>();
            int totalWeighting = grade.weighting;

            for(final Grade _grade : grades) {
                if (_grade.isSet) {
                    setGrades.add(_grade);
                    totalWeighting += _grade.weighting;
                }
            }

            target *= totalWeighting;
            for(final Grade _grade : setGrades) {
                target -= (_grade.weighting * _grade.value);
            }
            return target / grade.weighting;
        }

        // TODO: throw exception
        return 0.0D;
    }

    public double calculateAverage() {
        double total = 0.0D;
        int totalWeighting = 0;

        for(final Grade grade : grades) {
            if (grade.isSet) {
                totalWeighting += grade.weighting;
                total += grade.value * grade.weighting;
            }
        }

        if(totalWeighting != 0)
            return total / totalWeighting;
        return 0.0D;
     }

}
