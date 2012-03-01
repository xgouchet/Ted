package fr.xgouchet.ted.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import fr.xgouchet.ted.R;
import fr.xgouchet.ted.common.Constants;

public class DialogSave extends Dialog implements Constants,
		View.OnClickListener {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The Context the Dialog is to run it. In particular, it uses
	 *            the window manager and theme in this context to present its
	 *            UI.
	 */
	public DialogSave(final Activity context) {
		super(context);

		// setup content view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_save);
		setCancelable(true);

		// add Buttons actions
		findViewById(R.id.buttonCancel).setOnClickListener(this);
		findViewById(R.id.buttonDontSave).setOnClickListener(this);
		findViewById(R.id.buttonSave).setOnClickListener(this);

		// default values
		mCanceled = true;
		mSave = false;
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSave:
			mCanceled = false;
			mSave = true;
			break;
		case R.id.buttonDontSave:
			mCanceled = false;
			mSave = false;
			break;
		case R.id.buttonCancel:
		default:
			Log.i(TAG, "Unknown view id in DialogSave#onClick");
			mCanceled = true;
			break;
		}

		dismiss();
	}

	/**
	 * @return if the view was canceled by the user
	 */
	public boolean isCanceled() {
		return mCanceled;
	}

	/**
	 * @return if the save option was selected
	 */
	public boolean isSaved() {
		return mSave;
	}

	/** has the dialog been canceld */
	protected boolean mCanceled;
	/** save ? */
	protected boolean mSave;

}
