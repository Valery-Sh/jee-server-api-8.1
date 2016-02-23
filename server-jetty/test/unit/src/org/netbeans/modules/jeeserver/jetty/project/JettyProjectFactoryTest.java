/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.jetty.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Valery
 */
public class JettyProjectFactoryTest {
    
    public JettyProjectFactoryTest() {
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
     * Test of isProject method, of class JettyProjectFactory.
     */
    @Test
    public void testIsProject() {
        System.out.println("isProject");
        Path p = Paths.get("a", "webapps");
        String s = p.toString();
                
        FileObject projectDirectory = FileUtil.toFileObject(new File("d:/Netbeans_810_Plugins/JettyNewServer01"));
        JettyProjectFactory instance = new JettyProjectFactory();
        boolean expResult = false;
        boolean result = instance.isProject(projectDirectory);
        assertEquals(expResult, result);
    }

    /**
     * Test of loadProject method, of class JettyProjectFactory.
     */
    @Test
    @Ignore
    public void testLoadProject() throws Exception {
        System.out.println("loadProject");
        FileObject dir = null;
        ProjectState state = null;
        JettyProjectFactory instance = new JettyProjectFactory();
        Project expResult = null;
        Project result = instance.loadProject(dir, state);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveProject method, of class JettyProjectFactory.
     */
    @Test
    @Ignore
    public void testSaveProject() throws Exception {
        System.out.println("saveProject");
        Project project = null;
        JettyProjectFactory instance = new JettyProjectFactory();
        instance.saveProject(project);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
