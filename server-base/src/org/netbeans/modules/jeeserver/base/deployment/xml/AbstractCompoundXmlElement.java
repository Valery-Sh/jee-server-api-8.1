package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractCompoundXmlElement extends AbstractXmlElement implements XmlCompoundElement {

    //protected List<XmlElement> childList;
    //private Map<String, String> tagMapping;
    private XmlTagMap tagMapping;
    protected XmlChilds childs;
    
    protected AbstractCompoundXmlElement(String tagName) {
        this(tagName,null,null);
    }
    
    protected AbstractCompoundXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
    }
    /**
     * Returns an instance of object of type {@literal org.netbeans.modules.jeeserver.base.deployment.xml.XmlChilds}.
     * If the {@literal childs } property value has not been set yet the 
     * this method creates a new instance and sets the property value. 
     * 
     * @return an Object of type {@link XmlChilds }
     */
    @Override
    public XmlChilds getChilds() {
        if (childs == null) {
            childs = new XmlChilds(this);
        }
        return childs;
    }   
   
    @Override
    public XmlElement cloneXmlElementInstance() {
        XmlCompoundElement p = (XmlCompoundElement) super.cloneXmlElementInstance();
        List<XmlElement> list = getChilds().list();
        list.forEach(e -> {
            p.getChilds().add(e.cloneXmlElementInstance());
        });
        return p;
    }    
    /**
     * Checks whether the given tag name is supported as a child element. The
     * method uses the property {@link #tagMapping ) to check.
     * If map is not {@literal  null{ and is not empty and the result of
     * {@literal tagMapping.get(tagName) } is {@literal null} the the return
     * value is {@literal false } otherwise the method returns {@literal true}.
     *
     * <p>
     * If map is null or is empty then if the parameter {@literal tagName}
     * is equal to {@literal "not-supported"} then the method returns
     * {@literal false}. Otherwise the returned value is {@literal true}.
     *
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported
     *      as a child element of the current element
     */
    @Override
    public boolean isChildSupported(String tagName) {
        if (tagMapping != null && !tagMapping.isEmpty()) {
            return tagMapping.get(tagName) != null;
        }
        return false;
    }

    @Override
    public Map<String, String> getTagMap() {
        return tagMapping;
    }

    @Override
    public void setTagMapping(Map<String, String> tagMapping) {
        this.tagMapping = tagMapping;
    }

    @Override
    public void commitUpdates() {
        super.commitUpdates();
        //
        // We cannot use getChildElements() to scan because one or more elements may be deleted. 
        //
        List<XmlElement> list = getChilds().list();
        if ( this instanceof XmlTextElement ) {
            if (list.isEmpty() && ((XmlTextElement)this).getText() != null) {
                getElement().setTextContent(((XmlTextElement)this).getText());
                return;
            }
        }
        
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
            XmlRoot.check(el);
        });

    }

}
