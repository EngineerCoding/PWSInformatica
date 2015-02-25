package com.ameling.grademanager.grade;

import android.content.Intent;
import android.widget.ListView;
import com.ameling.grademanager.BaseActivity;
import com.ameling.grademanager.MainActivity;
import com.ameling.grademanager.R;
import com.ameling.grademanager.SubjectManager;
import com.ameling.grademanager.grade.tree.TreeAdapter;

public class ShowActivity extends BaseActivity {

	@Override
	public int getMainLayout () {
		return R.layout.show_subject;
	}

	@Override
	public int getMenuID () {
		return R.menu.main_acitivity_actions;
	}

	@Override
	public void initialize () {
		final Intent intent = getIntent();
		if (intent.hasExtra(MainActivity.RESULT_SUBJECT)) {
			final TreeAdapter adapter = new TreeAdapter(this, SubjectManager.instance.getSubject(intent.getStringExtra(MainActivity.RESULT_SUBJECT)).createTreeNode());
			((ListView) findViewById(R.id.subject_tree)).setAdapter(adapter);
			((ListView) findViewById(R.id.subject_tree)).setOnItemClickListener(adapter);
		} else {
			finish();
		}
	}
}
