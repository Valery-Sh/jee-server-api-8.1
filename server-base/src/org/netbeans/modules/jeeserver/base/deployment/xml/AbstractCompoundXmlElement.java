package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractCompoundXmlElement extends AbstractXmlElement implements XmlCompoundElement {

    protected List<XmlElement> childs;
    private Map<String, String> tagMapping;

    protected AbstractCompoundXmlElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
    }
    
    @Override
    public List<XmlElement> getChilds() {
        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            if ( this instanceof XmlTextElement ) {
                String text = ((XmlTextElement)this).getText();
                if (!domList.isEmpty() && text != null) {
                    throw new IllegalStateException(
                        " XmlDefaultElement.getChilds(): Can't get child elements since the element has not null text property.");
                }
            }

            domList.forEach(el -> {
                String s = el.getTagName();
                XmlChildElementFactory f = new XmlChildElementFactory(this);
                XmlElement xmlEl = f.createXmlElement(el);

                if ( xmlEl == null  ) {
                    xmlEl = new XmlDefaultElement(el, this);                
                }
                
                if ( (xmlEl instanceof XmlTextElement)   ) {
                    String content = xmlEl.getElement().getTextContent(); 
                    if ( ! XmlDocument.hasChildElements(xmlEl.getElement()) &&  content.length() > 0 ) {
                        ((XmlTextElement) xmlEl).setText(content);
                    } 
                }
                
                childs.add(xmlEl);
            });
        }
        return childs;
    }

    protected List<Element> getChildDomElements() {

        List<Element> list = new ArrayList<>();
        if (getElement() != null) {
            NodeList nl = getElement().getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if ((nl.item(i) instanceof Element)) {
                        Element el = (Element) nl.item(i);
                        if (!isChildSupported(el.getTagName())) {
                            continue;
                        }
                        list.add(el);
                    }
                }
            }
        }
        return list;
    }
    @Override
    public XmlElement cloneXmlElementInstance() {
        XmlCompoundElement p = (XmlCompoundElement) super.cloneXmlElementInstance();
        List<XmlElement> list = getChilds();
        list.forEach(e -> {
            p.addChild(e.cloneXmlElementInstance());
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
    public Map<String, String> getTagMapping() {
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
        // We cannot use getChilds() to scan because one or more elements may be deleted. 
        //
        List<XmlElement> list = new ArrayList<>(getChilds());
        if ( this instanceof XmlTextElement ) {
            if (list.isEmpty() && ((XmlTextElement)this).getText() != null) {
                getElement().setTextContent(((XmlTextElement)this).getText());
                return;
            }
        }
        
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });

    }

}
