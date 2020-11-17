package com.tesco.oms.transaction;

import com.tesco.oms.invertory.TSCGRAdjustInventory;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.omp.agent.YFSReleaseOrderAgent;
import com.yantra.omp.agent.YFSScheduleOrderAgent;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class CreateTescoInvoice extends YCPBaseAgent {
    private static YIFApi api;
    private static DocumentBuilder docBuilder;

    public CreateTescoInvoice() {
        System.out.println("CreateTescoInvoice created...");
        try {
            api = YIFClientFactory.getInstance().getLocalApi();
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (YIFClientCreationException ex) {
            System.out.println(this.getClass().getName() + ": can't create api");
        } catch (ParserConfigurationException ex) {
            System.out.println("can't create Document factory builder");
        }
    }


    @Override
    public List getJobs(YFSEnvironment env, Document inXML) throws Exception {
        System.out.println("CreateTescoInvoice getJobs() calling...");
        Document doc = docBuilder.newDocument();
        Element orderElement = doc.createElement("OrderInvoice");
        orderElement.setAttribute("EnterpriseCode", "TESCO GR");
        orderElement.setAttribute("Status", "00");
        doc.appendChild(orderElement);
        System.out.println("calling getOrderInvoiceList api...");
        Document orderInvoiceListApiResult = api.invoke(env, "getOrderInvoiceList", doc);
        Element orderInvoiceListRootElement = orderInvoiceListApiResult.getDocumentElement();
        NodeList orderInvoiceNodeList = orderInvoiceListRootElement.getElementsByTagName("OrderInvoice");
        System.out.println("getOrderInvoiceList: " + orderInvoiceNodeList.getLength() + "elements provided");

        List<Document> invoiceList = new ArrayList<>(orderInvoiceNodeList.getLength());
        for (int i = 0; i < orderInvoiceNodeList.getLength() ; i++) {
            Document invoiceDoc = docBuilder.newDocument();
            Node imported = invoiceDoc.importNode(orderInvoiceNodeList.item(i),true);
            invoiceDoc.appendChild(imported);
            invoiceList.add(invoiceDoc);
            System.out.println("getJobs() document added: ");
            System.out.println(TSCGRAdjustInventory.docToString(invoiceDoc));;
        }
        System.out.println("CreateTescoInvoice getJobs(): " + invoiceList.size() + "output elements in list");
        return invoiceList;
    }

    @Override
    public void executeJob(YFSEnvironment yfsEnvironment, Document document) throws Exception {
        System.out.println("executeJob document is: ");
        System.out.println(TSCGRAdjustInventory.docToString(document));

        System.out.println("CreateTescoInvoice executeJobs() calling...");
        Element rootElement = document.getDocumentElement();
        rootElement.setAttribute("Status", "01");

            Document doc = docBuilder.newDocument();

            System.out.println("++++++++++ inside doc:");
            System.out.println(TSCGRAdjustInventory.docToString(doc));

            Element invoiceElement = doc.createElement("OrderInvoice");

            invoiceElement.setAttribute("InvoiceNo",  rootElement.getAttribute("InvoiceNo"));
            invoiceElement.setAttribute("OrderInvoiceKey",  rootElement.getAttribute("OrderInvoiceKey"));
            invoiceElement.setAttribute("Status",  rootElement.getAttribute("Status"));
            doc.appendChild(invoiceElement);

            System.out.println("after property setup...");
            System.out.println(TSCGRAdjustInventory.docToString(doc));

            System.out.println("calling changeOrderInvoice api....");
            api.invoke(yfsEnvironment,"changeOrderInvoice", doc);

            api.executeFlow(yfsEnvironment,"TescoCreateInvoice",document);

    }
}
