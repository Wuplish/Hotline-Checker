package com.wuplish.http;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class HttpDashboard extends NanoHTTPD {
    private static int totalAccounts = 0;
    private static int licensedAccounts = 0;
    private static int unlicensedAccounts = 0;
    private static int failedAccounts = 0;
    private static String lastUsername = "-";
    private static String lastStatus = "-";

    private static String currentTheme = "light"; // light veya dark

    public HttpDashboard() throws IOException {
        super(1453);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("[+] Kontrol Paneli çalışıyor: http://localhost:1453");
    }

    public static void setTotalAccounts(int count) {
        totalAccounts = count;
    }

    public static void setLicensedAccounts(int count) {
        licensedAccounts = count;
    }

    public static void setUnlicensedAccounts(int count) {
        unlicensedAccounts = count;
    }

    public static void setFailedAccounts(int count) {
        failedAccounts = count;
    }
    public static void account() {

    }
    public static void updateStatus(String username, String status) {
        lastUsername = username;
        lastStatus = status;

        totalAccounts++;

        if (status.toLowerCase().contains("lisanslı") || status.toLowerCase().contains("aktif")) {
            licensedAccounts++;
        } else if (status.toLowerCase().contains("lisanssız") || status.toLowerCase().contains("lisans yok")) {
            unlicensedAccounts++;
        } else if (status.toLowerCase().contains("başarısız") || status.toLowerCase().contains("hatalı")) {
            failedAccounts++;
        }
    }


    public static void toggleTheme() {
        currentTheme = currentTheme.equals("light") ? "dark" : "light";
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/toggle-theme")) {
            toggleTheme();
            Response redirect = newFixedLengthResponse(Response.Status.REDIRECT, MIME_PLAINTEXT, "Redirecting...");
            redirect.addHeader("Location", "/");
            return redirect;
        }

        double successCount = licensedAccounts + unlicensedAccounts;
        double successRate = (totalAccounts == 0) ? 0 : (successCount / totalAccounts) * 100;

        String backgroundColor = currentTheme.equals("light") ? "#f5f5f5" : "#121212";
        String textColor = currentTheme.equals("light") ? "#222" : "#eee";
        String boxBackground = currentTheme.equals("light") ? "#fff" : "#1e1e1e";
        String boxShadow = currentTheme.equals("light") ? "rgba(0,0,0,0.15)" : "rgba(0,0,0,0.7)";
        String buttonBackground = currentTheme.equals("light") ? "#007bff" : "#3399ff";
        String buttonHover = currentTheme.equals("light") ? "#0056b3" : "#1a73e8";

        String html = """
            <!DOCTYPE html>
            <html lang="tr">
            <head>
                <meta http-equiv="refresh" content="3">
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <title>Hotline Checker Panel</title>
                <style>
                    body {
                        margin: 0;
                        padding: 20px;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: %s;
                        color: %s;
                        transition: background-color 0.3s, color 0.3s;
                    }
                    h1 {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .container {
                        max-width: 900px;
                        margin: 0 auto;
                    }
                    .stats {
                        display: flex;
                        justify-content: space-around;
                        flex-wrap: wrap;
                        gap: 20px;
                    }
                    .box {
                        flex: 1 1 200px;
                        background: %s;
                        padding: 20px;
                        border-radius: 12px;
                        box-shadow: 0 4px 12px %s;
                        text-align: center;
                        transition: background 0.3s, box-shadow 0.3s;
                    }
                    .box h2 {
                        margin: 0 0 10px;
                        font-size: 1.5rem;
                        color: %s;
                    }
                    .box p {
                        font-size: 2.2rem;
                        font-weight: bold;
                        margin: 0;
                    }
                    .last-status {
                        margin-top: 40px;
                        background: %s;
                        padding: 15px;
                        border-radius: 10px;
                        box-shadow: 0 4px 12px %s;
                        font-size: 1.1rem;
                    }
                    .theme-toggle {
                        display: block;
                        margin: 30px auto;
                        padding: 12px 25px;
                        font-size: 1rem;
                        font-weight: 600;
                        background-color: %s;
                        color: white;
                        border: none;
                        border-radius: 25px;
                        cursor: pointer;
                        box-shadow: 0 4px 8px %s;
                        transition: background-color 0.3s;
                        text-align: center;
                        text-decoration: none;
                        width: 200px;
                    }
                    .theme-toggle:hover {
                        background-color: %s;
                    }
                    .bar-container {
                        background: %s;
                        border-radius: 15px;
                        height: 25px;
                        width: 100%%;
                        margin-top: 20px;
                        box-shadow: inset 0 2px 5px rgba(0,0,0,0.1);
                    }
                    .bar-fill {
                        height: 100%%;
                        background: #4caf50;
                        border-radius: 15px;
                        width: %.2f%%;
                        transition: width 0.6s ease;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Hotline Checker Web Panel</h1>
                    <div class="stats">
                        <div class="box">
                            <h2>Toplam Hesap</h2>
                            <p>%d</p>
                        </div>
                        <div class="box">
                            <h2>Lisanslı Hesap</h2>
                            <p>%d</p>
                        </div>
                        <div class="box">
                            <h2>Lisanssız Hesap</h2>
                            <p>%d</p>
                        </div>
                        <div class="box">
                            <h2>Başarısız Hesap</h2>
                            <p>%d</p>
                        </div>
                    </div>

                    <div class="last-status">
                        <strong>Son İşlenen Kullanıcı:</strong> %s<br/>
                        <strong>Son Durum:</strong> %s<br/>
                        <strong>Başarı Oranı:</strong> %.2f%%

                        <div class="bar-container">
                            <div class="bar-fill"></div>
                        </div>
                    </div>

                    <a href="/toggle-theme" class="theme-toggle">Tema Değiştir (%s)</a>
                </div>

                <script>
                    const barFill = document.querySelector('.bar-fill');
                    barFill.style.width = '%.2f%%';
                </script>
            </body>
            </html>
            """.formatted(
                backgroundColor, textColor, boxBackground, boxShadow, textColor,
                boxBackground, boxShadow,
                buttonBackground, boxShadow, buttonHover,
                boxBackground,
                successRate,
                totalAccounts, licensedAccounts, unlicensedAccounts, failedAccounts,
                lastUsername, lastStatus, successRate,
                currentTheme.equals("light") ? "Açık" : "Karanlık",
                successRate
        );

        Response response = newFixedLengthResponse(html);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        return response;
    }
}
