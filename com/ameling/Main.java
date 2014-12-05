package com.ameling;

import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionParser;
import com.ameling.parser.grade.Grade;

public class Main {

    public static void main(String[] args) {
        final Grade SE1 = new Grade("SE1", 1);
        final Grade SE2 = new Grade("SE2", 1);
        final Grade SE3 = new Grade("SE3", 1);
        final Grade SE4 = new Grade("SE4", 1);

        SE1.setGrade(9.1);
        SE2.setGrade(7.7);

        final Calculator calc = new Calculator(new Grade[] {SE1, SE2, SE3, SE4});
        System.out.println("TEST: MANUAL GRADES");
        System.out.println(calc.calculateGrade("SE3", 8)); // 7.2
        System.out.println(calc.calculateAverage()); // 8.4

        final Calculator _calc = new ExpressionParser("(SE1 + SE2 + SE3 + SE4)/4").gradeCalculator;
        System.out.println("TEST: EXPRESSION GRADES");
        _calc.getGrade("SE1").setGrade(9.1); // 7.2
        _calc.getGrade("SE2").setGrade(7.7); // 8.4

        System.out.println(_calc.calculateGrade("SE3", 8));
        System.out.println(_calc.calculateAverage());

    }
}
