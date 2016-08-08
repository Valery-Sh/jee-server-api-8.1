/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.simple;

import org.netbeans.modules.jeeserver.base.deployment.xml.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Valery
 */
public class XmlChilds {
    private List<XmlElement> childList;
    private XmlCompoundElement owner;
            
    public XmlChilds(XmlCompoundElement owner) {
        childList = new ArrayList<>();
    }
    public XmlChilds add(XmlElement element) {
        childList.add(element);
        return this;
    }
}
