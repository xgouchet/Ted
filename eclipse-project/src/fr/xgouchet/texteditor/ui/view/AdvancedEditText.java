package fr.xgouchet.texteditor.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Scroller;
import fr.xgouchet.texteditor.R;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.Settings;

/**
 * TODO create a syntax highlighter
 */
public class AdvancedEditText extends EditText implements Constants,
		OnKeyListener, OnGestureListener {

	/**
	 * @param context
	 *            the current context
	 * @param attrs
	 *            some attributes
	 * @category ObjectLifecycle
	 */
	public AdvancedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		float scale;

		mPaintNumbers = new Paint();
		mPaintNumbers.setTypeface(Typeface.MONOSPACE);
		mPaintNumbers.setAntiAlias(true);

		mPaintHighlight = new Paint();
		

		scale = context.getResources().getDisplayMetrics().density;
		mPadding = (int) (mPaddingDP * scale);

		mHighlightedLine = mHighlightStart = -1;

		updateFromSettings();
	}

	/**
	 * @see android.widget.TextView#computeScroll()
	 * @category View
	 */
	public void computeScroll() {

		if (mScroller != null) {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			}
		} else {
			super.computeScroll();
		}
	}

	/**
	 * @see EditText#onDraw(Canvas)
	 * @category View
	 */
	public void onDraw(Canvas canvas) {
		int count, padding, lineX, baseline, selStart, selEnd;

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

		// display current line
		computeLineHighlight();

		// draw line numbers
		count = getLineCount();
		lineX = (drawing.left + padding - (Settings.TEXT_SIZE / 2));
		if (Settings.SHOW_LINE_NUMBERS) {
			for (int i = 0; i < count; i++) {
				baseline = getLineBounds(i, lineBounds);
				if (mMaxSize.x < lineBounds.right)
					mMaxSize.x = lineBounds.right;

				if (lineBounds.bottom < drawing.top)
					continue;
				if (lineBounds.top > drawing.bottom)
					continue;

				if (i == mHighlightedLine)
					canvas.drawRect(lineBounds, mPaintHighlight);

				canvas.drawText("" + (i + 1), drawing.left + mPadding,
						baseline, mPaintNumbers);
			}
			canvas.drawLine(lineX, drawing.top, lineX, drawing.bottom,
					mPaintNumbers);
		}

		// Log.d(TAG, (selStart + " - " + selEnd));

		if (mMaxSize != null) {
			mMaxSize.y = lineBounds.bottom;
			mMaxSize.x = Math.max(mMaxSize.x + mPadding - drawing.width(), 0);
			mMaxSize.y = Math.max(mMaxSize.y + mPadding - drawing.height(), 0);
		}
		super.onDraw(canvas);
	}

	/**
	 * @see OnKeyListener#onKey(View, int, KeyEvent)
	 * @category OnKeyListener
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
	 * @see android.widget.TextView#onTouchEvent(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onDown(MotionEvent e) {
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public void onShowPress(MotionEvent e) {
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	public void onLongPress(MotionEvent e) {
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent,
	 *      android.view.MotionEvent, float, float)
	 */
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent,
	 *      android.view.MotionEvent, float, float)
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (mScroller != null) {
			mScroller.fling(getScrollX(), getScrollY(), -(int) velocityX,
					-(int) velocityY, 0, mMaxSize.x, 0, mMaxSize.y);
		}
		return true;
	}

	/**
	 * Update view settings from the app preferences
	 * 
	 * @category Custom
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
			mPaintHighlight.setColor(Color.WHITE);
			mPaintNumbers.setColor(Color.GRAY);
			break;
		case COLOR_MATRIX:
			setBackgroundResource(R.drawable.textfield_matrix);
			setTextColor(Color.GREEN);
			mPaintHighlight.setColor(Color.GREEN);
			mPaintNumbers.setColor(Color.rgb(0, 128, 0));
			break;
		case COLOR_SKY:
			setBackgroundResource(R.drawable.textfield_sky);
			setTextColor(Color.rgb(0, 0, 64));
			mPaintHighlight.setColor(Color.rgb(0, 0, 64));
			mPaintNumbers.setColor(Color.rgb(0, 128, 255));
			break;
		case COLOR_DRACULA:
			setBackgroundResource(R.drawable.textfield_dracula);
			setTextColor(Color.RED);
			mPaintHighlight.setColor(Color.RED);
			mPaintNumbers.setColor(Color.rgb(192, 0, 0));
			break;
		case COLOR_CLASSIC:
		default:
			setBackgroundResource(R.drawable.textfield_white);
			setTextColor(Color.BLACK);
			mPaintHighlight.setColor(Color.BLACK);
			mPaintNumbers.setColor(Color.GRAY);
			break;
		}
		mPaintHighlight.setAlpha(48);
		
		// text size
		setTextSize(Settings.TEXT_SIZE);
		mPaintNumbers.setTextSize(Settings.TEXT_SIZE - 2);

		// refresh view
		postInvalidate();
		refreshDrawableState();

		// TODO use Fling when scrolling settings ?
		mGestureDetector = new GestureDetector(getContext(), this);
		mScroller = new Scroller(getContext());
		mMaxSize = new Point();
	}

	/**
	 * Compute the line to highlight based on selection
	 */
	protected void computeLineHighlight() {
		int i, line, selStart;
		String text;

		if (!isEnabled()) {
			mHighlightedLine = -1;
			return;
		}

		selStart = getSelectionStart();
		if (mHighlightStart != selStart) {
			text = getText().toString();

			line = i = 0;
			while (i < selStart) {
				i = text.indexOf("\n", i);
				if (i < 0)
					break;
				if (i < selStart)
					++line;
				++i;
			}

			mHighlightedLine = line;
		}
	}

	/** The line numbers paint */
	protected Paint mPaintNumbers;
	/** The line numbers paint */
	protected Paint mPaintHighlight;
	/** the offset value in dp */
	protected int mPaddingDP = 6;
	/** the padding scaled */
	protected int mPadding;

	/** the scroller instance */
	protected Scroller mScroller;
	/** the velocity tracker */
	protected GestureDetector mGestureDetector;
	/** the Max size of the view */
	protected Point mMaxSize;

	/** the highlighted line index */
	protected int mHighlightedLine;
	protected int mHighlightStart;
}
