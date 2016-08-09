/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.xml.pom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependencies;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Dependency;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Exclude;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.Exclusions;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomRoot;
import org.w3c.dom.Element;

/**
 *
 * @author Valery
 */
public class ExclusionsTest {
    
    public ExclusionsTest() {
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
     * Test of isChildSupported method, of class Exclusions.
     */
    @Test
    public void testIsChildSupported() {
        System.out.println("isChildSupported");
        PomDocument pomDocument = new PomDocument();
        Element el = pomDocument.getDocument().createElement("exclude");
        Exclusions exclusions = new Exclusions();
        assertTrue(exclusions.isChildSupported(el.getTagName())); 
    }

    /**
     * Test of getChildElements method, of class Exclusions.
     */
    @Test
    public void testGetChilds() {
        System.out.println("getChilds");
        PomDocument pomDocument = new PomDocument();
        PomRoot pomRoot = pomDocument.getXmlRoot();
        
        Dependencies dependencies = new Dependencies();
        
        pomRoot.addChild(dependencies);
        Dependency dependency = new Dependency();
        
        dependencies.addChild(dependency);

        
        Exclusions exclusions = new Exclusions();
        assertTrue(exclusions.getChilds().isEmpty()); 
        
        dependency.addChild(exclusions);
        Exclude exclude = new Exclude();
        
        exclusions.addChild(exclude);
        assertTrue(exclusions.getChilds().size() == 1); 
        assertTrue(exclusions.getChilds().get(0) instanceof Exclude); 
        
//        pomRoot.commitUpdates();
//        pomDocument.save(Paths.get("d:/0temp"), "exclusions01");
        
        
        
    }
    
}
