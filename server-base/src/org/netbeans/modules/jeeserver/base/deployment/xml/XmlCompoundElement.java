package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlCompoundElement extends XmlElement {
    
    Map<String, String> getTagMapping();
    void setTagMapping(Map<String, String>  tagMapping);    
    /**
     * Checks whether the given tag name is supported as a child element.
     * @param tagName the tag name to be checked
     * @return {@literal true} if the given tag name is supported 
     *      as a child element of the current element
     */
    boolean isChildSupported(String tagName);

    //PomElement createChildByDomElement(Element el);
    List<XmlElement> getChilds();

    default boolean beforeAddChild(XmlElement child) {
        return true;
    }

    /*    default XmlElement deleteChild(XmlElement toDelete) {
        if (getChilds().contains(toDelete)) {
            toDelete.markDeleted(true);
        }
        return this;
    }
     */
    default XmlCompoundElement deleteChild(XmlElement toDelete) {
        if (toDelete == null || !getChilds().contains(toDelete)) {
            return this;
        }
        if (toDelete.getElement() != null && toDelete.getElement().getParentNode() != null) {
            toDelete.getElement().getParentNode().removeChild(toDelete.getElement());
            XmlDocument.getParentElement(toDelete.getElement()).removeChild(toDelete.getElement());
        }
        getChilds().remove(toDelete);

        return this;
    }

    /**
     * 
     * @param child
     * @return
     */
    default XmlCompoundElement addChild(XmlElement child) {
        if (this instanceof XmlTextElement) {
            if ( ((XmlTextElement)this).getText() != null )
            throw new IllegalStateException(
                "XmlCompoundElement.addChild(): can't add child since the element has not emty text content");

        }

        if (child.getElement() != null && XmlDocument.existsInDOM(child.getElement())) {
            throw new IllegalArgumentException(" The parameter is already in DOM tree and cannot be added ");
        }
        if (getChilds().contains(child)) {
            return this;
        }
        if (!isChildSupported(child.getTagName())) {
            throw new IllegalArgumentException(" PomCompoundElement.addChild: The child element with a tag name '"
                    + child.getTagName()
                    + "' is not supported for the parent named as '"
                    + getTagName() + "' ");
        }
        beforeAddChild(child);
        child.setParent(this);
        getChilds().add(child);
        return this;

    }

    /**
     * Replaces the child element specified by the first parameter with a new
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
        if (child != null && getChilds().contains(child)) {
            deleteChild(child);
        }
        if (newChild == null) {
            throw new NullPointerException(
                " PomComponentElement.replaceChild: The second parameter of the method can't be null");
        }
        if (newChild.getElement() != null && XmlDocument.existsInDOM(newChild.getElement())) {
            throw new IllegalArgumentException("PomComponentElement.replaceChild: The newChild parameter is already in DOM tree and cannot be added ");
        }

        if (!isChildSupported(newChild.getTagName())) {
            throw new IllegalArgumentException(" PomCompoundElement.replaceChild: The child element with a tag name '"
                    + newChild.getTagName()
                    + "' is not supported for the parent named as '"
                    + getTagName() + "' ");
        }

        addChild(newChild);
        return this;

    }
}
