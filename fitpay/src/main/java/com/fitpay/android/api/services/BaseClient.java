package com.fitpay.android.api.services;

import android.os.Build;

import com.fitpay.android.utils.FPLog;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Created by tgs on 5/20/16.
 */
public class BaseClient {
    protected static final String FP_KEY_ID = "fp-key-id";
    protected static final String FP_KEY_SDK_VER = "X-FitPay-SDK";


    /*
     * OKClientBuilder for untrusted ssl endpoints
     * DO NOT USE IN PRODUCTION
     */
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                try {
                    SSLContext sc = SSLContext.getInstance("TLSv1.2");
                    sc.init(null, null, null);
                    builder.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), (X509TrustManager) trustAllCerts[0]);

//                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//                    builder.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                    ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build();

                    List<ConnectionSpec> specs = new ArrayList<>();
                    specs.add(cs);
                    specs.add(ConnectionSpec.COMPATIBLE_TLS);
                    specs.add(ConnectionSpec.CLEARTEXT);

                    builder.connectionSpecs(specs)
                            .followRedirects(true)
                            .followSslRedirects(true)
                            .retryOnConnectionFailure(true)
                            .cache(null);

                } catch (Exception exc) {
                    //"Error while setting TLS 1.2",
                    FPLog.e("OkHttpTLSCompat", exc);
                }
            } else {
                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                builder.sslSocketFactory(sslSocketFactory);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            return builder;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
