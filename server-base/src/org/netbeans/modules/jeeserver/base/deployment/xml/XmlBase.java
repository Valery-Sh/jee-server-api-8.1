package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * Serves as a root for {@code XmlElement Tree}. 
 * The class does not perform any operations with a {@code DOM Tree}
 * and works only with object of type {@link XmlElement } and knows nothing about 
 * {@code DOM Document}.  
 * 
 * @author Valery Shyshkin
 */
public class XmlBase extends AbstractCompoundXmlElement {

    private XmlTagMap xmlPaths;
    private XmlErrors commitErrors;
    
    /**
     * Create a new instance of the class by the given {@code tagName}
     * @param tagName a tag name to create an instance
     */
    public XmlBase(String tagName) {
        super(tagName);
    }
    /**
     * 
     * @param xmlElement
     * @return 
     */
    public static XmlBase findXmlRoot(XmlElement xmlElement) {
        XmlRoot root = null;
        XmlElement el = xmlElement;
        while (true) {
            if (el instanceof XmlRoot) {
                root = (XmlRoot) el;
                break;
            }
            if (el.getParent() == null) {
                break;
            }
            el = el.getParent();
        }
        return root;
    }

    public XmlTagMap getXmlPaths() {
        return xmlPaths;
    }

    public void setXmlPaths(XmlTagMap xmlPaths) {
        this.xmlPaths = xmlPaths;
    }
    /**
     * Checks whether a valid tag name of the given object as well as 
     * the class that was used to create an instance of type 
     * {@literal XmlElement}.
     * 
     * To achieve the correct result, you must be sure that a 
     * given element can be used to build a path from the root 
     * to this element. This means that the chain of calls such as
     * <pre>
     *    toCheck().getParent().getParent() ...
     * </pre>
     *  will give  the root of {@literal XmlDocument }. This is true
     * for example, when the method is called after the execution 
     * of the method {@link XmlRoot#commitUpdates() }.
     * 
     * <p>The method is automatically called for each child when 
     * a method {@link XmlCompoundElement#commitUpdates() }. All errors 
     * are accumulated in the list collection of the {@literal XmlRoot }
     * element. To access this collection you can call the method
     * {@link XmlRoot#getCommitErrors() }.- 
     * 
     * @param toCheck the object to be checked
     * @return an object that describes possible errors found during
     *      checking. If no error found then the method 
     * {@link XmlErrors#isEmpty() } returns {@literal true }
     * 
     * @see XmlTagMap 
     * @see XmlErrors
     */
    public static XmlErrors check(XmlElement toCheck) {
        
        XmlErrors errors = new XmlErrors();
        
        List<XmlElement> list = getParentChainList(toCheck);
        if ( ! ( list.get(0) instanceof XmlRoot)) {
            return errors;
        }
        XmlRoot root = (XmlRoot) list.get(0);
        if (root == null) {
            return errors;
        }
        if (root.getXmlPaths() == null) {
            return errors;
        }
        
        errors = root.getXmlPaths().check(list);
        if ( root.getCommitErrors() == null ) {
            root.setCommitErrors(new XmlErrors());
        }
        root.getCommitErrors().merge(errors);
        
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

            if (el instanceof XmlRoot) {
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
     * Returns null.
     * The class does not perform any operations with {@code DOM Tree }.
     * 
     * @return null
     */
    @Override
    public Element getElement() {
        return null;
    }

    /**
     * The method overrides the method of the base class 
     * and does nothing.
     */
    @Override
    public void createDOMElement() {
    }

    /**
     * Checks if the given tag name is supported by the api. 
     * For example the root may contain such element as 
     * {@literal build }. But for now that element is not 
     * supported.
     *
     * @return {@literal true } if the tagName is supported
     */
    @Override
    public boolean isChildSupported(String tagName) {
        return true;
    }

    /**
     * When working with a {@literal xml document } we can create, add, modify
     * or remove elements that are not actually {@literal  DOM Tree }
     * members. 
     * For each child element of type {@literal XmlElement } 
     * sets it's parent as this object and invokes child's 
     * {@literal  commitUpdates} method and then calls 
     * {@link  #check(org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement) }.  
     */
    @Override
    public void commitUpdates() {
        
        commitErrors = new XmlErrors();
        
        List<XmlElement> list = getChilds().list();
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
            XmlBase.check(el);
        });
    }
    /**
     * Returns an object representing the error that appeared
     * during the {@code commitUpdates } process.
     * 
     * @return the object that represents errors
     */
    public XmlErrors getCommitErrors() {
        return commitErrors;
    }
    /**
     * 
     * @param commitErrors the object to be set.
     */
    public void setCommitErrors(XmlErrors commitErrors) {
        this.commitErrors = commitErrors;
    }
}//class XmlBase
