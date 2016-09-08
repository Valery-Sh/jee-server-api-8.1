package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
     * Test of getChildElements method, of class XmlCompoundElement. 1.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();
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

    }

    /**
     * Test of getChildElements method, of class XmlCompoundElement.
     */
    @Test
    public void testGetChilds_with_add_child() {
        System.out.println("getChilds");
        XmlDocument pd = new XmlDocument("books");
        XmlRoot root = pd.getRoot();
        //
        // create <book> DOM Element and append it to the root
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
     * Test of setText method, of class XmlCompoundElement which implements
     * {@link XmlTextElement} interface. We'll try apply
     * the method to the element which has child elements. The method must call 
     * {@link IllegalStateException }.  
     */
    @Test(expected = IllegalStateException.class)
    public void testSetText_fail() {
        System.out.println("setText_fail");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();

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
        private XmlTagMap tagMap;

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
            tagMap = new XmlTagMap(); 
        }

        protected XmlCompoundElementImpl(Element element, XmlCompoundElement parent) {
            this.tagName = element.getTagName();
            this.element = element;
            this.parent = parent;
            tagMap = new XmlTagMap(); 
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

/*        @Override
        public void check() {
            if (getParent() == null) {
                throw new NullPointerException(
                        " The element '" + getTagName() + "' doesn't have a parent element");
            }
            //
            // We cannot use getChildElements() to scan because one or more elements may be deleted. 
            //
            List<XmlElement> list = getChilds().list();
            list.forEach(el -> {
                el.setParent(this);
                 XmlRoot.check(el);
            });

        }
  */      
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
            return tagMap;
        }
        
        @Override
        public XmlChilds getChilds() {
            if ( childs == null ) {
                childs = new XmlChilds(this);
            }
            return childs;
        }
        @Override
        public void setTagMap(XmlTagMap tagMapping) {
            this.tagMap = tagMapping;
        }

        @Override
        public XmlAttributes getAttributes() {
            return new XmlAttributes();
        }

        @Override
        public Element nullElement() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    } //class

    public class XmlTextCompoundElementImpl extends XmlCompoundElementImpl implements XmlTextElement {

        private String text;

        public XmlTextCompoundElementImpl(String tagName) {
            super(tagName);
        }

        @Override
        public String getTextContent() {
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
                .getResourceAsStream("resources/temp_books01_attr.xml"));
        XmlRoot root = xmlDocument.getRoot();
        XmlElement books = root.getChilds().get(0);
        XmlTagMap p;
        
        Element pen01 = xmlDocument.getDocument().getElementById("pen01");
        books.getAttributes();
    }
    /**
     * Test of findChilds method, of class XmlBase.
     */
    @Test
    public void testFindElementsByPath() {
        System.out.println("findElementsByPath");

        XmlBase root = new XmlBase("shop");
        
        XmlDefaultElement books = new XmlDefaultElement("books");
        root.addChild(books);
        XmlDefaultElement book01 = new XmlDefaultElement("book");
        books.addChild(book01);
        //
        // book02 is an root of XmlDefaultTextElement
        //
        XmlDefaultTextElement book02 = new XmlDefaultTextElement("book");
        books.addChild(book02);
        
        Predicate<XmlElement> predicate = (el) -> {return (el instanceof XmlDefaultTextElement);} ;
        //
        // Find child elements of the 'books' element
        //
        List<XmlElement> result = books.findElementsByPath("*", predicate);
        List<XmlElement> expResult = Arrays.asList(book02);
        assertEquals(expResult, result);        
    }
    /**
     * Test of getClone method, of class XmlCompoundElement
     */
    @Test
    public void testGetClone() {
        System.out.println("getClone");

        XmlCompoundElement books = new XmlCompoundElementImpl("books");
        XmlCompoundElement book = new XmlCompoundElementImpl("book");
        books.addChild(book);
        XmlElement clone = books.getClone();
        assertEquals(books.getClass(), clone.getClass());
        XmlChilds childs  = ((XmlCompoundElement)clone).getChilds();
        assertEquals(1, childs.size());
        
        

    }
}
