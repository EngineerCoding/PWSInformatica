package com.ameling.grademanager.tree.subject;

import android.content.Intent;
import com.ameling.grademanager.MainActivity;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.tree.ITreeNode;
import com.ameling.grademanager.tree.TreeActivity;

public class SubjectTreeActivity extends TreeActivity {

	private SubjectManager.Subject subject;
	private SubjectNode subjectNode;

	@Override
	public void initialize () {
		final Intent intent = getIntent();
		if (intent.hasExtra(MainActivity.RESULT_SUBJECT)) {
			subject = SubjectManager.instance.getSubject(intent.getStringExtra(MainActivity.RESULT_SUBJECT)).clone();
			subjectNode = new SubjectNode(subject);
		} else {
			finish();
		}

		// Let the tree activity do its work
		super.initialize();
	}

	@Override
	public ITreeNode getParentNode () {
		return subjectNode;
	}
}
