package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlCompoundElement extends XmlElement {

    Map<String, String> getTagMapping();

    void setTagMapping(Map<String, String> tagMapping);

    /**
     * Checks whether the given tag name is supported as a child element.
     *
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported as a child
     * element of the current element
     */
    boolean isChildSupported(String tagName);

    XmlChilds getChilds();

    default boolean beforeAddChild(XmlElement child) {
        return true;
    }

    /**
     * A convenient method which just invokes {@literal getChilds().addChild(child)
     * }
     *
     * @param child an element to be added.
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement addChild(XmlElement child) {
        return getChilds().addChild(child);
    }

    /**
     * A convenient method which just invokes {@literal getChilds().deleteChild(child)
     * }
     *
     * @param child an element to be deleted.
     * @return a calling instance of type {@link XmlCompoundElement }.
     */
    default XmlCompoundElement deleteChild(XmlElement toDelete) {
        return getChilds().deleteChild(toDelete);
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
        return getChilds().replaceChild(child, newChild);
    }

}
