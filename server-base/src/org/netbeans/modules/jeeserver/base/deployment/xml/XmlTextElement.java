package org.netbeans.modules.jeeserver.base.deployment.xml;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlTextElement {
    String getTextContent();
    void setText(String text);
    default String getText() {
        if ( getTextContent() != null) {
            return getTextContent().trim();
        }
        return null;
    }
    
}
