/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.openide.util.Exceptions;

/**
 *
 * @author Valery
 */
public class CommonPreferencesTest {
    public static String ROOT_EXT = "c:\\users\\ServersManager";
    public static String ROOT = "c:\\root";

    private final Path directory01 = Paths.get("c:/MyServers/Server01");
    private final Path directory02 = Paths.get("c:/MyServers/Server02");
    private final Path directory03 = Paths.get("c:/MyServers/Server03");

    private final String testId = "apps/MyApp01";
    
    public CommonPreferencesTest() {
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
        
        CommonPreferences r = new CommonPreferences(ROOT,ROOT_EXT);
        r.clearRoot();
        
        CommonPreferences r1 = new CommonPreferences("");
        r1.clearRoot();
        
        CommonPreferences r2 = new CommonPreferences("",ROOT_EXT);
        r2.clearRoot();

        Preferences p = AbstractPreferences.userRoot();
        p = p.node(ROOT);
        Preferences p1 = AbstractPreferences.userRoot();
        p1 = p1.node("");
        
        
        try {
            p.removeNode();
            p1.node(ROOT_EXT).removeNode();
        } catch (BackingStoreException ex) {
            System.out.println("tearDown EXCEPTION");
            Logger.getLogger(DirectoryPreferencesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of rootNamespace method, of class CommonPreferences.
     */
    @Test
    public void testRootNamespace() {
        System.out.println("rootNamespace");
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        String expResult = ROOT.replace("\\", "/");
        String result = instance.rootNamespace();
        assertEquals(expResult, result);
    }
    /**
     * Test of rootNamespace method, of class CommonPreferences.
     */
    @Test
    public void testRootNamespace_1() {
        System.out.println("rootNamespace");
        CommonPreferences instance = new CommonPreferences("",ROOT_EXT);
        String expResult = "";
        String result = instance.rootNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of commonUserRoot method, of class CommonPreferences.
     */
    @Test
    public void testUserRoot() {
        System.out.println("userRoot");
        CommonPreferences instance = new CommonPreferences(ROOT,ROOT_EXT);
        Preferences expResult = AbstractPreferences.userRoot().node(CommonPreferences.COMMON_ROOT);
        Preferences result = instance.commonUserRoot();
        assertEquals(expResult, result);
        
        Path p = Paths.get("c:\\a\\b");
        String s = p.getRoot().toString();
        s = p.getFileName().toString();
        s = Paths.get("u:/").toString();
        int i = 0;
        
        
    }

    /**
     * Test of rootNode method, of class CommonPreferences.
     */
    @Test
    public void testRootNode() {
        System.out.println("rootNode");
        CommonPreferences instance = new CommonPreferences(ROOT,ROOT_EXT);
        Preferences expResult = instance.commonUserRoot().node(ROOT.replace("\\", "/"));
        Preferences result = instance.rootNode();
        assertEquals(expResult, result);
    }
    /**
     * Test of rootNode method, of class CommonPreferences.
     */
    @Test
    public void testRootNode_1() {
        System.out.println("rootNode");
        CommonPreferences instance = new CommonPreferences("",ROOT_EXT);
        Preferences expResult = instance.commonUserRoot().node("");
        Preferences result = instance.rootNode();
        assertEquals(expResult, result);
    }
    /**
     * Test of rootExtended method, of class CommonPreferences.
     */
    @Test
    public void testRootExtended() {
        System.out.println("rootExtended");
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        Preferences expResult = instance.rootNode().node(ROOT_EXT.replace("\\","/"));
        Preferences result = instance.rootExtended();
        assertEquals(expResult, result);
    }
    /**
     * Test of rootExtended method, of class CommonPreferences when the 
     * second parameter is omitted.
     */
    @Test
    public void testRootExtended_1() {
        System.out.println("rootExtended");
        CommonPreferences instance = new CommonPreferences(ROOT);
        Preferences expResult = instance.rootNode();
        Preferences result = instance.rootExtended();
        assertEquals(expResult, result);
    }

    /**
     * Test of propertiesRoot method, of class CommonPreferences.
     */
    @Test
    public void testPropertiesRoot() {
        System.out.println("propertiesRoot");
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        Preferences expResult = instance.rootExtended();
        Preferences result = instance.propertiesRoot();
        assertEquals(expResult, result);
    }

    /**
     * Test of childrenNames method, of class CommonPreferences.
     */
    @Test
    public void testChildrenNames_Preferences() throws Exception {
        System.out.println("childrenNames");
        
        
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        Preferences forNode =  instance.rootExtended();        
        instance.rootExtended().node("child01");
        
        String[] expResult = new String[] {"child01"};
        String[] result = instance.childrenNames(forNode);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of childrenNames method, of class CommonPreferences.
     */
    @Test
    public void testChildrenNames_0args() throws Exception {
        System.out.println("childrenNames");
        
        CommonPreferences instance = new CommonPreferences(ROOT, ROOT_EXT);
        //Preferences forNode =  instance.rootExtended();        
        instance.rootExtended().node("child01");
        
        String[] expResult = new String[] {"child01"};
        String[] result = instance.childrenNames();
        assertArrayEquals(expResult, result);
        
    }

    /**
     * Test of clearRoot method, of class CommonPreferences.
     */
    @Test
    public void testClearRootNode() throws Exception {
        System.out.println("clearRootNode");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences expResult = instance.rootNode();
        Preferences result = instance.clearRoot();
        assertEquals(expResult, result);
    }
    /**
     * Test of clearRoot method, of class CommonPreferences.
     */
    @Test
    public void testClearRootNode_1() throws Exception {
        System.out.println("clearRootNode");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences expResult = instance.rootNode();
        Preferences result = instance.rootExtended().node("a/b").node("c:/d");
        result = instance.clearRoot();
        assertEquals(expResult, result);        
    }

    /**
     * Test of clearRoot method, of class CommonPreferences.
     */
    @Test
    public void testClearRootExtended() throws Exception {
        System.out.println("clearRootExtemded");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences expResult = instance.rootExtended();
        Preferences result = instance.clearRootExtended();
        assertEquals(expResult, result);
    }
    /**
     * Test of clearRoot method, of class CommonPreferences.
     * @throws java.lang.Exception
     */
    @Test
    public void testClearRootExtended_1() throws Exception {
        System.out.println("clearRootExtemded");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences expResult = instance.rootExtended();
        Preferences result = instance.rootExtended().node("a/b").node("c:/d");
        result = instance.clearRootExtended();
        assertEquals(expResult, result);
        //assertTrue(instance.rootExtended().node("a/b").equals(instance.rootExtended().node("a/b")));
    }
    
    /**
     * Test of clearUserRoot method, of class CommonPreferences.
     */
    @Test
    public void testClearCommonUserRoot() throws Exception {
        System.out.println("clearCommonUserRoot");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences expResult = instance.commonUserRoot();
        Preferences result = instance.rootExtended().node("a/b").node("c:/d");
        result = instance.clearCommonUserRoot();
        assertEquals(expResult, result);        
    }

    /**
     * Test of nodeExists method, of class CommonPreferences.
     */
    @Test
    public void testNodeExists() {
        System.out.println("nodeExists");
        String namespace = "";
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences node = instance.rootExtended().node("a/b").node("c:/d");

        boolean expResult = true;
        boolean result = instance.nodeExists("a");
        assertEquals(expResult, result);
    }
    /**
     * Test of nodeExists method, of class CommonPreferences.
     */
    @Test
    public void testNodeExists_1() {
        System.out.println("nodeExists");
        String namespace = "";
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences node = instance.rootExtended().node("a/b").node("c:/d");

        boolean expResult = true;
        boolean result = instance.nodeExists("a/b/c:/d");
        assertEquals(expResult, result);
        
    }
    /**
     * Test of nodeExists method, of class CommonPreferences.
     */
    @Test
    public void testNodeExists_2() {
        System.out.println("nodeExists");
        String namespace = "";
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences node = instance.rootExtended().node("a/b").node("c:/d");

        boolean expResult = true;
        boolean result = instance.nodeExists("a/NO NODE");
        assertNotEquals(expResult, result);
        
    }

    /**
     * Test of removeNode method, of class CommonPreferences.
     * Recursively removes nodes up to rootExtenced()
     */
    @Test
    public void testRemoveNode() throws Exception {
        System.out.println("removeNode");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences toRemove = instance.rootExtended().node("a/b").node("c:/d");        
        Preferences expResult = instance.rootExtended();
        instance.removeNode(toRemove);
        assertTrue(instance.rootExtended().childrenNames().length == 0);        

    }
    /**
     * Test of removeNode method, of class CommonPreferences.
     * Recursively removes nodes up to rootExtenced()
     */
    @Test
    public void testRemoveNode_1() throws Exception {
        System.out.println("removeNode");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences tree1 = instance.rootExtended().node("a/b").node("c:/d");        
        Preferences tree2 = instance.rootExtended().node("a/f").node("c/d");                
        Preferences toRemove = instance.rootExtended().node("a/b").node("c:/d");                
        instance.removeNode(toRemove);
        String[] expResult = new String[] {"a"}; 
        assertArrayEquals(expResult, instance.childrenNames() );        
    }
    /**
     * Test of removeNode method, of class CommonPreferences.
     * Recursively removes nodes up to rootExtenced()
     */
    @Test
    public void testRemoveNode_2() throws Exception {
        System.out.println("removeNode");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);
        Preferences rootTree1 = instance.rootExtended().node("a");        
        Preferences tree1 = rootTree1.node("b");
        Preferences tree2 = tree1.node("c:/d");        
        Preferences tree3 = rootTree1.node("f");
        Preferences tree4 = tree3.node("c/d");

        Preferences toRemove = instance.rootExtended().node("a/b").node("c:/d");                
        instance.removeNode(toRemove);
        String[] expResult = new String[] {"a"}; 
        assertArrayEquals(expResult, instance.childrenNames() );        
        //
        // node b recursivly removed
        //
        assertFalse(instance.nodeExists("a/b"));
        assertTrue(instance.nodeExists("a"));
        assertTrue(instance.nodeExists("a/f/c/d"));
        
    }


    /**
     * Test of getProperties method, of class CommonPreferences.
     */
    @Test
    public void testGetProperties() {
        System.out.println("getProperties");
        String id = "";
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);

        InstancePreferences expResult = null;
        InstancePreferences result = instance.getProperties("props/prop");
        assertEquals(expResult, result);
        assertFalse(instance.nodeExists("props/prop"));
    }
    /**
     * Test of getProperties method, of class CommonPreferences.
     */
    @Test
    public void testGetProperties_1() {
        System.out.println("getProperties");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);

        InstancePreferences expResult = instance.createProperties("props/prop");
        InstancePreferences result = instance.getProperties("props/prop");
        assertNotNull(result);
        assertEquals(expResult, result);
        
    }

    
    /**
     * Test of createProperties method, of class CommonPreferences.
     */
    @Test
    public void testCreateProperties() {
        System.out.println("createProperties");
        CommonPreferences instance = new CommonPreferences("", ROOT_EXT);

        InstancePreferences expResult = instance.createProperties("props/prop");
        InstancePreferences result = instance.getProperties("props/prop");
        assertNotNull(result);
        assertEquals(expResult, result);
    }
    @Test
    public void testTemptemp() {
        try {
            System.out.println("temptemp");
            String ROOT_EXT1 = "DD\\users\\ServersManager";
            String ROOT1 = "d:\\root";
            
            CommonPreferences instance = new CommonPreferences(ROOT1, ROOT_EXT1);
            String[] names = instance.rootExtended().childrenNames();
            if ( names.length > 0 ) {
                System.out.println("TEST TEMPTEMP childrenName=" + names[0]);
            }
            Preferences p = instance.rootExtended().node("MyNode");
            p.flush();
        } catch (BackingStoreException ex) {
            System.out.println("EXCEPTION " + ex.getMessage());
            Exceptions.printStackTrace(ex);
        }
        
        

    }
    
}