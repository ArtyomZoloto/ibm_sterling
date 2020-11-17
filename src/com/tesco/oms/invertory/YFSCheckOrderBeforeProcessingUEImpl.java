package com.tesco.oms.invertory;

import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCheckOrderBeforeProcessingUE;
import org.w3c.dom.Document;

public class YFSCheckOrderBeforeProcessingUEImpl implements YFSCheckOrderBeforeProcessingUE {

    @Override
    public boolean checkOrderBeforeProcessing(YFSEnvironment yfsEnvironment, Document document) throws YFSUserExitException {
        System.out.println("SOUT calling YFSCheckOrderBeforeProcessingUEImpl");
        String orderType = document.getDocumentElement().getAttribute("OrderType");
        System.out.println("SOUT ORDER TYPE is: " + orderType);
        boolean result = !orderType.equalsIgnoreCase("CSR");
        System.out.println("SOUT YFSCheckOrderBeforeProcessingUEImpl result is: " + result);
        return result;
    }
}
