package de.holhar.spring.soap.ws;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBElement;

import static de.holhar.spring.soap.ws.config.WebServiceConfig.TARGET_NAMESPACE;

@Endpoint
public class ServiceEndpoint {

    private final ObjectFactory movieObjectFactory;
    private final MovieService movieService;

    public ServiceEndpoint(ObjectFactory movieObjectFactory, MovieService movieService) {
        this.movieObjectFactory = movieObjectFactory;
        this.movieService = movieService;
    }

    @PayloadRoot(namespace = TARGET_NAMESPACE, localPart = "getMovieRequest")
    @ResponsePayload
    public JAXBElement<Movie> getData(@RequestPayload JAXBElement<UID> getMovieRequest, MessageContext context) {
        // MessageContext is available request-scoped bean
        context.setProperty("someKey", "someValue");
        UID movieUID = getMovieRequest.getValue();
        Movie movie = movieService.findService(movieUID);
        return movieObjectFactory.createMovieResponse(movie);
    }
}