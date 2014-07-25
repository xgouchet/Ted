package fr.xgouchet.texteditor;

import android.os.Bundle;
import android.view.Window;
import fr.xgouchet.androidlib.ui.activity.AboutActivity;
import fr.xgouchet.texteditor.common.Settings;

public class TedAboutActivity extends AboutActivity {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!Settings.SHOW_TITLE_BAR) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		setContentView(R.layout.layout_about);
	}
}
