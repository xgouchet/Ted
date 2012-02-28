package fr.xgouchet.texteditor.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import fr.xgouchet.texteditor.R;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.Settings;

/**
 * TODO create a syntax highlighter
 */
public class AdvancedEditText extends EditText implements Constants,
		OnKeyListener {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param attrs
	 *            some attributes
	 */
	public AdvancedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		float scale;

		mPaintNumbers = new Paint();
		mPaintNumbers.setTypeface(Typeface.MONOSPACE);
		mPaintNumbers.setAntiAlias(true);

		scale = context.getResources().getDisplayMetrics().density;
		mPadding = (int) (mPaddingDP * scale);

		updateFromSettings();
	}

	/**
	 * @see EditText#onDraw(Canvas)
	 */
	public void onDraw(Canvas canvas) {
		int count, padding, lineX;

		// padding
		count = getLineCount();
		padding = 5;
		if (Settings.SHOW_LINE_NUMBERS) {
			padding = (int) (Math.floor(Math.log10(count)) + 1);
			padding = (padding * (Settings.TEXT_SIZE - 2)) + mPadding
					+ (Settings.TEXT_SIZE / 2);
			setPadding(padding, mPadding, mPadding, mPadding);
		} else {
			setPadding(mPadding, mPadding, mPadding, mPadding);
		}

		// get the drawing boundaries
		Rect lineBounds = new Rect(), drawing = new Rect();
		getDrawingRect(drawing);

		// draw line numbers
		count = getLineCount();
		lineX = (drawing.left + padding - (Settings.TEXT_SIZE / 2));
		if (Settings.SHOW_LINE_NUMBERS)
			for (int i = 0; i < count; i++) {
				int baseline = getLineBounds(i, lineBounds);
				canvas.drawLine(lineX, lineBounds.top, lineX,
						lineBounds.bottom, mPaintNumbers);
				canvas.drawText("" + (i + 1), drawing.left + mPadding,
						baseline, mPaintNumbers);
			}

		super.onDraw(canvas);
	}

	/**
	 * @see OnKeyListener#onKey(View, int, KeyEvent)
	 */
	public boolean onKey(View view, int keycode, KeyEvent event) {
		if ((keycode == KeyEvent.KEYCODE_DPAD_UP)
				|| (keycode == KeyEvent.KEYCODE_DPAD_DOWN)
				|| (keycode == KeyEvent.KEYCODE_DPAD_LEFT)
				|| (keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
				|| (keycode == KeyEvent.KEYCODE_DPAD_CENTER))
			return false;

		if (keycode == KeyEvent.KEYCODE_MENU)
			return false;

		if (event.getAction() == KeyEvent.ACTION_DOWN)
			return false;

		return false;
	}

	/**
	 * Update view settings from the app preferences
	 */
	public void updateFromSettings() {

		if (isInEditMode())
			return;

		// wordwrap
		setHorizontallyScrolling(!Settings.WORDWRAP);

		// color Theme
		switch (Settings.COLOR) {
		case COLOR_NEGATIVE:
			setBackgroundResource(R.drawable.textfield_black);
			setTextColor(Color.WHITE);
			mPaintNumbers.setColor(Color.GRAY);
			break;
		case COLOR_MATRIX:
			setBackgroundResource(R.drawable.textfield_matrix);
			setTextColor(Color.GREEN);
			mPaintNumbers.setColor(Color.rgb(0, 128, 0));
			break;
		case COLOR_SKY:
			setBackgroundResource(R.drawable.textfield_sky);
			setTextColor(Color.rgb(0, 0, 64));
			mPaintNumbers.setColor(Color.rgb(0, 128, 255));
			break;
		case COLOR_CLASSIC:
		default:
			setBackgroundResource(R.drawable.textfield_white);
			setTextColor(Color.BLACK);
			mPaintNumbers.setColor(Color.GRAY);
			break;
		}

		// text size
		setTextSize(Settings.TEXT_SIZE);
		mPaintNumbers.setTextSize(Settings.TEXT_SIZE - 2);

		// refresh view
		postInvalidate();
		refreshDrawableState();

	}

	/** The line numbers paint */
	protected Paint mPaintNumbers;
	/** the offset value in dp */
	protected int mPaddingDP = 6;
	/** the padding scaled */
	protected int mPadding;

}
