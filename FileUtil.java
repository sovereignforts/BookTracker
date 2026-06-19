package com.booktracker.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

public class FileUtil {

    public static String getFileName(Context ctx, Uri uri) {
        if (uri == null) return "Unknown";
        String name = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = ctx.getContentResolver().query(
                    uri, new String[]{OpenableColumns.DISPLAY_NAME},
                    null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(0);
                }
            }
        }
        if (name == null) {
            name = uri.getLastPathSegment();
        }
        return name != null ? name : "Unknown file";
    }

    public static String getFileType(Context ctx, Uri uri) {
        String mime = ctx.getContentResolver().getType(uri);
        if (mime == null) {
            String ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        if (mime == null) return "PDF";
        if (mime.contains("pdf")) return "PDF";
        if (mime.contains("word") || mime.contains("openxmlformats")) return "DOCX";
        return "DOC";
    }
}
