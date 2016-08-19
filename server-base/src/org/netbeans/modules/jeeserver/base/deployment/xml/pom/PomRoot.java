package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTagMap;

/**
 * @author Valery Shyshkin
 */
//public class XmlRoot extends AbstractCompoundXmlElement implements PomRootElement {
public class PomRoot extends XmlRoot {//AbstractCompoundXmlElement {

    //private final Document document;


    public PomRoot(PomDocument pomDocument) {
        super(pomDocument);
        init();
    }
    private void init() {
        XmlTagMap map = new XmlTagMap();
        map.put("dependencies", Dependencies.class.getName());
        map.put("properties", PomProperties.class.getName());
        setTagMap(map);
        getTagMap().setDefaultClass(null);
    }

/*    @Override
    public Element getElement() {
        return document.getDocumentElement();
    }

    @Override
    public void createDOMElement() {
    }
*/    
    /**
     * A convenient method to access a {@code Dependencies} pom element.
     * 
     * @return an instance of type {@link Dependencies } or null if the pom
     * doesn't contain the {@code dependencies} tag.
     */
    public Dependencies getDependencies() {
        Dependencies dependencies = null;
        for (XmlElement e : getChilds().list()) {
            if (e instanceof Dependencies) {
                return (Dependencies) e;
            }
        }
        return dependencies;
    }
    public boolean isPomDocument() {
        if ( ! "project".equals(getElement().getTagName()) ) {
            return false;
        }
        String xmlns = "http://maven.apache.org/POM";
        String attr = getElement().getAttribute("xmlns");
        return !(attr == null || ! attr.substring(0,xmlns.length()).equals(xmlns));
    }
}//class XmlRoot
