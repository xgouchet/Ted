package fr.xgouchet.texteditor.box;

/*
 * Box One Cloud Integration example This requires the OneCloupAppToApp SDK which can be found at: https://github.com/box/box-android-sdk/tree/experimental
 * check that out and add the library to your project
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;

import com.box.onecloud.android.BoxOneCloudReceiver;
import com.box.onecloud.android.OneCloudData;
import com.box.onecloud.android.OneCloudData.UploadListener;

import fr.xgouchet.texteditor.R;
import fr.xgouchet.texteditor.TedActivity;
import fr.xgouchet.texteditor.common.Settings;

/**
 * Replace some Ted Activity functions by the equivalent operations for reading and decrypting a File from Box instead of reading from SD card and encrypting
 * and sending the file to Box for upload instead of writing the file to SD card.
 */

public class TedBoxActivity extends TedActivity {

    OneCloudData ocd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ocd = (OneCloudData) getIntent().getParcelableExtra("one_cloud_data");
        super.onCreate(savedInstanceState);
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();

        menu.add(1, MENU_ID_SAVE, Menu.NONE, R.string.menu_save).setIcon(R.drawable.file_save);

        if (mCurrentFileName == null) {
            menu.findItem(MENU_ID_SAVE).setVisible(false);
        }
        menu.add(2, MENU_ID_SAVE_AS, Menu.NONE, R.string.menu_save_as).setIcon(R.drawable.file_save_as);
        menu.add(3, MENU_ID_SEARCH, Menu.NONE, R.string.menu_search).setIcon(R.drawable.search);
        menu.add(2, MENU_ID_SETTINGS, Menu.NONE, R.string.menu_settings).setIcon(R.drawable.settings);
        menu.add(2, MENU_ID_ABOUT, Menu.NONE, R.string.menu_about).setIcon(R.drawable.unknown);

        return true;
    }

    /**
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(MENU_ID_SAVE).setVisible((mCurrentFileName != null));
        return true;
    }

    @Override
    protected void saveContent() {
        doSaveBoxFile(mCurrentFilePath, ocd);
    }

    @Override
    protected void saveContentAs() {
        this.getIntent().putExtra("SaveAs", true);
        doSaveBoxFile("", ocd);

    }

    @Override
    protected void readIntent() {
        Intent intent;
        intent = getIntent();
        if (intent == null) {
            Log.d(TAG, "No intent found, ignoring");
            return;
        }
        if (intent.getAction() == BoxOneCloudReceiver.ACTION_BOX_CREATE_FILE) {
            mCurrentFileName = null;
        }
        else { // only other possibility is ACTION_BOX_EDIT_FILE
            mEditor.setText(readStreamAsString(ocd));
            mDirty = false;
            mCurrentFileName = ocd.getFileName();
        }
        updateTitle();
    }

    @Override
    protected void updateTitle() {
        String title;
        String name;
        name = "?";
        if ((mCurrentFileName != null) && (mCurrentFileName.length() > 0))
            name = mCurrentFileName;
        if (mReadOnly)
            title = getString(R.string.title_box_editor_readonly, name);
        else {
            if (mDirty)
                title = getString(R.string.title_box_editor_dirty, name);
            else
                title = getString(R.string.title_box_editor, name);
        }
        setTitle(title);
    }

    /**
     * Saves the text editor's content into a file at the given path. If an after save {@link Runnable} exists, run it
     * 
     * @param path
     *            the path to the file (must be a valid path and not null)
     * 
     * @return The file object of the saved file or null if error occurred
     */
    protected void doSaveBoxFile(String path, final OneCloudData ocd) {
        boolean saveas = this.getIntent().getBooleanExtra("SaveAs", false);
        if (!saveas) { // Save to existing file in Box
            if (writeBoxExternal(ocd, mEditor.getText().toString())) {
                try {
                    ocd.uploadNewVersion(null);
                }
                catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mDirty = false;
                updateTitle();
            }
        }
        else {
            // Save As: Create a new file in Box (in the same directory)
            if (writeBoxExternal(ocd, mEditor.getText().toString())) {
                // Set up an UploadListener so we can monitor the upload (this is optional).
                UploadListener uploadListener = new UploadListener() {

                    @Override
                    public void onProgress(long bytesTransferred, long totalBytes) {
                    }

                    @Override
                    public void onComplete() {
                        mCurrentFileName = ocd.getFileName();
                        TedBoxActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mDirty = false;
                                updateTitle();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "upload to Box failed");
                    }
                };
                try {
                    ocd.uploadNewFile(ocd.getFileName() == null ? "TedTextFile.txt" : ocd.getFileName(), uploadListener);
                }
                catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                this.getIntent().putExtra("SaveAs", false);
            }
        }
        return;
    }

    /**
     * writeBoxExternal Encrypt a string to a OneCloudBoxFile
     * 
     * @param ocf
     * @param text
     * @return boolean operation did succeed
     */
    private boolean writeBoxExternal(OneCloudData ocd, String text) {
        OutputStream stream;
        OutputStreamWriter writer;
        BufferedWriter out;
        String eol_text = text;
        try {
            if (Settings.END_OF_LINE != EOL_LINUX) {
                eol_text = eol_text.replaceAll("\n", Settings.getEndOfLine());
            }
            stream = ocd.getOutputStream();
            if (stream != null) {
                writer = new OutputStreamWriter(stream);
                out = new BufferedWriter(writer);
                out.write(eol_text);
                out.close();
            }
            else {
                Log.e(TAG, "writeBoxExternal: ocd.getOutputStream returned null no text written" + " length of text was: " + text.length());
                return false;
            }
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory error");
            return false;
        }
        catch (IOException e) {
            Log.e(TAG, "Can't write to file " + ocd.getFileName());
            return false;
        }
        return true;
    }

    /**
     * Use the OneCloudFile object passed to decrypt the file passed from Box
     * 
     * @param ocf
     *            OnceCloudFile
     * @return A String containg the plain text of the file contents
     */
    private static String readStreamAsString(OneCloudData ocd) {
        StringBuilder fileData = new StringBuilder(1024);
        char[] buf = new char[1024];
        if (ocd == null) {
            Log.e(TAG, "readStreamAsString: ocd is null");
            return "ocd was null";
        }
        try {
            Reader in = new InputStreamReader(ocd.getInputStream(), "UTF-8");
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fileData.append(buf, 0, len);
            }
            in.close();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "IO UnsuportedEncodingException";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "IO Exception";
        }
        return fileData.toString();
    }

}
