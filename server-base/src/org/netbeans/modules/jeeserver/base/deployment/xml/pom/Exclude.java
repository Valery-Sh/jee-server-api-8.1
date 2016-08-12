package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.ArrayList;
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
public class Exclude extends AbstractCompoundXmlElement {


    public Exclude() {
        super("exclude", null, null);
        init();
    }
    public Exclude(String tagName) {
        super("exclude", null, null);
        init();
    }

    protected Exclude(Element element, XmlCompoundElement parent) {
        super("exclude", element, parent);
        init();
    }

    protected Exclude(XmlCompoundElement parent) {
        super("exclude", null, parent);
        init();
    }

    private boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 != null) {
            return s1.equals(s2);
        }
        return s2.equals(s1);

    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("groupId", DependencyArtifact.class.getName());
        map.put("artifactId", DependencyArtifact.class.getName());        
/*        map.put("version", DependencyArtifact.class.getName());        
        map.put("scope", DependencyArtifact.class.getName());        
        map.put("type", DependencyArtifact.class.getName());        
        map.put("optional", DependencyArtifact.class.getName());        
        map.put("systemPath", DependencyArtifact.class.getName());        
        map.put("exclusions", DependencyArtifact.class.getName());        
*/
        setTagMap(map);
    }

    private String getChildTagValue(String tagName) {
        List<XmlElement> list = getChilds().list();
        String value = null;
        for (XmlElement pe : list) {
            if (tagName.equals(pe.getTagName())) {
                value = ((DependencyArtifact) pe).getText();
            }
        }
        return value;
    }

    public String getGroupId() {
        return getChildTagValue("groupId");
    }
    public String getType() {
        return getChildTagValue("type");
    }

    public String getArtifactId() {
        return getChildTagValue("artifactId");
    }

    public String getVersion() {
        return getChildTagValue("version");
    }

    @Override
    public boolean equals(Object other) {
        Exclude o = (Exclude) other;
        if (other == null) {
            return false;
        }
        boolean b = false;
        String thisType = getType();
        if ( thisType == null  ) {
            thisType = "jar"; //default type
        }
        String otherType = o.getType();
        if ( otherType == null  ) {
            otherType = "jar"; //default type
        }
        
        return equals(getGroupId(), o.getGroupId())
                && equals(getArtifactId(), o.getArtifactId())
                && thisType.equals(otherType);
    }
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

}
