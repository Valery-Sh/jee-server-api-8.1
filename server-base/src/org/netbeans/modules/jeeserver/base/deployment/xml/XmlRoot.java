/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Valery Shyshkin
 */
public class XmlRoot extends AbstractCompoundXmlElement {

    private final Document document;
    private XmlPaths xmlPaths;
    private XmlErrors commitErrors;

    public XmlRoot(XmlDocument xmlDocument) {
        super(xmlDocument.getDocument().getDocumentElement().getTagName(), null, null);
        this.document = xmlDocument.getDocument();
    }

    public static XmlRoot findXmlRoot(XmlElement xmlElement) {
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

    public XmlPaths getXmlPaths() {
        return xmlPaths;
    }

    public void setXmlPaths(XmlPaths xmlPaths) {
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
     * @see XmlPaths 
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

    @Override
    public Element getElement() {
        return document.getDocumentElement();
    }

    /**
     * The method overrides the method of the base class 
     * and does nothing.
     */
    @Override
    public void createDOMElement() {
    }

    /**
     * Checks if the given dom element is supported by the api. For example the
     * root may contain such element as {@literal build }. But for now that
     * element is not supported/
     *
     * @param el a dom element to be checked
     * @return {@literal true } if the element is supported
     */
    @Override
    public boolean isChildSupported(String tagName) {
        return true;
    }

    /**
     * For now the only element that the API recognizes is the
     * {@literal  dependencies} element. Thus a list to be returned contains
     * zero ore one element.
     *
     * @return a list of {@link Dependencies } elements.
     */
    /*    @Override
    public List<XmlElement> getChildElements() {

        if (childs == null) {
            childs = new ArrayList<>();
            List<Element> domList = getChildDomElements();
            domList.forEach(el -> {
                XmlElement pel = null;
                switch (el.getTagName()) {
                    default:
                        pel = new XmlDefaultElement(el, this);
                        break;
                }
                assert pel != null;
                childs.add(pel);
            });
        }
        return childs;
    }
     */
    /**
     * When working with a {@literal xml document } we can create, add, modify
     * or mark as deleted elements that are not actually {@literal  DOM Tree }
     * members. The method does the necessary job to create and modify
     * {@literal  DOM Tree} using a list of {@link XmlElement }.
     * For each child element of type {@literal XmlElement } 
     * sets it's parent as this object and invokes child's 
     * {@literal  commitUpdates} method.
     */
    @Override
    public void commitUpdates() {
        
        commitErrors = new XmlErrors();
        
        List<XmlElement> list = getChilds().list();
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
            XmlRoot.check(el);
        });
    }

    public XmlErrors getCommitErrors() {
        return commitErrors;
    }

    public void setCommitErrors(XmlErrors commitErrors) {
        this.commitErrors = commitErrors;
    }
    

}//class XmlRoot
