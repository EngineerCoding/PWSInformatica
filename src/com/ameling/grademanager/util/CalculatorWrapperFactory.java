package com.ameling.grademanager.util;

import com.ameling.parser.grade.Calculator;
import com.ameling.parser.grade.ExpressionCalculator;
import com.ameling.parser.grade.Grade;

import java.util.List;

public abstract class CalculatorWrapperFactory {

	private static interface IWrapper {
		public String getExpression();

		public List<Grade> getGrades();
	}

	public static class CalculatorProxy extends Calculator {

		public final String expression;

		private CalculatorProxy (final IWrapper wrapper) {
			this(wrapper.getGrades().toArray(new Grade[wrapper.getGrades().size()]), wrapper.getExpression());
		}

		private CalculatorProxy (final Grade[] grades, final String expression) {
			super(grades);
			this.expression = expression;
		}

	}

	public static CalculatorProxy createCalculator (final Grade[] grades, final String expression) {
		if (expression == null || expression.isEmpty())
			throw new IllegalArgumentException();

		return grades == null ?
				new CalculatorProxy(new WrapperExpressionCalculator(expression)) :
				new CalculatorProxy(grades, expression);
	}

	private static class WrapperExpressionCalculator extends ExpressionCalculator implements IWrapper {

		private final String expression;

		public WrapperExpressionCalculator (final String expression) {
			super(expression);
			this.expression = expression;
		}

		@Override
		public String getExpression() {
			return expression;
		}

		@Override
		public List<Grade> getGrades () {
			return grades;
		}
	}

}
