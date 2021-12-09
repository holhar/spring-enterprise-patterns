package de.holhar.spring.jaxbdatabinding.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CustomNormalizedStringAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String text) {
        return text.trim();
    }

    @Override
    public String marshal(String text)  {
        return text.trim();
    }
}
