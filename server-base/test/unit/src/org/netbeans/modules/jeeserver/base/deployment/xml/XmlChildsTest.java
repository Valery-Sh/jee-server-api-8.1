/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChildsTest {

    public XmlChildsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addChild method, of class XmlCompoundElement.
     * If the child is already in a child list then the method does nothing.
     * The property {@literal parent} of the added child element is set to the
     * to the value of the object that calls the method.
     */
    @Test
    public void testAddChild() {
        System.out.println("addChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // addChild must return the root
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        XmlCompoundElement bookParent = root.getChilds().addChild(book);
        assertTrue(root == bookParent);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement chapter01Parent = book.getChilds().addChild(chapter01);

        //
        // addChild must return the book as a parent element.
        //
        assertTrue(book == chapter01Parent);

        //
        // chapter01 must have a parent set to book
        //
        assertTrue(book == chapter01.getParent());
    }
    /**
     * Test of {@literal addChild } method, of class XmlCompoundElement when
     * try to add an element with not supported tag name.
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_not_supported() {
        System.out.println("addChild_not_supported");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().addChild(book);
        //
        // addChild must throw IllegalArgumentException since 
        // XmlCompoundElementImpl implements isChildSupported method
        // in a way that "test-not-supported" cannot be used.
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().addChild(chapter01);
        
    }

    /**
     * Test of {@literal  addChild} method, of class {@literal  XmlCompoundElement} when
     * <ul>
     *    <li>
     *       The parent element is of type {@literal  XmlTextElement} and 
     *       it's text property is set to {@literal not null}
     *    </li>
     * </ul>
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_text_parent() {
        System.out.println("addChild_text_parent()");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundTextElementImpl book = new XmlCompoundTextElementImpl("book");
        book.setText("test when text is not null");
        root.getChilds().addChild(book);
        //
        // addChild must throw IllegalStateException since 
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().addChild(chapter01);
        
    }
    
    /**
     * Test of addChild method, of class XmlCompoundElement when
     * try to add an element which has a DOM element set to not null value
     * and that DOM element is already in a DOM Tree.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChild_element_in_DOM() {
        System.out.println("addChild__element_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        
        root.getChilds().addChild(book);
        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);
        book.setElement(bookDomElement);
        
        //
        // addChild must throw IllegalArgumentException because
        // the child to be added has a DOM element wich is already in a DOM Tree.
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        Element el = xmlDocument.createElement("chapter01");
        root.getElement().appendChild(el);
        chapter01.setElement(el);
        book.getChilds().addChild(chapter01);
        
    }
    
    /**
     * Test of deleteChild method, of class XmlChilds.
     */
    @Test
    public void testDeleteChild() {
        System.out.println("deleteChild");
          XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        
        root.getChilds().addChild(book);
        assertFalse(root.getChilds().isEmpty());        
        
        root.getChilds().deleteChild(book);
        assertTrue(root.getChilds().isEmpty());
        //
        // Add again and add a DOM Element to DOM Tree
        //
        root.getChilds().addChild(book);        
        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);
        NodeList nl = root.getElement().getChildNodes();
        assertEquals(1,nl.getLength());
        book.setElement(bookDomElement);
        root.getChilds().deleteChild(book);
        nl = root.getElement().getChildNodes();        
        assertEquals(0,nl.getLength());
        
    }

    /**
     * Test of replaceChild method, of class XmlChilds.
     */
    @Test
    public void testReplaceChild() {
        System.out.println("replaceChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // addChild must return the root
        //

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().addChild(chapter01);

        //
        // reolaceChild must return the book as a parent element.
        //
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement  resultBook = book.getChilds().replaceChild(chapter01, chapter02);
        assertTrue(book == resultBook);
        //
        // book element now must not contsin  an element with tag name 'chapter01'
        //
        assertFalse(book.getChilds().contains(chapter01));
        //
        // book element now must contsin  an element with tag name 'chapter02'
        //
        assertTrue(book.getChilds().contains(chapter02));
        
    }
    /**
     * Test of replaceChild method, of class XmlChilds.
     * The second parameter (newChild) must not be {@literal null}.
     * Otherwise {@link NullPointerException }  must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_null() {
        System.out.println("replaceChild_newChild_null");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().addChild(chapter01);

        //
        // replaceChild must throw exception when newChild parameter is null.
        //
        book.getChilds().replaceChild(chapter01,null);
        
    }

    /**
     * Test of replaceChild method, of class XmlChilds.
     * The second parameter (newChild) must not be {@literal null}.
     * Otherwise {@link NullPointerException }  must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_in_DOM() {
        System.out.println("replaceChild_newChild_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);
        book.setElement(bookDomElement);
        
        root.getChilds().addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().addChild(chapter01);
        //
        // replaceChild must throw exception when newChild is already in DOM Tree.
        //
        XmlCompoundElementImpl newChild = new XmlCompoundElementImpl("chapter02");
        Element el = xmlDocument.createElement("chapter02");
        newChild.setElement(el);
        book.getChilds().replaceChild(chapter01, newChild);
        
        
    }
    /**
     * Test of {@literal replaceChild } method, of class XmlCompoundElement when
     * try to replace an element with a newChild whose tagName is not supported .
     */
    @Test(expected = IllegalStateException.class)
    public void testRepalceChild_not_supported() {
        System.out.println("replaceChild_not_supported");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().addChild(chapter01);

        //
        // replaceChild must throw IllegalArgumentException since 
        // XmlCompoundElementImpl implements isChildSupported method
        // in a way that "test-not-supported" cannot be used.
        //
        XmlCompoundElementImpl newChild = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().replaceChild(chapter01, newChild);
        
    }
    
    
    public static class XmlCompoundElementImpl implements XmlCompoundElement {

        private String tagName;
        private XmlCompoundElement parent;
        private Element element;
        private XmlChilds childs;
        

        public XmlCompoundElementImpl(String tagName) {
            this.tagName = tagName;
        }
        @Override
        public XmlChilds getChilds() {
            if (childs == null) {
                childs = new XmlChilds(this);
            }
            return childs;
        }

        @Override
        public Map<String, String> getTagMapping() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setTagMapping(Map<String, String> tagMapping) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isChildSupported(String tagName) {
            if ( tagName.equals("test-not-supported")) {
                return false;
            }
            return true;
        }


        @Override
        public String getTagName() {
            return tagName;
        }

        @Override
        public void commitUpdates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public XmlCompoundElement getParent() {
            return parent;
        }

        @Override
        public void setParent(XmlCompoundElement parent) {
            this.parent = parent;
        }

        @Override
        public Element getElement() {
            return element;
        }
        
        /**
         * Implemented only for test purpose.
         */
        public void setElement(Element el) {
            this.element = el;
        }
    }//XmlCompoundElementInpl
    
    public static class XmlCompoundTextElementImpl extends  XmlCompoundElementImpl implements XmlTextElement {
        private String text;
        public XmlCompoundTextElementImpl(String tagName) {
            super(tagName);
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

    }
    
}
