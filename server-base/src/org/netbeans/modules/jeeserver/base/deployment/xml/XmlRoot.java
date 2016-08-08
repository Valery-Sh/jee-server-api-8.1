/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependencies;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Valery Shyshkin
 */
public class XmlRoot extends AbstractCompoundXmlElement {

    private final Document document;

    public XmlRoot(Document document) {
        super(document.getDocumentElement().getTagName(), null, null);
        this.document = document;
    }

    public XmlRoot(XmlDocument xmlDocument) {
        super(xmlDocument.getDocument().getDocumentElement().getTagName(), null, null);
        this.document = xmlDocument.getDocument();
    }

    public static XmlRoot findXmlRoot(XmlElement xmlElement) {
        XmlRoot root = null;
        XmlElement el = xmlElement;
        while (true) {
            if (el instanceof XmlRoot ) {
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

    @Override
    public Element getElement() {
        return document.getDocumentElement();
    }

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
    public List<XmlElement> getChilds() {

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
     */
    @Override
    public void commitUpdates() {
        //
        // Create a copy of child list because one or more elements of 
        // the original one may be deleted.
        //
        List<XmlElement> list = new ArrayList<>(getChilds());
        list.forEach(el -> {
            el.setParent(this);
            el.commitUpdates();
        });
    }

}//class XmlRoot
