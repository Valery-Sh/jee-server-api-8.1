package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * Serves as a root for {@code XmlElement Tree}. The class does not perform any
 * operations with a {@code DOM Tree} and works only with object of type {@link XmlElement
 * } and knows nothing about {@code DOM Document}.
 *
 * @author Valery Shyshkin
 */
public class XmlBase extends AbstractCompoundXmlElement {

    //private XmlTagMap tagMap;
    private XmlErrors checkErrors;

    /**
     * Create a new instance of the class by the given {@code tagName}
     *
     * @param tagName a tag name to create an instance
     */
    public XmlBase(String tagName) {
        super(tagName);
        getTagMap().setDefaultClass(XmlDefaultElement.class.getName());
        
    }
    private void init() {
        getTagMap().setDefaultClass(XmlDefaultElement.class.getName());
    }
    //public static XmlElement getElementByPath(X)
    /**
     * Creates and return a string of tag names separated by slash.
     * 
     * @param el
     * @param tagNames
     * @return
     */
    public String getRootPath(XmlElement el, String... tagNames) {
        List<XmlElement> parentChainList = XmlBase.getParentChainList(el);
        if (parentChainList.isEmpty()) {
            return null;
        }
        if (parentChainList.get(0) != this) {
            return null;
        }
        
        String rp = rootRelativePath(parentChainList);
        StringBuilder pathBuilder = new StringBuilder(rp);
        for ( String tag : tagNames ) {
            pathBuilder
                    .append("/")
                    .append(tag);
        }
        return pathBuilder.toString();
    }
    /**
     * The path parameter may take two forms. For example
     * <ul>
     *    <li>
     *       "p1/p2/p3". then all child elements with tag name "p3" which
     *       resides in the parent "p1/p2" will be searched.
     *    </li>
     *    <li>
     *    <li>
     *       "p1/p2/*". then all child elements which
     *       resides in the parent "p1/p2" will be searched regardless of tag names.
     *    </li>
     *    </li>
     * </ul>
     * 
     * @param from
     * @param path 
     * @return 
     */
    public static List<XmlElement> findChildsByPath(XmlCompoundElement from, final String path) {
        return XmlChilds.findChildsByPath(from, path);
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
    
    //@Override
    public void check() {
        
        checkErrors = new XmlErrors();

        List<XmlElement> list = getChilds().list();
        list.forEach(el -> {
            el.setParent(this);
            XmlBase.check(el);
            XmlBase.generateErrors(el);
        });
    }
    /**
     * Just invokes the {@link #check() ) method.
     */
    @Override
    public void commitUpdates() {
        check();
    }    
    
    public static void check(XmlElement element) {
        if ( element.getParent() == null ) {
            throw new NullPointerException(
               " XmlChilds.check: The xml element '" + element.getTagName() + "' doesn't have a parent element");
        }
        if ( ! (element instanceof XmlCompoundElement )) {
            return;
        }
        XmlCompoundElement compoundElement = (XmlCompoundElement) element;
        List<XmlElement> list = compoundElement.getChilds().list();
        if (compoundElement instanceof XmlTextElement) {
            if (list.isEmpty() && ((XmlTextElement) compoundElement).getText() != null) {
                return;
            }
        }

        list.forEach(el -> {
            el.setParent(compoundElement);
            XmlBase.check(el);
            XmlRoot.generateErrors(el);
        });

    }
    
    /**
     * Checks whether a valid tag name of the given object as well as the class
     * that was used to create an instance of type {@literal XmlElement}.
     *
     * To achieve the correct result, you must be sure that a given element can
     * be used to build a path from the root to this element. This means that
     * the chain of calls such as
     * <pre>
     *    toCheck().getParent().getParent() ...
     * </pre> will give the root of {@literal XmlDocument }. This is true for
     * example, when the method is called after the execution of the method 
     * {@link XmlBase#commitUpdates() }.
     *
     * <p>
     * The method is automatically called for each child when a method {@link XmlCompoundElement#commitUpdates()
     * }. All errors are accumulated in the list collection of the {@literal XmlBase
     * }
     * element. To access this collection you can call the method
     * {@link XmlBase#getCheckErrors() }.-
     *
     * @param toCheck the object to be checked
     * @return an object that describes possible errors found during checking.
     * If no error found then the method {@link XmlErrors#isEmpty() } returns {@literal true
     * }
     *
     * @see XmlTagMap
     * @see XmlErrors
     */
    public static XmlErrors generateErrors(XmlElement toCheck) {

        XmlErrors errors = new XmlErrors();

        List<XmlElement> list = getParentChainList(toCheck);
        
        XmlBase root = (XmlBase) list.get(0);
        if (root == null) {
            return errors;
        }     
        
        if (!(list.get(0) instanceof XmlBase)) {
            return errors;
        }
      
        if (root.getTagMap() == null) {
            return errors;
        }

        errors = root.getnerateErrors(list);
        
        if (root.getCheckErrors() == null) {
            root.setCommitErrors(new XmlErrors());
        }
        root.getCheckErrors().merge(errors);

        return errors;
    }
    /**
     * 
     * Checks  whether the given list of elements represents 
     * a valid chain of  parent elements.
     * The element with an index 0 must be a reference to this object.
     * The last element is a start element of the parent chain.  
     * The method builds a string relative path as specified by the method 
     * {@link #rootRelativePath(java.util.List) } and checks it against 
     * the property {@code tagMap}.
     * 
     * @param parentChainList the list to be checked
     * 
     * @return  the object of type XmlErrors.
     * 
     * @see #generateErrors(org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement) 
     */
    public XmlErrors getnerateErrors(List<XmlElement> parentChainList) {
        XmlErrors errors = new XmlErrors();
        if (getTagMap().isEmpty() || getTagMap().getDefaultClass() != null ) {
            return errors;
        }
        String rp = rootRelativePath(parentChainList);
        String clazz = getTagMap().get(rp);
        
        XmlElement elem = parentChainList.get(parentChainList.size() - 1);
        String msg = "Error. Element class=" 
                + elem.getClass().getName()
                + ". Root relative path='" + rp + "'";
        
        if (clazz == null) {
            msg += ". Invalid tag name '"
                    + elem.getTagName() + "'";
            XmlErrors.XmlError error = new XmlErrors.XmlError(msg,
                    new XmlErrors.InvalidTagNameException(msg), parentChainList);
            errors.addError(error);
        } else if ( ! clazz.equals(elem.getClass().getName())) {
            msg += ". Invalid class name. Must be " 
                    + clazz; 
            XmlErrors.XmlError error = new XmlErrors.XmlError(msg,
                    new XmlErrors.InvalidClassNameException(msg), parentChainList);                    
            errors.addError(error);
        }
        return errors;
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

        XmlElement el = leaf;
        list.add(el);

        while (true) {
            String s = el.getTagName();

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

    /**
     * Checks if the given dom element is supported by the api. For example the
     * root may contain such element as {@literal build }. But for now that
     * element is not supported.
     * <p>
     * The class implements this method to always return {@code true}.
     *
     * @return {@literal true } if the element is supported
     */
/*    @Override
    public boolean isChildSupported(String tagName) {
        return true;
    }
*/

    /**
     * Returns an object representing the error that appeared during the {@code commitUpdates
     * } process.
     *
     * @return the object that represents errors
     */
    public XmlErrors getCheckErrors() {
        return checkErrors;
    }

    /**
     *
     * @param commitErrors the object to be set.
     */
    public void setCommitErrors(XmlErrors commitErrors) {
        this.checkErrors = commitErrors;
    }


    /**
     * Create path relative to the root as a string.
     *
     * @param parentChainList a list of {@literal  XmlElement}
     * @return a concatenation of tag names separated by a slash. The first
     * symbol cannot be slash.
     */
    public static String rootRelativePath(List<XmlElement> parentChainList) {
        StringBuilder pathBuilder = new StringBuilder();
        String slash = "/";
        for (int i = 1; i < parentChainList.size(); i++) {
            if (i == parentChainList.size() - 1) {
                slash = "";
            }

            pathBuilder
                    .append(parentChainList.get(i).getTagName())
                    .append(slash);
        }
        return pathBuilder.toString();
    }
    
}//class XmlBase
