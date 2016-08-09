package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public class XmlChildElementFactoryTest {

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
//        assertEquals(expResult, result);
    }

    /**
     * Test of cloneXmlElementInstance method, of class XmlChildElementFactory.
     */
    @Test
    public void testCreateInstance() {
        System.out.println("createInstance");
        XmlDocument xmlDocument = new XmlDocument("shop");
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlDefaultElement books = new XmlDefaultElement("books");
        Map<String,String> tagMapping = new HashMap<>();
        tagMapping.put("book", XmlDefaultTextElement.class.getName());
        books.setTagMapping(tagMapping);
        
        root.addChild(books);
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);
        root.commitUpdates();
        XmlChildElementFactory factory = new XmlChildElementFactory(books);
        XmlElement factoryResult = factory.createXmlElement(book.getElement());
        
        Element domElement = null;
        XmlChildElementFactory instance = null;
        XmlElement expResult = null;
    }

    /**
     * Test of cloneXmlElementInstance method, of class XmlChildElementFactory.
     */
    @Test
    public void testCreateInstance_root_mapping() {
        System.out.println("createInstance_root_mapping");
        Map<String,String> rootMapping = new HashMap<>();
        rootMapping.put("books",XmlDefaultElement.class.getName());
        rootMapping.put("books/pen",XmlDefaultElement.class.getName());
        rootMapping.put("books/book",XmlDefaultTextElement.class.getName());
        
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");
        //InputStream is = BaseUtil.getResourceAsStream("resources/xml-shop-template.xml");
        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument); 
        root.setTagMapping(rootMapping);

        root.commitUpdates();
        
        int i = 0;
        
/*        
        XmlChildElementFactory factory = new XmlChildElementFactory(books);
        XmlElement factoryResult = factory.createXmlElement(book.getElement());
        
        Element domElement = null;
        XmlChildElementFactory instance = null;
        XmlElement expResult = null;
    
*/  
    }
}
