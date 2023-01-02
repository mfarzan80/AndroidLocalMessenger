package net.group.androidlocalmessanger.utils;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class Utils {

    public static final String TAG = "Utils";

    public static String getServerIP(Context context) {
        WifiManager manager;
        DhcpInfo dhcp;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcp = manager.getDhcpInfo();
        return Formatter.formatIpAddress(dhcp.gateway);
    }

    public static void sendFile(File file, ObjectOutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024];
        outputStream.writeLong(file.length());
        InputStream in = new FileInputStream(file);
        Log.d(TAG, "sendFile:Start " + file.length());
        int count = in.read(bytes);
        while (count != -1) {
            outputStream.write(bytes, 0, count);
            count = in.read(bytes);
        }
        Log.d(TAG, "sendFile: End" + file.length());
        outputStream.flush();
        in.close();
    }

    public static void receiveFile(File file, ObjectInputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        FileOutputStream outputStream = new FileOutputStream(file);
        long length = inputStream.readLong();
        Log.d(TAG, "receiveFile: inputStream.read..." + length);
        do {
            int count = inputStream.read(bytes);
            outputStream.write(bytes, 0, count);
            length -= count;
        } while (length > 0);
        Log.d(TAG, "receiveFile: FINISH  " + file.length());

        outputStream.flush();
        outputStream.close();
    }

    public static File getCashFolder(Context context) {
        return context.getExternalCacheDir();
    }


    public static File getDocumentFolder() {
        String folder_main = "University";
        File documentDirPath = commonDocumentDirPath(folder_main);
        documentDirPath.mkdir();

        return documentDirPath;
    }

    private static File commonDocumentDirPath(String FolderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }
        return dir;

    }


    public static void saveToDownload(Context context, File file) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(file.getName(), file.getName(), true,
                "text/plain", file.getAbsolutePath(), file.length(), true);
    }

    public static String getPath(String path, Context context) {
        Uri uri = Uri.parse(path);
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
