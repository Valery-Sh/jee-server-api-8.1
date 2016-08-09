package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;

/**
 * @author Valery Shyshkin
 */
//public class XmlRoot extends AbstractCompoundXmlElement implements PomRootElement {
public class PomRoot extends XmlRoot {//AbstractCompoundXmlElement {

    private final Document document;


    public PomRoot(Document document) {
        super(document);
        this.document = document;
        init();
    }
    private void init() {
        Map<String,String> map = new HashMap<>();
        map.put("dependencies", Dependencies.class.getName());
        map.put("properties", PomProperties.class.getName());
        setTagMapping(map);
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
     * A convenient method to access a {@literal Dependencies} pom element.
     * 
     * @return an instance of type {@link Dependencies } or null if the pom
     * doesn't contain the {@literal dependencies} tag.
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
    /**
     * Checks if the given dom element is supported by the api.
     * For example the root may contain such element as {@literal build }.
     * But for now that element is not supported/
     * @param el a dom element to be checked
     * @return {@literal true } if the element is supported 
     */
    @Override
    public boolean isChildSupported(String tagName) {
        //if ( ! "project".equals(getElement().getTagName()) ) {
        if ( !isPomDocument() ) {
            return true;
        } 
        if (getTagMapping() != null && !getTagMapping().isEmpty()) {
            return getTagMapping().get(tagName) != null;
        }
        return false;
    }
    
    
    public boolean isPomDocument() {
        if ( ! "project".equals(getElement().getTagName()) ) {
            return false;
        }
        String xmlns = "http://maven.apache.org/POM";
        String attr = getElement().getAttribute("xmlns");
        if ( attr == null || ! attr.substring(0,xmlns.length()).equals(xmlns)) {
            return false;
        }
        return true;
    }
    /**
     * For now the only element that the API recognizes is 
     * the {@literal  dependencies} element.
     * Thus a list to be returned contains zero ore one element.
     * 
     * @return a list of {@link Dependencies } elements.
     */
/*    @Override
    public List<XmlElement> getChildElements() {

        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            domList.forEach(el -> {
                XmlElement pel = null;
                switch (el.getNodeName()) {
                    case "dependencies":
                        pel = new Dependencies(el, this);
                        break;
                    case "properties":
                        pel = new PomProperties(el, this);
                        break;
                        
                }
                assert pel != null;
                childs.add(pel);
            });
        }
        return childs;
    }
*/    
    /**
     * When working with a {@literal pom document } we can create,
     * add, modify or mark as deleted elements that are not actually 
     * {@literal  DOM Tree } members. The method does the necessary job to create and modify
     * {@literal  DOM Tree} using a list of {@link XmlElement }.  
     */
/*    @Override
    public void commitUpdates() {
        //
        // Create a copy of child list because one or more elements of 
        // the original one may be deleted.
        //
        List<XmlElement> list = new ArrayList<>(getChildElements());
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });
    }
*/
}//class XmlRoot
