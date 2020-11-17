package com.tesco.oms.invertory;


import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TSCGRAdjustInventory {
    public Document prepareAdjustInventory(YFSEnvironment env, Document inputDoc) {
        NodeList nodeList = inputDoc.getElementsByTagName("Item");
        Element documentElement = inputDoc.getDocumentElement();
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Element nodeItem = (Element) nodeList.item(i);
            int quantityToAdjust = Integer.parseInt(nodeItem.getAttribute("QuantityToAdjust"));
            if (quantityToAdjust < 10) {
                documentElement.removeChild(nodeItem);
                continue;
            }
            nodeItem.setAttribute("AdjustmentType", "ADJUSTMENT");
            nodeItem.setAttribute("SupplyType", "ONHAND");
            nodeItem.setAttribute("Quantity", nodeItem.getAttribute("QuantityToAdjust"));
            nodeItem.setAttribute("OrganizationCode", "TESCO US");
        }
        return inputDoc;
    }

    public static void main(String[] args) throws Exception {
        TSCGRAdjustInventory service = new TSCGRAdjustInventory();
        String inputXml = "<Items>\n" +
                "               <Item UnitOfMeasure=\"EACH\" ShipNode=\"Node1\" QuantityToAdjust=\"5\" ProductClass=\"GOOD\" ItemID=\"123\" />\n" +
                "\n" +
                "               <Item UnitOfMeasure=\"EACH\" ShipNode=\"Node2\" QuantityToAdjust=\"30\" ProductClass=\"GOOD\" ItemID=\"456\" />\n" +
                "</Items>";
        Document inputDoc = stringToDom(inputXml);
        Document output = service.prepareAdjustInventory(null, inputDoc);
        System.out.println(docToString(output));

    }

    private static Document stringToDom(String xmlSource)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    public static String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}
