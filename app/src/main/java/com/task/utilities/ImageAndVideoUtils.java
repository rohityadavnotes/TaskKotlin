package com.task.utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;

public class ImageAndVideoUtils {

    private ImageAndVideoUtils() {
        throw new UnsupportedOperationException("Should not create instance of Util class. Please use as static..");
    }

    public static void cameraIntent(int mediaType, Uri storeOnThisUri, Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
        if (mediaType == 1)
        {
            /* Capture image intent */
            Intent captureImageIntent   = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean canCaptureImage     = storeOnThisUri != null && captureImageIntent.resolveActivity(activity.getPackageManager()) != null;

            if(canCaptureImage)
            {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, storeOnThisUri);
                captureImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activityResultLauncher.launch(captureImageIntent);
            }
        }
        else if (mediaType == 2)
        {
            /* Capture video intent */
            Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            boolean canCaptureVideo     = storeOnThisUri != null && captureVideoIntent.resolveActivity(activity.getPackageManager()) != null;

            if(canCaptureVideo)
            {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                captureVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, storeOnThisUri);
                captureVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activityResultLauncher.launch(captureVideoIntent);
            }
        }
    }
}
