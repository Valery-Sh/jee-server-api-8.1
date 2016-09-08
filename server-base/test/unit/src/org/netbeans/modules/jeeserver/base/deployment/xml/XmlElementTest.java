package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;

/**
 *
 * @author Valery
 */
public class XmlElementTest {

    public XmlElementTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    PomDocument pomDocument;
    Document document;
    Element rootElem;

    @Before
    public void setUp() {
        InputStream is = this.getClass().getResourceAsStream("resources/temp_doc01.xml");
        pomDocument = new PomDocument(is);
        document = pomDocument.getDocument();
        NodeList nl = document.getElementsByTagName("parentTag01");
        rootElem = (Element) nl.item(0);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getTagName method, of class XmlElement.
     */
    @Test
    public void testGetTagName() {
        System.out.println("getTagName");
        XmlElement instance = new XmlElementImpl("parentTag01", null, null);
        String expResult = "parentTag01";
        String result = instance.getTagName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getParent method, of class XmlElement.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        XmlElement instance = new XmlElementImpl("parentTag01", rootElem, null);

        XmlElement result = instance.getParent();
        assertNull(result);

        instance = new XmlElementImpl("tag01");
        result = instance.getParent();
        assertNull(result);

    }

    /**
     * Test of getElement method, of class XmlElement.
     */
    @Test
    public void testGetElement() {
        System.out.println("getElement");
        XmlElement instance = new XmlElementImpl("dependency");
        Element expResult = null;
        Element result = instance.getElement();
        assertEquals(expResult, result);
    }

    /**
     * Test of commitUpdates method, of class XmlElement. Must throw {@link NullPointerException
     * }. Each element must have a parent element to allow apply {@link XmlElement#commitUpdate
     * } method
     */
    @Test(expected = NullPointerException.class)
    public void testCommitUpdates() {
        System.out.println("commitUpdates");
        XmlElement instance = new XmlElementImpl("book");
        instance.commitUpdates();

    }
    /**
     * Test of newInstance method, of class XmlElement.
     */
    @Test
    public void testNewInstance() {
        System.out.println("newInstance");
        XmlElement instance = new XmlElementImpl("book");
        

        Class result = instance.newInstance().getClass();
        Class expResult = XmlElementImpl.class;
        
        assertEquals(expResult,result);

    }
    /**
     * Test of getClone method, of class XmlElement.
     */
    @Test
    public void testgetClone() {
        System.out.println("getClone");
        XmlElement instance = new XmlElementImpl("book");
        instance.getAttributes().put("attr1", "attr1Value");
        
        XmlElement clone = instance.getClone();
        Class expResult = XmlElementImpl.class;
        Class result = clone.getClass();
        //
        // check tagName
        //
        assertEquals(expResult,result);
        assertEquals(instance.getTagName(),clone.getTagName());
        //
        // check attributes
        //
        assertEquals(instance.getAttributes().toMap(),clone.getAttributes().toMap());
    }

    public static class XmlElementImpl implements XmlElement {

        private String tagName;
        private Element element;
        private XmlCompoundElement parent;
        private XmlAttributes attributes = new XmlAttributes();

        public XmlElementImpl(String tagName) {
            this(tagName, null, null);
        }

        protected XmlElementImpl(String tagName, Element el, XmlCompoundElement parent) {
            this.tagName = tagName;
            this.element = el;
            this.parent = parent;
        }

        @Override
        public String getTagName() {
            return tagName;
        }

        @Override
        public XmlCompoundElement getParent() {
            return parent;
        }

        @Override
        public Element getElement() {
            return element;
        }

        /**
         * The method must be private. Don't apply in in code.
         *
         * @param parent sets the parent
         */
        @Override
        public void setParent(XmlCompoundElement parent) {
            if (this.parent != null || parent == null) {
                return;
            }
            this.parent = parent;
        }

        @Override
        public void commitUpdates() {
            if (getParent() == null) {
                throw new NullPointerException(
                        " The element '" + getTagName() + "' doesn't have a parent element");
            }

            if (getElement() == null) {
                Document doc = null;
                if (getParent().getElement() != null) {
                    doc = getParent().getElement().getOwnerDocument();
                }
                if (doc == null) {
                    return;
                }

                this.element = doc.createElement(getTagName());
            }
            if (getElement().getParentNode() == null) {
                getParent().getElement().appendChild(getElement());
            }

        }


        @Override
        public XmlAttributes getAttributes() {
            return attributes;
        }

        @Override
        public Element nullElement() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
