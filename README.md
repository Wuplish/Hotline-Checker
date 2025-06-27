# Hotline OrbitPremium Combolist Checker

**OrbitPremium hesaplarını otomatik olarak deneyen, lisans doğrulaması yapan ve sonuçları web paneli ile takip edebileceğiniz bir hesap kontrol sistemi.**

---

## Özellikler

* `data/accounts.txt` dosyasına koyduğunuz kullanıcı adı:şifre formatındaki hesapları otomatik test eder.
* Lisans kodu ve cihaz UUID kontrolü ile güvenli erişim sağlar.
* Playwright kullanarak OrbitPremium sitesine giriş yapar, hesapların lisans durumunu kontrol eder.
* Test edilen hesapları üç farklı kategoriye ayırır ve `data/accs/` klasöründe ayrı dosyalara kaydeder:
  * Lisanslı hesaplar
  * Lisanssız ama giriş başarılı hesaplar
  * Giriş başarısız (şifre hatalı) hesaplar
* Gerçek zamanlı sonuçlar, `localhost:1453` adresinde çalışan web panelinde görüntülenebilir.
* Discord webhook entegrasyonu ile test sonuçlarını anlık bildirim olarak gönderebilir.
* Yapılandırma için `data/config.json` dosyasını kullanır; thread sayısı, tarayıcı ayarları ve webhook URL'si gibi parametreler burada ayarlanabilir.

---

## Teknik Detaylar

* Program, Playwright kütüphanesi ile web otomasyonu yapmaktadır.
* `claz` sınıfı şu anda işlevsizdir sadece null döner, canım sıkıldıgı için yapmıştım.

---

## Kullanım

1. `data/accounts.txt` dosyasına kullanıcı adı ve şifreleri `username:password` formatında ekleyin.
2. Programı çalıştırın.
3. Lisans kodunuzu girin.
4. Program cihazınızın UUID'sini alarak lisans doğrulaması yapar.
5. Test işlemi başladıktan sonra [http://localhost:1453](http://localhost:1453) adresinden sonuçları canlı takip edin.
6. Test tamamlandığında sonuçlar `data/accs/` klasöründe kategorilere göre kaydedilir.

---

## Gereksinimler

* Java 11 veya üstü
* Playwright Java kütüphanesi
* İnternet bağlantısı (lisans doğrulama ve test işlemleri için)
* `data/config.json` dosyasının doğru yapılandırılması
* `data/accounts.txt` dosyasına hesapların eklenmesi
* Kodun içersinden Webhook ayarlandırması yapılması
  
---

## config.json örneği

```json
{
  "threads": 1,
  "headless": true,
  "slowMo": 0,
  "devtools": false,
  "ignoreHTTPSErrors": false,
  "viewportWidth": 1920,
  "viewportHeight": 1080,
}
```

---

## Geliştirici

Bu proje **wuplish** tarafından geliştirilmiştir. Sorularınız ve destek için Discord üzerinden `.wuplish` yani benimle iletişime geçebilirsiniz.

---

## Lisans ve Katkı

Bu proje kişisel kullanım için geliştirilmiştir. Lisans sistemi ve diğer özellikler ihtiyacınıza göre değiştirilebilir.

---

Sevgilerimle.
**wuplish**

---
