package net.group.androidlocalmessanger.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;

public class Catcher {
    private static final String TAG = "Catcher";
    private static HashMap<String, String> fileNameToLocal;
    private static final Gson gson = new Gson();
    private static final String SH_KEY = "serverToLocal";
    private static boolean hasBeenLoaded = false;
    private SharedPrefManager sh;


    public Catcher(Context context) {
        sh = new SharedPrefManager(context);
        load(sh);
    }

    public String getLocalPathByFileName(String fileName) {
        String localPath = fileNameToLocal.get(fileName);
        Log.d(TAG, "getLocalPathByFileName:key: " + fileName);
        Log.d(TAG, "getLocalPathByFileName:value: " + localPath);
        return localPath;
    }

    public void saveLocalPath(String fileName, String localPath) {
        Log.d(TAG, "saveLocalPath:key: " + fileName);
        Log.d(TAG, "saveLocalPath:value: " + localPath);
        fileNameToLocal.put(fileName, localPath);
        save(sh);
    }

    private static void load(SharedPrefManager sh) {
        Log.d(TAG, "start load: ");
        if (hasBeenLoaded)
            return;
        String mapJson = sh.getString(SH_KEY);
        if (mapJson != null)
            fileNameToLocal = gson.fromJson(mapJson, HashMap.class);
        else
            fileNameToLocal = new HashMap<>();
        hasBeenLoaded = true;
        Log.d(TAG, "end load: ");
    }

    private static void save(SharedPrefManager sh) {
        load(sh);
        sh.putString(SH_KEY, gson.toJson(fileNameToLocal));
    }
}
