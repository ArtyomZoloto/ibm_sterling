package com.tesco.oms.userexit;

import com.yantra.ycp.japi.YCPDynamicCondition;
import com.yantra.ycp.japi.ue.YCPBeforeCreateExceptionUE;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/*
* Configure the BeforeCreateExceptionUE to stamp Inbox/@DetailDescription, if the value is not present, as below -
If value of Inbox/@Description = 'UI Exception', then set Inbox/@DetailDescription = 'This is a Tesco UI Exception'
Else, set Inbox/@DetailDescription = value of Inbox/@Description.
* */

public class YCPBeforeCreateExceptionUEImpl implements YCPBeforeCreateExceptionUE {

    private static Logger logger = LoggerFactory.getLogger(YCPBeforeCreateExceptionUEImpl.class);

    @Override
    public Document beforeCreateException(YFSEnvironment yfsEnvironment, Document document) throws YFSUserExitException {

        logger.debug("calling YCPBeforeCreateExceptionUE callback!!!");
        System.out.println("com.tesco callback calling!!!");
        Element root = document.getDocumentElement();
        setDetails(root);
        System.out.println("SOUT Done with calling YCPBeforeCreateExceptionUE callback!!!");
        return document;
    }

    private void setDetails(Element element){
        String detailDescription = element.getAttribute("DetailDescription");
        System.out.println("DetailDescription is: " + detailDescription);
        if (detailDescription != null && !detailDescription.isEmpty()) {
            System.out.println("SOUT returning line 37");
            return;
        }
        String description = element.getAttribute("Description");
        System.out.println("SOUT process line 41");
        System.out.println("Description is: " + description);
        if (description.equals("UI Exception")) {
            System.out.println("description equals UI Exception!");
            element.setAttribute("DetailDescription","This is a Tesco UI Exception");
        } else {
            System.out.println("description not equals UI Exception!");
            element.setAttribute("DetailDescription","This is NOT UI EXCEPTION");
            System.out.println("SOUT DetailDescription from Element is: " + element.getAttribute("DetailDescription"));
        }
    }
}
