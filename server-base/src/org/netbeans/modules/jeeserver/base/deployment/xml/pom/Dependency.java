package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractCompoundXmlElement;
import java.util.List;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

/**
 *
 * @author Valery Shyshkin
 */
public class Dependency extends AbstractCompoundXmlElement {


    public Dependency() {
        super("dependency", null, null);
        init();
    }
    public Dependency(String tagName) {
        super("dependency", null, null);
        init();
    }

    protected Dependency(Element element, XmlCompoundElement parent) {
        super("dependency", element, parent);
        init();
    }

    protected Dependency(XmlCompoundElement parent) {
        super("dependency", null, parent);
        init();
    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("dependency", Dependency.class.getName());
        map.put("groupId", DependencyArtifact.class.getName());
        map.put("artifactId", DependencyArtifact.class.getName());        
        map.put("version", DependencyArtifact.class.getName());        
        map.put("scope", DependencyArtifact.class.getName());        
        map.put("type", DependencyArtifact.class.getName());        
        map.put("optional", DependencyArtifact.class.getName());        
        map.put("systemPath", DependencyArtifact.class.getName());        
        map.put("exclusions", Exclusions.class.getName());        

        setTagMap(map);
        getTagMap().setDefaultClass(null);
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
    public DependencyArtifact findByTagName(String tagName) {
        List<XmlElement> list = getChilds().list();
        DependencyArtifact result = null;
        for ( XmlElement el : list ) {
            if ( (el instanceof DependencyArtifact) && tagName.equals(el.getTagName()) ) {
                result = (DependencyArtifact) el;
                break;
            }
        }
        return result;
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
        Dependency o = (Dependency) other;
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
