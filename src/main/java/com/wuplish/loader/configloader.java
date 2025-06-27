package com.wuplish.loader;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class configloader {
    private JSONObject config;

    public configloader(String path) {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(path)));
            config = new JSONObject(jsonText);
        } catch (IOException e) {
            System.out.println("❌ config.json okunamadı.");
            e.printStackTrace();
            config = new JSONObject(); // boş JSON
        }
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return config.optBoolean(key, defaultVal);
    }

    public int getInt(String key, int defaultVal) {
        return config.optInt(key, defaultVal);
    }

    public String getString(String key, String defaultVal) {
        return config.optString(key, defaultVal);
    }


}
