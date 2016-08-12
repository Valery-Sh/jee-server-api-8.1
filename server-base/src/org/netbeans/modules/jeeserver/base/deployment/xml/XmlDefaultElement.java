package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlDefaultElement extends AbstractCompoundXmlElement implements XmlTextElement {

    //protected List<String> supportedChildElements = new ArrayList<>();

    private String text;

    public XmlDefaultElement(String tagName) {
        this(tagName, null);
    }

    protected XmlDefaultElement(Element element, XmlCompoundElement parent) {
        super(element.getTagName(), element, parent);
        init();
    }

    protected XmlDefaultElement(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
        init();
    }

    private void init() {

    }


    /**
     * Checks whether the given tag name is supported as a child element.
     * The method invokes {@link AbstractCompoundXmlElement#isChildSupported(java.lang.String)
     * and if the last returns {@literal true } then this method returns
     * {@literal true }. Otherwise if the  parameter value equals
     * to the string value of {@literal  "not-supported" } then it returns 
     * {@literal false } otherwise it return {@literal true }.
     * 
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported
     *      as a child element of the current element
     */
    @Override
    public boolean isChildSupported(String tagName) {
        if (getTagMap() != null && !getTagMap().isEmpty()) {
            return getTagMap().get(tagName) != null;
        }
        return   !"not-supported".equals(tagName);
    }

/*    @Override
    public void commitUpdates() {
        super.commitUpdates();
        //
        // Copy childs to another list.
        // We cannot use getChildElements() to scan because one or more elements 
        // may be deleted. 
        //
        List<XmlElement> list = new ArrayList<>(getChildElements());
        if (list.isEmpty() && text != null) {
            getElement().setTextContent(text);
            return;
        }
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });

    }
*/
/*    @Override
    public List<XmlElement> getChildElements() {
        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            if (!domList.isEmpty() && text != null) {
                throw new IllegalStateException(
                        " XmlDefaultElement.getChildElements(): Can't get child elements since the element has not null text property.");
            }

            domList.forEach(el -> {
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
*/
    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        if (!getChilds().isEmpty()) {
            throw new IllegalStateException(
                    "XmlDefaultElement.setText: can't set text since the element has child elements");
        }
        this.text = text;
        if ( getElement() != null ) {
            if ( text == null ) {
                getElement().setTextContent("");
            } else {
                getElement().setTextContent(text);
            }
        }
    }
}
