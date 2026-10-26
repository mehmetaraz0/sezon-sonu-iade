package com.mehmetaraz.sezonsonuiade;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;

/** Yayındaki iade sayfasını tam ekran açar; Excel dosyalarını İndirilenler klasörüne kaydeder. */
public class MainActivity extends Activity {
    private static final String ADRES = "https://mehmetaraz0.github.io/sezon-sonu-iade/";
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        setContentView(web);

        WebSettings ayar = web.getSettings();
        ayar.setJavaScriptEnabled(true);
        ayar.setDomStorageEnabled(true);
        web.addJavascriptInterface(new Kopru(), "IadeAndroid");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if (u.toString().startsWith(ADRES)) return false;
                // Sayfa dışı bağlantılar telefonun tarayıcısında açılır
                startActivity(new Intent(Intent.ACTION_VIEW, u));
                return true;
            }
        });

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl(ADRES);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    private class Kopru {
        @JavascriptInterface
        public void kaydet(String ad, String base64) {
            try {
                byte[] veri = Base64.decode(base64, Base64.DEFAULT);
                ContentValues d = new ContentValues();
                d.put(MediaStore.Downloads.DISPLAY_NAME, ad);
                d.put(MediaStore.Downloads.MIME_TYPE, XLSX);
                ContentResolver cr = getContentResolver();
                Uri uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, d);
                if (uri == null) throw new IllegalStateException("Dosya oluşturulamadı");
                try (OutputStream os = cr.openOutputStream(uri)) { os.write(veri); }
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "İndirilenler klasörüne kaydedildi: " + ad, Toast.LENGTH_LONG).show();
                    Intent ac = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, XLSX)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try { startActivity(Intent.createChooser(ac, "Excel dosyasını aç")); } catch (Exception ignored) { }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Kaydedilemedi: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
    }
}
