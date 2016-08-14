package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

/**
 *
 * @author Valery Shyshkin
 */
public class Exclusions  extends AbstractCompoundXmlElement {


    public Exclusions() {
        super("exclusions", null, null);
        init();
    }
    public Exclusions(String tagName) {
        super("exclusions", null, null);
        init();
    }

    protected Exclusions(Element element, XmlCompoundElement parent) {
        super("exclusions", element, parent);
        init();        
    }

    protected Exclusions(XmlCompoundElement parent) {
        super("exclusions", null, parent);
        init();        
    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("exclude", Exclude.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }

}
