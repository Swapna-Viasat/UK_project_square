/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaTools {

    private static final String TAG = "MediaTools";
    private final static String PRIMARY_VOLUME_NAME = "primary";
    private final static String IMAGE_MEDIA_TYPE_NAME = "image";
    private final static String VIDEO_MEDIA_TYPE_NAME = "video";
    private final static String DOWNLOADS_CONTENT_URI = "content://downloads/public_downloads";
    private final static String CONTENT_SCHEME = "content";
    private final static String FILE_SCHEME = "file";
    private final static String EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents";
    private final static String DOWNLOADS_AUTHORITY = "com.android.providers.downloads.documents";
    private final static String MEDIA_AUTHORITY = "com.android.providers.media.documents";
    private static final String IMAGE_DIRECTORY_NAME = "The Square";

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        TextTools.log(TAG, "getPath()");
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {// DocumentProvider
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                TextTools.log(TAG, "getPath() isExternalStorageDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if (PRIMARY_VOLUME_NAME.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                TextTools.log(TAG, "getPath() DownloadsProvider");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse(DOWNLOADS_CONTENT_URI), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                TextTools.log(TAG, "getPath().isMediaDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                TextTools.log(TAG, "getPath().isMediaDocument.docId: " + docId);
                final String[] split = docId.split(":");
                final String type = split[0];
                TextTools.log(TAG, "getPath().isMediaDocument.type: " + type);

                if (IMAGE_MEDIA_TYPE_NAME.equals(type)) {
                    Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                } else if (VIDEO_MEDIA_TYPE_NAME.equals(type)) {
                    Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        } else if (CONTENT_SCHEME.equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            TextTools.log(TAG, "getPath() CONTENT_SCHEME");
            return getDataColumn(context, uri, null, null);
        } else if (FILE_SCHEME.equalsIgnoreCase(uri.getScheme())) {// File
            TextTools.log(TAG, "getPath() FILE_SCHEME");
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return EXTERNAL_STORAGE_AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return DOWNLOADS_AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return MEDIA_AUTHORITY.equals(uri.getAuthority());
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
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Nullable
    public static String encodeToBase64(Bitmap bitmap) {
        try {
            if (bitmap == null) return null;
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOS);
            return "data:image/jpeg;base64," + Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        return null;
    }

    public static File getOutputImageFile() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MediaTools.IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            TextTools.log(TAG, "Oops! Failed create " + MediaTools.IMAGE_DIRECTORY_NAME + " directory");
            return null;
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public static Uri getOutputImageUri(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        cv.put(MediaStore.Video.Media.TITLE, timeStamp + ".jpg");
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
    }
}
