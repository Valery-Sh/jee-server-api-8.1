package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlChilds;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependencies;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependency;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.DependencyArtifact;
import org.w3c.dom.Element;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomRoot;

/**
 *
 * @author Valery
 */
public class PomRootTest {

    public PomRootTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    PomDocument pomDocument01;
    PomDocument pomDocument02;
    PomDocument pomDocument;

    @Before
    public void setUp() {
        InputStream is = this.getClass().getResourceAsStream("resources/pom01.xml");
        pomDocument01 = new PomDocument(is);
        InputStream is02 = this.getClass().getResourceAsStream("resources/pom02.xml");
        pomDocument02 = new PomDocument(is02);

        pomDocument = new PomDocument();

    }

    @After
    public void tearDown() {
    }

    protected void savePom() {
        Path p = Paths.get("d:/0temp");
        pomDocument01.save(p, "pom01");
    }

    protected void savePom02() {
        Path p = Paths.get("d:/0temp");
        pomDocument02.save(p, "pom02");
    }
    
    Dependencies dependencies01;
    Dependency dependency01;
    DependencyArtifact groupId01;
    DependencyArtifact artifactId01;
    DependencyArtifact type01;
    
    protected PomRoot initPomRoot(PomDocument pd) {
        PomRoot pomRoot = pd.getXmlRoot();

        dependencies01 = new Dependencies();
        dependency01 = new Dependency();
        groupId01 = new DependencyArtifact("groupId");
        groupId01.setText("a.b.c");
        artifactId01 = new DependencyArtifact("artifactId");
        artifactId01.setText("a1.b1.c1");
        type01 = new DependencyArtifact("type");
        type01.setText("pom");

        pomRoot.addChild(dependencies01);
        dependencies01.addChild(dependency01);

        dependency01.addChild(groupId01);
        dependency01.addChild(artifactId01);
        dependency01.addChild(type01);
        return pomRoot;
    }
    
    /**
     * <ul>
     *    <li>
     *      Create pom document without {@literal dependencies } tag.
     *    </li>
     *    <li>
     *      add {@literal Dependencies } object
     *    </li>
     *    <li>
     *      add three {@literal Dependency } object to the {@literal Dependencies }
     *    </li>
     *    <li>
     *      apply the method {@literal commitUpdates } which updates the DOM.
     *    </li>
     * </ul>

     * @throws TransformerException 
     */
    @Test
    public void testCommitUpdates_new_pom_addChild() throws TransformerException {
        System.out.println("testCommitUpdates_new_pom");

        pomDocument = new PomDocument();
        PomRoot pomRoot = initPomRoot(pomDocument);
        
        pomRoot.commitUpdates();

        Element domDependencies = pomDocument.getDomDependencies();
        assertNotNull(domDependencies);        
        
        List<Element> domElements = pomDocument.getDomDependencyList();
        assertEquals(domElements.size(),1);        
        
        domElements = pomDocument.getDomDependencyChilds(domElements.get(0));
        assertEquals(domElements.size(),3);        
        assertTrue("groupId".equals(domElements.get(0).getTagName()));
        assertTrue("artifactId".equals(domElements.get(1).getTagName()));
        assertTrue("type".equals(domElements.get(2).getTagName()));

        assertTrue("a.b.c".equals(domElements.get(0).getTextContent()));
        assertTrue("a1.b1.c1".equals(domElements.get(1).getTextContent()));
        assertTrue("pom".equals(domElements.get(2).getTextContent()));
        

        //pomDocument.save(Paths.get("d:/0temp"), "pom04");
        
    }

    /**
     * <ul>
     *    <li>
     *      Create pom document without {@literal dependencies } tag.
     *    </li>
     *    <li>
     *      add {@literal Dependencies } object
     *    </li>
     *    <li>
     *      add three {@literal Dependency } object to the {@literal Dependencies }
     *    </li>
     *    <li>
     *      apply the method {@literal commitUpdates } which updates the DOM.
     *    </li>
     * </ul>
     * @throws TransformerException 
     */
    @Test
    public void testCommitUpdates_new_pom_deleteChild() throws TransformerException {
        System.out.println("testCommitUpdates_new_pom_deleteChild");

        pomDocument = new PomDocument();
        PomRoot pomRoot = initPomRoot(pomDocument);
        
        pomRoot.commitUpdates();
        String s = type01.getText();        
        List<Element> domElements = pomDocument.getDomDependencyList();
        
        domElements = pomDocument.getDomDependencyChilds(domElements.get(0));
        //PomElement toDelete = new DependencyArtifact("type");
        XmlElement toDelete = dependency01.getChilds().get(2);
        dependency01.removeChild(toDelete);
        
        pomRoot.commitUpdates();
        
        domElements = pomDocument.getDomDependencyList();
        domElements = pomDocument.getDomDependencyChilds(domElements.get(0));
        assertEquals(domElements.size(),2);        
        assertTrue("groupId".equals(domElements.get(0).getTagName()));
        assertTrue("artifactId".equals(domElements.get(1).getTagName()));
        //assertTrue("type".equals(domElements.get(2).getTagName()));
        
        pomDocument.save(Paths.get("d:/0temp"), "pom04");
    }
    

    /**
     * Test of getElement method, of class XmlRoot.
     */
    @Test
    public void testGetElement() {
        System.out.println("getElement");
        PomRoot instance = pomDocument01.getXmlRoot();
        Element expResult = pomDocument01.getDocument().getDocumentElement();
        Element result = instance.getElement();
        assertEquals(expResult, result);
    }

    /**
     * Test of createDOMElement method, of class XmlRoot.
     * The method is overwritten and must do nothing.
     */
    @Test
    public void testCreateElement() {
        System.out.println("createElement");
        PomRoot instance = pomDocument01.getXmlRoot();
        Element expResult = pomDocument01.getDocument().getDocumentElement();
        instance.createDOMElement();
        Element result = instance.getElement();
        //
        // the method must not change the element
        //
        assertTrue(expResult == result);
    }

    /**
     * Test of markDeleted method, of class PomRoot.
     * The method must do nothing as the root element
     * cannot be deleted.
     */
/*    @Test
    public void testMarkDeleted() {
        System.out.println("setDeleted");
        XmlRoot instance = pomDocument01.getXmlRoot();
        
        instance.markDeleted(true);
        assertFalse(instance.isMarkedDeleted()); // markDeleted doen't change the value
        
        instance.markDeleted(false);
        assertFalse(instance.isMarkedDeleted());

    }
*/
    /**
     * Test of isMarkedDeleted method, of class PomRoot.
     * 
     * The method should always return {@literal  false } as 
     * the root element cannot be deleted.
     */
/*    @Test
    public void testIsMarkedDeleted() {
        System.out.println("isDeleted");
        XmlRoot instance = pomDocument01.getXmlRoot();
        instance.markDeleted(true);
        assertFalse(instance.isMarkedDeleted());
        instance.markDeleted(false);
        assertFalse(instance.isMarkedDeleted());
    }
*/

    /**
     * Test of getDependencies method, of class XmlRoot.
     */
    @Test
    public void testGetDependencies_when_no_dependencies_tag() {
        System.out.println("getDependencies");
        
        pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        //
        // The DOM Tree doesn't contain dependencies tag still.
        //
        Dependencies deps = pomRoot.getDependencies();
        assertNull(deps);
    }
    /**
     * Test of getDependencies method, of class XmlRoot.
     * First add {@link Dependencies} element as a child og
     * a root element.
     */
    @Test
    public void testGetDependencies_with_dependencies() {
        System.out.println("getDependencies");
        
        pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        pomRoot.addChild(new Dependencies());
        //
        // The the child list now contains Dependencies element.
        //
        Dependencies deps = pomRoot.getDependencies();
        assertNotNull(deps);
    }

    /**
     * Test of isChildSupported method, of class XmlRoot.
     * The method {@link XmlRoot#isChildSupported(org.w3c.dom.Element) }
     * must return {@literal  true} if the parameter specified
     * corresponds to the {@literal dependencies ) tag. 
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        Element el = pomDocument.getDocument().createElement("dependencies");
        
        assertTrue(pomRoot.isChildSupported(el.getTagName())); 
    }

    /**
     * Test of getChildElements method, of class XmlRoot.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        //
        // No dependencies tag still
        //
        XmlChilds expResult = pomRoot.getChilds();
        assertTrue(expResult.isEmpty());
        //
        // Now add dependencies
        //
        pomRoot.addChild(new Dependencies());
        expResult = pomRoot.getChilds();
        assertTrue(expResult.size() == 1);
    }
    @Test
    public void testIsPomDocument() {
        System.out.println("isPomDocument");
        //
        // Create document from a pom.xml template resource file
        //
        pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        assertTrue(pomRoot.isPomDocument());
        
        //
        // Create in-memory not a pom document
        //
        //pomDocument = new PomDocument("books");
        //pomRoot = pomDocument.getXmlRoot();
        //assertFalse(pomRoot.isPomDocument());
        
    }
    
}
