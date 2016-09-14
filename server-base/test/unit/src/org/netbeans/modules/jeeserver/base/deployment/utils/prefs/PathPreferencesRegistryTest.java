package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;

/**
 *
 * @author Valery Shyshkin
 */
public class PathPreferencesRegistryTest {

    //public static String TEST_UUID = PathPreferencesRegistry.TEST_UUID;
    public static String TEST_UUID = "c:\\users\\ServersManager";
    //public static String UUID_ROOT = PathPreferencesRegistry.UUID_ROOT;

    private final Path directory01 = Paths.get("c:/MyServers/Server01");
    private final Path directory02 = Paths.get("c:/MyServers/Server02");
    private final Path directory03 = Paths.get("c:/MyServers/Server03");

    private final String testId = "apps/MyApp01";

    public PathPreferencesRegistry create() {
        return new PathPreferencesRegistry(directory01, TEST_UUID );
    }

    public PathPreferencesRegistry create(Path dir) {
        return create(dir.toString());
    }

    public PathPreferencesRegistry create(String dir) {
        return new PathPreferencesRegistry(Paths.get(dir), TEST_UUID);
    }

    public void initProperties(PreferencesProperties instance) {
        instance.setProperty("testName0", "testValue0");
        instance.setProperty("testName1", "testValue1");
        instance.setProperty("testName2", "testValue2");
        instance.setProperty("testName3", "testValue3");

    }

    public void initProperties(PathPreferencesRegistry instance) {
        initProperties(instance.createProperties("apps/MyApp01"));
    }

    public PathPreferencesRegistryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //factory = new TestPreferencesProperiesFactory();
    }

    @After
    public void tearDown() throws BackingStoreException {
        PathPreferencesRegistry r = create();
        r.clearRegistryRoot();
        
        Preferences p = AbstractPreferences.userRoot();
        p = p.node(PathPreferencesRegistry.UUID_ROOT);
        p = p.node(PathPreferencesRegistry.TEST_UUID);
        try {
            p.removeNode();
        } catch (BackingStoreException ex) {
            System.out.println("tearDown EXCEPTION");
            Logger.getLogger(PathPreferencesRegistryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of registryRootNamespace method, of class PathPreferencesRegistry.
     */
    @Test
    public void testRegistryRootNamespace() {
        System.out.println("registryRootNamespace");
        PathPreferencesRegistry instance = new PathPreferencesRegistry(directory01,TEST_UUID );
        String expResult = PathPreferencesRegistry.UUID_ROOT;
        String result = instance.registryRootNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of registryRootExtended method, of class PathPreferencesRegistry.
     */
    @Test
    public void testRegistryRoot() {
        System.out.println("registryRoot");
        PathPreferencesRegistry instance = new PathPreferencesRegistry(directory01,TEST_UUID );
        //
        // userRoot() = "/"
        // RegistryRootNamespace() = UUID_ROOT
        //
        // When we use two parameters in the constructor of PathPreferencesRegistry
        // than the first parameter is used as a last part of the registryRootExtended()
        //
        String expResult = "/" + PathPreferencesRegistry.UUID_ROOT + "/" + TEST_UUID.replace("\\","/");
        Preferences resultPrefs = instance.registryRootExtended();
        
        assertEquals(expResult,resultPrefs.absolutePath());
        
        
        
    }
    /**
     * Test of removeRegistryDirectory method, of class PathPreferencesRegistry.
     * 
     * TEST_UUID = "c:\\users\\MyServerInstance01";
     * UUID_ROOT = PathPreferencesRegistry.UUID_ROOT;
     * directory01 = Paths.get("c:/MyServers/Server01");
     * directory02 = Paths.get("c:/MyServers/Server02");
     * directory03 = Paths.get("c:/MyServers/Server03");
     * 
     * testId = "apps/MyApp01";
     * 
     */
    @Test
    public void testRemoveRegistryDirectory_Preferences() throws BackingStoreException {
        System.out.println("removeRegistry(Preferences)");
        PathPreferencesRegistry instance01 = new PathPreferencesRegistry(directory01,TEST_UUID );
        initProperties(instance01);

        PathPreferencesRegistry instance02 = new PathPreferencesRegistry(directory02,TEST_UUID );
        initProperties(instance02);

        PathPreferencesRegistry instance03 = new PathPreferencesRegistry(directory03,TEST_UUID );
        initProperties(instance03);

        instance01.removeRegistryDirectory(instance01.directoryPropertiesRoot());
        //
        // Must be removed
        //
        assertFalse(instance01.nodeExists(instance01.getDirectoryNamespace()));

        //
        // The node c:/MyServers must ne kept
        //
        String remainder = directory01.getParent().toString();
        assertTrue(instance01.nodeExists(remainder));

    }
    /**
     * Test of removeRegistryDirectory method, of class PathPreferencesRegistry.
     * The method removes directoryPropertiesRoot() and all it's parents until
     * rootNode.
     * 
     * TEST_UUID = "c:\\users\\MyServerInstance01";
     * UUID_ROOT = PathPreferencesRegistry.UUID_ROOT;
     * directory01 = Paths.get("c:/MyServers/Server01");
     * directory02 = Paths.get("c:/MyServers/Server02");
     * directory03 = Paths.get("c:/MyServers/Server03");
     * 
     * testId = "apps/MyApp01";
     * 
     */
    @Test
    public void testRemoveRegistryDirectory() throws BackingStoreException {
        System.out.println("removeRegistry");
        PathPreferencesRegistry instance = new PathPreferencesRegistry(directory01,TEST_UUID );
        initProperties(instance);


        instance.removeRegistryDirectory();
        //
        // All nodes must be removed at least up to registryRoot().
        // registryRoot may contain other nodes which are created outside 
        // this unit tests.
        //
        assertFalse(instance.registryRoot().nodeExists(PathPreferencesRegistry.TEST_UUID));
    }
    /**
     * Test of getProperties method, of class PathPreferencesRegistry.
     */
    @Test
    public void testGetProperties_String() {
        System.out.println("getProperties(String)");
        String id = "web-apps/MyApp01";
        
        //
        // If the namespace specified by id parameter doesn't exist
        // then the method returns null.
        //
        PathPreferencesRegistry instance =  new PathPreferencesRegistry(directory01,TEST_UUID);
        PreferencesProperties result = instance.getProperties(id);
        assertNull(result);
        
        //
        // Now create properties namespace specified by the id parameter.
        //
        result = instance.createProperties(id);
        assertNotNull(instance.getProperties(id));        
        
        
        result.setProperty("mykey", "myValue");
        assertEquals("myValue", result.getProperty("mykey"));

        result = create().getProperties(id);        
        assertEquals("myValue", result.getProperty("mykey"));        
/*        System.out.println("getId = " + result.getId());
        System.out.println("rootNode.abs = " + create().userRoot().absolutePath());        
        System.out.println("rootRegistryNode.abs = " + create().registryRootExtended().absolutePath());        
        System.out.println("properties.abs = " + result.getPreferences().absolutePath());                
*/        
    }

    /**
     * Test of getProperties method, of class PathPreferencesRegistry.
     * @throws java.io.IOException
     */
    @Test
    public void testEXTERNAL() throws IOException {
        System.out.println("EXTERNAL");
        String ROOT_RESOURCE = "org/netbeans/modules/jeeserver/jetty/project/template/ext/";
        String NB_DEPLOY_XML = ROOT_RESOURCE + "min-nb-deploy.xml";
        Enumeration<URL> en = BaseConstants.class.getClassLoader().getResources("org/netbeans/modules/jeeserver/base/deployment/resources");
        while (en.hasMoreElements()) {
            URL u = en.nextElement();
            System.out.println( "file = " + u.getFile());
        }

    }
}