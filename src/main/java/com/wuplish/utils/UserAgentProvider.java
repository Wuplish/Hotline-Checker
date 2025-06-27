package com.wuplish.utils;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class UserAgentProvider {
    static String workingDir = System.getProperty("user.dir");
    private static final String FILE_PATH = workingDir + "/data/useragents.json";
    private static final Random random = new Random();
    private static JSONArray agents = new JSONArray();

    static {
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            agents = new JSONArray(content);
        } catch (IOException e) {
            System.out.println("❌ useragents.json okunamadı.");
            e.printStackTrace();
        }
    }

    public static String getRandomUserAgent() {
        if (agents.length() == 0) return "Mozilla/5.0";
        int index = random.nextInt(agents.length());
        return agents.getString(index);
    }
}
