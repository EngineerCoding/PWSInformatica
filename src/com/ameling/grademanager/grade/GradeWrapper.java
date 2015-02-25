package com.ameling.grademanager.grade;


import android.view.View;
import android.widget.TextView;
import com.ameling.grademanager.R;
import com.ameling.grademanager.grade.tree.GradeNode;
import com.ameling.grademanager.grade.tree.ITreeNode;
import com.ameling.parser.grade.Grade;

/**
 * A wrapper class for {@link Grade} so it can contain sub-expressions which are stored in a {@link CalculatorWrapper}. The converter of this class can be found in {@link
 * com.ameling.grademanager.grade.GradeConverter}.<br/>
 * This implements {@link ITreeNode} because the structure of this class is perfect to create the subject nodes from. This might be temporary and moved to a separate class to still
 * have a nice overview of what class interferes with what.
 */
public class GradeWrapper extends Grade implements ITreeNode {

	/**
	 * The calculator with sub grades
	 */
	public CalculatorWrapper calculator;

	/**
	 * The child nodes from this object, or the grades in the calculator
	 */
	private ITreeNode[] childNodes = new ITreeNode[0];

	/**
	 * Setup super values
	 *
	 * @param name      The grade's id
	 * @param weighting The grade's weighting
	 */
	public GradeWrapper (final String name, final int weighting) {
		super(name, weighting);
	}

	/**
	 * Creates from the given grade a wrapper
	 *
	 * @param grade The grade with the weighting and name
	 */
	public GradeWrapper (final Grade grade) {
		this(grade.name, grade.weighting);
	}

	/**
	 * Sets the calculator as the calculator to use on how to calculate this grade. When the calculator
	 * has no sub-grades, the calculator is discarded.
	 *
	 * @param calculator The calculator to set
	 */
	public void setSubGrades (final CalculatorWrapper calculator) {
		if (calculator != null && calculator.grades.size() > 0) {
			this.calculator = calculator;

			childNodes = new ITreeNode[calculator.grades.size()];
			for (int i = 0; i < childNodes.length; i++) {
				final Grade grade = calculator.grades.get(i);
				childNodes[i] = (grade instanceof GradeWrapper ? (GradeWrapper) grade : new GradeNode(grade));
			}
		}
	}

	@Override
	public double getValue () {
		if (calculator != null)
			return calculator.calculateAverage();
		return 0D;
	}

	@Override
	public void setValue (final double value) {
		// This method is not available since this is a wrapper
		throw new IllegalAccessError();
	}

	@Override
	public boolean hasValue () {
		if (calculator != null)
			for (final Grade grade : calculator.grades)
				if (grade.hasValue())
					return true;
		return false;
	}

	@Override
	public boolean hasChildNodes () {
		return calculator != null;
	}

	@Override
	public ITreeNode[] getChildNodes () {
		return childNodes;
	}

	@Override
	public int getInflatableResource () {
		return R.layout.text_item_list;
	}

	@Override
	public void populateView (final View view) {
		((TextView) view.findViewById(android.R.id.text1)).setText(name);
	}

	@Override
	public Grade clone () {
		final GradeWrapper wrapper = new GradeWrapper (this);
		wrapper.setSubGrades(calculator.clone());
		return wrapper;
	}
}
