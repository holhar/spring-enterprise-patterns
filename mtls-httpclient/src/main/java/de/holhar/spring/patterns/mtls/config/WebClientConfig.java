package de.holhar.spring.patterns.mtls.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${app.sslcontext.truststore-path}")
    private File trustStoreFile;
    @Value("${app.sslcontext.truststore-secret}")
    private String trustStoreSecret;
    @Value("${app.sslcontext.keystore-path}")
    private File keyStoreFile;
    @Value("${app.sslcontext.keystore-secret}")
    private String keyStoreSecret;

    @Bean
    public WebClient mtlsWebClient() {
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(getSslContext()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS))
                );
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private SslContext getSslContext() {
        try (FileInputStream trustStoreInStream = new FileInputStream(trustStoreFile.getAbsolutePath());
             FileInputStream keyStoreInStream = new FileInputStream(keyStoreFile.getAbsolutePath())) {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyStoreInStream, keyStoreSecret.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStoreSecret.toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(trustStoreInStream, trustStoreSecret.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            return SslContextBuilder.forClient()
                    .keyManager(keyManagerFactory)
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to establish SSL context", e);
        }
    }
}
