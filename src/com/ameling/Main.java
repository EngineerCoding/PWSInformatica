package com.ameling;

import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;

import java.math.BigDecimal;

public class Main {

	public static void main (String[] args) {
		final Grade SE1 = new Grade("SE1", 1);
		final Grade SE2 = new Grade("SE2", 1);
		final Grade SE3 = new Grade("SE3", 1);
		final Grade SE4 = new Grade("SE4", 1);

		SE1.setGrade(9.1);
		SE2.setGrade(7.7);

		final Calculator calc = new Calculator(new Grade[]{ SE1, SE2, SE3, SE4 });
		System.out.println("TEST: MANUAL GRADES");
		System.out.println(calc.calculateGrade("SE3", 8)); // 7.2
		System.out.println(calc.calculateAverage()); // 8.4

		final Calculator _calc = new ExpressionCalculator("(SE1 + 5((SE2 + SE3)/2) + SE4)/7");
		System.out.println("TEST: EXPRESSION GRADES");
		_calc.getGrade("SE1").setGrade(9.1);
		_calc.getGrade("SE2").setGrade(7.7);

		System.out.println(_calc.calculateGrade("SE3", 8)); // 7.2
		System.out.println(_calc.calculateAverage()); // 8.4

		BigDecimal dec = new BigDecimal(_calc.calculateAverage());
		BigDecimal a = dec.setScale(1, BigDecimal.ROUND_HALF_EVEN);
		System.out.println(a.toString());
	}
}
