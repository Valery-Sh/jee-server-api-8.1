package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.util.NbPreferences;

/**
 *
 * @author Valery
 */
public class NbBasePreferencesTest {
    
    public static String ROOT_EXT = "c:\\users\\ServersManager";
    public static String ROOT = "c:\\root";
    
    public NbBasePreferencesTest() {
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
    
    //@After
    public void tearDown1() throws BackingStoreException {
        NbBasePreferences r = new NbBasePreferences(ROOT,ROOT_EXT);
        r.clearRoot();
        
        NbBasePreferences r1 = new NbBasePreferences("");
        r1.clearRoot();
        
        NbBasePreferences r2 = new NbBasePreferences("",ROOT_EXT);
        r2.clearRoot();

        Preferences p = r.userRoot();
        p = p.node(ROOT);
        Preferences p1 = r1.userRoot();
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
     * Test of commonUserRoot method, of class NbBasePreferences.
     */
    @Test
    public void testUserRoot() throws BackingStoreException {
        System.out.println("userRoot");
/*        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        Preferences expResult = NbPreferences.forModule(BaseDeploymentManager.class).commonUserRoot();
        Preferences result = instance.commonUserRoot();
        assertEquals(expResult, result);
*/        
            String ROOT_EXT1 = "d:\\new_users02\\ServersManager";
            String ROOT1 = "d:\\new_root02";
            NbBasePreferences instance = new NbBasePreferences(ROOT1, ROOT_EXT1);

            BaseUtil.out("0 startTest02: SuiteProjectsManager rootExtended().absPath=" + instance.rootExtended().absolutePath());
            BaseUtil.out("1 startTest02: SuiteProjectsManager childrenNames.length=" + instance.childrenNames().length);
            
            InstancePreferences ip = instance.createProperties("nbconf/server-properties");
            BaseUtil.out("2 startTest02: SuiteProjectsManager ip=" + ip);
            
            String hiddenProp = ip.getProperty(InstancePreferences.HIDDEN_KEY);
            
            BaseUtil.out("3 startTest02: SuiteProjectsManager hiddenProp=" + hiddenProp);
            
            String[] result = instance.childrenNames();
            instance.rootNode().sync();
            String[] expResult = new String[]{"nbconf"};
            
            BaseUtil.out("4 startTest02: SuiteProjectsManager childrenNames.length=" + result.length);
            NbBasePreferences instance1 = new NbBasePreferences(ROOT1, ROOT_EXT1);
            BaseUtil.out("5 startTest02: SuiteProjectsManager childrenNames.length=" + instance1.childrenNames().length);
        
    }
    /**
     * Test of childrenNames method, of class NbBasePreferences.
     */
    @Test
    public void testChildrenNames() throws BackingStoreException {
        System.out.println("userRoot");
        //System.setProperty("netbeans.user", "C:\\Users\\Valery\\AppData\\Roaming\\NetBeans\\8.1");
        //String s = System.getProperty("netbeans.user");        
        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        instance.createProperties("myprops");
        
        String[] c = instance.rootNode().childrenNames();
        boolean b1 =  instance.rootNode().nodeExists("c_");
        boolean b2 =  instance.rootNode().nodeExists("C_");
        
        Preferences expResult = NbPreferences.forModule(BaseDeploymentManager.class).userRoot();
        Preferences result = instance.userRoot();
        
        assertEquals(expResult, result);
    }
    /**
     * Test of childrenNames(Preferences) method, of class NbBasePreferences.
     */
    @Test
    public void testChildrenNames_1() throws BackingStoreException {
        System.out.println("childrenNames");
        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        instance.createProperties("server-properties");
        String[] result =  instance.childrenNames();
        String[] expResult = new String[] {"server-properties"};
        
        assertArrayEquals(expResult, result);
    }
    /**
     * Test of childrenNames(Preferences) method, of class NbBasePreferences.
     */
    @Test
    public void testChildrenNames_2() throws BackingStoreException {
        System.out.println("childrenNames");
        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        instance.createProperties("nbconf/server-properties");
        String[] result =  instance.childrenNames();
        String[] expResult = new String[] {"nbconf"};
        
        assertArrayEquals(expResult, result);
    }
    /**
     * Test of childrenNames(Preferences) method, of class NbBasePreferences.
     */
    @Test
    public void testChildrenNames_3() throws BackingStoreException {
        System.out.println("childrenNames");
        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        InstancePreferences ip = instance.createProperties("nbconf/server-properties", false);
        String hiddenProp = ip.getProperty(InstancePreferences.HIDDEN_KEY);
        String[] result =  instance.childrenNames();
        String[] expResult = new String[] {"nbconf"};
        
        assertArrayEquals(expResult, result);
    }
    /**
     * Test of childrenFileNames(Preferences) method, of class NbBasePreferences.
     */
    @Test
    public void testChildrenFileNames() {
        System.out.println("childrenFileNames");
        NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        InstancePreferences ip = instance.createProperties("nbconf/server-properties", false);
        String hiddenProp = ip.getProperty(InstancePreferences.HIDDEN_KEY);
        String s = instance.rootNode().node("a/b").absolutePath();
        int i = 0;
        //tring[] result =  instance.childrenFileNames();
        //String[] expResult = new String[] {"nbconf"};
        
       // assertArrayEquals(expResult, result);
    }
    /**
     * Test of testNormalize method, of class DirectoryRegistry.
     */
    @Test
    public void testConvert_String_array()  {
        System.out.println("convert(String[])");
        //NbBasePreferences instance = new NbBasePreferences(ROOT,ROOT_EXT);
        String[] ar = new String[] {"d:\\\\/a\\f","b////g","c///"};
        
        String[] expResult = new String[] {"d_/a/f","b/g","c"};
        String[] result = NbBasePreferences.convert(ar);
        assertArrayEquals(expResult, result);
    }        
    
}
