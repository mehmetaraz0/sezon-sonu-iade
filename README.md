# Sezon Sonu İade

Depoda sezon sonu iade edilecek ürünlerin kayıt listesi ve firma tutanağı.

- Sayfa: `index.html` (GitHub Pages ile yayınlanır)
- Kayıtlar: Firebase Firestore (`sezon-sonu-iade` projesi; `katalog`, `ayarlar`, `kayitlar` koleksiyonları)
- Excel: ExcelJS ile tarayıcıda üretilir

SKT'si iade sınırına (varsayılan 30.04.2027) kadar olan ürünler kırmızı görünür ve kendiliğinden seçili gelir.
Excel'e yalnızca seçili ürünler aktarılır. Silinen kayıtlar Ayarlar bölümünden geri alınabilir.
