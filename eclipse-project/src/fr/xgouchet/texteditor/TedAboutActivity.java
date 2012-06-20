package fr.xgouchet.texteditor;

import static fr.xgouchet.androidlib.common.MiscUtils.openMarket;
import static fr.xgouchet.androidlib.common.MiscUtils.sendEmail;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TedAboutActivity extends Activity implements OnClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_about);

		findViewById(R.id.buttonMail).setOnClickListener(this);
		findViewById(R.id.buttonMarket).setOnClickListener(this);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.buttonMail:
			sendEmail(this, getResources().getString(R.string.app_name_full));
			break;
		case R.id.buttonMarket:
			openMarket(this);
			break;
		}
	}

}
