package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;

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
        Map<String,String> map = new HashMap<>();
        map.put("property", Property.class.getName());
        setTagMapping(map);
    }

}
