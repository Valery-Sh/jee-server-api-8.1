/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Valery
 */
public class AbstractXmlTextElementTest {
    
    public AbstractXmlTextElementTest() {
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
     * Test of getTextContent method, of class AbstractXmlTextElement.
     */
    @Test
    public void testGetText() {
        System.out.println("getText");
        String text = "test text";
        AbstractXmlTextElement instance = new AbstractXmlTextElementImpl("book");
        instance.setText(text);
        String expResult = text;
        String result = instance.getTextContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setText method, of class AbstractXmlTextElement.
     */
    @Test
    public void testSetText() {
        System.out.println("setText");
        String text = "test text";
        AbstractXmlTextElement instance = new AbstractXmlTextElementImpl("book");
        instance.setText(text);
        String expResult = text;
        String result = instance.getTextContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of commitUpdates method, of class AbstractXmlTextElement.
     * Checks whether a DOM element has the same textContent as the text property 
     * of the corresponding XmlTextElement
     */
    @Test
    public void testCommitUpdates() {
        System.out.println("commitUpdates");
        XmlDocument doc = new XmlDocument("shop");
        XmlBase root = doc.getRoot();
        
        String text = "test text";
        AbstractXmlTextElement instance = new AbstractXmlTextElementImpl("book");
        root.addChild(instance);
        
        instance.setText(text);
        String expResult = text;
        
        root.commitUpdates();
        
        String result = instance.getElement().getTextContent();
        
        assertEquals(expResult, result);
        
        //
        //  Checks when the text property has null value. Must be an empty string
        //
        instance.setText(null);
        root.commitUpdates();
        
        result = instance.getElement().getTextContent();
        expResult = "";
        assertEquals(expResult, result);
        
    }

    /**
     * Test of weakEquals method, of class AbstractXmlTextElement.
     */
    @Test
    public void testWeakEquals() {
        System.out.println("weakEquals");
        AbstractXmlTextElement instance = new AbstractXmlTextElementImpl("books");
        
        //
        // The same instance 
        //
        boolean expResult = true;
        Object other = instance;
        boolean result = instance.weakEquals(other);
        assertEquals(expResult, result);
        
        //
        // The text properties are not set to both elements 
        //
        other = new AbstractXmlTextElementImpl("books");
        result = instance.weakEquals(other);
        assertEquals(expResult, result);
        
        //
        // Explicit set not null the same text to both elements 
        //
        String text = "test text";
        instance.setText(text);
        ((AbstractXmlTextElement)other).setText(text);

        result = instance.weakEquals(other);
        assertEquals(expResult, result);     

        //
        // Explicit set not null different text to both elements 
        //
        instance.setText("another test text");
        result = instance.weakEquals(other);        
        assertNotEquals(expResult, result);     
        
        //
        // Set null value to text property of one element and not null value to another
        //
        instance.setText(null);
        result = instance.weakEquals(other);        
        assertNotEquals(expResult, result);     

        //
        // Set null value to text properties of both elements 
        //
        instance.setText(null);
        ((AbstractXmlTextElement)other).setText(null);
        result = instance.weakEquals(other);        
        expResult = true;
        assertEquals(expResult, result);     
    }

    /**
     * Test of getClone method, of class AbstractXmlTextElement.
     */
    @Test
    public void testGetClone() {
        System.out.println("getClone");
        AbstractXmlTextElement instance = new AbstractXmlTextElementImpl("books");
        //
        // Check if the clone is of the same class as the element and
        // has the same tagName
        //
        XmlElement clone = instance.getClone();
        assertEquals(AbstractXmlTextElementImpl.class, clone.getClass());
        assertEquals(instance.getTagName(), clone.getTagName());
        //
        // Check if the clone has the same value of the text property
        // as the element itself has
        //
        String text = "test text";
        instance = new AbstractXmlTextElementImpl("books");
        instance.setText(text);
        clone = instance.getClone();
        assertEquals(instance.getTextContent(), ((AbstractXmlTextElement)clone).getTextContent());
        
    }

    public class AbstractXmlTextElementImpl extends AbstractXmlTextElement {

        public AbstractXmlTextElementImpl(String tagName) {
            super(tagName, null, null);
        }
    }
    
}
