package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
    
    default String getAttribute(String name) {
        return getElement() == null ? "" :  getElement().getAttribute(name);
    }
    default String getAttribute(String namespaceURI,String localName) {
        return getElement() == null ? "" :  getElement().getAttributeNS(namespaceURI,localName);
    }
    default void removeAttribute(String name) {
        if ( getElement() == null ) {
            return;
        }
        getElement().removeAttribute(name);
    }    
    default void setAttribute(String name, String value) {
        if ( getElement() == null ) {
            return;
        }
        getElement().setAttribute(name, value);
    }    
    default void setAttributes(Map<String,String> attrMap) {
        attrMap.forEach((name,value) -> {
            setAttribute(name, value);
        });
    }    
    
    default Map<String,String> getAttributes() {
        Map<String, String> map = new HashMap<>();
        
        if ( getElement() == null ) {
            return map;
        }
        NamedNodeMap nodeMap = getElement().getAttributes();

        if ( nodeMap == null || nodeMap.getLength() == 0 ) {
            return map;
        }
        for ( int i=0; i < nodeMap.getLength(); i++ ) {
             Node n = nodeMap.item(i); 
             String s = null;
             if ( nodeMap.item(i) instanceof Attr ) {
                Attr attr = (Attr) nodeMap.item(i);
                String name = attr.getName();
                String attrValue = attr.getValue();
                map.put(name, attrValue);
             }
        }
        return map;
    }

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
