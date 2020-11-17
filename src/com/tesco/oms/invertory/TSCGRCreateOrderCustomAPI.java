package com.tesco.oms.invertory;

import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.rmi.RemoteException;

public class TSCGRCreateOrderCustomAPI {
    public static final String WEBSITE = "WEBSITE";
    public static final String CALLCENTER = "CALLCENTER";
    public static final String STANDART = "STANDART";
    public static final String EXPRESS = "EXPRESS";
    private YIFApi yifApi;
    private YFSEnvironment env;
    private DocumentBuilder docBuilder;

    public Document createOrderCustom(YFSEnvironment env, Document inputDoc) {
        this.env = env;
        try {
            this.yifApi = YIFClientFactory.getInstance().getLocalApi();
            this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException | YIFClientCreationException e) {
            e.printStackTrace();
            return null;
        }
        Element rootElement = inputDoc.getDocumentElement();
        processOrderType(rootElement);
        processLeadTime(rootElement);
        return inputDoc;
    }

    private void processOrderType(Element rootElement) {
        String orderNo = rootElement.getAttribute("OrderNo");
        String orderType = startsWith100(orderNo) ? WEBSITE : CALLCENTER;
        rootElement.setAttribute("OrderType", orderType);
    }

    private void processLeadTime(Element inputElement) {
        NodeList inputItems = inputElement.getElementsByTagName("Item");
        for (int i = 0; i < inputItems.getLength(); i++) {
            String itemId = inputItems.item(i).getAttributes().getNamedItem("ItemID").getNodeValue();
            Document doc = docBuilder.newDocument();
            Element item = doc.createElement("Item");
            item.setAttribute("ItemID", itemId);
            doc.appendChild(item);
            Document docOutXML = null;
            try {
                docOutXML = yifApi.invoke(env, "getItemList", doc);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (docOutXML != null) {
                Element outputDocElem = docOutXML.getDocumentElement();
                Element leadTime = (Element) outputDocElem.getElementsByTagName("InventoryParameters").item(0);
                int leadTimeInt = Integer.parseInt(leadTime.getAttribute("LeadTime"));
                Element orderLineElement = (Element) inputItems.item(i).getParentNode();
                orderLineElement.setAttribute("LineType", leadTimeInt > 5 ? STANDART : EXPRESS);
            }
        }
    }

    static boolean startsWith100(String s) {
        return s.substring(0, 3).equals("100");
    }
}
