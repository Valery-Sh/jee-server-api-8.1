package org.netbeans.modules.jeeserver.base.deployment.utils.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlTextElement;

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
     * Test of getChilds method, of class XmlCompoundElement. 1.
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

        List<XmlElement> result = bookXmlElement.getChilds();
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
     * Test of getChilds method, of class XmlCompoundElement.
     */
    @Test
    public void testGetChilds_with_add_child() {
        System.out.println("getChilds");
        XmlDocument pd = new XmlDocument("books");
        XmlRoot root = pd.getXmlRoot();
        Element bookElement = pd.getDocument().createElement("book");
        root.getElement().appendChild(bookElement);
        Element chapterEl01 = pd.getDocument().createElement("chapter");
        bookElement.appendChild(chapterEl01);
        chapterEl01.setTextContent("chapter number 1");

        Element chapterEl02 = pd.getDocument().createElement("chapter");
        bookElement.appendChild(chapterEl02);
        chapterEl01.setTextContent("chapter number 2");

        XmlCompoundElement bookPomElement = new XmlCompoundElementImpl(bookElement, root);

        List<XmlElement> result = bookPomElement.getChilds();
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
     * Test of deleteChild method, of class XmlCompoundElement.
     */
    @Test
    public void testDeleteChild() {
        System.out.println("deleteChild");
        XmlElement toDelete = null;
//        XmlCompoundElement instance = new XmlCompoundElementImpl();
        XmlElement expResult = null;
//        XmlElement result = instance.deleteChild(toDelete);
    }

    /**
     * Test of addChild method, of class XmlCompoundElement.
     * <ul>
     * <li>
     * The method {@link XmlElement#getElement() } must return {@literal null }
     * value or the DOM Tree should not contain the element mentioned.
     *
     * The element to be added methods {@link XmlElement#getElement() } and 
     * {@link XmlElement#getElement() }
     * of a child element to be added must return {@literal null } value the DOM
     * Tree should not contain the element mentioned. Otherwise an  {@link IllegalArgumentException }
     * is thrown.
     * </li>
     * <li>
     * If the child is already in a child list then the method does nothing.
     * </li>
     * <li>
     * The property {@literal parent} of the added child element is set to the
     * current object that calls the method.
     * </li>
     * </ul>
     */
    @Test
    public void testAddChild() {
        System.out.println("addChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // addChild must return the root
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        XmlCompoundElement bookParent = root.addChild(book);
        assertTrue(root == bookParent);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement chapter01Parent = book.addChild(chapter01);

        Element el = xmlDocument.getDocument().createElement("chapter02");
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter02", el, book);

        //
        // addChild must return the book as a parent element.
        //
        assertTrue(book == chapter01Parent);

        //
        // chapter01 must have a parent set to book
        //
        assertTrue(book == chapter01.getParent());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddChild_allready_in_DOM() {
        System.out.println("addChild_allready_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // addChild must return the book as a parent element
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.addChild(chapter01);
        //
        // Put everythings to DOM Tree
        //
        root.commitUpdates();
        //pomDocument.save(Paths.get("d:/0temp"), "books01");

        /**
         * We use a constructor with protected visibility to try add an element
         * which has already been put into DOM Tree. This will cause an
         * IllegalArgumentException
         */
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter02", chapter01.getElement(), book);
        book.addChild(chapter02);
    }

    /**
     * We'll try apply the method when its parameter is an element whose tagName
     * is not supported.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChild_not_supported() {
        System.out.println("addChild_not_supported");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();
        //
        // addChild must return the book as a parent element
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.addChild(book);
        //
        // The method isChildSupported is implemented by the class
        // XmlCompoundElementImpl and return false for the tagName 
        // that equals to 'not-supported'. addChild method must throw
        // an IllegalArgumentException
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("not-supported");
        book.addChild(chapter01);
    }

    /**
     * Test of replaceChild method, of interface XmlCompoundElement.
     */
    @Test
    public void testReplaceChild() {
        System.out.println("replaceChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.addChild(chapter01);

        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter02");
        //
        // Replace chapter01 element with chapter02
        //
        book.replaceChild(chapter01, chapter02);
        XmlCompoundElement chapter = (XmlCompoundElement) book.getChilds().get(0);
        List<XmlElement> list = book.getChilds();
        assertEquals(chapter.getTagName(), "chapter02");

        //root.commitUpdates();
        //pomDocument.save(Paths.get("d:/0temp"), "books01");
    }

    /**
     * Test of replaceChild method, of class XmlCompoundElement. We'll try apply
     * the method with the second parameter set to {@literal  null}. Must
     * generate {@link NullPointerException due to rule that
     * the second param cannot be null)
     */
    @Test(expected = NullPointerException.class)
    public void testReplaceChild_fail() {
        System.out.println("replaceChild_when_fail");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.addChild(chapter01);

        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter02");
        //
        // Must generate NullPointerExceptions (second param cannot be null)
        //
        book.replaceChild(chapter01, null);
    }

    /**
     * Test of replaceChild method, of class XmlCompoundElement. We'll try apply
     * the method with the second parameter set to element with a tagName which
     * is not supported. Must generate {@link IllegalArgumentException }
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_fail_not_suppotyed() {
        System.out.println("replaceChild_fail_not_suppotyed");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.addChild(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.addChild(chapter01);

        // The method isChildSupported is implemented in the class
        // XmlCompoundElementImpl and returns false for the tagName 
        // that equals to 'not-supported'. replaceChild method must throw
        // an IllegalArgumentException
        //
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("not-supported");
        book.replaceChild(chapter01, chapter02);

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
    /**
     * Test of addChild method, of class XmlCompoundElement which implements
     * {@link XmlTextElement} interface. We'll try apply
     * the method to the element which {@literal text} property is set to 
     * not {@literal null} value.
     * {@link IllegalStateException }.  
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_with_text_fail() {
        System.out.println("setText_fail");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getXmlRoot();

        XmlTextCompoundElementImpl book = new XmlTextCompoundElementImpl("book");
        root.addChild(book);
        book.setText("My Life");        
        
        //
        // addChild must throw IllegalStateExeptions. We can't add child
        // to the element whose text property is set to not-null value
        //
        XmlTextCompoundElementImpl chapter01 = new XmlTextCompoundElementImpl("chapter01");
        book.addChild(chapter01);

    }
    
    
    public class XmlCompoundElementImpl implements XmlCompoundElement {

        private List<XmlElement> childs;
        private String tagName;
        private Element element;
        private XmlCompoundElement parent;
        private boolean deleted;

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
            // We cannot use getChilds() to scan because one or more elements may be deleted. 
            //
            List<XmlElement> list = new ArrayList<>(getChilds());
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
        public List<XmlElement> getChilds() {
            if (childs == null) {
                childs = new ArrayList<>();
                List<Element> domList = getChildDomElements();
                domList.forEach(el -> {
                    XmlElement pel = null;
                    pel = new XmlCompoundElementImpl(el.getTagName(), el, this);
                    childs.add(pel);
                });
            }
            return childs;
        }

        protected List<Element> getChildDomElements() {

            List<Element> list = new ArrayList<>();
            if (getElement() != null) {
                NodeList nl = getElement().getChildNodes();
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        if ((nl.item(i) instanceof Element)) {
                            Element el = (Element) nl.item(i);
                            if (!isChildSupported(el.getTagName())) {
                                continue;
                            }
                            list.add(el);
                        }
                    }
                }
            }
            return list;
        }

        @Override
        public Map<String, String> getTagMapping() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setTagMapping(Map<String, String> tagMapping) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            List<XmlElement> list = getChilds();
            if (!list.isEmpty()) {
                throw new IllegalStateException(
                        "XmlTextCompoundElementImpl.setText: can't set text since the element has child elements");
            }
            this.text = text;
        }
    }

}
