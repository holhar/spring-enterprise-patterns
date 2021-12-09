package de.holhar.spring.soap.client;

import de.holhar.spring.soap.ws.Movie;
import de.holhar.spring.soap.ws.ObjectFactory;
import de.holhar.spring.soap.ws.UID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.bind.JAXBElement;

@Component
public class WsClient {

    private final ObjectFactory objectFactory;
    private final SOAPConnector connector;

    public WsClient(ObjectFactory objectFactory, SOAPConnector connector) {
        this.objectFactory = objectFactory;
        this.connector = connector;
    }

    public Movie getMovie(String id) {
        UID uid = new UID();
        uid.setId(id);
        JAXBElement<UID> movieRequest = objectFactory.createMovieRequest(uid);
        return ((JAXBElement<Movie>) connector.callWebService("http://localhost:8883/ws/movies", movieRequest)).getValue();
    }
}
