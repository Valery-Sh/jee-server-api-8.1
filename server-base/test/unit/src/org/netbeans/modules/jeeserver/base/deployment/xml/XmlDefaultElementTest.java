package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlDefaultElementTest {

    public XmlDefaultElementTest() {
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
     * Test of getTextContent method, of class XmlDefaultElement.
     */
    @Test
    public void testGetText() {
        System.out.println("getText");
        XmlDocument xmlDocument = new XmlDocument("shop");
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);

        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);
        
        books.addChild(book);

        String text = "My Favorite Book";
        book.setText(text);
        assertEquals(book.getTextContent(), text);
    }

    /**
     * Test of setText method, of class XmlDefaultElement. 1.
     */
    @Test
    public void testSetText() {
        System.out.println("setText");
        XmlDocument xmlDocument = new XmlDocument("shop");
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);

        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);

        String text = "My Favorite Book";
        book.setText(text);
        assertEquals(book.getTextContent(), text);
        //
        // The book element doesn't have a DOM Element ( getElement() == null }
        // Thus we do commitUpdates in order the book got DOM Element 
        //
        book.commitUpdates();
        assertEquals(book.getElement().getTextContent(), text);
        //
        // Now the element named 'book' has a DOM Element ( getElement() != null ).
        // When the "text" property changes then "textContext" property
        // of rhe DOM Element should be modified too.
        //
        text = "Not a Favorite Book";
        book.setText(text);
        assertEquals(book.getElement().getTextContent(), text);
        //
        // When we set the 'text' property value to null then the
        // value of the 'textContent' property is set to an empty string
        //
        book.setText(null);
        assertEquals(book.getElement().getTextContent().length(), 0);
    }

    /**
     * Test of setText method, of class XmlDefaultElement.
     * Must throw an exception when try to set a value to text property
     * and the element has child elements
     */
    @Test(expected = IllegalStateException.class)
    public void testSetText_1() {
        System.out.println("setText");
        XmlDefaultElement books = new XmlDefaultElement("books");
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);
        books.setText("must throw an exception");

    }
    
}
