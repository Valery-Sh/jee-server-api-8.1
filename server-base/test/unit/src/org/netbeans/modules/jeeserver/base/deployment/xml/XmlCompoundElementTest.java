package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlCompoundElementTest {

    public XmlCompoundElementTest() {
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
     * Test of isChildSupported method, of class XmlCompoundElement.
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        Element el = null;
        XmlCompoundElement instance = new XmlCompoundElementImpl("myTagName");
        boolean result = instance.isChildSupported("anyTagName");
        assertTrue(result);
        //
        // 
        //
        result = instance.isChildSupported("not-supported");
        assertFalse(result);

    }

    /**
     * Test of getChildElements method, of class XmlCompoundElement. 1.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        Element bookElement = xmlDocument.getDocument().createElement("book");
        root.getElement().appendChild(bookElement);

        Element chapterEl01 = xmlDocument.getDocument().createElement("chapter");
        bookElement.appendChild(chapterEl01);

        Element chapterEl02 = xmlDocument.getDocument().createElement("chapter");
        bookElement.appendChild(chapterEl02);

        XmlCompoundElement bookXmlElement = new XmlCompoundElementImpl(bookElement, root);

        List<XmlElement> result = bookXmlElement.getChilds().list();
        assertTrue(result.size() == 2);

        XmlElement ch01 = result.get(0);
        assertEquals("chapter", ch01.getTagName());
        assertNotNull(ch01.getElement());
        assertNotNull(ch01.getParent());

        XmlElement ch02 = result.get(1);
        assertEquals("chapter", ch02.getTagName());

        Element chapterEl03 = xmlDocument.getDocument().createElement("chapter");
        chapterEl01.appendChild(chapterEl03);

        /*        root.commitUpdates();
        String s = bookElement.getTextContent();        
        int i = 0;
        xmlDocument.save(Paths.get("d:/0temp"), "books03");        
         */
    }

    /**
     * Test of getChildElements method, of class XmlCompoundElement.
     */
    @Test
    public void testGetChilds_with_add_child() {
        System.out.println("getChilds");
        XmlDocument pd = new XmlDocument("books");
        XmlRoot root = pd.getXmlRoot();
        //
        // create <book> DOM Element and append it to thr root
        //
        Element bookElement = pd.getDocument().createElement("book");
        root.getElement().appendChild(bookElement);
        //
        // create <chapter> DOM Element and append it to thr bookElement
        //
        Element chapter01 = pd.getDocument().createElement("chapter");
        chapter01.setTextContent("chapter number 1");
        bookElement.appendChild(chapter01);
        
        //
        //
        // create another <chapter> DOM Element and append it to thr bookElement
        //

        Element chapter02 = pd.getDocument().createElement("chapter");
        chapter02.setTextContent("chapter number 2");
        bookElement.appendChild(chapter02);
        

        XmlCompoundElement bookPomElement = new XmlCompoundElementImpl(bookElement, root);

        List<XmlElement> result = bookPomElement.getChilds().list();
        assertTrue(bookPomElement.getChilds().size() == 2);        
        assertTrue(result.size() == 2);

        XmlElement ch01 = result.get(0);
        assertEquals("chapter", ch01.getTagName());
        assertNotNull(ch01.getElement());
        assertNotNull(ch01.getParent());

        XmlElement ch02 = result.get(1);
        assertEquals("chapter", ch02.getTagName());
    }

    /**
     * Test of beforeAddChild method, of class XmlCompoundElement.
     */
    @Test
    public void testBeforeAddChild() {
        System.out.println("beforeAddChild");
        XmlElement child = null;
        //PomCompoundElement instance = new XmlCompoundElementImpl();
        boolean expResult = false;
        //boolean result = instance.beforeAddChild(child);
        //assertEquals(expResult, result);
    }

    /**
     * Test of setText method, of class XmlCompoundElement which implements
     * {@link XmlTextElement} interface. We'll try apply
     * the method to the element which has child elements. The method must call 
     * {@link IllegalStateException }.  
     */
    @Test(expected = IllegalStateException.class)
    public void testSetText_fail() {
        System.out.println("setText_fail");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlTextCompoundElementImpl book = new XmlTextCompoundElementImpl("book");
        root.addChild(book);
        
        //
        // setText must throw IllegalStateExeptions. We can't set value
        // to the text property of the element which has child elements 
        //
        XmlTextCompoundElementImpl chapter01 = new XmlTextCompoundElementImpl("chapter01");
        book.addChild(chapter01);
        book.setText("My Life");        

    }
    
    
    public class XmlCompoundElementImpl implements XmlCompoundElement {

        private XmlChilds childs;
        private String tagName;
        private Element element;
        private XmlCompoundElement parent;

        public XmlCompoundElementImpl(String tagName) {
            this(tagName, null, null);
        }

        protected XmlCompoundElementImpl(String tagName, XmlCompoundElement parent) {
            this(tagName, null, parent);
        }

        protected XmlCompoundElementImpl(String tagName, Element element, XmlCompoundElement parent) {
            this.tagName = tagName;
            this.element = element;
            this.parent = parent;
        }

        protected XmlCompoundElementImpl(Element element, XmlCompoundElement parent) {
            this.tagName = element.getTagName();
            this.element = element;
            this.parent = parent;
        }

        @Override
        public boolean isChildSupported(String tagName) {
            if ("not-supported".equals(tagName)) {
                return false;
            }
            return true;
        }

        @Override
        public String getTagName() {
            return tagName;
        }

        @Override
        public void commitUpdates() {
            if (getParent() == null) {
                throw new NullPointerException(
                        " The element '" + getTagName() + "' doesn't have a parent element");
            }
            Element parentEl = getParent().getElement();
            if (getElement() == null) {
                createElement();
            }
            if (getElement().getParentNode() == null) {
                parentEl.appendChild(getElement());
            }
            //
            // We cannot use getChildElements() to scan because one or more elements may be deleted. 
            //
            List<XmlElement> list = getChilds().list();
            list.forEach(el -> {
                el.setParent(this);
                el.commitUpdates();
            });

        }

        @Override
        public XmlCompoundElement getParent() {
            return this.parent;
        }

        @Override
        public void setParent(XmlCompoundElement parent) {
            this.parent = parent;
        }

        @Override
        public Element getElement() {
            return this.element;
        }

        public void createElement() {
            Document doc = null;
            if (getElement() != null) {
                return;
            }
            if (getParent().getElement() != null) {
                doc = getParent().getElement().getOwnerDocument();
            }
            assert doc != null;

            this.element = doc.createElement(getTagName());

        }


        @Override
        public XmlTagMap getTagMap() {
            return null;
        }
        
        public XmlChilds getChilds() {
            if ( childs == null ) {
                childs = new XmlChilds(this);
            }
            return childs;
        }
        @Override
        public void setTagMap(XmlTagMap tagMapping) {
            
        }

    } //class

    public class XmlTextCompoundElementImpl extends XmlCompoundElementImpl implements XmlTextElement {

        private String text;

        public XmlTextCompoundElementImpl(String tagName) {
            super(tagName);
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            if ( ! getChilds().isEmpty()) {
                throw new IllegalStateException(
                        "XmlTextCompoundElementImpl.setText: can't set text since the element has child elements");
            }
            this.text = text;
        }
    }

    
    /**
     * Test of getChildElements method, of class XmlCompoundElement. 1.
     */
    @Test
    public void testTemp() {
        System.out.println("testTemp");
        XmlDocument xmlDocument = new XmlDocument(getClass()
                .getResourceAsStream("resources/books01_attr.xml"));
        XmlRoot root = xmlDocument.getXmlRoot();
        XmlElement books = root.getChilds().get(0);
        XmlTagMap p;
        
        Element pen01 = xmlDocument.getDocument().getElementById("pen01");
        books.getAttributes();
    }
    
}
