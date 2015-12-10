package com.example.androidtester.TestTool;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PlanHandler extends DefaultHandler{
    private String att = "";

    public PlanHandler() {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        att = qName;
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        String sResult = new String(ch, start, length).trim();
        if (sResult.equals(""))
            return;
        if (sResult.equals("true") || sResult.equals("false")) {
            if (sResult.equals("true"))
                ParserPlan.atributos.put(att, true);
            else
                ParserPlan.atributos.put(att, false);
        } else {
            if (att.equals("ProviderName")) {
                ParserPlan.providers.add(sResult);
            }
            if (att.equals("Metric")) {
                ParserPlan.metrics.add(sResult);
            }

            if (att.equals("NumberOfExecutionsPerImageAndProvider") && (!sResult.equals("1"))) {
                ParserPlan.tries = Integer.parseInt(sResult);
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        att = "";
    }
}
