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
    }

    protected XmlDefaultElement(String tagName, XmlCompoundElement parent) {
        super(tagName, null, parent);
    }


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
