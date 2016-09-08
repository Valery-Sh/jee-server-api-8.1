package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.io.InputStream;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlErrors.InvalidClassNameException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlChildsTest {

    public XmlChildsTest() {
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
     * Test of checkElement method, of class XmlChilds. 
     */
    @Test
    public void testCheckElement() {
        
    }

    /**
     * Test of findElementClass method, of class XmlChilds. 
     */
    @Test
    public void testFindElementClass() {
        
    }
    
    /**
     * Test of add method, of class XmlChilds. If the child is already
     * in a child list then the method does nothing. The property
     * {@code parent} of the added child element is set to the to the value
     * of the object that calls the method.
     */
    @Test
    public void testAddChild() {
        System.out.println("add");
        XmlBase root = new XmlBase("books");
        
        //
        // add must return the root
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        //book.addChild(new XmlDefaultTextElement("ch01"));
        
        XmlCompoundElement bookParent = root.getChilds().add(book);
        assertTrue(root == bookParent);
        
        
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement chapter01Parent = book.getChilds().add(chapter01);

        //
        // add must return the book as a parent element.
        //
        assertTrue(book == chapter01Parent);

        //
        // chapter01 must have a parent set to book
        //
        assertTrue(book == chapter01.getParent());
    }
    /**
     * Test of add method, of class XmlChilds
     */
    @Test(expected = InvalidClassNameException.class)
    public void testAdd_supported_with_root_other_class() {
        System.out.println("add()");
        XmlBase root = new XmlBase("shop");
        root.getTagMap()
                .put("books", XmlCompoundElementImpl.class.getName())
                .put("books/pen", XmlCompoundElementImpl.class.getName())
                .put("books/book", XmlDefaultTextElement.class.getName());
        

        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        root.addChild(books);                
        
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        
        //
        // Try add  the 'book' element of type XmlDefaultTextElement class.
        // Despite the fact that the required class name of the 'book' element
        // is defined in the 'root' this will cause an Exception. 
        // And this is because the 'book' tag is defined in the parent and
        // other upper parents are not considered.
        //
        XmlDefaultTextElement book = new XmlDefaultTextElement("book");
        books.addChild(book);
    }

    /**
     * Test of add method, of class XmlChilds
     */
    @Test(expected = InvalidClassNameException.class)
    public void testAdd_supported_no_root_other_class() {
        System.out.println("add()");
        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        
        //
        // Try add  the 'book' element of type XmlDefaultTextElement class.
        // Despite the fact that the required class name of the 'book' element
        // is defined in the 'root' this will cause an Exception. 
        // And this is because the 'book' tag is defined in the parent and
        // other upper parents are not considered.
        //
        XmlDefaultTextElement book = new XmlDefaultTextElement("book");
        books.addChild(book);
    }
    /**
     * Test of add method, of class XmlChilds
     */
    @Test(expected = InvalidClassNameException.class)
    public void testAdd_supported_no_root_other_class_1() {
        System.out.println("add()");
        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        books.addChild(book);
        
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'chapter' element
        //
        books.getTagMap().put("book/chapter", XmlCompoundElementImpl.class.getName());        
        //
        // Try add  the 'chapter' element of type XmlDefaultTextElement class.
        // Despite the fact that the required class name of the 'chapter' element
        // is defined in the 'books' this will cause an Exception. 
        // 
        // And this is because:
        //  1. The 'chapter' tag is not defined in the immediate parent - 'book'
        //     The tagMap object of the 'book' element has a defaultClass 
        //     property set to null (default)
        //  2. The 'chapter' tag is defined in the next parent - 'books' with a 
        //     class name XmlCompoundElementImpl but we use the class 
        //     XmlDefaultTextElement.
        //
        XmlDefaultTextElement chapter = new XmlDefaultTextElement("chapter");
        book.addChild(chapter);
        
        
        
    }

    /**
     * Test of add method, of class XmlChilds
     */
    @Test //(expected = InvalidClassNameException.class)
    public void testAdd_supported_no_root_other_class_2() {
        System.out.println("add()");
        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        book.getTagMap().setDefaultClass(XmlCompoundElementImpl.class.getName());
        books.addChild(book);
        
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'chapter' element
        //
        books.getTagMap().put("book/chapter", XmlCompoundElementImpl.class.getName());        
        //
        // Try add  the 'chapter' element of type XmlDefaultTextElement class.
        // The 'add' method  doesn't trow an Exception.
        // 
        // And this is because:
        //     The 'chapter' tag is not defined in the immediate parent - 'book'
        //     The tagMap object of the 'book' element has a defaultClass 
        //     property set to XmlCompoundElementImpl and therefore allows 
        //     any class for child elements of the 'book'.
        //
        XmlDefaultTextElement chapter = new XmlDefaultTextElement("chapter");
        book.addChild(chapter);
    }
    /**
     * Test of add method, of class XmlChilds
     */
    @Test //(expected = InvalidClassNameException.class)
    public void testAdd_supported_no_root_other_class_3() {
        System.out.println("add()");
        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        XmlBase root = new XmlBase("shop");
        root.addChild(books);
        
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap of the 'books' element for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        books.addChild(book);
        
        //
        // Try add  the 'chapter' element of type XmlDefaultTextElement class.
        // The 'add' method  doesn't throw an Exception.
        // 
        // And this is because:
        //     The 'chapter' tag is not defined in the parent elements. 
        //     And the uppermost element 'books' is not added to the root of type
        //     XmlBase.
        //
        XmlDefaultTextElement chapter = new XmlDefaultTextElement("chapter");
        book.addChild(chapter);
    }
    /**
     * Test of add method, of class XmlCompoundElement which implements
     * XmlTextElement and it's text property is set to not null
     *
     */
    @Test(expected = IllegalStateException.class)
    public void testAddChild_text_parent() {
        System.out.println("addChild_text_parent()");

        XmlBase root = new XmlBase("books");

        //
        // Creates an element and set it's text property to not null value.
        //
        XmlCompoundTextElementImpl book = new XmlCompoundTextElementImpl("book");
        book.setText("test when text is not null");
        root.getChilds().add(book);
        //
        // Try to add a child.  
        // An exception of type IllegalStateException must be thrown
        //
        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("test-not-supported");
        book.getChilds().add(chapter01);

    }

    /**
     * Test of add method, of class XmlCompoundElement when try to add an
     * element which has a DOM element set to not null value and that DOM
     * element is already in a DOM Tree.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChild_element_in_DOM() {
        System.out.println("addChild__element_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();
        //
        // Create a DOM element with a tagName equalt to 'book'.
        // Append a created element toe the root DOM element.
        //
        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);
        //
        // Create an XmlElement with a tagName 'book'. Add it to the childs.
        // An exeption IllegalArgumentException must be thrown because
        // we try to add xml element with an existing DOM Element in a DOM Tree. 
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);
        root.getChilds().add(book);
    }

    /**
     * Test of remove method, of class XmlChilds.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();
        //
        // Create a DOM element with a tag name 'book' and then 
        // create  an XmlElement for it.
        // Add the created DOM element to the DOM Tree.
        // Add the XmlElement to the root 
        //
        Element bookDomElement = xmlDocument.createElement("book");
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);

        root.getChilds().add(book);
        root.getElement().appendChild(bookDomElement);
        //
        // root.getChilds cannot be empty
        //
        assertFalse(root.getChilds().isEmpty());
        //
        // Remove  'book' element
        //
        root.getChilds().remove(book);
        assertTrue(root.getChilds().isEmpty());
        //
        // Add 'book' again and don't add a DOM Element to DOM Tree
        // As a rusult root has zero DOM Elements and one XmlElement
        //
        root.getChilds().add(book);
        NodeList nl = root.getElement().getChildNodes();
        assertEquals(0, nl.getLength());
        assertEquals(1, root.getChilds().size());

        //
        // Remove 'book' 
        // As a rusult root has zero zero XmlElement objects
        //
        root.getChilds().remove(book);
        nl = root.getElement().getChildNodes();
        assertEquals(0, nl.getLength());

    }

    /**
     * Test of replace method, of class XmlChilds.
     */
    @Test
    public void testReplaceChild() {
        System.out.println("replaceChild");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();
        //
        // add must return the root
        //
        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);

        //
        // reolaceChild must return the book as a parent element.
        //
        XmlCompoundElementImpl chapter02 = new XmlCompoundElementImpl("chapter01");
        XmlCompoundElement resultBook = book.getChilds().replace(chapter01, chapter02);
        assertTrue(book == resultBook);
        //
        // book element now must not contsin  an element with tag name 'chapter01'
        //
        assertFalse(book.getChilds().contains(chapter01));
        //
        // book element now must contsin  an element with tag name 'chapter02'
        //
        assertTrue(book.getChilds().contains(chapter02));

    }

    /**
     * Test of replace method, of class XmlChilds. The second parameter
     * (newChild) must not be {@code null}. Otherwise {@link NullPointerException
     * } must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_null() {
        System.out.println("replaceChild_newChild_null");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book");
        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);

        //
        // replace must throw exception when newChild parameter is null.
        //
        book.getChilds().replace(chapter01, null);

    }

    /**
     * Test of replace method, of class XmlChilds. The second parameter
     * (newChild) must not be {@code null}. Otherwise {@link NullPointerException
     * } must be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplaceChild_newChild_in_DOM() {
        System.out.println("replaceChild_newChild_in_DOM");
        XmlDocument xmlDocument = new XmlDocument("books");
        XmlRoot root = xmlDocument.getRoot();

        Element bookDomElement = xmlDocument.createElement("book");
        root.getElement().appendChild(bookDomElement);

        XmlCompoundElementImpl book = new XmlCompoundElementImpl("book", bookDomElement);

        root.getChilds().add(book);

        XmlCompoundElementImpl chapter01 = new XmlCompoundElementImpl("chapter01");
        book.getChilds().add(chapter01);
        //
        // replace must throw exception when newChild is already in DOM Tree.
        //
        Element el = xmlDocument.createElement("chapter02");
        XmlCompoundElementImpl newChild = new XmlCompoundElementImpl("chapter02", el);
        book.getChilds().replace(chapter01, newChild);

    }

    /**
     * Test of {@code findElementsByPath } method, of class
     * XmlCompoundElement when try to replace an element with a newChild whose
     * tagName is not supported .
     */
    @Test
    public void testFindChildsByPath() {
        System.out.println("findChildsByPath");
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template.xml");

        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books = (XmlCompoundElement) root.getChilds().get(0);
        //
        // Find all child elements of the 'books' element
        // we know there are four elements 
        //
        List<XmlElement> result = books.getChilds().findChildsByPath("*");
        assertEquals(4, result.size() );
        //
        // Find all child elements of the 'books' element
        // with a tag name "book". There are three elements.
        //
        result = books.getChilds().findChildsByPath("book");
        assertEquals(3,result.size());

        //
        // Find all child elements of the 'books' element
        // with a tag name "pen". There are one element.
        //
        result = books.getChilds().findChildsByPath("pen");
        assertEquals(1, result.size());

        //
        // Find all child elements of the 'books/pen' element
        // regardless of the tag name. There are two elements.
        //
        result = books.getChilds().findChildsByPath("pen/*");
        assertEquals(2, result.size());

        //
        // Find all child elements of the 'books/pen element
        // with a tag name "ink-pen" tag name. There are one element.
        //
        result = books.getChilds().findChildsByPath("pen/ink-pen");
        assertEquals(1, result.size());

    }
    /**
     * Test of {@code findElementsByPath } method, of class
     * XmlCompoundElement when try to replace an element with a newChild whose
     * tagName is not supported .
     */
    @Test
    public void testFindChildsByPath_1() {
        System.out.println("findChildsByPath");
        //
        // Has tow tags with a name 'pen'
        //
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template_1.xml");

        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books = (XmlCompoundElement) root.getChilds().get(0);
        //
        // Find all child elements of the 'books' element
        // we know there are five elements 
        //
        List<XmlElement> result = books.getChilds().findChildsByPath("*");
        assertEquals(5,result.size());
        //
        // Find all child elements of the 'books' element
        // with a tag name "book". There are three elements.
        //
        result = books.getChilds().findChildsByPath("book");
        assertEquals(3,result.size());

        //
        // Find all child elements of the 'books' element
        // with a tag name "pen". There are two elements.
        //
        result = books.getChilds().findChildsByPath("pen");
        assertEquals(2, result.size());

        //
        // Find all child elements of the 'books/pen' element
        // regardless of the tag name. There are four elements.
        //
        result = books.getChilds().findChildsByPath("pen/*");
        assertEquals(4, result.size());

        //
        // Find all child elements of the 'books/pen element
        // with a tag name "ink-pen" tag name. There are two elements.
        //
        result = books.getChilds().findChildsByPath("pen/ink-pen");
        assertEquals(2, result.size());

    }

    /**
     * Test of {@code findElementsByPath } method, of class
     * XmlCompoundElement when try to replace an element with a newChild whose
     * tagName is not supported .
     */
    @Test
    public void testRelativePath() {
        System.out.println("relativePath(XmlElement)");
        //
        // Has tow tags with a name 'pen'
        //
        InputStream is = BaseUtil.getResourceAsStream("org/netbeans/modules/jeeserver/base/deployment/xml/resources/xml-shop-template_1.xml");

        XmlDocument xmlDocument = new XmlDocument(is);
        XmlRoot root = new XmlRoot(xmlDocument);
        XmlCompoundElement books = (XmlCompoundElement) root.getChilds().get(0);
        XmlCompoundElement book = (XmlCompoundElement) books.getChilds().get(0);        
        //
        // Find all child elements of the 'books' element
        // we know there are five elements 
        //
        String result = books.getChilds().relativePath(book);
        String expResult = "book";
        assertEquals(expResult,result);
        //
        // Find all child elements of the 'books' element
        // with a tag name "book". There are three elements.
        //

        //
        // Find all child elements of the 'books' element
        // with a tag name "pen". There are two elements.
        //

        //
        // Find all child elements of the 'books/pen' element
        // regardless of the tag name. There are four elements.
        //


        //
        // Find all child elements of the 'books/pen element
        // with a tag name "ink-pen" tag name. There are two elements.
        //

    }
    
    
    /**
     * Test of check method, of class XmlCompoundElement
     */
    @Test
    public void testCheck_not_supported_tag() {
        System.out.println("check()");
        XmlErrors errors;
        //
        // The root tagMap specifies that any element is supported.
        // Also it defines a default class with a name XmlDefaultElement
        //
        XmlBase root = new XmlBase("shop");
       
        XmlDefaultElement books = new XmlDefaultElement("books");


        //
        // We actually use XmlDefaultTextElement class for the 'book' element.
        // But the 'books' tagMap specifies the class XmlCompoundElementImpl
        // An error with a code == '210' wil appear.
        //
        XmlDefaultTextElement book = new XmlDefaultTextElement("book");
        books.addChild(book);
        //root.getTagMap().setDefaultClass(null);        
        root.addChild(books);
        
        errors = books.getChilds().check();
        //
        // A warning with (code 210)  
        //
        assertEquals(1, errors.size());
        assertEquals("100", errors.getErrorList().get(0).getErrorCode());

        //XmlErrors newerrors = books.getChilds().newcheck();
        //
        // A warning with (code 210)  
        //
        //assertEquals(1, newerrors.size());
        //assertEquals("100", newerrors.getErrorList().get(0).getErrorCode());
        
    }

    /**
     * Test of check method, of class XmlCompoundElement
     */
    @Test
    public void testCheck_not_supported_tag_with_root() {
        System.out.println("check()");
        XmlBase root = new XmlBase("shop");
        root.getTagMap()
                .put("books", XmlCompoundElementImpl.class.getName())
                .put("books/pen", XmlCompoundElementImpl.class.getName())
                .put("books/book", XmlDefaultTextElement.class.getName());
        

        XmlCompoundElementImpl books = new XmlCompoundElementImpl("books");
        XmlDefaultTextElement book = new XmlDefaultTextElement("book");
        books.addChild(book);
        //
        // We define XmlCompoundElementImpl class 
        // in the tagMap for the 'book' element
        //
        books.getTagMap().put("book", XmlCompoundElementImpl.class.getName());        
        //
        // Now add 'books' to the root. We do so because otherwise
        // the line books.addChild(book) will cause an Exception.
        //
        root.addChild(books);         
        XmlErrors errors = books.getChilds().check();
        //
        // An error with (code 210)  
        //
        assertEquals(1, errors.size());
        assertEquals("210", errors.getErrorList().get(0).getErrorCode());

    }
    /**
     * Test of check method, of class XmlChild
     */
/*    @Test
    public void testMerge() {
        System.out.println("merge");
        XmlBase root = new XmlBase("shop");

        XmlDefaultElement books = new XmlDefaultElement("books");
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);

        //
        // Now add 'books' to the root. We do so because otherwise
        // the line books.addChild(book) will cause an Exception.
        //
        root.addChild(books);         
        //
        // To Merge
        //
        XmlDefaultElement mbooks = new XmlDefaultElement("books");
        XmlDefaultElement mbook = new XmlDefaultElement("book");
        mbook.getAttributes().put("attr1", "attr1Value");
        mbooks.addChild(mbook);
        
        books.getChilds().merge(mbooks.getChilds());
        
        assertEquals("attr1Value",books.getChilds().get(0).getAttributes().get("attr1"));
        
        //
        // Now add 'books' to the root. We do so because otherwise
        // the line books.addChild(book) will cause an Exception.
        //
        
        
    }
*/
        /**
     * Test of check method, of class XmlChild
     */
  /*  @Test
    public void testMerge_predicate() {
        System.out.println("merge(XmlChilds, BiPredicate)");
        XmlBase root = new XmlBase("shop");

        XmlDefaultElement books = new XmlDefaultElement("books");
        XmlDefaultElement book = new XmlDefaultElement("book");
        books.addChild(book);

        //
        // Now add 'books' to the root. We do so because otherwise
        // the line books.addChild(book) will cause an Exception.
        //
        root.addChild(books);         
        //
        // To Merge
        //
        XmlDefaultElement mbooks = new XmlDefaultElement("books");
        XmlDefaultElement mbook = new XmlDefaultElement("book");
        mbook.getAttributes().put("attr1", "attr1Value");
        mbooks.addChild(mbook);
        

        assertNotEquals("attr1Value",books.getChilds().get(0).getAttributes().get("attr1"));        
        
        books.getChilds().merge(mbooks.getChilds(), (el1, el2) -> {
            
            boolean b =  false;
            if ( el1.equals(el2)) {
                b = true;
            }
            return b;
        } );
        
        assertEquals("attr1Value",books.getChilds().get(0).getAttributes().get("attr1"));
    }
*/
    public static class XmlCompoundElementImpl extends AbstractCompoundXmlElement {

        public XmlCompoundElementImpl(String tagName) {
            super(tagName);
        }

        public XmlCompoundElementImpl(String tagName, Element el) {
            super(tagName, el, null);
        }

    }//XmlCompoundElementInpl

    public static class XmlCompoundTextElementImpl extends XmlCompoundElementImpl implements XmlTextElement {

        private String text;

        public XmlCompoundTextElementImpl(String tagName) {
            super(tagName);
        }

        @Override
        public String getTextContent() {
            return text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

    }
}
