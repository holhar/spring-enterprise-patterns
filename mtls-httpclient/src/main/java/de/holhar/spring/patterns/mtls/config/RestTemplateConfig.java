package de.holhar.spring.patterns.mtls.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

@Configuration
public class RestTemplateConfig {

    @Value("${app.sslcontext.truststore-path}")
    private File trustStoreFile;

    @Value("${app.sslcontext.truststore-secret}")
    private String trustStoreSecret;

    @Value("${app.sslcontext.keystore-path}")
    private File keyStoreFile;

    @Value("${app.sslcontext.keystore-secret}")
    private String keyStoreSecret;

    @Bean
    public RestTemplate mtlsRestTemplate() {
        CloseableHttpClient httpClient = getHttpClient();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(10000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }

    private CloseableHttpClient getHttpClient() {
        try (FileInputStream trustStoreInStream = new FileInputStream(trustStoreFile.getAbsolutePath());
             FileInputStream keyStoreInStream = new FileInputStream(keyStoreFile.getAbsolutePath())) {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = KeyStore.getInstance("JKS");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            trustStore.load(trustStoreInStream, trustStoreSecret.toCharArray());
            trustManagerFactory.init(trustStore);
            keyStore.load(keyStoreInStream, keyStoreSecret.toCharArray());
            keyManagerFactory.init(keyStore, keyStoreSecret.toCharArray());
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            return HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to establish SSL context", e);
        }
    }
}
