package com.example.shell.moduleservice.core;



import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Created by keke.tian on 2017/7/31.
 */

public class SaxHandler extends DefaultHandler {

    private HashMap<String, String> protocols;
    private String content;

    private String key;
    private String value;


    public SaxHandler(){
    }

    public HashMap<String, String> getProtocols() {
        return protocols;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        protocols = new HashMap<String, String>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

//
//        if("module-service".equals(qName)){
//        }


    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        content = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("stub-class".equals(qName)){
            key = content;
        }else if("target-class".equals(qName)){
            value = content;
        }else if("module-service".equals(qName)){
            protocols.put(key, value);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
