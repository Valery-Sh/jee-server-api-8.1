package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

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
        //Map<String,String> map = new HashMap<>();
        XmlTagMap map = new XmlTagMap();
        map.put("dependency", Dependency.class.getName());
        setTagMap(map);
    }
    
    public Dependency findDependency(String groupId,String artifactId, String type) {
        Dependency result = null;
        List<XmlElement> list = getChilds().list();
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
