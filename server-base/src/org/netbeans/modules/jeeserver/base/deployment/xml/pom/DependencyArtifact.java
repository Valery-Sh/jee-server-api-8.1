package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.AbstractXmlTextElement;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;

/**
 *
 * @author Valery Shyshkin
 */
public class DependencyArtifact extends AbstractXmlTextElement {
    
    public DependencyArtifact(String tagName) {
        super(tagName, null, null);
    }

    protected DependencyArtifact(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
    }

    protected DependencyArtifact(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
    }
    public boolean equals(Object other) {
        String s1 = getText();
        String s2 = "jar";
        if ( "type".equals(getTagName() )) {
            if ( other != null ) {
                s2 = ((DependencyArtifact)other).getText();
            } 
        } else {
            s2 = ((DependencyArtifact)other).getText();
        }
        return equals(s1, s2);
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
}
