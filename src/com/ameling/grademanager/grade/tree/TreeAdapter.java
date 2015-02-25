package com.ameling.grademanager.grade.tree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class TreeAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	protected static class IdCounter {

		private IdCounter () {}

		private int nextID = 1;
		private int nextIndent = 1;

		public int nextID () {
			return nextID++;
		}

		public int nextIndent () {
			return nextIndent++;
		}
	}

	private final Context context;
	private final LayoutInflater layoutInflater;

	private final TreeGroup group;
	private final int itemCount;

	public TreeAdapter(final Context context, final ITreeNode parentNode) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);

		if (parentNode.hasChildNodes()) {
			final IdCounter counter = new IdCounter();
			group = new TreeGroup(parentNode, counter);
			this.itemCount = counter.nextID;
		} else {
			throw new NullPointerException();
		}
		Log.i("", "");
	}

	@Override
	public int getCount () {
		return itemCount;
	}

	@Override
	public Object getItem (final int id) {
		if (id < 0 || id >= itemCount)
			return null;

		if (id == 0)
			return group;
		return group.getItem(id);
	}

	@Override
	public long getItemId (final int id) {
		return id;
	}

	@Override
	public View getView (final int id, View view, final ViewGroup parent) {
		final Object object = (id == 0 ? group : group.getItem(id));
		Log.i("test", "creating view: " + id);

		if (object instanceof TreeGroup) {
			final TreeGroup group = (TreeGroup) object;
			view = layoutInflater.inflate(group.getInflatableResource(), parent, false);
			group.populateView(view);
		} else {
			final ITreeNode node = (ITreeNode) object;
			view = layoutInflater.inflate(node.getInflatableResource(), parent, false);
			node.populateView(view);
		}

		final TreeGroup group = (id == 0 ? this.group : this.group.getParent(id));
		if (id != 0 && group.isCollapsed) {
			Log.i("test", "setting invisible");
			view.setVisibility(View.GONE);
		}

		// Create a wrapper with the indentation
		final LinearLayout wrapperLayout = new LinearLayout(context);
		wrapperLayout.setOrientation(LinearLayout.VERTICAL);
		wrapperLayout.setPadding((id == 0 ? 0 : group.getIndentation(id)) * 30, 0, 0, 0);
		wrapperLayout.addView(view);
		return wrapperLayout;
	}

	@Override
	public void onItemClick (final AdapterView<?> adapterView, final View view, final int id, final long l) {
		final Object object = (id == 0 ? group : getItem(id));
		Log.i("test", "Clicked item");
		if (object instanceof TreeGroup) {
			final TreeGroup group = (TreeGroup) object;
			group.isCollapsed = !group.isCollapsed;
			notifyDataSetChanged();
		}
	}
}
