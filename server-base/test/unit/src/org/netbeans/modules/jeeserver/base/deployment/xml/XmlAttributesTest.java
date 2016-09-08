package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public class XmlAttributesTest {
    
    XmlDocument xmlDocument;
    XmlBase root;
    
    public XmlAttributesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        xmlDocument = new XmlDocument(getClass().getResourceAsStream("resources/test_attrs_jetty_web.xml"));
        root = xmlDocument.getRoot();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toMap method, of class XmlAttributes.
     */
    @Test
    public void testGetAttributes() {
        System.out.println("getAttributes");
        XmlAttributes instance = new XmlAttributes();
        //
        // Checks whether XmlAttributes is not null and is not empty
        //
        assertNotNull(instance.toMap());
        assertTrue(instance.isEmpty());
    }


    /**
     * Test of copyFrom method, of class XmlAttributes.
     */
    @Test
    public void testCopyFrom() {
        System.out.println("copyFrom");
         
        Element domElement = root.findFirstElementByPath("Set").getElement();
        
        XmlElement xmlElement = new XmlDefaultElement("Custom");
        
        XmlAttributes instance = new XmlAttributes();
        
        instance.copyFrom(domElement);
        assertEquals(2, instance.size());
        assertEquals("contextPath", instance.get("name"));
        assertEquals("warPath", instance.get("war"));
        
    }
    
    /**
     * Test of copyFrom method, of class XmlAttributes.
     * We use a concrete xml file as a resource 'resources/test_attrs_jetty_web.xml
     */
    @Test
    public void testCopyFrom_with_xmlDocument() {
        System.out.println("copyFrom");
        XmlElement xmlElement = root.findFirstElementByPath("Set");
        
        Element el = null;
        XmlAttributes instance = new XmlAttributes();
        instance.copyFrom(xmlElement.getElement());
        assertEquals(2, instance.size());
        assertEquals("contextPath", instance.get("name"));
        assertEquals("warPath", instance.get("war"));
        
        XmlAttributes attrs = xmlElement.getAttributes();
        attrs.put("myName", "myValue");
        
        assertEquals(3, attrs.size());
        root.commitUpdates();
        //xmlDocument.save(Paths.get("d:/0temp"), "attr01");              

    }

    /**
     * Test of copyTo method, of class XmlAttributes.
     */
    @Test
    public void testCopyTo() {
        System.out.println("copyTo");
        Element domElement = root.findFirstElementByPath("Set").getElement();
        
        XmlElement xmlElement = new XmlDefaultElement("Custom");
        
        XmlAttributes instance = new XmlAttributes();
        instance.put("myAttr", "myAttrValue");
        
        instance.copyTo(domElement);
        assertEquals(3, domElement.getAttributes().getLength());
        assertEquals("myAttrValue", domElement.getAttribute("myAttr"));
    }
    
}
