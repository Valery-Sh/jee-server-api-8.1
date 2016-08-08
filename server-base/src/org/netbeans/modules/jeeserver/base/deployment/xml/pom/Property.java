package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractXmlTextElement;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;

/**
 *
 * @author Valery Shyshkin
 */
public class Property extends AbstractXmlTextElement {
    
    public Property() {
        super("property",null,null);
    }
    public Property(String tagName) {
        super("property",null,null);
    }
    
    protected Property(Element element, XmlCompoundElement parent) {
        super("property",element, parent);
    }

    protected Property(XmlCompoundElement parent) {
        super("property", null, parent);
    }
    
}
