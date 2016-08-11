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
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlPathsTest {
    
    private XmlRoot root;
    private XmlDocument xmlDocument;    
    Map<String,String> rootMapping = new HashMap<>();    
    
    public XmlPathsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        rootMapping.put("books",XmlDefaultElement.class.getName());
        rootMapping.put("books/pen",XmlDefaultElement.class.getName());
        rootMapping.put("books/book",XmlDefaultTextElement.class.getName());
        
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        xmlDocument = new XmlDocument(is);
        root = new XmlRoot(xmlDocument); 
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of get method, of class XmlPaths.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String path = "";
        XmlPaths instance = new XmlPaths();
        String expResult = "";
        String result = instance.get(path);
        assertNotEquals(expResult, result);
    }



    /**
     * Test of check method, of class XmlPaths.
     */
    @Test
    public void testCheck() {
        System.out.println("check");
        
        XmlCompoundElement books = new XmlDefaultElement("books");
        root.addChild(books);
        
        XmlElement book = new XmlDefaultTextElement("book");
        books.addChild(book);
        
        
        List<XmlElement> parentChainList = XmlRoot.getParentChainList(book);

        XmlPaths instance = new XmlPaths(this.rootMapping);
        //XmlErrors expResult = null;
        XmlErrors result = instance.check(parentChainList);
        assertTrue(result.isEmpty());
        
        XmlElement book01 = new XmlDefaultElement("book01");
        books.addChild(book01);
        instance = new XmlPaths(rootMapping);
        //XmlErrors expResult = null;
        parentChainList = XmlRoot.getParentChainList(book01);
        result = instance.check(parentChainList);
        assertEquals(result.size(),1);
        
        XmlElement book02 = new XmlDefaultElement("book");
        books.addChild(book02);
        instance = new XmlPaths(rootMapping);
        parentChainList = XmlRoot.getParentChainList(book02);
        result = instance.check(parentChainList);
        assertEquals(result.size(),1);        
    }
    
}
