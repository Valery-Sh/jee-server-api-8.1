/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.simple;

import org.netbeans.modules.jeeserver.base.deployment.xml.*;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public interface XmlElement {
    Element getElement();
    /**
     * Just a convenient method.
     * @return a DOM element
     */
    default String getTagName() {
        return getElement().getNodeName();
    }
}
