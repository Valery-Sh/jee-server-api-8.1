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
        Map<String,String> map = new HashMap<>();
        map.put("exclude", Exclude.class.getName());
        setTagMapping(map);
    }

}
