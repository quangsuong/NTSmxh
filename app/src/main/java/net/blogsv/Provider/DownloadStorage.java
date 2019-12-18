package net.blogsv.Provider;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.blogsv.model.Video;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Tamim on 08/06/2018.
 */


public class DownloadStorage {
    private final String STORAGE_IMAGE = "MY_Download_IMAGE";

    private SharedPreferences preferences;
    private Context context;

    public DownloadStorage(Context context) {
        this.context = context;
    }

    public void storeImage(ArrayList<Video> arrayList) {
        preferences = context.getSharedPreferences(STORAGE_IMAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("DownloadsListImage", json);
        editor.apply();
    }

    public ArrayList<Video> loadImagesFavorites() {
        preferences = context.getSharedPreferences(STORAGE_IMAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("DownloadsListImage", null);
        Type type = new TypeToken<ArrayList<Video>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
