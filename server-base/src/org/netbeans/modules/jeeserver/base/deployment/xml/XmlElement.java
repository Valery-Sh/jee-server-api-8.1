package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlElement {

    String getTagName();
    
    void commitUpdates();
    
    //void check();    
    
    XmlCompoundElement getParent();
    
    void setParent(XmlCompoundElement parent);

    Element getElement();
    
    XmlAttributes getAttributes();

    default XmlElement cloneXmlElementInstance() {
        XmlElement element;

        try {
            Class<?> clazz = Class.forName(getClass().getName());
            Constructor<?> ctor = clazz.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);
            element = (XmlElement) ctor.newInstance(new Object[]{this.getTagName()});
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
            element = null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            element = null;
        }
        if ( element != null && (element instanceof XmlTextElement) && ! PomDocument.hasChildElements(getElement())) {
           ((XmlTextElement)element).setText(((XmlTextElement)this).getText());  
        }
        return element;
    }   
}
