package de.holhar.spring.soap.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Iterator;

@Component
public class LoggingInterceptor implements ClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean handleRequest(MessageContext context) {
        debug(context.getRequest(), true);
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext context) {
        debug(context.getResponse(), false);
        return true;
    }

    @Override
    public boolean handleFault(MessageContext context) {
        debug(context.getResponse(), false);
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) {
        // Nothing to do
    }

    private void debug(WebServiceMessage wsMessage, boolean isRequest) {
        String endpoint = wsMessage.toString().replace("SaajSoapMessage ", "");
        String message = payloadToString(wsMessage);
        if (isRequest) {
            debugHeaders(wsMessage, endpoint);
            logger.info("SOAP request: {}; {}", endpoint, message);
        } else {
            logger.info("SOAP response: {}; {}", endpoint, message);
        }
    }

    private void debugHeaders(WebServiceMessage wsMessage, String endpoint) {
        Iterator headers = ((SaajSoapMessage) wsMessage).getSaajMessage().getMimeHeaders().getAllHeaders();
        if (headers.hasNext()) {
            StringBuilder sb = new StringBuilder();
            while (headers.hasNext()) {
                MimeHeader header = (MimeHeader) headers.next();
                sb.append(header.getName()).append(": ").append(header.getValue()).append("\n");
            }
            logger.info("Headers for SOAP message: {}; {}", endpoint, sb.toString());
        }
    }

    private String payloadToString(WebServiceMessage message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            message.writeTo(out);
            return toPrettyString(new String(out.toByteArray()), 2);
        } catch (Exception e) {
            logger.error("Could not write web service byte array to string");
            return "Error during message parsing";
        }
    }

    private String toPrettyString(String xmlString, int indent) throws Exception {
        if (xmlString.isEmpty()) {
            return "Unparseable xml string content";
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        Document document = factory.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8"))));

        document.normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            node.getParentNode().removeChild(node);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }
}