package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractXmlElement implements XmlElement {

    //protected abstract List<PomElement> getChilds();

    private final String tagName;
    private Element element;
    private XmlCompoundElement parent;

//    private boolean deleted;

    protected AbstractXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        this.tagName = tagName;
        this.parent = parent;
        this.element = element;

    }
    /**
     * The method must be private. For use in addChild method only.
     * Don't use in in code.
     * 
     * @param parent sets the parent {@link org.netbeans.modules.jeeserver.base.deployment.utils.pom#PomElement}
     */
    @Override
    public void setParent(XmlCompoundElement parent) {
        if (this.parent != null || parent == null) {
            return;
        }
        this.parent = parent;
    }


    @Override
    public XmlCompoundElement getParent() {
        return parent;
    }

    @Override
    public Element getElement() {
        return element;
    }
    protected XmlElement findFirstWithDOMElement() {
        XmlElement foundElement = null;
        XmlElement xmlElement = this;
        while (true) {
            if ( xmlElement.getElement() != null ) {
                foundElement = xmlElement;
                break;
            }
            if ( xmlElement.getParent() == null ) {
                break;
            }
            xmlElement = xmlElement.getParent();
        }
        return foundElement;
    }
            
    protected void createDOMElement() {
        Document doc = null;
        if (getElement() != null) {
            return;
        }
        XmlElement hasDomElement = findFirstWithDOMElement();
        
//        if (getParent().getElement() != null) {
        if (hasDomElement != null) {
            doc = hasDomElement.getElement().getOwnerDocument();
        }
        
        if ( doc == null ) {
            throw new NullPointerException(
               " AbstractXmlElement.createElement: Can't find an object of type org.w3c.dom.Document ");
        }

        this.element = doc.createElement(getTagName());

    }
    
    @Override
    public void commitUpdates() {
        if ( getParent() == null ) {
            throw new NullPointerException(
               " AbstractXmlElement.commitUpdates: The element '" + getTagName() + "' doesn't have a parent element");
        }
        Element parentEl = getParent().getElement();
        if (getElement() == null) {
            createDOMElement();
        }
        if (parentEl != null && ! XmlDocument.hasParentElement(getElement())) {
            parentEl.appendChild(getElement());
        }

    }


    @Override
    public String getTagName() {
        return tagName;
    }

}
