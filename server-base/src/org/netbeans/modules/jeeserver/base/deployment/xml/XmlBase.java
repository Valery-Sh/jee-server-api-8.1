package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlChilds.ParentVisitor;
import org.w3c.dom.Element;

/**
 * Serves as a root for {@code XmlElement Tree}. The class does not perform any
 * operations with a {@code DOM Tree} and works only with object of type {@link XmlElement
 * } and knows nothing about {@code DOM Document}.
 *
 * @author Valery Shyshkin
 */
public class XmlBase extends AbstractCompoundXmlElement {

    /**
     * Create a new instance of the class by the given {@code tagName}
     *
     * @param tagName a tag name to create an instance
     */
    public XmlBase(String tagName) {
        super(tagName);
        init();

    }

    private void init() {
        getTagMap().setDefaultClass(XmlDefaultElement.class.getName());
    }

    /**
     * Creates and return a string of tag names separated by slash.
     * The last item of the result is the last element of the 
     * {@code tagNames} array. And the first item is a tag name of the uppermost
     * parent of the element specified by the parameter. If the element has no
     * parent then the first item is a tag name of the element itself.
     * 
     * @param element an object of type XmlElement
     * @param tagNames an array of additional tag names to build result string
     * @return a string of tag names separated by slash.
     */
/*    public String getParentChainList_1(XmlElement element, String... tagNames) {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }    
    public String getParentChainList_old(XmlElement element, String... tagNames) {
        List<XmlElement> parentChainList = XmlBase.getParentChainList(element);
        if (parentChainList.isEmpty()) {
            return null;
        }
        if (parentChainList.get(0) != this) {
            return null;
        }

        String rp = toStringPath(parentChainList, false);
        StringBuilder pathBuilder = new StringBuilder(rp);

        for (String tag : tagNames) {
            pathBuilder
                    .append("/")
                    .append(tag);
        }
        String result = pathBuilder.toString();
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }
*/
    /**
     * The path parameter may take two forms. For example
     * <ul>
     * <li>
     * "p1/p2/p3". then all child elements with tag name "p3" which resides in
     * the parent "p1/p2" will be searched.
     * </li>
     * <li>
     * <li>
     * "p1/p2/*". then all child elements which resides in the parent "p1/p2"
     * will be searched regardless of tag names.
     * </li>
     * </li>
     * </ul>
     *
     * @param from
     * @param path
     * @return
     */
    public static List<XmlElement> findElementsByPath(XmlCompoundElement from, final String path) {
        return from.getChilds().findChildsByPath(path);
        //return XmlChilds.findChildsByPath(from, path);
    }
    /**
     * Return the value as specifies by the static method 
     * {@code XmlChilds.findChildsByPath((String) }.
     * 
     * 
     * @param path the string representation of items separated by the slash
     * @return a collection of elements of type XmlElement.  
     * 
     * @see XmlChilds#findChildsByPath(org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement, java.lang.String) 
     */
    public List<XmlElement> findElementsByPath(String path) {
        return findElementsByPath(this, path);
    }
    /**
     * 
     * @param path
     * @return 
     */
    public XmlElement findFirstElementByPath(String path) {
        List<XmlElement> list = findElementsByPath(this, path);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public List<XmlElement> findElementsByPath(String path, Predicate<XmlElement> predicate) {
        List<XmlElement> list = findElementsByPath(path);
        List<XmlElement> result = new ArrayList<>();
        list.forEach(el -> {
            if (predicate.test(el)) {
                result.add(el);
            }
        });
        return result;
    }

    public List<XmlElement> findChilds(XmlElement parent, Predicate<XmlElement> predicate) {
        final List<XmlElement> result = new ArrayList<>();

        if (!(parent instanceof XmlCompoundElement)) {
            return result;
        }
        XmlChilds childs = ((XmlCompoundElement) parent).getChilds();

        childs.list().forEach(el -> {
            if (predicate.test(el)) {
                result.add(el);
            }
        });
        return result;
    }

    /**
     *
     * @param xmlElement
     * @return
     */
    public static XmlBase findXmlRoot(XmlElement xmlElement) {
        XmlBase root = null;
        XmlElement el = xmlElement;
        while (true) {
            if (el instanceof XmlBase) {
                root = (XmlBase) el;
                break;
            }
            if (el.getParent() == null) {
                break;
            }
            el = el.getParent();
        }
        return root;
    }

    /**
     * Just invokes the {@link #check() ) method.
     */
    @Override
    public void commitUpdates() {
        commitUpdates(true);
    }

    public void commitUpdates(boolean throwException) {

        if (!throwException) {
            return;
        }
        
        XmlErrors errors = check();

        if (errors.isEmpty()) {
            return;
        }
        errors.getErrorList().forEach(er -> {
            if (!er.isWarning()) {
                RuntimeException ex = er.getException();
                if (ex != null) {
                    throw ex;
                }
            }
        });

    }

    public XmlErrors check(XmlElement element) {
        XmlErrors errors = new XmlErrors();
        if (element.getParent() != null) {
            errors = element.getParent().getChilds().checkElement(element);
        }
        return errors;
    }

    public XmlErrors check() {
        return getChilds().check();
    }

    /**
     * Returns a list of elements. Each subsequent element is a child of the
     * previous element. The first element is the one that has no parent.
     *
     * @param leaf the element that ends the result of the method.
     * @return a list of elements.
     */
    public static List<XmlElement> getParentChainList(XmlElement leaf) {   
        List<XmlElement> list = new ArrayList<>();
        ParentVisitor v = new ParentVisitor();
        v.visit(leaf, el -> {
            list.add(0,el);
        });
        return list;
    } 
/*    public static List<XmlElement> getParentChainList_old(XmlElement leaf) {
        List<XmlElement> list = new ArrayList<>();

        XmlElement el = leaf;
        list.add(el);

        while (true) {
            if (el instanceof XmlBase) {
                break;
            }
            if (el.getParent() == null) {
                break;
            }
            el = el.getParent();
            list.add(0, el);
        }
        return list;

    }
*/
    /**
     * Returns {code null}.
     *
     * The class does not perform any operations with {@code DOM Tree }.
     *
     * @return null
     */
    @Override
    public Element getElement() {
        return null;
    }

    /**
     * The method overrides the method of the base class and does nothing.
     */
    @Override
    public void createDOMElement() {
    }
}//class XmlBase
