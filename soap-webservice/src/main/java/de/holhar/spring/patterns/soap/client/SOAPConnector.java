package de.holhar.spring.patterns.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import java.io.IOException;

public class SOAPConnector extends WebServiceGatewaySupport {

    // FIXME: throws WebServiceTransportException:  [404]
    public Object callWebService(String url, Object request) {
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }

    public Object callWebServiceWithAuthHeader(String url, Object request, String token) {
        String endpoint = ((JAXBElement) request).getName().toString();
        return getWebServiceTemplate().marshalSendAndReceive(url, request,
                webServiceMessage -> decorateWithToken(token, endpoint));
    }

    void decorateWithToken(String token, String endpoint) throws IOException {
        if (!token.isEmpty()) {
            TransportContext context = TransportContextHolder.getTransportContext();
            final HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            connection.addRequestHeader("Authorization", token);
        }
    }

    public Object unmarshal(Source detailSource) throws IOException {
        return getWebServiceTemplate().getUnmarshaller().unmarshal(detailSource);
    }
}