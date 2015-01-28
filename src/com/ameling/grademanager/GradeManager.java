package com.ameling.grademanager;

import android.app.Activity;
import android.os.Bundle;
import com.ameling.grademanager.io.FileManager;

public class GradeManager extends Activity {

	public static FileManager fileManager;

	@Override
	public void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);

		fileManager = new FileManager(this);


	}
}
