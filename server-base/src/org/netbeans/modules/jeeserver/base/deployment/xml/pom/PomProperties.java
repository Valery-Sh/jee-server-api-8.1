package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;

/**
 *
 * @author Valery
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

/*    @Override
    public List<XmlElement> getChilds() {

        if (childs == null) {
            childs = new ArrayList<>();

            List<Element> domList = getChildDomElements();
            domList.forEach((Element el) -> {
                XmlElement pel = null;
                switch (el.getNodeName()) {
                    case "property":
                        pel = new Property(el, this);
                        ((Property)pel).setText(el.getTextContent());
                        break;
                }
                assert pel != null;
                childs.add(pel);
            });
        }
        return childs;
    }
*/
}
