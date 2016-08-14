package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;

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
     * Test of getTagMap method, of class XmlDefaultElement.
     */
    @Test
    public void testGetTagMapping() {
        System.out.println("getTagMapping");
        XmlDefaultElement instance = new XmlDefaultElement("books");
        XmlTagMap expResult = null;        
        XmlTagMap result = instance.getTagMap();        
        assertNotEquals(expResult, result);
        
        XmlTagMap map = new XmlTagMap();
        instance.setTagMap(map);
        expResult = map;
        result = instance.getTagMap();        
        assertEquals(expResult, result);        
    }

    /**
     * Test of setTagMapping method, of class XmlDefaultElement.
     */
    @Test
    public void testSetTagMapping() {
        System.out.println("setTagMapping");
        XmlDefaultElement instance = new XmlDefaultElement("books");
        XmlTagMap expResult = new XmlTagMap();
        instance.setTagMap(expResult);        
        XmlTagMap result = instance.getTagMap();        
        assertEquals(expResult, result);
    }


    @Test
    public void testCheck() {
        System.out.println("check");
        //XmlDocument xmlDocument = new XmlDocument("shop");
        XmlBase root = new XmlBase("shop");
        root.getTagMap().put("books", XmlDefaultElement.class.getName());
        root.getTagMap().put("books/book", XmlDefaultTextElement.class.getName());
        root.getTagMap().setDefaultClass(null);          
        
        String tagName = "books";
        XmlDefaultElement books = new XmlDefaultElement(tagName);
        
        //
        // Create an element with a tag name 'boooooook'. 
        // The tag name 'boooooook' is not supported
        // It is an error.
        //
        XmlDefaultElement errorElement = new XmlDefaultElement("boooooook");
        books.addChild(errorElement);
        
        root.addChild(books);        
        
        
        root.check();
        XmlErrors errors = root.getCheckErrors();                
        assertEquals(1, errors.size());
        
        root.getTagMap().setDefaultClass(XmlDefaultElement.class.getName());         
        
        root.check();
        
        errors = root.getCheckErrors();
        assertEquals(0, errors.size());
        //
        // Set defaultClass to null
        //
        root.getTagMap().setDefaultClass(null);
        //
        // Create an element with a tag name 'book'. 
        // The tag name 'book' is supported but we create invalid class. Must be 
        // XmlDefaultTextElement. It is an error.
        //
        XmlCompoundElement book = new XmlDefaultElement("book");
   //
        // Create an element with a tag name 'chapter'. 
        // The tag name 'chapter' is not supported.
        // It is an error.
        //        
        XmlDefaultTextElement chapter = new XmlDefaultTextElement("chapter");
        book.addChild(chapter);
        books.addChild(book);
        
        root.check();
        errors = root.getCheckErrors();
        assertEquals(3, errors.size());        
    }    
    
    
    /**
     * Test of commitUpdates method, of class XmlDefaultElement.
     * We are testing this method due to the fact that the class 
     * implements an interface  with the name {@link XmlTextElement}.
     */
    @Test
    public void testCommitUpdates() {
        System.out.println("commitUpdates");
        XmlDocument xmlDocument = new XmlDocument("shop");
        
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);        
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);
        String text = "My Favorite Book";
        book.setText(text);
        //
        // As we have applied the method setText we expect that
        // setTextContext is performed on the DOM Element
        //
        book.commitUpdates();
        Element el = book.getElement();
        assertEquals(text, el.getTextContent());
        
        //root.commitUpdates();
        //xmlDocument.save(Paths.get("d:/0temp"), "shop01");        
        
        
    }

    /**
     * Test of getChildElements method, of class XmlDefaultElement.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        XmlTagMap tagMapping = new XmlTagMap();
        
        InputStream is = this.getClass().getResourceAsStream("/org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        //XmlDocument xmlDocument = new XmlDocument("shop");
        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books0 = (XmlCompoundElement) root.getChilds().get(0);
        
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books0.setTagMap(tagMapping);
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books.setTagMap(tagMapping);
        
        
        root.addChild(books);        
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);
        
 
        root.commitUpdates();
        xmlDocument.save(Paths.get("d:/0temp"), "shop01");        
        
        

    }

    /**
     * Test of getText method, of class XmlDefaultElement.
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
        
        String text = "My Favorite Book";
        book.setText(text);
        assertEquals(book.getText(),text);
    }

    /**
     * Test of setText method, of class XmlDefaultElement.
     * 1. 
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
        assertEquals(book.getText(),text);
        //
        // The book element doesn't have a DOM Element ( getElement() == null }
        // Thus we do commitUpdates in order the book got DOM Element 
        //
        book.commitUpdates();
        assertEquals(book.getElement().getTextContent(),text);
        //
        // Now the element named 'book' has a DOM Element ( getElement() != null ).
        // When the "text" property changes then "textContext" property
        // of rhe DOM Element should be modified too.
        //
        text = "Not a Favorite Book";
        book.setText(text);
        assertEquals(book.getElement().getTextContent(),text);
        //
        // When we set the 'text' property value to null then the
        // value of the 'textContent' property is set to an empty string
        //
        book.setText(null);
        assertEquals(book.getElement().getTextContent().length(), 0);
    }
    
}
