/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryPreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryRegistry;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryRegistry.DirectoryRegistryImpl;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import static org.netbeans.modules.jeeserver.base.embedded.project.SuiteCommonSupport.SUITES_ROOT;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteCommonSupport.WebApplicationRegistry;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteCommonSupport.WebAppsRegistry;
import static org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants.TEST_SUITES_ROOT;
import org.openide.util.Exceptions;

/**
 *
 * @author Valery
 */
public class SuiteCommonSupportTest {
    private static final Path ROOT_01 = Paths.get("root/dir/01");
    private static final Path CHILD_PATH01 = Paths.get("a/b/C");
    private static final Path CHILD_PATH02 = Paths.get("a/b/D");
    public static final String DEFAULT_PROPERTIES_NAMESPACE = "config/instance-properties";

    public static final String SERVER01_PATH = "f:\\TestDir\\Server01";
    public static final String SERVER02_PATH = "f:\\TestDir\\Server02";
    
    //TEST_SUITES_ROOT = 376b0c67-f76a-4b41-90b4-705536190a13
    public SuiteCommonSupportTest() {
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
        new DirectoryRegistryImpl(ROOT_01).remove();
        new DirectoryRegistryImpl(Paths.get(TEST_SUITES_ROOT)).remove();
        new DirectoryRegistryImpl(Paths.get(SUITES_ROOT)).remove();
    }
    /**
     * Test of TEMP_TEMP method, of class SuiteCommonSupport.
     */
    @Test
    public void testTEMP_TEMP() {
        //boolean exists = new DirectoryRegistryImpl(Paths.get(SUITES_ROOT)).nodeExists();
        
        Path p = Paths.get("c:/A/B");
        boolean b = Paths.get("c:/A/B").isAbsolute();
        Preferences root = AbstractPreferences.userRoot().node("123456_87");
        Preferences node = root.node("/");
        Preferences node1 = root.node("");
        
        Preferences node2 = root.node("/A");
        Preferences node3 = root.node("\\");
        Preferences node4 = root.node("A/B/C");
        try {
            b = node4.nodeExists("");
            
//        Preferences node4 = root.node("/A/");
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        
        String s = p.toString();
        int i = 0;
    }    
    
    /**
     * Test of getInstance method, of class SuiteCommonSupport.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        Path instancePath = null;
        SuiteCommonSupport expResult = null;
        SuiteCommonSupport result = SuiteCommonSupport.getInstance(instancePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findSuite method, of class SuiteCommonSupport.
     */
    @Test
    public void testFindSuite() {
        System.out.println("findSuite");
        Path instancePath = null;
        DirectoryRegistry suitesRoot = null;
        SuiteCommonSupport.SuiteRegistry expResult = null;
        SuiteCommonSupport.SuiteRegistry result = SuiteCommonSupport.findSuite(instancePath, suitesRoot);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of filterSuiteName method, of class SuiteCommonSupport.
     */
    @Test
    public void testFilterSuiteName() {
        System.out.println("filterSuiteName");
        String source = "";
        boolean expResult = false;
        boolean result = SuiteCommonSupport.filterSuiteName(source);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceProperties method, of class SuiteCommonSupport.
     */
    @Test
    public void testGetInstanceProperties() {
        System.out.println("getInstanceProperties");
        SuiteCommonSupport instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.getInstanceProperties();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSuiteProperties method, of class SuiteCommonSupport.
     */
    @Test
    public void testGetSuiteProperties() {
        System.out.println("getSuiteProperties");
        SuiteCommonSupport instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.getSuiteProperties();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createSuiteProperties method, of class SuiteCommonSupport.
     */
    @Test
    public void testCreateSuiteProperties() {
        System.out.println("createSuiteProperties");
        SuiteCommonSupport instance = null;
        InstancePreferences expResult = null;
        InstancePreferences result = instance.createSuiteProperties();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of suiteRegistry method, of class SuiteCommonSupport.
     */
    @Test
    public void testGetSuiteDirectoryRegistry() {
        System.out.println("getSuiteDirectoryRegistry");
        SuiteCommonSupport instance = null;
        SuiteCommonSupport.SuiteRegistry expResult = null;
        SuiteCommonSupport.SuiteRegistry result = instance.suiteRegistry();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of instanceRegistry method, of class SuiteCommonSupport.
     */
    @Test
    public void testGetInstanceDirectoryRegistry() {
        System.out.println("getInstanceDirectoryRegistry");
        SuiteCommonSupport instance = null;
        SuiteCommonSupport.InstanceRegistry expResult = null;
        SuiteCommonSupport.InstanceRegistry result = instance.instanceRegistry();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of instanceRegistry method, of class SuiteCommonSupport.
     */
    @Test
    public void testWebAppsRegistry_childrens() {
        System.out.println("webappsRegistry_childrens()");
        
//        boolean b = new DirectoryRegistryImpl(Paths.get(SUITES_ROOT)).nodeExists();
        DirectoryPreferences dirPrefs = new DirectoryPreferences(Paths.get(TEST_SUITES_ROOT));        
        boolean b = dirPrefs.nodeExists(dirPrefs.userRoot(),SUITES_ROOT);
        DirectoryRegistry suitesRoot = new DirectoryRegistryImpl(Paths.get(TEST_SUITES_ROOT));
        b  = dirPrefs.nodeExists(dirPrefs.userRoot(),TEST_SUITES_ROOT);        
        String UID = "uid-12-34";
        
        //SuiteCommonSupport instance = new SuiteCommonSupport(Paths.get(SERVER01_PATH),UID);
        SuiteCommonSupport instance = SuiteCommonSupport.getInstance(suitesRoot, Paths.get(SERVER01_PATH),UID);

        WebAppsRegistry result = instance.instanceRegistry()
                .webappsRegistry();
        result.addWebapp(Paths.get("d:/apps/web01"));
        List<DirectoryRegistry> list = result.childrens();
        
        assertTrue(list.size() == 1);
        assertTrue(list.get(0) instanceof WebApplicationRegistry);
        assertEquals(list.get(0).directoryNamespace(), "D:/apps/web01");
        
        instance.webAppsRegistry().addWebapp(Paths.get("d:/apps01/web01"));
        //list = result.childrens();        
        int i = 0;
        //assertEquals(expResult, result);
    }
    
    
}
