package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChilds {

    private final List<XmlElement> childs;
    private final XmlCompoundElement childsOwner;

    public XmlChilds(XmlCompoundElement childsOwner) {
        childs = new ArrayList<>();
        this.childsOwner = childsOwner;
        init();
    }

    private void init() {
        List<Element> domList = getChildDomElements();
        if (this instanceof XmlTextElement) {
            String text = ((XmlTextElement) this).getText();
            if (!domList.isEmpty() && text != null) {
                throw new IllegalStateException(
                        " XmlChilds.getChilds(): Can't get child elements since the element has not null text property.");
            }
        }

        domList.forEach(el -> {
            String s = el.getTagName();
            XmlChildElementFactory f = new XmlChildElementFactory(childsOwner);
            XmlElement xmlEl = f.createXmlElement(el);

//            if (xmlEl == null) {
//                xmlEl = new XmlDefaultElement(el, childsOwner);
//            }
            if ((xmlEl instanceof XmlTextElement)) {
                String content = xmlEl.getElement().getTextContent();
                if (!XmlDocument.hasChildElements(xmlEl.getElement()) && content.length() > 0) {
                    ((XmlTextElement) xmlEl).setText(content);
                }
            }

            childs.add(xmlEl);
        });

    }

    protected List<Element> getChildDomElements() {

        List<Element> list = new ArrayList<>();
        if (childsOwner.getElement() != null) {
            NodeList nl = childsOwner.getElement().getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if ((nl.item(i) instanceof Element)) {
                        Element el = (Element) nl.item(i);
                        //if (!childsOwner.isChildSupported(el.getTagName())) {
                        if (!isChildSupported(childsOwner, el.getTagName())) {
                            continue;
                        }
                        list.add(el);
                    }
                }
            }
        }
        return list;
    }

    protected boolean isChildSupported(XmlCompoundElement xmlElement, String tagName) {
        boolean result = xmlElement.getTagMap().isTagPathSupported(tagName);
        if (result) {
            return true;
        }
        if (xmlElement.getParent() == null) {
            return false;
        }
        String path = xmlElement.getTagName() + "/" + tagName;
        return isChildSupported(xmlElement.getParent(), path);

    }

    public List<XmlElement> list() {
        return new ArrayList(childs);
    }

    public XmlElement get(int index) {
        return childs.get(index);
    }

    public boolean isEmpty() {
        return childs.isEmpty();
    }

    public int size() {
        return childs.size();
    }

    public boolean contains(XmlElement e) {
        return childs.contains(e);
    }

    //
    //
    //
    /**
     * Appends the specified {@literal  xml element } to the end of an internal
     * collection.
     *
     * <ul>
     * <li>
     * Sets the 'childsOwner' property of the child.
     * </li>
     * <li>
     * If the child is already in the internal collections then the method does
     * nothing.
     * </li>
     * <li>
     * The method doesn't change the DOM Tree and doesn't create DOM Elements.
     * </li>
     * </ul>
     *
     * <p>
     * The method may throw an {@link IllegalStateException } when the
     * conditions below are all satisfied:
     * <ul>
     * <li>
     * a childsOwner element is an instance of {@link XmlTextElement }
     * and the childsOwner's 'text' property is not {@literal null }.
     * </li>
     * </ul>
     *
     * <p>
     * The method may throw an {@link IllegalArgumentException }
     * when {@literal  child.getElement() != null } and  
     * {@literal  child.getElement() } is already in DOM Tree.
     *
     * <p>
     * Also the method may throw an {@link IllegalArgumentException }
     * in case when the tagName property of the child is not supported by the
     * childsOwner {@literal  xml element }.
     *
     * @param child the element to be added
     * @return a childsOwner {@literal  xml element}.
     */
    public XmlCompoundElement add(XmlElement child) {
        if (childsOwner instanceof XmlTextElement) {
            if (((XmlTextElement) childsOwner).getText() != null) {
                throw new IllegalStateException(
                        "XmlCompoundElement.addChild(): can't add child since the element has not emty text content");
            }
        }

        if (child.getElement() != null && XmlDocument.existsInDOM(child.getElement())) {
            throw new IllegalArgumentException(" The parameter is already in DOM tree and cannot be added ");
        }
        if (contains(child)) {
            return childsOwner;
        }
        //        if (!childsOwner.isChildSupported(child.getTagName())) {
        if ( XmlBase.findXmlRoot(childsOwner) != null && !isChildSupported(childsOwner, child.getTagName())) {
            throw new IllegalStateException(" XmlCompoundElement.addChild: The child element with a tag name '"
                    + child.getTagName()
                    + "' is not supported for the childsOwner named as '"
                    + childsOwner.getTagName() + "' ");
        }

        childsOwner.beforeAddChild(child);
        child.setParent(childsOwner);
        childs.add(child);
        return childsOwner;

    }

    /**
     * Deletes a given element from an internal collection. If the internal
     * collections already contains such an element then the method does
     * nothing.
     *
     * @param child an object to be deleted.
     *
     * @return a childsOwner object of type {@link XmlCompoundElement }
     * of the element to be deleted.
     */
    public XmlCompoundElement remove(XmlElement child) {
        if (child == null || !contains(child)) {
            return childsOwner;
        }
        if (child.getElement() != null && child.getElement().getParentNode() != null) {
            XmlDocument.getParentElement(child.getElement()).removeChild(child.getElement());
        }
        childs.remove(child);

        return childsOwner;
    }

    /**
     * Replaces the child element specified by the first parameter with a new
     * element as specified by the second parameter. If an element specified by
     * the first parameter cannot be found in the child list of the childsOwner
     * element then the newChild specified by the second parameter is just added
     * to the child list.
     *
     * <p>
     * The method may throw an {@link IllegalStateException } when a childsOwner
     * element where the newChild is adding to is an instance of  
 {@link XmlTextElement } and the childsOwner's 'text' property is not 
 {@literal null }.
     *
     * <p>
     * The method may throw an {@link IllegalArgumentException }
     * when {@literal  newChild.getElement() != null } and  
     * {@literal  newChild.getElement() } is already in DOM Tree.
     *
     * <p>
     * Also the method may throw an {@link IllegalArgumentException }
     * in case when the tagName property of the newChild is not supported by the
     * childsOwner {@literal  xml element }.
     *
     *
     * @param child the element to be replaced
     * @param newChild the element to replace an existing one
     * @return an object of type {@link XmlCompoundElement } which represents an
     * object which calls this method (childsOwner element)
     */
    public XmlCompoundElement replace(XmlElement child, XmlElement newChild) {
        if (newChild == null) {
            throw new IllegalArgumentException(
                    " XmlChilds.replaceChild: The second parameter of the method can't be null");
        }
        if (child != null && contains(child)) {
            remove(child);
        }
        if (newChild.getElement() != null && XmlDocument.existsInDOM(newChild.getElement())) {
            throw new IllegalStateException("XmlChilds.replaceChild: The newChild parameter is already in DOM tree and cannot be added ");
        }
//isChildSupported(childsOwner,newChild.getTagName())
//        if (!childsOwner.isChildSupported(newChild.getTagName())) {
        if (XmlBase.findXmlRoot(childsOwner) != null && !isChildSupported(childsOwner, newChild.getTagName())) {
            throw new IllegalStateException(" XmlChilds.replaceChild: The child element with a tag name '"
                    + newChild.getTagName()
                    + "' is not supported for the childsOwner named as '"
                    + childsOwner.getTagName() + "' ");
        }

        childsOwner.getChilds().add(newChild);
        return childsOwner;

    }

    /**
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the childsOwner "p1/p2" will be searched.
     * </li>
     * <li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the childsOwner
     * "p1/p2" will be searched regardless of tag names.
     * </li>
     * </li>
     * </ul>
     *
     * @param from
     * @param path
     * @return
     */
    public static List<XmlElement> findChildsByPath(XmlCompoundElement from, final String path) {
        final List<XmlElement> result = new ArrayList<>();

        XmlChilds childs = from.getChilds();
        String[] paths = path.split("/");

        String first = paths[0];
        if ("*".equals(first)) {
            result.addAll(childs.list());
        } else {
            List<XmlElement> list = new ArrayList<>();
            List<XmlCompoundElement> clist = new ArrayList<>();
            childs.list().forEach(e -> {
                if (e.getTagName().equals(first)) {
                    list.add(e);
                    if (e instanceof XmlCompoundElement) {
                        clist.add((XmlCompoundElement) e);
                    }
                }

            });
            if (paths.length == 1) {
                result.addAll(list);
            } else {
                String nextPath = path.substring(paths[0].length() + 1);
                clist.forEach(e -> {
                    result.addAll(findChildsByPath(e, nextPath));
                });
            }
        }
        return result;
    }
}
