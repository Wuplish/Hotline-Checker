package com.wuplish.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class Output {
    private static final String workingDir = System.getProperty("user.dir");
    private static final Path invalidPath = Paths.get(workingDir, "data", "/accs/invalid.txt");
    private static final Path noLicensePath = Paths.get(workingDir, "data", "/accs/nolicense.txt");
    private static final Path licensedPath = Paths.get(workingDir, "data", "/accs/licensed.txt");

    // Dosyalara yazmak için genel metod
    private static synchronized void writeLine(Path path, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("❌ Dosyaya yazılamadı: " + path);
            e.printStackTrace();
        }
    }

    public static void writeInvalid(String username, String password) {
        writeLine(invalidPath, username + ":" + password);
    }

    public static void writeNoLicense(String username, String password, String money) {
        writeLine(noLicensePath, username + ":" + password + ":" + money);
    }

    public static void writeLicensed(String username, String password, String license, String money) {
        writeLine(licensedPath, username + ":" + password + ":" + license + ":" + money);
    }

    // İstersen dosyaları temizlemek için metodlar da ekleyebilirim
    public static void clearOutputs() {
        try {
            Files.deleteIfExists(invalidPath);
            Files.deleteIfExists(noLicensePath);
            Files.deleteIfExists(licensedPath);
        } catch (IOException e) {
            System.err.println("❌ Dosya silme hatası");
            e.printStackTrace();
        }
    }
}
