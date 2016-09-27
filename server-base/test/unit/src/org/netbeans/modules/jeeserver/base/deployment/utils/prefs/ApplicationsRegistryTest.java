/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
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
public class ApplicationsRegistryTest {
    
    public ApplicationsRegistryTest() {
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
     * Test of applicationsNodeName method, of class ApplicationsRegistry.
     */
    @Test
    public void testApplicationsNodeName() {
        System.out.println("applicationsNodeName");
        ApplicationsRegistry instance = null;
        String expResult = "";
        String result = instance.applicationsNodeName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of applicationsRoot method, of class ApplicationsRegistry.
     */
    @Test
    public void testApplicationsRoot() {
        System.out.println("applicationsRoot");
        ApplicationsRegistry instance = null;
        Preferences expResult = null;
        Preferences result = instance.applicationsRoot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of size method, of class ApplicationsRegistry.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        ApplicationsRegistry instance = null;
        int expResult = 0;
        int result = instance.size();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEmpty method, of class ApplicationsRegistry.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        ApplicationsRegistry instance = null;
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAppPropertiesList method, of class ApplicationsRegistry.
     */
    @Test
    public void testGetAppPropertiesList() {
        System.out.println("getAppPropertiesList");
        ApplicationsRegistry instance = null;
        List<InstancePreferences> expResult = null;
        List<InstancePreferences> result = instance.getAppPropertiesList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addApplication method, of class ApplicationsRegistry.
     */
    @Test
    public void testAddApplication_Path() {
        System.out.println("addApplication");
        Path app = null;
        ApplicationsRegistry instance = null;
        String expResult = "";
        String result = instance.addApplication(app);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addApplication method, of class ApplicationsRegistry.
     */
    @Test
    public void testAddApplication_Path_Properties() {
        System.out.println("addApplication");
        Path app = null;
        Properties props = null;
        ApplicationsRegistry instance = null;
        String expResult = "";
        String result = instance.addApplication(app, props);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeApplication method, of class ApplicationsRegistry.
     */
    @Test
    public void testRemoveApplication_InstancePreferences() {
        System.out.println("removeApplication");
        InstancePreferences appProps = null;
        ApplicationsRegistry instance = null;
        instance.removeApplication(appProps);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeApplication method, of class ApplicationsRegistry.
     */
    @Test
    public void testRemoveApplication_Path() {
        System.out.println("removeApplication");
        Path app = null;
        ApplicationsRegistry instance = null;
        String expResult = "";
        String result = instance.removeApplication(app);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findProperties method, of class ApplicationsRegistry.
     */
    @Test
    public void testFindProperties_String() {
        System.out.println("findProperties");
        String appDir = "";
        ApplicationsRegistry instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.findProperties(appDir);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findProperties method, of class ApplicationsRegistry.
     */
    @Test
    public void testFindProperties_Path() {
        System.out.println("findProperties");
        Path appDir = null;
        ApplicationsRegistry instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.findProperties(appDir);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findProperties method, of class ApplicationsRegistry.
     */
    @Test
    public void testFindProperties_File() {
        System.out.println("findProperties");
        File appDir = null;
        ApplicationsRegistry instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.findProperties(appDir);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findProperties method, of class ApplicationsRegistry.
     */
    @Test
    public void testFindProperties_String_String() {
        System.out.println("findProperties");
        String key = "";
        String value = "";
        ApplicationsRegistry instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.findProperties(key, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAppNodeName method, of class ApplicationsRegistry.
     */
    @Test
    public void testFindAppNodeName() {
        System.out.println("findAppNodeName");
        Path app = null;
        ApplicationsRegistry instance = null;
        String expResult = "";
        String result = instance.findAppNodeName(app);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class ApplicationsRegistryImpl extends ApplicationsRegistry {

        public ApplicationsRegistryImpl() {
            super(null);
        }

        public String applicationsNodeName() {
            return "test-apps";
        }
    }
    
}
