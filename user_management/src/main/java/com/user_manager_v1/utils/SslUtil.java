package com.user_manager_v1.utils;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;

public class SslUtil {
    public static SSLSocketFactory getSocketFactory() throws Exception {
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        context.init(null, tmf.getTrustManagers(), null);
        return context.getSocketFactory();
    }
}

