package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractXmlElement implements XmlElement {
    /**
     * The tag name of the element.
     */
    private final String tagName;
    /**
     * The {@code DOM Element} this element is bound to..
     */
    private Element element;
    /**
     * The parent element of {@code this} element.
     */
    private XmlCompoundElement parent;
    /**
     * A collection of {@code attributes} of this element. Never {@code null}.
     */
    private final XmlAttributes attributes;
            
    protected AbstractXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        this.tagName = tagName;
        this.parent = parent;
        this.element = element;
        this.attributes = new XmlAttributes(this);
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

    @Override
    public XmlAttributes getAttributes() {
        return attributes;
    }
    
    protected XmlElement findFirstParentWithDOMElement() {
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
        XmlElement hasDomElement = findFirstParentWithDOMElement();
        
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
    /**
     * Does work to create a {@code DOM Element} and append it to
     * a {@code Dom Tree}.
     * Fist checks whether the element has a {@link #parent}. If 
     * the parent property is {@code null } then a {@code NullPointerException} 
     * is thrown. 
     * <p>If the property {@link #element } is null then creates
     * a new element of type {@code org.w3c.dom.Element} and sets the property.
     * <p>
     * If this element implements {@link XmlTextElement} then sets if necessary
     * the {@code textContent} property of the DOM element.
     * <p>
     * Copies values of {@link #attributes} to the the {@code DOM element}.
     * <p>
     * Appends the DOM {@link element} to the {@code DOM Tree}. 
     */
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
        if ( (this instanceof XmlTextElement) && ! ((this instanceof XmlCompoundElement))) {
            if ( null != ((XmlTextElement)this).getText()) {
                getElement().setTextContent(((XmlTextElement) this).getText());
//                return;
            }
        }
        
        this.getAttributes().copyTo(element);        
        if (parentEl != null && ! XmlDocument.hasParentElement(getElement())) {
            parentEl.appendChild(getElement());
        }

    }
    /**
     * Returns the tag name of the element.
     * @return the tag name of the element.
     */
    @Override
    public String getTagName() {
        return tagName;
    }

}
