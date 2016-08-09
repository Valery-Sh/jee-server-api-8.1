package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlCompoundElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
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
        InputStream is = this.getClass().getResourceAsStream("resources/doc01.xml");
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
     * Test of commitUpdates method, of class XmlElement.
     * Must throw {@link NullPointerException }. Each element must have a parent
     * element to allow apply {@link XmlElement#commitUpdate } method
     */
   @Test(expected=NullPointerException.class)
    public void testCommitUpdates() {
        System.out.println("commitUpdates");
        XmlElement instance = new XmlElementImpl("book");
        instance.commitUpdates();
    }

    public class XmlElementImpl implements XmlElement {

        private String tagName;
        private Element element;
        private XmlCompoundElement parent;

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
    }
}
