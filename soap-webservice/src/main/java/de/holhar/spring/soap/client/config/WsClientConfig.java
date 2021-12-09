package de.holhar.spring.soap.client.config;

import de.holhar.spring.soap.client.SOAPConnector;
import de.holhar.spring.soap.ws.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WsClientConfig {

    @Bean
    public ObjectFactory objectFactory() {
        return new ObjectFactory();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // This is the package name specified in the <generated-sources> specified in the pom.xml
        marshaller.setContextPath("de.holhar.spring.soap.ws");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller, LoggingInterceptor loggingInterceptor) {
        SOAPConnector client = new SOAPConnector();
        client.setDefaultUri("http://localhost:8883/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);

        List<ClientInterceptor> clientInterceptors = Arrays.asList(loggingInterceptor);
        client.setInterceptors(clientInterceptors.toArray(new ClientInterceptor[1]));
        return client;
    }
}