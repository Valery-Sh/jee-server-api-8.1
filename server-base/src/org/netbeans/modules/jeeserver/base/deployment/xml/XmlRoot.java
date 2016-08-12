package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Valery Shyshkin
 */
public class XmlRoot extends XmlBase {//AbstractCompoundXmlElement {

    private final Document document;

    public XmlRoot(XmlDocument xmlDocument) {
        super(xmlDocument.getDocument().getDocumentElement().getTagName());
        this.document = xmlDocument.getDocument();
    }

    public Document getDocument() {
        return document;
    }


    /**
     * Return a DOM element which this object is a wrapper of.
     * 
     * @return the value of document.getDocumentElement() where the document 
     *   is a DOM Document
     */
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
     * Checks if the given dom element is supported by the api. 
     * For example the root may contain such element as 
     * {@literal build }. But for now that element is not 
     * supported.
     *
     * @return {@literal true } if the element is supported
     */
    @Override
    public boolean isChildSupported(String tagName) {
        return true;
    }

    /**
     * Adds the comment lines after the last child node of the target.
     * 
     * @param target the element where the comment is appended.
     * @param comment a string to be added
     */
    public static void addComment(XmlElement target, String comment) {
        if ( target.getElement() == null ) {
            return;
        }
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(target);
        if ( root == null || root.getDocument() == null) {
            return;
        }
        Document doc = root.getDocument();
        Comment c = doc.createComment(comment);
        Element el = target.getElement();
        el.appendChild(c);
        
    }
    /**
     * Inserts the specified comment before the given element.
     * 
     * @param target an element to insert a node before it.
     * 
     * @param comment a string to be inserted
     */
    public static void insertComment(XmlElement target, String comment) {
        if ( target.getElement() == null ) {
            return;
        }
        XmlRoot root = (XmlRoot) XmlBase.findXmlRoot(target);
        if ( root == null ) {
            return;
        }
        Document doc = root.getDocument();
        Comment c = doc.createComment(comment);
        Element el = target.getElement();
        Node parentNode = el.getParentNode();
        if ( parentNode == null ) {
            return;
        }
        
        parentNode.insertBefore(c, el);
        
    }
    
    

}//class XmlRoot
