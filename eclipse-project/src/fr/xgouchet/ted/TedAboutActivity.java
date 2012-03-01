package fr.xgouchet.ted;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import fr.xgouchet.ted.ui.Toaster;

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
			sendEmail();
			break;
		case R.id.buttonMarket:
			openMarket();
			break;
		}
	}

	/**
	 * Start an email composer to send an email
	 */
	public void sendEmail() {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources()
				.getString(R.string.ui_mail) });
		email.putExtra(Intent.EXTRA_SUBJECT, "Ted (Text Editor)");
		startActivity(Intent.createChooser(email,
				getString(R.string.ui_choose_mail)));
	}

	/**
	 * Open the market on my apps
	 */
	public void openMarket() {
		Intent market = new Intent(Intent.ACTION_VIEW);
		// market.setData(Uri.parse("market://search?q=pub:Xavier Gouchet"));
		market.setData(Uri
				.parse("https://market.android.com/developer?pub=Xavier+Gouchet"));
		try {
			startActivity(market);
		} catch (ActivityNotFoundException e) {
			Toaster.showToast(this, R.string.toast_no_market, true);
		}
	}

}
