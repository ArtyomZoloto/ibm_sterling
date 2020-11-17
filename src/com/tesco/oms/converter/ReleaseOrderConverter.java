package com.tesco.oms.converter;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.rmi.RemoteException;

public class ReleaseOrderConverter {

    public Document writeJsonToDb(YFSEnvironment env, Document inputDoc) {
        System.out.println("ARTEM input doc: " + docToString(inputDoc));
        JSONObject jsonRoot = new JSONObject();
        JSONObject jsonOrder = new JSONObject();
        JSONArray jsonOrderLines = new JSONArray();

        Element inputRootElement = inputDoc.getDocumentElement();
        Element orderElement = (Element) inputRootElement.getElementsByTagName("Order").item(0);

        NodeList orderLines = inputRootElement.getElementsByTagName("OrderLine");

        for (int i = 0; i < orderLines.getLength(); i++) {
            JSONObject jsonOrderLine = new JSONObject();
            Element orderLineElement = (Element) orderLines.item(i);
            Element itemElement = (Element) orderLineElement.getElementsByTagName("Item").item(0);
            try {
                jsonOrderLine.put("PrimeLineNo", orderLineElement.getAttribute("PrimeLineNo"));
                jsonOrderLine.put("ItemID", itemElement.getAttribute("ItemID"));
                jsonOrderLine.put("Quantity", orderLineElement.getAttribute("OrderedQty"));
                jsonOrderLine.put("ProductClass", itemElement.getAttribute("ProductClass"));
                jsonOrderLine.put("UOM", itemElement.getAttribute("ItemWeightUOM"));
                jsonOrderLines.put(jsonOrderLine);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonOrder.put("OrderNo", orderElement.getAttribute("OrderNo"));
            jsonOrder.put("EnterpriseCode", orderElement.getAttribute("EnterpriseCode"));
            jsonOrder.put("ReleaseNo", inputRootElement.getAttribute("ReleaseNo"));
            jsonOrder.put("OrderDate", inputRootElement.getAttribute("OrderDate"));
            jsonOrder.put("ReqDeliveryDate", inputRootElement.getAttribute("ReqDeliveryDate"));
            jsonOrder.put("ShipNode", inputRootElement.getAttribute("ShipNode"));
            jsonOrder.put("OrderLines", jsonOrderLines);
            jsonRoot.put("Order", jsonOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("ARTEM GENERATED JSON: " + jsonRoot);

        Document apiDoc = null;
        try {
            YIFApi api = YIFClientFactory.getInstance().getLocalApi();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            apiDoc = builder.newDocument();
            Element rootElement = apiDoc.createElement("CreateExportDataEx");
            rootElement.setAttribute("EnterpriceCode", orderElement.getAttribute("EnterpriseCode"));
            rootElement.setAttribute("FlowName","Artem flow name");
            rootElement.setAttribute("SubFlowName","Artem sub flow name");

            Element exportSystemIds = apiDoc.createElement("ExportSystemIds");
            Element exportSystemId = apiDoc.createElement("ExportSystemId");
            exportSystemId.setAttribute("SystemId","Artem System");
            exportSystemIds.appendChild(exportSystemId);
            rootElement.appendChild(exportSystemIds);

            Element xmlExportData = apiDoc.createElement("XmlExportData");
            xmlExportData.setTextContent(jsonRoot.toString());
            rootElement.appendChild(xmlExportData);
            apiDoc.appendChild(rootElement);
            String output = docToString(apiDoc);
            System.out.println("ARTEM: " + output);

            api.invoke(env, "createExportDataEx", apiDoc);
        } catch (RemoteException | YIFClientCreationException | ParserConfigurationException e) {
            e.printStackTrace();
        }



        return apiDoc;
    }

    public static void main(String[] args) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new File("ReleaseOrderXML.xml"));

        ReleaseOrderConverter c = new ReleaseOrderConverter();
        c.writeJsonToDb(null, document);

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
