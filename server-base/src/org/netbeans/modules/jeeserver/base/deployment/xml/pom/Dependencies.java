package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
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
public class Dependencies extends AbstractCompoundXmlElement {

    public Dependencies() {
        super("dependencies", null, null);
        init();
    }
    protected Dependencies(String tagName) {
        super("dependencies", null, null);
        init();
    }

    protected Dependencies(Element element, XmlCompoundElement parent) {
        super("dependencies", element, parent);
        init();
    }

    protected Dependencies(XmlCompoundElement parent) {
        super("dependencies", null, parent);
        init();
    }

    private void init() {
        Map<String,String> map = new HashMap<>();
        map.put("dependency", Dependency.class.getName());
        setTagMapping(map);
    }
    
/*    @Override
    public List<XmlElement> getChilds() {

        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            domList.forEach(el -> {
                if ("dependency".equals(el.getTagName())) {
                    childs.add(new Dependency(el, this));
                }
            });
        }
        return childs;
    }
*/
    public Dependency findDependency(String groupId,String artifactId, String type) {
        Dependency result = null;
        List<XmlElement> list = getChilds();
        for ( XmlElement e : list ) {
            if ( e instanceof Dependency) {
                Dependency d = (Dependency) e;
                if ( ! groupId.equals(d.getGroupId()) || ! artifactId.equals(d.getArtifactId()) )  {
                    continue;
                }
                String t0 = type != null ? type : "jar";
                String t1 = d.getType() != null ? d.getType() : "jar";
                if ( t0.equalsIgnoreCase(t1)) {
                    result = d;
                    break;
                }
            }
        }
        return result;
    }
}
