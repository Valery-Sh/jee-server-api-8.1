/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.embedded.SuiteProjectsManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author Valery
 */
public class NbSuitePreferencesTest {

    private static final String SUIDE_UID = "fffb0fd9-da7b-478e-a427-9c7d2f8babcb";
    private static final String SUIDE_UID_NAMESPACE = "uid-fffb0fd9-da7b-478e-a427-9c7d2f8babcb";

    private static final String TEST_DIR = "c:\\TestFolder01/ChildFolder01";
    private static final String TEST_PROP_FILE = "server-instance";
    private static final String TEST_SUITE_UID_PREFIX = "uid-" + SUIDE_UID + "/";
//    private static String TEST_NAMESPACE = TEST_SUITE_UID_PREFIX + "c_/TestFolder01/ChildFolder01";     
    private static final String TEST_NAMESPACE = "c_/TestFolder01/ChildFolder01";

    public NbSuitePreferences create() {
        return create(TEST_DIR, SUIDE_UID);
    }

    public NbSuitePreferences create(String dir, String uid) {
        return NbSuitePreferences.newInstance(dir, uid);
    }

    public NbSuitePreferences create(String dir) {
        return NbSuitePreferences.newInstance(dir, SUIDE_UID);
    }

    public NbSuitePreferencesTest() {
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
    public void tearDown() throws BackingStoreException {
        //SuiteRegistry r = create();
        //r.rootNode().removeNode();
        
        //create().clearSuite();
    }

    /**
     * Test of getNamespace method, of class NbSuitePreferences.
     */
    @Test
    public void testGetNamespace_0args() {
        System.out.println("getNamespace()");
        //
        // TEST_DIR = "c:\\TestFolder01/ChildFolder01";
        // SUIDE_UID = "fffb0fd9-da7b-478e-a427-9c7d2f8babcb";
        // TEST_NAMESPACE = "c_/TestFolder01/ChildFolder01";
        NbSuitePreferences instance = NbSuitePreferences.newInstance(TEST_DIR, SUIDE_UID);
        
        String expResult = TEST_NAMESPACE;
        String result = instance.getNamespace();
        System.out.println("expResult = ---" + expResult);
        System.out.println("   result = ---" + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class NbSuitePreferences.
     */
    @Test
    public void testGetNamespace_String() {
        System.out.println("getNamespace(String)");
        String forDir = "c:\\a/b/c";
        NbSuitePreferences instance = create();
        String expResult = "c_/a/b/c";
        String result = instance.getNamespace(forDir);
        assertEquals(expResult, result);
    }

    /**
     * Test of getProperties method, of class NbSuitePreferences.
     */
    @Test
    public void testGetProperties() {
        System.out.println("getProperties(String id)");
        String id = "server-instance";
        NbSuitePreferences instance = create();
        //InstancePreferences expResult = null;
        InstancePreferences result = instance.getProperties(id);
        result.setProperty("memememe", "mymymymy");
        //result.setProperty("MyProp01", "MyProp01Value");
        //String s = result.getProperty("MyProp01");

        assertNotNull(result);

        assertEquals("server-instance", result.getPreferences().name());

        //assertEquals(expResult, result);
    }

    /**
     * Test of clearSuite method, of class NbSuitePreferences.
     */
    @Test
    public void testClearSuite() throws BackingStoreException {
        System.out.println("clearSuite");
        NbSuitePreferences instance = create();
        instance.getProperties("propName01");

        NbSuitePreferences instance1 = create("d:/a/b/c/d");
        instance1.getProperties("propName002");

        int i = instance.rootSuiteNode().childrenNames().length;
        boolean b = instance.rootSuiteNode().nodeExists(instance.getNamespace());
        assertTrue(b);
        b = instance.rootSuiteNode().nodeExists(instance1.getNamespace());
        assertTrue(b);

        Preferences prefs = instance.clearSuite(); // return rootSuitNode

        assertFalse(prefs.nodeExists(instance.getNamespace()));
        assertFalse(prefs.nodeExists(instance1.getNamespace()));

    }
    /**
     * Test of clearSuite method, of class NbSuitePreferences.
     */
    @Test
    public void testClearRoot() throws BackingStoreException {
        System.out.println("clearRoot");
        NbSuitePreferences instance = create();
        instance.getProperties("propName01");

        NbSuitePreferences instance1 = create("d:/a/b/c/d");
        instance1.getProperties("propName002");

        Preferences prefs = instance.clearRoot(); // return rootSuitNode

        assertFalse(prefs.nodeExists(SUIDE_UID_NAMESPACE));

    }    
/*
    @Test(expected = IllegalStateException.class)
    public void testRemoveProperties() throws BackingStoreException {

        System.out.println("removeProperties(String)");

        NbSuitePreferences instance = create();
        InstancePreferences ip = instance.getProperties("test-properties");
        assertNull(ip.getPreferences().get("propName01", null));
        ip.setProperty("propName01", "propValue01");
        assertEquals("propValue01", ip.getPreferences().get("propName01", null));

        
        instance.getProperties("test-properie")removeProperties("test-properties");
        //
        // First check if another property file exists
        //
        InstancePreferences ip1 = instance.getProperties("test-properties-1");
        assertNull(ip1.getPreferences().get("propName01-1", null));
        ip1.setProperty("propName01-1", "propValue01-1");
        assertEquals("propValue01-1", ip1.getPreferences().get("propName01-1", null));
        //
        //Must throw IllegalStateException
        //
        ip.getPreferences().get("propName01", null);

    }
*/
//    @Test(expected = IllegalStateException.class)  
    @Test
    public void testRemoveAllProperties() throws BackingStoreException {

        System.out.println("removeProperties(String)");

        NbSuitePreferences instance = create();

        InstancePreferences ip = instance.getProperties("test-properties");
        InstancePreferences ip1 = instance.getProperties("test-properties-1");

        assertNull(ip.getPreferences().get("propName01", null));
        ip.setProperty("propName01", "propValue01");
        assertEquals("propValue01", ip.getPreferences().get("propName01", null));

        assertNull(ip1.getPreferences().get("propName01-1", null));
        ip1.setProperty("propName01-1", "propValue01-1");
        assertEquals("propValue01-1", ip1.getPreferences().get("propName01-1", null));
        instance.removeAllProperties();
        //
        //Must throw IllegalStateException
        //
        boolean exception = true;
        try {
            ip.getPreferences().get("propName01", null);
            exception = false;
        } catch (IllegalStateException ex) {
            try {
                ip1.getPreferences().get("propName01-1", null);
                exception = false;
            } catch (IllegalStateException ex1) {
            }
        }
        assertTrue("The node has bee removed", exception);
    }
    
    @Test
    public void testTemptemp() throws BackingStoreException {
        SuiteProjectsManager sr = new SuiteProjectsManager(true);
        
        String[] childs = sr.childrenNames();
        int i=0;
    }
}
