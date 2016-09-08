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

/**
 *
 * @author Valery
 */
public class XmlChildElementFactoryTest {
    
    private XmlRoot root;
    XmlTagMap rootMapping;
    
    public XmlChildElementFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        rootMapping = new XmlTagMap();
        rootMapping.put("books",XmlDefaultElement.class.getName())
            .put("books/pen",XmlDefaultElement.class.getName())
            .put("books/book",XmlDefaultTextElement.class.getName());
        
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        XmlDocument xmlDocument = new XmlDocument(is);
        root = new XmlRoot(xmlDocument); 
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createXmlElement method, of class XmlChildElementFactory.
     */
    @Test
    public void testCreateXmlElement() {
        System.out.println("createXmlElement");
        Element domElement = null;
        XmlChildElementFactory instance = null;
        XmlElement expResult = null;
//        XmlElement result = instance.createXmlElement(domElement);
//        assertEquals(expResult, null);
    }

    /**
     * Test of createInstance method, of class XmlChildElementFactory.
     */
    @Test
    public void testCreateInstance() {
        System.out.println("createInstance");
        XmlDocument xmlDocument = new XmlDocument("shop");
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlDefaultElement books = new XmlDefaultElement("books");
        XmlTagMap tagMapping = new XmlTagMap();
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books.setTagMap(tagMapping);
        
//        root.addChild(books);
//        XmlDefaultElement book = new XmlDefaultElement("book");
//        books.addChild(book);
    }

    /**
     * Test of  method createInstamce of class XmlChildElementFactory.
     * Use tagMapping
     */
    @Test
    public void testCreateInstance_root_mapping_all_supopoted() {
        System.out.println("createInstance_root_mapping_all_supopoted");
        root.setTagMap(rootMapping);
        root.getTagMap().setDefaultClass(XmlDefaultElement.class.getName());
        
        //
        // Now we create an element of type XmlDefaultElement
        // with a tag name 'book'
        //
        XmlDefaultTextElement book = new XmlDefaultTextElement("book");
        XmlDefaultElement books = (XmlDefaultElement) root.getChilds().get(0); // this is a 'books' element
        books.addChild(book);
        
        //
        // do commitUpdates() just to appenf a new book element to the DOM Tree
        //
        root.commitUpdates();

        XmlChildElementFactory factory = new XmlChildElementFactory(books);
        XmlElement factoryResult = factory.createXmlElement(book.getElement());
        
        assertNotNull(factoryResult);
        //
        //tagMamming maps an element with a tag name 'book' to a class
        // XmlDefaultTextElement
        // 
        assertTrue(factoryResult instanceof XmlDefaultTextElement);
        
        List<XmlElement> list = root.findElementsByPath("books/pen/ink-pen");
        assertEquals(1,list.size());
        assertTrue(list.get(0) instanceof XmlDefaultElement);
    
        XmlElement otherElement = new XmlDefaultTextElement("boook");
        books.addChild(otherElement);
        list = root.findElementsByPath("books/boook");
        assertTrue(list.get(0) instanceof XmlDefaultTextElement);
    }
    
    /**
     * Test of  method getClassName of class XmlChildElementFactory.
     */
    @Test
    public void testGetClassName() {
        System.out.println("getClassName");
        root.setTagMap(rootMapping);
        //
        // We set the default class to 'null'. Then for any tag name that 
        // cannot be found an error wil appear
        //
        rootMapping.setDefaultClass(null);
        XmlCompoundElement books = (XmlCompoundElement) root.findFirstElementByPath("books");        
        
        XmlChildElementFactory factory = new XmlChildElementFactory(books);        
        

        XmlDefaultElement pen = (XmlDefaultElement) root.findFirstElementByPath("books/pen");
        Element penDomElement = pen.getElement();
        String className = factory.getClassName(penDomElement);
        assertEquals(XmlDefaultElement.class.getName(),className);

        XmlElement book = root.findFirstElementByPath("books/book");        
        Element bookDomElement = book.getElement();
        
        className = factory.getClassName(bookDomElement);
        assertEquals(XmlDefaultTextElement.class.getName(),className);
        
        //
        // Modify the tagMap of the element 'books'.
        // It will contain an item with a key='pen'.
        // Then if we try to add a child element with a tagName='pen' to
        // the 'books' element then the class name of the child to be added
        // must be XmlDefaultTextElement.class.getName()
        //
        XmlTagMap booksMap = books.getTagMap();
        booksMap.put("pen", XmlDefaultTextElement.class.getName() );
        
        //
        // Create a new element with a tag name equals to 'pen'.
        // Add it to the 'books'
        //
        XmlDefaultTextElement pen01 = new XmlDefaultTextElement("pen");
        books.addChild(pen01);
        //
        // do commitUpdates() just to append a new 'pen' element to the DOM Tree
        //
        root.commitUpdates(false);
        
        //
        // The class name of the pen01 element will be get from the tagMap
        // of the 'books' element and not from the 'root'. Recap that 
        // the tagMap of the 'root' specifies a class XmlDefaultTextElement
        // But the nearest parent has priority over 'root'.
        //
        className = factory.getClassName(pen01.getElement());
        assertEquals(XmlDefaultTextElement.class.getName(),className);
    }
    /**
     * Test of  method createInstamce of class XmlChildElementFactory.
     * Use tagMapping
     */
    @Test
    public void testCreateInstance_root_mapping_not_all_supopoted() {
        System.out.println("createInstance_root_mapping_not_all_supopoted");
        root.setTagMap(rootMapping);
        rootMapping.setDefaultClass(null);
        //
        // do commitUpdates() just to appenf a new book element to the DOM Tree
        //
        //root.commitUpdates();
        List<XmlElement> list = root.findElementsByPath("books/pen/ink-pen");
        //assertEquals(0,list.size());
        
    }
    
}
