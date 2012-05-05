package fr.xgouchet.texteditor.undo;

import android.text.Editable;

public interface TextChange {

	/**
	 * Undo this change
	 * 
	 * @param text
	 *            the editable object on which the undo is done
	 * @return the caret position after the undo
	 */
	public int undo(Editable text);

	/**
	 * @return the caret position after this change
	 */
	public int getCaret();
}
