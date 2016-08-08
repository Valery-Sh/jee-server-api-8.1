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
public class Dependency extends AbstractXmlCompoundElement {
    
    public Dependency(Element dependencyElement) {
        setElement(dependencyElement);
    }
    public String getGroupId() {
        return getValueByTagName("groupId");
    }
    public String getArtifactId() {
        return getValueByTagName("artifactId");
    }
    public String getType() {
        return getValueByTagName("type");
    }
    public String getVersion() {
        return getValueByTagName("version");
    }
    
    public String getValueByTagName(String tagName) {
        
        List<Element> childs = getChilds();
        for ( Element el : childs) {
            if ( tagName.equals(el.getNodeName()) ) {
                return el.getTextContent();
            }
        }
        return null;
        
    }
    protected boolean compare(Element el, String groupId, String srtifactId, String type ) {
        Dependency dep = new Dependency(el);
        boolean b = compareString(getElement().getNodeName(),getGroupId());
        if ( ! b ) {
            return false;
        } 
        b = compareString(getElement().getNodeName(),getArtifactId());
        return b;
                
    } 
    protected boolean compareString(String s1, String s2) {
        if ( s1 == null && s2 == null) {
            return true;
        }
        if ( s1 != null ) {
            return s1.equals(s2);
        }
        return false;
    } 

    public List<Element> getChilds() {
        List<Element> deps = new ArrayList<>();
        NodeList nl = getElement().getChildNodes();
        if ( nl.getLength() > 0 ) {
            for ( int i=0; i < nl.getLength(); i++) {
                    deps.add((Element)nl.item(0));
            }
        }
        return deps;
    }
    
}
