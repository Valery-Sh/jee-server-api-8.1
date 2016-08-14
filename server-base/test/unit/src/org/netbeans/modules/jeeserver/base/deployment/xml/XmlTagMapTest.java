package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.util.HashMap;
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
public class XmlTagMapTest {
    
    private XmlRoot root;
    private XmlDocument xmlDocument;    
    private Map<String,String> rootMapping =  new HashMap<>();
    
    public XmlTagMapTest() {
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
     * Test of get method, of class XmlTagMap.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String path = "";
        XmlTagMap instance = new XmlTagMap();
        String expResult = "";
        String result = instance.get(path);
        assertNotEquals(expResult, result);
    }
    
     // The test uses start settings as the method {@code setUp() ) has done.
     //  XmlRoot has {@code } 'defaultClass' set to {@code 'XmlDefaulr.class.getName() }.  
    
    
    /**
     * Test of isTagPathSupported method, of class XmlTagMap.
     * The test uses start settings as the method code setUp() has done.
     * The XmlTagInstance has the property defaultClass
     * set to null .
     */
    @Test
    public void testIsTagPathSupported() {
        System.out.println("isTagPathSupported");
        

        XmlTagMap instance = new XmlTagMap(rootMapping);
        //
        // The XmlTagMap contains an element with a key equals to 'books'.
        //
        String path = "books";
        boolean expResult = true;
        boolean result = instance.isTagPathSupported(path);
        assertEquals(expResult, result);

        //
        // The XmlTagMap contains an element with a key equals to 'books/pen'.
        //
        path = "books/pen";
        result = instance.isTagPathSupported(path);
        assertEquals(expResult, result);    

        //
        // The XmlTagMap doesn't contain an element with a key equals to 'books/pennnnnn'
        // and the tagMap's defaultClass property is set to null.
        //
        path = "books/pennnnnn";
        result = instance.isTagPathSupported(path);
        assertNotEquals(expResult, result);    
        
        
    }
    /**
     * Test of isTagPathSupported method, of class XmlTagMap.
     * The test uses start settings as the method code setUp() has done.
     * The XmlTagInstance has the property defaultClass
     * set to null .
     */
    @Test
    public void testIsTagPathSupported_defaultClass_not_null() {
        System.out.println("isTagPathSupported_defaultClass_not_null");
        

        XmlTagMap instance = new XmlTagMap(rootMapping);
        instance.setDefaultClass(XmlDefaultElement.class.getName());
        //
        // The XmlTagMap contains an element with a key equals to 'books'.
        //
        String path = "books";
        boolean expResult = true;
        boolean result = instance.isTagPathSupported(path);
        assertEquals(expResult, result);

        //
        // The XmlTagMap contains an element with a key equals to 'books/pen'.
        //
        path = "books/pen";
        result = instance.isTagPathSupported(path);
        assertEquals(expResult, result);    

        //
        // The XmlTagMap doesn't contain an element with a key equals to 'books/pennnnnn'
        // but the tagMap's defaultClass property is set not null value.
        //
        path = "books/pennnnnn";
        result = instance.isTagPathSupported(path);
        assertEquals(expResult, result);    
        
        
    }

    
}
