package de.holhar.spring.soap.ws.config;

import de.holhar.spring.soap.ws.ObjectFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@EnableWs
@Configuration
public class WebServiceConfig {

    public static final String TARGET_NAMESPACE = "http://movies.ws.holhar.de";

    @Bean
    @SuppressWarnings("unchecked")
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    /*
     * WSDL file retrievable via http://localhost:8883/ws/movieWebService.wsdl
     */
    @Bean("movieWebService")
    public Wsdl11Definition serviceWsdl11Definition(XsdSchemaCollection serviceSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("movies");
        wsdl11Definition.setLocationUri("/ws/movies");
        wsdl11Definition.setTargetNamespace(TARGET_NAMESPACE);
        wsdl11Definition.setSchemaCollection(serviceSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchemaCollection serviceSchema() {
        ClassPathResource serviceXsdResource = new ClassPathResource("movieservice.xsd");
        CommonsXsdSchemaCollection schemaCollection = new CommonsXsdSchemaCollection(serviceXsdResource);
        schemaCollection.setInline(true);
        return schemaCollection;
    }

    @Bean
    public ObjectFactory movieObjectFactory() {
        return new ObjectFactory();
    }
}
