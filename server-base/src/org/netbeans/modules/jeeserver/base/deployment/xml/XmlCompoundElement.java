package org.netbeans.modules.jeeserver.base.deployment.xml;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlCompoundElement extends XmlElement {

    //Map<String, String> getTagMap();
    //void setTagMapping(Map<String, String> tagMapping);
    XmlTagMap  getTagMap();
    void setTagMap(XmlTagMap tagMapping);    
    
    //void setTagMapping(Map<String, String> tagMapping);

    /**
     * Checks whether the given tag name is supported as a child element.
     *
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported as a child
     * element of the current element
     */
    //boolean isChildSupported(String tagName);

    XmlChilds getChilds();

    default boolean beforeAddChild(XmlElement child) {
        return true;
    }

    /**
     * A convenient method which just invokes {@literal getChilds().add(child)
 }
     *
     * @param child an element to be added.
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement addChild(XmlElement child) {
        return getChilds().add(child);
    }

    /**
     * A convenient method which just invokes {@literal getChilds().remove(child)
 }
     *
     * @param toRemove the element to be removed
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement removeChild(XmlElement toRemove) {
        return getChilds().remove(toRemove);
    }

    /**
     * A convenient Replaces the child element specified by the first parameter with a new
     * element as specified by the second parameter. If an element specified by
     * the first parameter cannot be found in the child list of this element
     * then the pom element specified by the second parameter is just added to
     * child list of this parent element.
     *
     * @param child the element to be replaced
     * @param newChild the element to replace an existing one
     * @return an object of type {@link XmlCompoundElement } which represents an
     * object which calls this method (parent element)
     */
    default XmlCompoundElement replaceChild(XmlElement child, XmlElement newChild) {
        return getChilds().replace(child, newChild);
    }

}
