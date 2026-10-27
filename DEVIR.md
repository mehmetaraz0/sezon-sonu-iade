# DEVİR — Sezon Sonu İade Kayıt Listesi

> Bu dosya işi devralacak bir sonraki Claude oturumu (ya da kişi) için yazıldı. Kullanıcıya **her zaman Türkçe** yanıt ver.
> Son güncelleme: 27.10.2026

## Ne bu

Depoda sezon sonunda tedarikçiye iade edilecek ürünler (ürün, miktar, SKT, geliş tarihi) eskiden kâğıda yazılıyordu. Yerine 2-3 kişinin telefondan ve bilgisayardan ortak kullandığı bir sayfa kuruldu. Seçilen firmanın ürünleri tek bir Excel **iade tutanağına** dökülür.

- **Canlı link:** https://mehmetaraz0.github.io/sezon-sonu-iade/
- **Android uygulaması:** https://mehmetaraz0.github.io/sezon-sonu-iade/SezonSonuIade.apk (sayfada Android'den açılınca indirme düğmesi çıkar)
- **iPhone:** Safari → Paylaş → Ana Ekrana Ekle (PWA; sayfada iPhone'dan açılınca yardım düğmesi çıkar)
- Şifre/giriş yok (kullanıcı istemedi). Linki bilen herkes okuyup yazabilir.

## Nerede ne var

| Şey | Yer |
|---|---|
| Kaynak (tek sayfa) | `E:\sezon-sonu-iade\index.html` — GitHub `mehmetaraz0/sezon-sonu-iade` (public), `main` dalı |
| Yayın | GitHub Pages, `main` / kök. Push sonrası ~1 dakikada canlıya geçer |
| PWA | `manifest.webmanifest`, `sw.js` (ağ öncelikli; çevrimdışında son kopya), `ikon/` |
| Android | `android/` — WebView sarmalayıcı, canlı linki açar. APK kökte `SezonSonuIade.apk` |
| Veritabanı | Firebase projesi `sezon-sonu-iade` (kullanıcının Google hesabı), Firestore, bölge eur3 |
| Eski sürüm | claude.ai artifact `Pxgby4bPqqMyPHqpCvZMyp` — **artık kullanılmıyor**, verisi ayrı kaldı |

Git kimliği: `mehmetaraz868` / `mehmetaraz868@gmail.com` (depoda yerel ayarlı). Bilgisayarda `gh` yok; `git push` kayıtlı kimlikle çalışıyor.

## Veritabanı (Firestore)

| Yol | İçerik |
|---|---|
| `katalog/p0` (gerekirse p1…) | `{items: [{k: kod, a: ad, b: birim}]}` — 976 ürün (ERP'den, YIY kodlu), doküman başına en fazla 1500 |
| `ayarlar/genel` | `{firmalar: [637 ad], yapan: "HALİL KAPDAN", not: "SEZON SONU GELDİĞİ İÇİN", sinir: "YYYY-MM-DD"}` |
| `kayitlar/<id>` | `{kod, ad, miktar, birim, skt, gelis, firma, t, secim?, silindi?}` — tarihler `YYYY-MM-DD`, `firma` boş olabilir |

- **Kurallar:** yalnızca `katalog`, `ayarlar`, `kayitlar` koleksiyonlarına herkese açık okuma/yazma.
- **Firma adları Türkçe karaktersiz** (ör. `BIDFOOD AKDENIZ DAGITIM PAZ.A.S.`). Bütün aramalar `norm()` ile yapılır (tr küçük harf + ç/ğ/ı/ö/ş/ü → ASCII). Tarayıcının `datalist`'i Türkçe eşleştirmediği için firma kutularında kendi öneri listesi (`firmaOnerici`) var; `datalist` yalnızca birimler için kaldı.
- **Silme yumuşak:** `silindi: ms` alanı yazılır; Ayarlar bölümündeki "Son silinen kayıtlar"dan geri alınır.
- `secim` alanı yoksa seçim SKT kuralından gelir (`seciliMi`).
- Kayıtlar `orderBy('t').limit(1000)` ile okunuyor (silinmişler dahil). 1000'e yaklaşılırsa sayfalama gerekir.
- 27.10.2026 itibarıyla 5 kayıt var, firmasız ya da silinmiş yok.

## Sayfanın davranışı (kullanıcıyla netleşenler)

- **Giriş (telefon odaklı):** ürün ara → **+** → miktar, SKT, geliş tarihi. **Firma girişte sorulmaz**, sonradan atanır.
- **Tarihler rakamla yazılır:** 6/8 hane gün.ay.yıl (`250427`, `25042027` → 25.04.2027), 4 hane ay.yıl → **ayın 01'i** (`0427` → 01.04.2027). Okunuş kutunun yanında anında görünür.
- **Tarih engeli:** SKT bugünden önce olamaz; geliş tarihi bugünden sonra olamaz. Düzenlemede tarih değiştirilmediyse eski kayıt kaydedilebilir.
- **"Kaydet, aynı ürüne yeni parti":** ürün ve birim kalır, alanlar boşalır (aynı ürünün farklı partileri art arda girilir). Klavyedeki İleri: miktar → SKT → geliş → kaydet.
- **İade sınırı:** SKT ≤ sınır tarihi (varsayılan 30.04.2027, ayarlardan değişir) olanlar **kırmızı** ve kendiliğinden seçili.
- **Seçim kutuları:** kullanıcı istediğini kaldırır/ekler. Excel'e yalnızca seçililer gider. Aynı kutular toplu firma atamada da kullanılıyor (bilinen karışıklık, aşağıda).
- **Filtreler:** firma, **Firmasız**, Tümü / İade edilecek / Kalacak, listede ürün ara.
- **Firma atama:** kayıtları seç → "Seçili N kayda firma ata", ya da tek tek **Düzenle**.
- **Excel (ExcelJS 4.4.0):** "Tutanak" = seçili firmanın seçili ürünleri, kullanıcının şablonunu taklit eder (B3 "T U T A N A K T I R", 17. satır başlıklar, 18'den itibaren veri, en az 10 satır, altta İADEYİ YAPAN / TESLİM ALAN). "Seçilileri Excel indir" = görünen listedeki seçililer, 8 sütun.
- **Telefonda** kayıtlar kart olarak görünür. Dokunmatikte bütün kutular ≥16px (iPhone yakınlaştırmasın diye).

## Android uygulaması

- `android/` — Java, `minSdk 29` (Android 10+), AGP 8.10.1, paket `com.mehmetaraz.sezonsonuiade`.
- WebView canlı linki açar; sayfa dışı bağlantılar tarayıcıda açılır. Excel, `window.IadeAndroid.kaydet(ad, base64)` köprüsüyle **İndirilenler** klasörüne kaydedilir (`index.html` içindeki `indir()` bunu kontrol eder).
- Sayfa değişiklikleri uygulamaya kendiliğinden gelir; APK yalnızca Android kodu değişirse yeniden derlenir.
- **İmza anahtarı:** `android\iade-imza.jks` + `android\keystore.properties` (şifre içinde). **Git'e girmez, yalnızca bu bilgisayarda.** Kaybolursa kurulu uygulamalar güncellenemez. Kullanıcıya yedeklemesi söylendi.
- **Derleme:**
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
  cd E:\sezon-sonu-iade\android
  & "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.14.3-all\10utluxaxniiv4wxiphsi49nj\gradle-8.14.3\bin\gradle.bat" --no-daemon assembleRelease
  ```
  Çıktı `app\build\outputs\apk\release\app-release.apk` → köke `SezonSonuIade.apk` olarak kopyala, `versionCode`'u artır, commit + push.
- Emülatör kurulu ama sanal cihaz (AVD) yok; APK hiçbir cihazda denenmedi.

## Çalışma notları

- **Yerel önizleme:** `python -m http.server 5181 --directory E:/sezon-sonu-iade`. Yerel sayfa **gerçek** Firestore'a bağlanır: deneme kaydı açarsan sonunda sil, deneme firması ekleme (`ayarYaz` firma listesine yazar).
- Bu bilgisayarda Node 24 ve Python 3.12 var (Pillow yok). Simgeler `ikon/` altında; Node ile çizildi.
- Firestore'a toplu veri aktarımı REST ile yapıldı (`PATCH .../documents/<koleksiyon>/<id>?key=<apiKey>`); kurallar açık olduğu için anahtar yeterli.
- Kullanıcı kısa yazar, Türkçe ve çoğu zaman büyük harfle. Görsel taslak gösterilince hızlı karar veriyor.

## Açık işler / bilinen durumlar

- [ ] **Gerçek cihaz testi yok:** Android APK kurulumu, uygulama içinde Excel kaydetme ve iPhone'da ana ekrana ekleme / Excel önizleme kullanıcı tarafından denenmedi.
- [ ] **Seçim kutuları iki işe yarıyor:** tutanak seçimi ile toplu firma atama aynı kutuları kullanıyor. Firma atamak için işaret kaldırılan kayıt tutanaktan da çıkar. Ayrı bir firma atama seçimi önerildi; kullanıcı henüz cevap vermedi.
- [ ] Geliş tarihine 4 hane yazılırsa ay.yıl okunur (`1205` → 01.12.2005); kullanıcıya 6 hane yazması söylendi. İstenirse geliş tarihinde 4 haneyi gün.ay (bu yıl) olarak yorumlamak düşünülebilir.
- [ ] Eski claude.ai artifact sayfası hâlâ açık; kullanıcı isterse silinebilir.
