package fr.xgouchet.texteditor.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.xgouchet.texteditor.R;

/**
 * A File List Adapter
 * 
 * @author x.gouchet
 * 
 */
public class PathListAdapter extends ArrayAdapter<String> {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 */
	public PathListAdapter(Context context, ArrayList<String> objects) {
		super(context, R.layout.item_file, objects);
	}

	/**
	 * @see ArrayAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		String path;
		TextView compound;

		// recycle view
		v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_file, null);
		}

		// get displayed file and current view
		path = getItem(position);

		// set the layout content
		compound = (TextView) v.findViewById(R.id.textFileName);
		if (compound != null) {
			if (path == null) {
				compound.setText("");
				compound.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.unknown, 0, 0, 0);
			} else {
				compound.setText(path);
				compound.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.file, 0, 0, 0);
			}
		}
		return v;
	}

}
