/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.simple;

import org.netbeans.modules.jeeserver.base.deployment.xml.*;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery
 */
public class Dependencies extends AbstractXmlCompoundElement{
    
    public Dependencies(Element dependenciesElement) {
        setElement(dependenciesElement);
    }
    
    public Element find(String groupId, String srtifactId, String type) {
        Element dep = null;
        List<Element> childs = getChilds();
        childs.forEach(c -> {
            
        });
        return dep;
    }
    
    
    public List<Element> getChilds() {
        List<Element> deps = new ArrayList<>();
        NodeList nl = getElement().getChildNodes();
        if ( nl.getLength() > 0 ) {
            for ( int i=0; i < nl.getLength(); i++) {
                if ( XmlRoot.DEPENDENCY.equals(nl.item(0).getNodeName())) {
                    deps.add((Element)nl.item(0));
                }
            }
        }
        return deps;
    }
}
