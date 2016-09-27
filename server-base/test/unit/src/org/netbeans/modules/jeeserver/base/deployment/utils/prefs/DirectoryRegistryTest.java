/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.netbeans.modules.jeeserver.base.deployment.utils.prefs.CommonPreferencesTest.ROOT;
import static org.netbeans.modules.jeeserver.base.deployment.utils.prefs.CommonPreferencesTest.ROOT_EXT;

/**
 *
 * @author Valery
 */
public class DirectoryRegistryTest {

    private static final Path ROOT_01 = Paths.get("root/dir/01");
    private static final Path CHILD_PATH01 = Paths.get("a/b/C");
    private static final Path CHILD_PATH02 = Paths.get("a/b/D");
    public static final String DEFAULT_PROPERTIES_NAMESPACE = "config/instance-properties";
//    public static final Path SERVER01_PATH = Paths.get("f:\\TestDir\\Server01");
//    public static final Path SERVER02_PATH = Paths.get("f:\\TestDir\\Server02");
    public static final String SERVER01_PATH = "f:\\TestDir\\Server01";
    public static final String SERVER02_PATH = "f:\\TestDir\\Server02";

    public DirectoryRegistryTest() {
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
        new DirectoryRegistryTestImpl(ROOT_01).remove();
        //new DirectoryRegistryTestImpl(SERVER02_PATH, CHILD_PATH02).removeDirectory();
    }

    /**
     * Test of getPropertiesNamespace method, of class
 DirectoryRegistry.
     */
    @Test
    public void testGetPropertiesNamespace() {
        System.out.println("getPropertiesNamespace");

        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        String expResult = "p01/p02/p03";
        String result = instance.getPropertiesNamespace();
        assertEquals(expResult, result);
    }
    /**
     * Test of getPropertiesNamespace method, of class
 DirectoryRegistry.
     */
    @Test
    public void testGetDirectoryPropertiesNamespace() {
        System.out.println("getPropertiesNamespace");

        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        String expResult = "D01/D02/D03";
        String result = instance.getDirectoryPropertiesNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of createDelegate method, of class DirectoryRegistry.
     */
    @Test
    public void testGetDelegate() {
        System.out.println("getDelegate");

        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);

        DirectoryPreferences expResult = new DirectoryPreferences(Paths.get(SERVER01_PATH), ROOT_01.toString());
        DirectoryPreferences result = instance.getDelegate();
        
        String expAbsolutePath = expResult.absolutePath();
        String absolutePath = result.absolutePath();

        assertEquals(expResult, result);
        assertEquals(expAbsolutePath, absolutePath);
        
    }

    /**
     * Test of getProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testGetProperties_0args() {
        System.out.println("getProperties");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);

        InstancePreferences expResult = instance.createProperties();
        InstancePreferences result = instance.getProperties();
        assertNotNull(result);
        assertEquals(expResult, result);

        String expAbsPath = expResult.getPreferences().absolutePath();
        String absPath = result.getPreferences().absolutePath();
        assertEquals(expAbsPath, absPath);

    }

    /**
     * Test of getProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testGetProperties_boolean() {
        System.out.println("getProperties");
        boolean createIfNotExists = false;
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);

        InstancePreferences result = instance.getProperties(createIfNotExists);
        assertNull(result);
    }

    /**
     * Test of getProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testGetProperties_boolean_1() {
        System.out.println("getProperties");
        boolean createIfNotExists = true;
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences result = instance.getProperties(createIfNotExists);
        assertNotNull(result);
    }

    /**
     * Test of createProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testCreateProperties() {
        System.out.println("createProperties");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences result = instance.createProperties();
        InstancePreferences expResult = instance.getProperties();
        assertNotNull(result);
        assertEquals(expResult, result);
    }
    /**
     * Test of createProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testCreateProperties_1() {
        System.out.println("createProperties");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);
        
        //
        // We create config properties for directory properties for
        // SERVER01_PATH/CHILD_PATH02. We expect that the directory propertie
        // are created for SERVER01_PATH and SERVER01_PATH/CHILD_PATH02.
        // Here we check only whether the directory properties for
        // SERVER01_PATH/CHILD_PATH02 are created
        //
        instance.createProperties();

        InstancePreferences result = instance.getProperties(instance.getDirectoryPropertiesNamespace());
        String value = result.getProperty(DirectoryRegistry.LOCATION_PROPERTY_KEY);
        assertNotNull(value);
        assertEquals(CHILD_PATH02, Paths.get(value));
    }

    /**
     * Test of createProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testCreateProperties_2() {
        System.out.println("createProperties");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);

        //
        // We create config properties for directory properties for
        // SERVER01_PATH/CHILD_PATH02. We expect that the directory propertie
        // are created for SERVER01_PATH and SERVER01_PATH/CHILD_PATH02.
        // Here we check only whether the directory properties for
        // SERVER01_PATH are created
        //
        instance.createProperties();
        
        DirectoryRegistry parentInstance = root.children(SERVER01_PATH);        
        InstancePreferences result = parentInstance.getProperties(instance.getDirectoryPropertiesNamespace());
        String value = result.getProperty(DirectoryRegistry.LOCATION_PROPERTY_KEY);
        assertNotNull(value);
        assertEquals(Paths.get(SERVER01_PATH), Paths.get(value));
    }
    
    /**
     * Test of nodeExists method, of class DirectoryRegistry.
     */
    @Test
    public void testNodeExists() {
        System.out.println("nodeExists");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);
        
        assertTrue(instance.nodeExists());
    }
    /**
     * Test of nodeExists method, of class DirectoryRegistry.
     */
    @Test
    public void testNodeExists_1() {
        System.out.println("nodeExists");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);
        
        assertTrue(root.nodeExists());
    }
    /**
     * Test of nodeExists method, of class DirectoryRegistry.
     */
    @Test
    public void testNodeExists_2() {
        System.out.println("nodeExists");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);
        
        instance.remove();
        
        assertFalse(instance.nodeExists());
    }

    /**
     * Test of nodeExists method, of class DirectoryRegistry.
     */
    @Test
    public void testNodeExists_3() {
        System.out.println("nodeExists");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.children(SERVER01_PATH).children(CHILD_PATH02);
        //
        // Remove root node and check whether the root exists and it's children
        // exist.
        //
        root.remove();
        
        assertFalse(root.nodeExists());
        assertFalse(instance.nodeExists());
        
    }
    /**
     * Test of add method, of class DirectoryRegistry.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry impl = new DirectoryRegistryTestImpl(Paths.get(SERVER01_PATH),root);
        DirectoryRegistry result = root.add(Paths.get(SERVER01_PATH));
        DirectoryRegistry expResult = new DirectoryRegistryTestImpl(Paths.get(SERVER01_PATH), root);
        assertEquals(expResult,result);
        //
        // The side effect is that the root and result objects have location.properties
        //
        assertTrue(root.nodeExists(DirectoryRegistry.LOCATION));
        String dir = root.getProperties(DirectoryRegistry.LOCATION)
                .getProperty(DirectoryRegistry.LOCATION_PROPERTY_KEY);
        assertTrue(ROOT_01.equals(Paths.get(dir)));
        
        assertTrue(result.nodeExists(DirectoryRegistry.LOCATION));
        dir = result.getProperties(DirectoryRegistry.LOCATION)
                .getProperty(DirectoryRegistry.LOCATION_PROPERTY_KEY);
        assertTrue(Paths.get(SERVER01_PATH).equals(Paths.get(dir)));
        
        
    }    

    /**
     * Test of childrens method, of class DirectoryRegistry.
     */
    @Test
    public void testChildrens() {
        System.out.println("chldrens");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root
                .children(SERVER01_PATH)
                .children(CHILD_PATH02);
        
        Object o = instance.parent();
        instance.commit();
        InstancePreferences ip = instance.getProperties(instance.getDirectoryPropertiesNamespace());
        //
        // Remove root node and check whether the root exists and it's children
        // exist.
        //
        List<DirectoryRegistry> result = root.childrens();
        List<DirectoryRegistry> expResult = new ArrayList<>();
        expResult.add(root.children(SERVER01_PATH));

        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of childrens method, of class DirectoryRegistry.
     */
    @Test
    public void testChildren_1() {
        System.out.println("chldrens");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry server01 =
                root.children(SERVER01_PATH);
                
        DirectoryRegistry instance = server01
                .children(CHILD_PATH02);
        

        instance.commit();
        InstancePreferences ip = instance.getProperties(instance.getDirectoryPropertiesNamespace());
        //
        // Remove root node and check whether the root exists and it's children
        // exist.
        //
        List<DirectoryRegistry> result = server01.childrens();
        List<DirectoryRegistry> expResult = new ArrayList<>();
        expResult.add(server01.children(CHILD_PATH02));

        assertEquals(expResult, result);
    }

    /**
     * Test of childrens method, of class DirectoryRegistry.
     */
    @Test
    public void testChildren_2() {
        System.out.println("chldrens");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry server01 =
                root.children(SERVER01_PATH);
        DirectoryRegistry server02 =
                root.children(SERVER02_PATH);
                
        DirectoryRegistry instance = server01
                .children(CHILD_PATH02);

        server02.commit();
        
        instance.commit();
        
//        InstancePreferences ip = instance.getProperties(instance.getDirectoryPropertiesNamespace());
        //
        // Remove root node and check whether the root exists and it's children
        // exist.
        //
        List<DirectoryRegistry> result = root.childrens();
        List<DirectoryRegistry> expResult = new ArrayList<>();
        expResult.add(server01);
        expResult.add(server02);
        System.out.println( "SUITE="+java.util.UUID.randomUUID().toString());
        assertEquals(expResult, result);
    }
    
    /**
     * Test of replaceWith method, of class DirectoryRegistry. Old
     * properties are removed and new ones added
     */
    @Test
    public void testReplaceWith() {
        System.out.println("replaceWith");
        Properties props = new Properties();
        props.setProperty("key-new", "value-new");

        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences ip = instance.createProperties();
        ip.setProperty("key", "value");
        ip.setProperty("key1", "value1");

        boolean expResult = true;
        boolean result = instance.replaceWith(props);
        assertEquals(expResult, result);
        assertNotEquals("value", ip.getProperty("key"));
        assertNotEquals("value1", ip.getProperty("key1"));

        assertEquals("value-new", ip.getProperty("key-new"));
    }

    /**
     * Test of updateWith method, of class DirectoryRegistry.
     */
    @Test
    public void testUpdateWith() {
        System.out.println("updateWith");
        Properties props = new Properties();
        props.setProperty("key-new", "value-new");
        props.setProperty("key", "value-replace");

        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences ip = instance.createProperties();
        ip.setProperty("key", "value");
        ip.setProperty("key1", "value1");
        InstancePreferences ip1 = instance.createProperties();
        String s = ip.getProperty("key");
        boolean expResult = true;
        boolean result = instance.updateWith(props);
        assertEquals(expResult, result);
        assertNotEquals("value", ip.getProperty("key"));
        assertEquals("value-replace", ip.getProperty("key"));
        assertEquals("value1", ip.getProperty("key1"));

        assertEquals("value-new", ip.getProperty("key-new"));
    }

    /**
     * Test of removeProperty method, of class DirectoryRegistry.
     */
    @Test
    public void testRemoveProperty() {
        System.out.println("removeProperty");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences ip = instance.createProperties();
        ip.setProperty("key", "value");
        ip.setProperty("key1", "value1");

        instance.removeProperty("key");
        assertEquals("value1", ip.getProperty("key1"));
        assertNull(ip.getProperty("key"));

    }

    /**
     * Test of removeProperties method, of class DirectoryRegistry.
     */
    @Test
    public void testRemoveProperties() {
        System.out.println("removeProperties");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        InstancePreferences ip = instance.createProperties();
        ip.setProperty("key", "value");
        ip.setProperty("key1", "value1");

        instance.removeProperties();
        assertNotNull(instance.getProperties());
        assertNull(ip.getProperty("key1"));
        assertNull(ip.getProperty("key"));
    }

    /**
     * Test of testRemoveAll method, of class DirectoryRegistry.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        Preferences root_01 = root.getDelegate().propertiesRoot();
        DirectoryRegistry instance = root.children(SERVER01_PATH);
        instance.getDelegate().propertiesRoot();
        
        assertTrue(root.getDelegate().nodeExists(root_01,SERVER01_PATH));
        
        root.remove();
        String rootAbsPath = root.absolutePath();
        assertEquals(root.getDelegate().userRoot().absolutePath(),rootAbsPath);
        
    }

    /**
     * Test of testRemove method, of class DirectoryRegistry.
     */
    @Test
    public void testRemove_1() {
        System.out.println("remove");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        //Preferences root_01 = root.getDelegate().propertiesRoot();
        DirectoryRegistry instance = root.children(SERVER01_PATH);
        //instance.getDelegate().propertiesRoot();
        String absPath = instance.absolutePath();        
        
        instance.remove();
        
        absPath = instance.absolutePath();
        int i = 0;
    }
    
    /**
     * Test of testRemoveAll method, of class DirectoryRegistry.
     */
    @Test
    public void testRemoveAll_1() {
        System.out.println("testRemoveAll");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        /*        DirectoryPreferences dp = new DirectoryPreferences(instancePath, rootExtention);
        dp.rootExtended();
        boolean b = dp.nodeExists(dp.rootNode(), CHILD_PATH01);
        instance.removeAll();
        b = dp.nodeExists(dp.rootNode(), CHILD_PATH01);
        int i = 0;
         */
    }

    /**
     * Test of testRemoveDirectory method, of class DirectoryRegistry.
     */
    @Test
    public void testRemoveDirectory() {
        System.out.println("removeDirectory");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        /*        DirectoryPreferences dp = new DirectoryPreferences(instancePath, rootExtention);
        dp.propertiesRoot();

        assertTrue(dp.nodeExists(dp.rootExtended(), SERVER01_PATH.toString()));
        instance.removeDirectory();
        assertFalse(dp.nodeExists(dp.rootNode(), CHILD_PATH01));

        assertFalse(dp.nodeExists(dp.rootExtended(), SERVER01_PATH.toString()));
         */
    }

    /**
     * Test of testRemoveDirectory method, of class DirectoryRegistry.
     */
    @Test
    public void testRemoveDirectory_1() {
        System.out.println("removeDirectory");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        /*        DirectoryPreferences dp = new DirectoryPreferences(instancePath, rootExtention);
        dp.propertiesRoot();
        DirectoryPreferences dp01 = new DirectoryPreferences(SERVER02_PATH, CHILD_PATH02);
        dp01.propertiesRoot();

        assertTrue(dp.nodeExists(dp.rootExtended(), SERVER01_PATH.toString()));
        assertTrue(dp01.nodeExists(dp01.rootExtended(), SERVER02_PATH.toString()));
        instance.removeDirectory();
        assertFalse(dp.nodeExists(dp.rootNode(), CHILD_PATH01));

        assertFalse(dp.nodeExists(dp.rootExtended(), SERVER01_PATH.toString()));

        assertTrue(dp01.nodeExists(dp01.rootExtended(), SERVER02_PATH.toString()));
        assertTrue(dp01.nodeExists(dp01.rootNode(), CHILD_PATH02));

        int i = 0;
         */
    }


    /**
     * Test of absolutePath method, of class DirectoryRegistry.
     */
    @Test
    public void testAbsolutePath() {
        System.out.println("absolutePath");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        String result = instance.absolutePath();
        /*        DirectoryPreferences prefs = new DirectoryPreferences(SERVER01_PATH, CHILD_PATH01);
        String expResult = prefs.absolutePath();
        assertEquals(expResult, result);
         */
    }

    /**
     * Test of absolutePath method, of class DirectoryRegistry.
     */
    @Test
    public void testAbsolutePath_1() {
        System.out.println("absolutePath");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);

        /*        DirectoryPreferences prefs = new DirectoryPreferences(SERVER01_PATH, CHILD_PATH01);
        instance.getProperties();
        prefs.getProperties(DirectoryRegistry.DEFAULT_PROPERTIES_NAMESPACE);

        String result = instance.absolutePath();
        String expResult = prefs.absolutePath(DirectoryRegistry.DEFAULT_PROPERTIES_NAMESPACE);
        assertEquals(expResult, result);
         */
    }

    /**
     * Test of absolutePath method, of class DirectoryRegistry.
     */
    @Test
    public void testAbsolutePath_2() {
        System.out.println("absolutePath");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);
        /*        DirectoryPreferences prefs = new DirectoryPreferences(SERVER01_PATH, CHILD_PATH01);

        instance.createProperties();

        prefs.createProperties(DirectoryRegistry.DEFAULT_PROPERTIES_NAMESPACE);

        String result = instance.absolutePath();
        String expResult = prefs.absolutePath(DirectoryRegistry.DEFAULT_PROPERTIES_NAMESPACE);
        assertEquals(expResult, result);
         */
    }

    /**
     * Test of absolutePath method, of class DirectoryRegistry.
     */
    @Test
    public void testAbsolutePath_3() {
        System.out.println("absolutePath");
        System.out.println("absolutePath");
        DirectoryRegistry root = new DirectoryRegistryTestImpl(ROOT_01);
        DirectoryRegistry instance = root.newInstance(SERVER01_PATH);

        /*DirectoryPreferences prefs = new DirectoryPreferences(SERVER01_PATH, CHILD_PATH01);

        instance.createProperties();

        prefs.createProperties(propNamespace);

        String result = instance.absolutePath();
        String expResult = prefs.absolutePath(propNamespace);

        assertEquals(expResult, result);
         */
    }

    /**
     * Test of absolutePath method, of class DirectoryRegistry.
     */
    @Test
    public void testAbsolutePath_4() {
        System.out.println("absolutePath");
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        String expResult = instance.createProperties("config/properties")
                .getPreferences().absolutePath();
        String result = instance.absolutePath("config/properties");
        assertEquals(expResult, result);
    }

    public static class DirectoryRegistryTestImpl<T extends DirectoryRegistry> extends DirectoryRegistry {
        
        private String absolutePath;
        private String directory;
        
        
        
        
        public DirectoryRegistryTestImpl(Path instancePath) {
            super(instancePath);
        }

        protected DirectoryRegistryTestImpl(Path instancePath, T parent) {
            super(instancePath, parent);
        }

        @Override
        public DirectoryRegistry newInstance(String path) {
            return new DirectoryRegistryTestImpl(Paths.get(path), this); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        protected DirectoryRegistry newInstance(DirectoryRegistry root, String path) {
            return new DirectoryRegistryTestImpl(Paths.get(path), root);
        }
        
        @Override
        protected String getPropertiesNamespace() {
            return "p01/p02/p03";
        }
        @Override
        protected String getDirectoryPropertiesNamespace() {
            return "D01/D02/D03";
        }        

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DirectoryRegistryTestImpl<?> other = (DirectoryRegistryTestImpl<?>) obj;
            if (!Objects.equals(this.absolutePath(), other.absolutePath())) {
                return false;
            }
            if (!Objects.equals(this.getDelegate().directoryNamespace(), other.getDelegate().directoryNamespace())) {
                return false;
            }
            return true;
        }

    }
}
