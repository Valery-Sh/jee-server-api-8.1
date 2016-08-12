package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> expResult = null;        
        Map<String, String> result = instance.getTagMap();        
        assertEquals(expResult, result);
        
        Map<String,String> map = new HashMap<>();
        instance.setTagMapping(map);
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
        Map<String, String> expResult = new HashMap<>();
        instance.setTagMapping(expResult);        
        Map<String, String> result = instance.getTagMap();        
        assertEquals(expResult, result);
    }

    /**
     * Test of isChildSupported method, of class XmlDefaultElement.
     * We expect that the method {@link XmlDefaultElement#isChildSupported(java.lang.String)
     * does following:
     * 
     * <ul>
     *    <li>
     *       If the tagMapping is null or is empty then:
     *       If the given tagName equals to "not-suppoted" then the 
     *       method returns {@literal false }. Otherwise it returns {@literal true }
     *    </li>
     *    <li>
     *      If the tagMapping is not null or is not empty then:
     *       Tries get the value by the given tagName from the tagMapping
     *       property. If we get {@literal null } value then the method returns
     *       {@literal false }. otherwise it returns {@literal true }
     *    </li>
     * </ul>
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        String tagName = "books";
        
        XmlDefaultElement instance = new XmlDefaultElement(tagName);
        //
        // expResult must be true because the property tagMapping is null
        // and tagName is not equals to "not-supported".
        //
        boolean expResult = true;
        boolean result = instance.isChildSupported("book");
        assertEquals(expResult, result);
        
        Map<String,String> map = new HashMap<>();
        instance.setTagMapping(map);
        //
        // expResult must be true because the property tagMapping is empty
        // and tagName is not equals to "not-supported".
        //
        expResult = true;
        result = instance.isChildSupported("book");
        assertEquals(expResult, result);

        //
        // expResult must be true because the property tagMapping contains
        // not null value for the key "book"
        //
        expResult = true;
        map.put("book", "");
        result = instance.isChildSupported("book");
        assertEquals(expResult, result);
        
        //
        // expResult must be false because the property tagMapping 
        // doesn't contain  not null value for the key "chapter"
        //
        expResult = false;
        result = instance.isChildSupported("chapter");
        assertEquals(expResult, result);

        //
        // expResult must be true because the property tagMapping 
        // is empty and "chapter" is not equals to "not-supported"
        //
        expResult = true;
        map.remove("book");
        result = instance.isChildSupported("chapter");
        assertEquals(expResult, result);
        //
        // expResult must be false because the property tagMapping 
        // is empty and the parameter is equals to "not-supported"
        //
        expResult = true;
        result = instance.isChildSupported("not-chapter");
        assertEquals(expResult, result);
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
        Map<String,String> tagMapping = new HashMap<>();
        
        InputStream is = this.getClass().getResourceAsStream("/org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        //XmlDocument xmlDocument = new XmlDocument("shop");
        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books0 = (XmlCompoundElement) root.getChilds().get(0);
        
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books0.setTagMapping(tagMapping);
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books.setTagMapping(tagMapping);
        
        
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
