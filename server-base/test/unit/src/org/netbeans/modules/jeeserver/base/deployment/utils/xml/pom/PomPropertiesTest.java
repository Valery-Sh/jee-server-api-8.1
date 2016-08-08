package org.netbeans.modules.jeeserver.base.deployment.utils.xml.pom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomProperties;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Property;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public class PomPropertiesTest {
    
    public PomPropertiesTest() {
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
     * Test of isChildSupported method, of class PomProperties.
     * The only {@literal property } DOM Element is supported as a child. 
     *
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        PomDocument pomDocument = new PomDocument();
        Element el = pomDocument.getDocument().createElement("property");
        PomProperties properties = new PomProperties();
        assertTrue(properties.isChildSupported(el.getTagName())); 
    }

    /**
     * Test of getChilds method, of class PomProperties.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        PomDocument pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        
        PomProperties properties = new PomProperties();
        assertTrue(properties.getChilds().isEmpty()); 
        
        pomRoot.addChild(properties);
        Property property = new Property();
        
        properties.addChild(property);
        assertTrue(properties.getChilds().size() == 1); 
        assertTrue(properties.getChilds().get(0) instanceof Property); 
    }
    
}
