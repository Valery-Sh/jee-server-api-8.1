package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlDefaultElement extends AbstractCompoundXmlElement implements XmlTextElement {

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
     * Checks whether the given tag name is supported as a child element. The
     * method invokes {@link AbstractCompoundXmlElement#isChildSupported(java.lang.String)
     * and if the last returns {@literal true } then this method returns
     * {@literal true }. Otherwise if the  parameter value equals
     * to the string value of {@literal  "not-supported" } then it returns
     * {@literal false } otherwise it return {@literal true }.
     *
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported
     *      as a child element of the current element
     */
/*    @Override
    public boolean isChildSupported(String tagName) {
        
        if (getTagMap() != null && !getTagMap().isEmpty() ) {
            return getTagMap().get(tagName) != null;
        }
        
        XmlBase root = XmlBase.findXmlRoot(this);

        if (root != null && root.getTagMap() != null && !root.getTagMap().isEmpty() ) {
            return root.getTagMap().get(root.getRootPath(this, tagName)) != null;            
        }

        return ! "not-supported".equals(tagName);
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
        if (getElement() != null) {
            if (text == null) {
                getElement().setTextContent("");
            } else {
                getElement().setTextContent(text);
            }
        }
    }
}
