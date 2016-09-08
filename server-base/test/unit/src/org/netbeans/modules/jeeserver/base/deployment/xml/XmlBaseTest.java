package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
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
public class XmlBaseTest {
    
    public XmlBaseTest() {
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
     * Test of getParentChainList method, of class XmlBase.
     */
/*    @Test
    public void testGetParentChainList_xmlElem_tagNames() {
        System.out.println("getParentChainList");
        
        XmlBase root = new XmlBase("shop");        
        String[] tagNames = new String[] {"tag1", "tag2"};
        
        XmlCompoundElement books = new XmlDefaultElement("books");
        root.addChild(books);
        
        XmlElement book = new XmlDefaultElement("book");
        books.addChild(book);
        
        String expResult = "books/tag1/tag2";
        String result = root.getParentChainList(books, tagNames);
        assertEquals(expResult, result);
        
        expResult = "books/book/tag1/tag2";
        result = root.getParentChainList(book, tagNames);
        assertEquals(expResult, result);
        
        
        //
        // When the first parameter is an object of type XmlBase
        //
        assertEquals("tag1/tag2", root.getParentChainList(root, tagNames));
        
    }
*/
    /**
     * Test of findElementsByPath method, of class XmlBase.
     */
    @Test
    public void testFindElementsByPath_XmlCompoundElement_String() {
        System.out.println("findElementsByPath_XmlCompoundElement_String");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        
        XmlCompoundElement from = books;
        
        String path = "book";
        
        List<XmlElement> expResult = Arrays.asList(book01,book02);
        
        List<XmlElement> result = from.findElementsByPath(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of findElementsByPath method, of class XmlBase.
     */
    @Test
    public void testFindElementsByPath_String() {
        System.out.println("findElementsByPath");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        
        XmlCompoundElement from = books;
        
        String path = "books/book";
        
        List<XmlElement> expResult = Arrays.asList(book01,book02);

        List<XmlElement> result = root.findElementsByPath(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of findFirstElementByPath method, of class XmlBase.
     */
    @Test
    public void testFindFirstElementByPath() {
        System.out.println("findFirstElementsByPath");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        
        String path = "books/book";
        
        XmlElement expResult = book01;

        XmlElement result = root.findFirstElementByPath(path);
        assertEquals(expResult, result);         
    }

    /**
     * Test of findElementsByPath method, of class XmlBase.
     */
    @Test
    public void testFindElementsByPath_String_Predicate() {
        System.out.println("findElementsByPath");
        XmlBase root = new XmlBase("shop");
        
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        //
        // book02 is an root of XmlDefaultTextElement
        //
        XmlDefaultTextElement book02 = new XmlDefaultTextElement("book");
        books.addChild(book02);

        Predicate<XmlElement> predicate = (el) -> {return true;} ;
        String path = "books/book";
        List<XmlElement> result = root.findElementsByPath(path, predicate);
        List<XmlElement> expResult = Arrays.asList(book01,book02);
        assertEquals(expResult, result);        
        
        predicate = (el) -> {return (el instanceof XmlDefaultTextElement);} ;
        result = root.findElementsByPath(path, predicate);
        expResult = Arrays.asList(book02);
        assertEquals(expResult, result);        
        
    }


    /**
     * Test of findXmlRoot method, of class XmlBase.
     */
    @Test
    public void testFindXmlRoot() {
        System.out.println("findXmlRoot(XmlElement)");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        
        
        XmlElement expResult = root;

        XmlElement result = XmlBase.findXmlRoot(book02);
        assertEquals(expResult, result);         
    }


    /**
     * Test of check method, of class XmlBase.
     * Test when no tagMap is specified.
     */
    @Test
    public void testCheck() {
        System.out.println("check");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        //
        // As we don't specify any tagMap no error can be found
        //
        XmlErrors errors = root.check();
        assertEquals(0, errors.size());
        
    }
    /**
     * Test of check method, of class XmlBase.
     * Test when tagMap is specified.
     */

    @Test
    public void testCheck_with_tagMap_no_defaultClass() {
        System.out.println("check_with_tagMap");
        XmlBase root = new XmlBase("shop");
        root.getTagMap().put("books", XmlDefaultElement.class.getName());
        root.getTagMap().put("books/book", XmlDefaultElement.class.getName());        
        //
        // We set 'defaultClass' property value to null. This means that only
        // tags specified by tagMap are supported.
        //
        root.getTagMap().setDefaultClass(null);
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("boooook");
        books.addChild(book02);
        
        root.addChild(books); 
        
        //
        // The error wil be found because a tag with a name "boooook"
        // is not supported
        //
        XmlErrors errors = root.check();
        assertEquals(1, errors.size());
        
    }
    /**
     * Test of check method, of class XmlBase.
     * Test when tagMap is specified and it's defaultClass property
     * is not null (it's not null by default for XmlBase instances)  .
     */
    @Test
    public void testCheck_with_tagMap_with_defaultClass() {
        System.out.println("check_with_tagMap");
        XmlBase root = new XmlBase("shop");
        root.getTagMap().put("books", XmlDefaultElement.class.getName());
        root.getTagMap().put("books/book", XmlDefaultElement.class.getName());        
        //
        // 'defaultClass' is not null.
        //
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("boooook");
        books.addChild(book02);
        
        root.addChild(books); 
        
        //
        // The error wil be found because a tag with a name "boooook"
        // is not supported
        //
        XmlErrors errors = root.check();
        assertEquals(0, errors.size());
        
    }
    /**
     * Test of check method, of class XmlBase.
     * Test when tagMap is specified.
     */
    @Test
    public void testCheck_with_tagMap_invalid_classname() {
        System.out.println("check_with_tagMap");
        XmlBase root = new XmlBase("shop");
        //
        // We set 'defaultClass' property value to null. This means that only
        // tags specified by tagMap are supported.
        //
        root.getTagMap().setDefaultClass(null);
        root.getTagMap().put("books", XmlDefaultElement.class.getName());
        //
        // We assigned XmlDefaultTextElement and not XmlDefaultElement
        //
        root.getTagMap().put("books/book", XmlDefaultTextElement.class.getName());        
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("boooook");
        books.addChild(book02);
        
        root.addChild(books); 
        
        //
        // Tow errors are found: 
        // 1. a tag with a name "boooook" is not supported
        // 2. an element 'book' must be of type XmlDefaultTextElement
        //    and not XmlDefaultElement 
        //
        XmlErrors errors = root.check();
        //root.commitUpdates();
        assertEquals(2, errors.size());
        
    }

    
    /**
     * Test of checkChilds method, of class XmlBase.
     * We model a situation where the method must throw NullPointerException.
     */
/*    @Test(expected = NullPointerException.class)
    public void testCheckChilds_exception() {
        System.out.println("checkChilds_exception");
        XmlBase root = new XmlBase("shop");
        XmlDefaultElement books = new XmlDefaultElement("books");
        
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("boooook");
        books.addChild(book02);
        //
        // Must throw NullPointerException because we did not add "books"
        // element to the root and therefore the books doesn't have a parent element
        //
        root.checkChilds(books);
        
    }
*/    
    /**
     * Test of checkChilds method, of class XmlBase.
     * We define a tagMap in the root and try to find errors in
     * the element specified by the method parameter.
     */
/*    @Test
    public void testCheckChilds() {
        System.out.println("checkChilds");
        XmlBase root = new XmlBase("shop");
         //
        // We set 'defaultClass' property value to null. This means that only
        // tags specified by tagMap are supported.
        //
        root.getTagMap().setDefaultClass(null);
        root.getTagMap().put("books", XmlDefaultElement.class.getName());
        //
        // We assigned XmlDefaultTextElement and not XmlDefaultElement
        //
        root.getTagMap().put("books/book", XmlDefaultTextElement.class.getName());        
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        XmlDefaultElement book02 = new XmlDefaultElement("boooook");
        books.addChild(book02);
        
        root.addChild(books); 

        root.checkChilds(books);
        //
        // Tow errors are found: 
        // 1. a tag with a name "boooook" is not supported
        // 2. an element 'book' must be of type XmlDefaultTextElement
        //    and not XmlDefaultElement 
        //
        XmlErrors errors = root.getCheckErrors();
        assertEquals(2, errors.size());
        
    }
*/

    /**
     * Test of getParentChainList method, of class XmlBase.
     */
    @Test
    public void testGetParentChainList() {
        System.out.println("getParentChainList");
        XmlElement leaf = null;
        List<XmlElement> expResult = null;
//        List<XmlElement> result = XmlBase.getParentChainList(leaf);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getElement method, of class XmlBase.
     */
    @Test
    public void testGetElement() {
        System.out.println("getElement");
        XmlBase instance = null;
        Element expResult = null;
//        Element result = root.getElement();
//        assertEquals(expResult, result);
    }

    /**
     * Test of createDOMElement method, of class XmlBase.
     */
    @Test
    public void testCreateDOMElement() {
        System.out.println("createDOMElement");
        XmlBase instance = null;
//        root.createDOMElement();
    }

    /**
     * Test of getCheckErrors method, of class XmlBase.
     */
    @Test
    public void testGetCheckErrors() {
        System.out.println("getCheckErrors");
        XmlBase instance = null;
        XmlErrors expResult = null;
//        XmlErrors result = root.getCheckErrors();
//        assertEquals(expResult, result);
    }

    /**
     * Test of setCheckErrors method, of class XmlBase.
     */
    @Test
    public void testSetCheckErrors() {
        System.out.println("setCheckErrors");
        XmlErrors checkErrors = null;
        XmlBase instance = null;
//        root.setCheckErrors(checkErrors);
    }

    /**
     * Test of toStringPath method, of class XmlBase.
     */
    @Test
    public void testRootRelativePath() {
        System.out.println("rootRelativePath");
        List<XmlElement> parentChainList = null;
        String expResult = "";
//        String result = XmlBase.toStringPath(parentChainList);
//        assertEquals(expResult, result);
    }
    
}
