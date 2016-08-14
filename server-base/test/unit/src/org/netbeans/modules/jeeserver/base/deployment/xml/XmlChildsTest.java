package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
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
     * Test of add method, of class XmlCompoundElement. If the child is already
     * in a child list then the method does nothing. The property
     * {@literal parent} of the added child element is set to the to the value
     * of the object that calls the method.
     */
    @Test
    public void testAddChild() {
        System.out.println("add");
        XmlBase root = new XmlBase("books");
        //
        // add must return the root
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        XmlCompoundElement bookParent = root.getChilds().add(book);
        assertTrue(root == bookParent);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement chapter01Parent = book.getChilds().add(chapter01);

        //
        // add must return the book as a parent element.
        //
        assertTrue(book == chapter01Parent);

        //
        // chapter01 must have a parent set to book
        //
        assertTrue(book == chapter01.getParent());
    }

    /**
     * Test of {@literal add } method, of class XmlCompoundElement when try to
     * add an element with not supported tag name.
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_not_supported() {
        System.out.println("addChild_not_supported");

        XmlBase root = new XmlBase("books");
        //
        // Configure root tagMap with a single supported tag name
        // wich equals to 'book' and the property 'defaultClass' set to null. 
        // 

        root.getTagMap().put("book", XmlCompoundTextElementImpl.class.getName());
        root.getTagMap().setDefaultClass(null);
        //
        // Create and add an element with not supported tag name
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("boooook");
        root.getChilds().add(book);
        //
        // add must throw IllegalArgumentException since 
        // XmlCompoundElementImpl implements isChildSupported method
        // in a way that "test-not-supported" cannot be used.
        //
        //XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        //book.getChilds().add(chapter01);

    }

    /**
     * Test of add method, of class XmlCompoundElement which implements 
     * XmlTextElement and it's text property is set to not null
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_text_parent() {
        System.out.println("addChild_text_parent()");
        
        XmlBase root = new XmlBase("books");
        
        //
        // Creates an element and set it's text property to not null value.
        //
        XmlCompoundTextElementImpl book = new XmlCompoundTextElementImpl("book");
        book.setText("test when text is not null");
        root.getChilds().add(book);
        //
        // Try to add a child.  
        // An exception of type IllegalStateException must be thrown
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().add(chapter01);

    }

    /**
     * Test of add method, of class XmlCompoundElement when try to add an
     * element which has a DOM element set to not null value and that DOM
     * element is already in a DOM Tree.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChild_element_in_DOM() {
        System.out.println("addChild__element_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // Create a DOM element with a tagName equalt to 'book'.
        // Append a created element toe the root DOM element.
        //
        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);
        //
        // Create an XmlElement with a tagName 'book'. Add it to the childs.
        // An exeption IllegalArgumentException must be thrown because
        // we try to add xml element with an existing DOM Element in a DOM Tree. 
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);
        root.getChilds().add(book);
    }

    /**
     * Test of remove method, of class XmlChilds.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // Create a DOM element with a tag name 'book' and then 
        // create  an XmlElement for it.
        // Add the created DOM element to the DOM Tree.
        // Add the XmlElement to the root 
        //
        Element bookDomElement = xmlDocument.createElement("book");
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);

        root.getChilds().add(book);
        root.getElement().appendChild(bookDomElement);
        //
        // root.getChilds cannot be empty
        //
        assertFalse(root.getChilds().isEmpty());
        //
        // Remove  'book' element
        //
        root.getChilds().remove(book);
        assertTrue(root.getChilds().isEmpty());
        //
        // Add 'book' again and don't add a DOM Element to DOM Tree
        // As a rusult root has zero DOM Elements and one XmlElement
        //
        root.getChilds().add(book);
        NodeList nl = root.getElement().getChildNodes();
        assertEquals(0, nl.getLength());
        assertEquals(1, root.getChilds().size());

        //
        // Remove 'book' 
        // As a rusult root has zero zero XmlElement objects
        //
        root.getChilds().remove(book);
        nl = root.getElement().getChildNodes();
        assertEquals(0, nl.getLength());

    }

    /**
     * Test of replace method, of class XmlChilds.
     */
    @Test
    public void testReplaceChild() {
        System.out.println("replaceChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // add must return the root
        //

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);

        //
        // reolaceChild must return the book as a parent element.
        //
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement resultBook = book.getChilds().replace(chapter01, chapter02);
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
     * Test of replace method, of class XmlChilds. The second parameter
     * (newChild) must not be {@literal null}. Otherwise {@link NullPointerException
     * } must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_null() {
        System.out.println("replaceChild_newChild_null");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);

        //
        // replace must throw exception when newChild parameter is null.
        //
        book.getChilds().replace(chapter01, null);

    }

    /**
     * Test of replace method, of class XmlChilds. The second parameter
     * (newChild) must not be {@literal null}. Otherwise {@link NullPointerException
     * } must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_in_DOM() {
        System.out.println("replaceChild_newChild_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);

        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);
        //
        // replace must throw exception when newChild is already in DOM Tree.
        //
        Element el = xmlDocument.createElement("chapter02");
        XmlCompoundElementImpl newChild = new XmlCompoundElementImpl("chapter02", el);
        book.getChilds().replace(chapter01, newChild);

    }

    /**
     * Test of {@literal replace } method, of class XmlCompoundElement when try
     * to replace an element with a newChild whose tagName is not supported .
     */
    /*    @Test(expected = IllegalStateException.class)
    public void testRepalceChild_not_supported() {
        System.out.println("replaceChild_not_supported");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().add(chapter01);

        //
        // replace must throw IllegalArgumentException since 
        // XmlCompoundElementImpl implements isChildSupported method
        // in a way that "test-not-supported" cannot be used.
        //
        XmlCompoundElementImpl newChild = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().replace(chapter01, newChild);
        
    }
     */
    /**
     * Test of {@literal findChildsByPath } method, of class XmlCompoundElement
     * when try to replace an element with a newChild whose tagName is not
     * supported .
     */
    @Test
    public void testFindChildsByPath() {
        System.out.println("findChildsByPath");
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books = (XmlCompoundElement) root.getChilds().get(0);
        //
        // Find all child elements of the 'books' element
        // we know there are four elements 
        //
        List<XmlElement> result = XmlChilds.findChildsByPath(books, "*");
        assertEquals(result.size(), 4);
        //
        // Find all child elements of the 'books' element
        // with a tag name "book". There are three elements.
        //
        result = XmlChilds.findChildsByPath(books, "book");
        assertEquals(result.size(), 3);

        //
        // Find all child elements of the 'books' element
        // with a tag name "pen". There are one element.
        //
        result = XmlChilds.findChildsByPath(books, "pen");
        assertEquals(1, result.size());

        //
        // Find all child elements of the 'books/pen' element
        // regardless of the tag name. There are two elements.
        //
        result = XmlChilds.findChildsByPath(books, "pen/*");
        assertEquals(2, result.size());

        //
        // Find all child elements of the 'books/pen element
        // with a tag name "ink-pen" tag name. There are one element.
        //
        result = XmlChilds.findChildsByPath(books, "pen/ink-pen");
        assertEquals(1, result.size());

    }

    public static class XmlCompoundElementImpl extends AbstractCompoundXmlElement {

        public XmlCompoundElementImpl(String tagName) {
            super(tagName);
        }

        public XmlCompoundElementImpl(String tagName, Element el) {
            super(tagName, el, null);
        }

    }//XmlCompoundElementInpl

    public static class XmlCompoundTextElementImpl extends XmlCompoundElementImpl implements XmlTextElement {

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
