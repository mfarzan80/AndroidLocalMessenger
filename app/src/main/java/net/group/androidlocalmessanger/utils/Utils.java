package net.group.androidlocalmessanger.utils;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Utils {

    public static final String TAG = "Utils";

    public static String getServerIP(Context context) {
        WifiManager manager;
        DhcpInfo dhcp;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcp = manager.getDhcpInfo();
        return Formatter.formatIpAddress(dhcp.gateway);
    }

    public static void sendFile(File file, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        int count;
        while ((count = in.read(bytes)) > 0) {
            outputStream.write(bytes, 0, count);
        }
        outputStream.flush();
        in.close();
    }

    public static void receiveFile(File file, InputStream inputStream) throws IOException {
        byte[] bytes = new byte[16 * 1024];
        FileOutputStream outputStream = new FileOutputStream(file);
        Log.d(TAG, "receiveFile: inputStream.read...");
        int count;
        count = inputStream.read(bytes);
        outputStream.write(bytes, 0, count);
        Log.d(TAG, "receiveFile: FINISH");

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
}
