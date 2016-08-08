package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public abstract class AbstractXmlTextElement extends AbstractXmlElement implements XmlTextElement{
    
    private String text;
    
    public AbstractXmlTextElement(String tagName, Element element, XmlCompoundElement parent) {
        super(tagName, element, parent);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }
    @Override
    public void commitUpdates() {
        super.commitUpdates();
        getElement().setTextContent(text);

    }
  
}
