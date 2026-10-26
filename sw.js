// Sayfa her zaman ağdan gelir (güncel sürüm); bağlantı yoksa son kopya açılır.
// Kayıtlar Firestore'dan geldiği için çevrimdışıyken yalnızca sayfa kabuğu görünür.
const ONBELLEK = 'iade-v1';

self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', e => e.waitUntil(self.clients.claim()));

self.addEventListener('fetch', e => {
  const u = new URL(e.request.url);
  if (e.request.method !== 'GET' || u.origin !== location.origin) return;
  e.respondWith(
    fetch(e.request).then(r => {
      if (r.ok && !u.pathname.endsWith('.apk')) { const kopya = r.clone(); caches.open(ONBELLEK).then(c => c.put(e.request, kopya)); }
      return r;
    }).catch(() => caches.match(e.request, { ignoreSearch: true }))
  );
});
