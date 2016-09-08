package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractXmlTextElement;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

/**
 * The class that corresponds to the  child element of the element
 * {@code "PomProperties" }.
 * 
 * @see PomProperties
 * @author Valery Shyshkin
 */
public class Property extends AbstractXmlTextElement {
    /**
     * Creates a new instance of the class with a specified {@code tagName}.
     *
     * @param tagName the name of the tag property
     */
    public Property(String tagName) {
        super(tagName,null,null);
    }
    
    protected Property(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }
    
    protected Property(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName,element, parent);
    }

    protected Property(String tagName,XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
    
}
