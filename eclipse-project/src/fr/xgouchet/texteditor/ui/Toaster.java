package fr.xgouchet.texteditor.ui;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

public class Toaster {

	/**
	 * Show a toast message to the user
	 * 
	 * @param ctx
	 *            The context to use
	 * @param resId
	 *            the id of the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	public static void showToast(Context ctx, int resId, boolean error) {
		Toast toast;
		TextView v;

		toast = Toast.makeText(ctx, resId, Toast.LENGTH_SHORT);
		if (error) {
			v = (TextView) toast.getView().findViewById(android.R.id.message);
			v.setTextColor(mError);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();
	}

	protected static final int mError = Color.rgb(255, 128, 64);
}
