package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
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

/*    @Override
    public boolean isChildSupported(String tagName) {
        return SUPPORTED_CHILD.contains(tagName);
    }
*/
/*    public List<XmlElement> getChilds() {

        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            domList.forEach(el -> {
                if ("exclude".equals(el.getTagName())) {
                    childs.add(new Dependency(el, this));
                }
            });
        }
        return childs;
    }
*/
}
