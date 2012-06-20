package fr.xgouchet.androidlib.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.ui.Toaster;

public class MiscUtils {

	/**
	 * Start an email composer to send an email
	 * 
	 * @param ctx
	 *            the current context
	 * @param object
	 *            the title of the mail to compose
	 */
	public static void sendEmail(Context ctx, String object) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { ctx.getResources()
				.getString(R.string.ui_mail) });
		email.putExtra(Intent.EXTRA_SUBJECT, object);
		ctx.startActivity(Intent.createChooser(email,
				ctx.getString(R.string.ui_choose_mail)));
	}

	/**
	 * Open the market on my apps
	 * 
	 * @param ctx
	 *            the current context
	 */
	public static void openMarket(Context ctx) {
		String url;
		Intent market = new Intent(Intent.ACTION_VIEW);
		// market.setData(Uri.parse("market://search?q=pub:Xavier Gouchet"));
		url = ctx.getResources().getString(R.string.ui_market_url);
		market.setData(Uri.parse(url));
		try {
			ctx.startActivity(market);
		} catch (ActivityNotFoundException e) {
			Log.e("AndroidLib", "Market Activity", e);
			Toaster.showToast(ctx, R.string.toast_no_market, true);
		}
	}
}
