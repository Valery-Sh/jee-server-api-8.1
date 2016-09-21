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
import org.netbeans.modules.jeeserver.base.deployment.BaseJ2eePlatformImpl;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.openide.util.Exceptions;

/**
 *
 * @author Valery Shyshkin
 */
public class DirectoryPreferencesTest {

    public static String ROOT_EXT = "c:\\users\\ServersManager";

    private final Path directory01 = Paths.get("c:/MyServers/Server01");
    private final Path directory02 = Paths.get("c:/MyServers/Server02");
    private final Path directory03 = Paths.get("c:/MyServers/Server03");

    private final String testId = "apps/MyApp01";

    public DirectoryPreferences create() {
        return new DirectoryPreferences(directory01, ROOT_EXT);
    }

    public DirectoryPreferences create(Path dir) {
        return create(dir.toString());
    }

    public DirectoryPreferences create(String dir) {
        return new DirectoryPreferences(Paths.get(dir), ROOT_EXT);
    }

    public void initProperties(PreferencesProperties instance) {
        instance.setProperty("testName0", "testValue0");
        instance.setProperty("testName1", "testValue1");
        instance.setProperty("testName2", "testValue2");
        instance.setProperty("testName3", "testValue3");

    }

    public void initProperties(DirectoryPreferences instance) {
        initProperties(instance.createProperties("apps/MyApp01"));
    }

    public DirectoryPreferencesTest() {
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
        DirectoryPreferences r = create();
        r.clearRoot();

        Preferences p = AbstractPreferences.userRoot();
        p = p.node(ROOT_EXT);
        
        try {
            p.removeNode();
        } catch (BackingStoreException ex) {
            System.out.println("tearDown EXCEPTION");
            Logger.getLogger(DirectoryPreferencesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of rootNamespace method, of class DirectoryPreferences.
     */
    @Test
    public void testRegistryRootNamespace() {
        System.out.println("registryRootNamespace");
        DirectoryPreferences instance = new DirectoryPreferences(directory01, ROOT_EXT);
        String expResult = "";
        String result = instance.rootNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of rootExtended method, of class DirectoryPreferences.
     */
    @Test
    public void testRegistryRoot() {
        System.out.println("registryRoot");
        DirectoryPreferences instance = new DirectoryPreferences(directory01, ROOT_EXT);
        //
        // commonUserRoot() = "/"
        // RegistryRootNamespace() = UUID_ROOT
        //
        // When we use two parameters in the constructor of DirectoryPreferences
        // than the first parameter is used as a last part of the rootExtended()
        //
        String expResult = instance.rootNode().absolutePath() + "/" + ROOT_EXT.replace("\\", "/");
        Preferences resultPrefs = instance.rootExtended();

        assertEquals(expResult, resultPrefs.absolutePath());

    }

    /**
     * Test of removeNode method, of class DirectoryPreferences.
     *
     * ROOT_EXT = "c:\\users\\MyServerInstance01"; UUID_ROOT =
 DirectoryPreferences.UUID_ROOT; directory01 =
 Paths.get("c:/MyServers/Server01"); directory02 =
 Paths.get("c:/MyServers/Server02"); directory03 =
 Paths.get("c:/MyServers/Server03");

 testId = "apps/MyApp01";
     *
     */
    @Test
    public void testRemoveRegistryDirectory_Preferences() throws BackingStoreException {

        System.out.println("removeRegistry(Preferences)");
        DirectoryPreferences instance01 = new DirectoryPreferences(directory01, ROOT_EXT);
        initProperties(instance01);

        DirectoryPreferences instance02 = new DirectoryPreferences(directory02, ROOT_EXT);
        initProperties(instance02);

        DirectoryPreferences instance03 = new DirectoryPreferences(directory03, ROOT_EXT);
        initProperties(instance03);

        instance01.removeNode(instance01.propertiesRoot());
        //
        // Must be removed
        //
        assertFalse(instance01.nodeExists(instance01.getDirectoryNamespace()));

        //
        // The node c:/MyServers must be kept
        //
        String remainder = directory01.getParent().toString();
        assertTrue(instance01.nodeExists(remainder));

    }


    /**
     * Test of getProperties method, of class DirectoryPreferences.
     */
    @Test
    public void testGetProperties_String() {
        System.out.println("getProperties(String)");
        String id = "web-apps/MyApp01";

        //
        // If the namespace specified by id parameter doesn't exist
        // then the method returns null.
        //
        DirectoryPreferences instance = new DirectoryPreferences(directory01, ROOT_EXT);
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
        System.out.println("rootNode.abs = " + create().commonUserRoot().absolutePath());        
        System.out.println("rootRegistryNode.abs = " + create().rootExtended().absolutePath());        
        System.out.println("properties.abs = " + result.getPreferences().absolutePath());                
         */
    }

    /**
     * Test of getProperties method, of class DirectoryPreferences.
     *
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
            System.out.println("file = " + u.getFile());
        }

    }

    /**
     * Test TO DELETE in production
     *
     * @throws java.io.IOException
     */
    @Test
    public void testTempTemp() throws IOException {
        System.out.println("temptemp");
        String root = "";
        //String forCheck = "org/netbeans/modules/jeeserver/base/embedded/uid-025c0287-3243-4a16-837e-94be30ecfc19";
        String forCheck = "org/netbeans/modules/jeeserver/base/embedded/uid-fffb0fd9-da7b-478e-a427-9c7d2f8babcb";
        //uid-fffb0fd9-da7b-478e-a427-9c7d2f8babcb        

        CommonPreferences pp = new CommonPreferences(root);
        String ap = pp.rootNode().absolutePath();

        boolean b = pp.nodeExists("c:\\Users\\Valery\\AppData\\Roaming\\NetBeans\\8.1\\config\\Preferences\\org\\netbeans\\modules\\");
        pp = new CommonPreferences(root, forCheck);
        try {
            String[] names = pp.rootExtended().childrenNames();
            ap = pp.rootNode().absolutePath();
            for (String s : names) {
                System.out.println("child name = " + s);
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        Preferences nb = MyNbPreferences.forModule(BaseJ2eePlatformImpl.class);

        int i = 0;
        //pp = new CommonPreferences(root,"a/b");

        //ap = pp.rootExtended().absolutePath();
        i = 0;

    }
}
