package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

/**
 *
 * @author Valery Shyshkin
 */
public class PomProperties extends AbstractCompoundXmlElement {


    public PomProperties() {
        super("properties", null, null);
        init();
    }
    public PomProperties(String tagName) {
        super("properties", null, null);
        init();
    }

    protected PomProperties(Element element, XmlCompoundElement parent) {
        super("properties", element, parent);
        init();        
    }

    protected PomProperties(XmlCompoundElement parent) {
        super("properties", null, parent);
        init();        
    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("property", Property.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }

}
