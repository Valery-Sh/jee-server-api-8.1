/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.simple;

import org.netbeans.modules.jeeserver.base.deployment.xml.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery
 */
public class XmlRoot extends AbstractXmlCompoundElement {

    
    //private Dependencies dependencies;
    public static final String DEPENDENCIES = "dependencies";
    public static final String DEPENDENCY = "dependency";

    public Element getDependencies() {
        NodeList nl = getElement().getElementsByTagName(DEPENDENCIES);
        Element depsEl = null;

        if (nl.getLength() > 0) {
            depsEl = (Element)nl.item(0);
        }

        return depsEl;

    }

    public Element addDependencies() {

//            dependencies = new Dependencies();
        NodeList nl = getElement().getElementsByTagName(DEPENDENCIES);
        if ( nl.getLength() > 0 ) {
            return (Element)nl.item(0);
        }
        Element depsEl = getElement()
                    .getOwnerDocument()
                    .createElement(DEPENDENCIES);
        getElement().appendChild(depsEl);
        return depsEl;
    }
}
