package fr.xgouchet.ted.ui.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.xgouchet.ted.R;

/**
 * A File List Adapter used to display folders and files
 * 
 */
public class FileListAdapter extends ArrayAdapter<File> {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public FileListAdapter(Context context, ArrayList<File> objects) {
		super(context, R.layout.item_file, objects);
	}

	/**
	 * @see ArrayAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		File file;
		View v;
		TextView textView;
		String text;
		int icon;

		// recycle the view
		v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_file, null);
		}

		// get the file
		file = getItem(position);

		if (file != null) {
			text = file.getName();
			if (file.isDirectory()) {
				if (!file.canRead())
					icon = R.drawable.folder_private;
				else if (!file.canWrite())
					icon = R.drawable.folder_locked;
				else
					icon = R.drawable.folder;
			} else {
				if (!file.canRead())
					icon = R.drawable.file_private;
				else if (!file.canWrite())
					icon = R.drawable.file_locked;
				else
					icon = R.drawable.file;
			}
		} else {
			text = "";
			icon = R.drawable.unknown;
		}

		textView = (TextView) v.findViewById(R.id.textFileName);

		if (textView != null) {
			textView.setText(text);
			textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		}

		return v;
	}
}
