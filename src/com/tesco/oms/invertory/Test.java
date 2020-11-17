package com.tesco.oms.invertory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;


public class Test {

    private static  DocumentBuilder builder;

    public static void main(String[] args) throws  Exception {

        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new File("createOrderXML.xml"));
        processLeadTime(document.getDocumentElement());
    }

    private static void processLeadTime(Element element) throws IOException, SAXException {
        NodeList items = element.getElementsByTagName("Item");
        for (int i = 0; i < items.getLength(); i++) {
            String itemId = items.item(i).getAttributes().getNamedItem("ItemID").getNodeValue();
            Document doc = builder.newDocument();
            Element item = doc.createElement("Item");
            item.setAttribute("ItemID", itemId);
            doc.appendChild(item);
            Document docOutXML =  builder.parse(new File("output.xml"));

                Element rootElement = docOutXML.getDocumentElement();
                Element leadTime = (Element) rootElement.getElementsByTagName("InventoryParameters").item(0);
                int leadTimeInt = Integer.parseInt(leadTime.getAttribute("LeadTime"));
                Element orderLineElement = (Element) items.item(i).getParentNode();
                orderLineElement.setAttribute("LineType", leadTimeInt > 5 ? "STANDART" : "EXPRESS");
            System.out.println(orderLineElement.hasAttribute("LineType"));
        }
    }
}
