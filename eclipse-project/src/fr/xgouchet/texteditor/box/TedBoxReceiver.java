package fr.xgouchet.texteditor.box;

/*
 * Box One Cloud Integration example This requires the OneCloupAppToApp SDK which can be found at: https://github.com/box/box-android-sdk/tree/experimental
 * check that out and add the library to your project
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.box.onecloud.android.OneCloudData;

public class TedBoxReceiver extends com.box.onecloud.android.BoxOneCloudReceiver {

    @Override
    public void onEditFileRequested(Context context, OneCloudData oneCloudData) {
        // Box has requested that a file be edited.
        Intent editIntent;
        editIntent = new Intent(context, TedBoxActivity.class);
        editIntent.setAction("fr.xgouchet.texteditor.ACTION_TED_BOX_OPEN");
        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        editIntent.putExtra("one_cloud_data", oneCloudData);
        editIntent.setType("text/plain");
        try {
            context.startActivity(editIntent);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateFileRequested(Context context, OneCloudData oneCloudData) {
        // Box has requested that a file be created.
        Intent createIntent;
        createIntent = new Intent(context, TedBoxActivity.class);
        createIntent.setAction("fr.xgouchet.texteditor.ACTION_TED_BOX_CREATE_FILE");
        createIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        createIntent.setPackage(context.getPackageName());
        createIntent.putExtra("one_cloud_data", oneCloudData);
        createIntent.setType("text/plain");
        try {
            context.startActivity(createIntent);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewFileRequested(Context context, OneCloudData oneCloudData) {
    }

    @Override
    public void onLaunchRequested(Context context, OneCloudData oneCloudData) {
    };
}
