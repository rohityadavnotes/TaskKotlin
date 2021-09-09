package com.task.utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.loader.content.CursorLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RealPathUtils {

    public static final String TAG = RealPathUtils.class.getSimpleName();

    private static Uri contentUri = null;

    private RealPathUtils() {
        throw new UnsupportedOperationException("You can't create instance of Util class. Please use as static..");
    }

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        /* SDK < API11 */
        if (Build.VERSION.SDK_INT < 11)
        {
            realPath = RealPathUtils.getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        /* SDK >= 11 && SDK < 19 */
        else if (Build.VERSION.SDK_INT < 19)
        {
            realPath = RealPathUtils.getRealPathFromURI_API11to18(context, fileUri);
        }
        /* SDK > 19 (Android 4.4) and up */
        else
        {
            realPath = RealPathUtils.getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }

    public static String getRealPathFromURI_BelowAPI11(final Context context, final Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null)
        {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    public static String getRealPathFromURI_API11to18(final Context context, final Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context application context
     * @param uri uri from path
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        Log.e(TAG, "File - " +
                "Authority: " + uri.getAuthority() +
                ", Fragment: " + uri.getFragment() +
                ", Port: " + uri.getPort() +
                ", Query: " + uri.getQuery() +
                ", Scheme: " + uri.getScheme() +
                ", Host: " + uri.getHost() +
                ", Segments: " + uri.getPathSegments().toString()
        );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        String selection = null;
        String[] selectionArgs = null;

        /* DocumentProvider */
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            /* ExternalStorageProvider */
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "")
                {
                    return fullPath;
                }
                else
                {
                    return null;
                }
            }
            /* DownloadsProvider */
            else if (isDownloadsDocument(uri))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst())
                        {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path))
                            {
                                return path;
                            }
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id))
                    {
                        if (id.startsWith("raw:"))
                        {
                            return id.replaceFirst("raw:", "");
                        }

                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };

                        for (String contentUriPrefix : contentUriPrefixesToTry)
                        {
                            try
                            {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                                /* final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id)); */
                                return getDataColumn(context, contentUri, null, null);
                            }
                            catch (NumberFormatException e)
                            {
                                /* In Android 8 and Android P the id is not a number */
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }
                    }
                }
                else
                {
                    final String id = DocumentsContract.getDocumentId(uri);

                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }

                    try
                    {
                        contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    }
                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }

                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                }
            }
            /* MediaProvider */
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                selection = "_id=?";
                selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
            else if (isGoogleDriveUri(uri))
            {
                return getDriveFilePath(uri, context);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            /* Return the remote address */
            if (isGooglePhotosUri(uri))
            {
                return uri.getLastPathSegment();
            }

            if (isGoogleDriveUri(uri))
            {
                return getDriveFilePath(uri, context);
            }

            if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            {
                return getMediaFilePathForN(uri, context);
            }
            else
            {
                return getDataColumn(context, uri, null, null);
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        /*
         * on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
         * something like "71F8-2C0A", some kind of unique id per storage
         * don't know any API that can get the root path of that storage based on its id.
         * so no "primary" type, but let the check here for other devices
         */
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (isFileExists(fullPath)) {
                return fullPath;
            }
        }

        /**
         * fix some devices(Android Q),'type' like "71F8-2C0A"
         * but "primary".equalsIgnoreCase(type) is false
         */
        fullPath = "/storage/" + type + "/" + relativePath;
        if (isFileExists(fullPath)) {
            return fullPath;
        }

        /*
         * Environment.isExternalStorageRemovable() is `true` for external and internal storage
         * so we cannot relay on it.
         * instead, for each possible path, check if file exists
         * we'll start with secondary storage as this could be our (physically) removable sd card
         */
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (isFileExists(fullPath)) {
            Log.e("File Size", "SECONDARY_STORAGE " + fullPath);
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (isFileExists(fullPath)) {
            Log.e("File Size", "SECONDARY_STORAGE " + fullPath);
            return fullPath;
        }

        return "";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static String getMediaFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            /* int bufferSize = 1024; */
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            /* int bufferSize = 1024; */
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return ("com.google.android.apps.photos.content".equals(uri.getAuthority())
                || "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority()));
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    /**
     * create file object using String file path
     *
     * @param filePath file path as string
     * @return file object
     */
    public static File getFileByPath(String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Determine whether the file exists
     *
     * @param filePath file path
     * @return {@code true}: exists<br>{@code false}: does not exist
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * Determine whether the file exists
     *
     * @param file file
     * @return {@code true}: exists<br>{@code false}: does not exist
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * Determine whether the string is null or all blank characters
     *
     * isSpace(null)        = true;
     * isSpace("     ")     = true;
     * isSpace("   Hello ") = false;
     * isSpace("null")      = false;
     * isSpace("")          = true;
     *
     * @param string String to be verified
     * @return {@code true}: null or all whitespace characters<br> {@code false}: not null and not all whitespace characters
     */
    public static boolean isSpace(String string) {
        if (string == null) return true;
        for (int i = 0, len = string.length(); i < len; ++i) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}