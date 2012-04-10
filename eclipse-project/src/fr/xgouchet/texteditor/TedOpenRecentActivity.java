package fr.xgouchet.texteditor;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.RecentFiles;
import fr.xgouchet.texteditor.ui.adapter.PathListAdapter;

public class TedOpenRecentActivity extends Activity implements Constants,
		OnClickListener, OnItemClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup content view
		setContentView(R.layout.layout_open);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);

		// widgets
		mFilesList = (ListView) findViewById(R.id.listItems);
		mFilesList.setOnItemClickListener(this);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();

		fillRecentFilesView();
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String path;

		path = mList.get(position);

		if (setOpenResult(new File(path))) {
			finish();
		} else {
			RecentFiles.removePath(path);
			RecentFiles.saveRecentList(getSharedPreferences(PREFERENCES_NAME,
					MODE_PRIVATE));
			((PathListAdapter) mListAdapter).notifyDataSetChanged();
		}

	}

	/**
	 * Fills the files list with the recent files
	 * 
	 */
	protected void fillRecentFilesView() {
		mList = RecentFiles.getRecentFiles();

		if (mList.size() == 0) {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		// create string list adapter
		mListAdapter = new PathListAdapter(this, mList);

		// set adpater
		mFilesList.setAdapter(mListAdapter);
	}

	/**
	 * Set the result of this activity to open a file
	 * 
	 * @param file
	 *            the file to return
	 * @return if the result was set correctly
	 */
	protected boolean setOpenResult(File file) {
		Intent result;

		if ((file == null) || (!file.isFile()) || (!file.canRead())) {
			return false;
		}

		result = new Intent();
		result.putExtra("path", file.getAbsolutePath());

		setResult(RESULT_OK, result);
		return true;
	}

	/** the dialog's list view */
	protected ListView mFilesList;
	/** The list adapter */
	protected ListAdapter mListAdapter;

	/** the list of recent files */
	protected ArrayList<String> mList;
}
