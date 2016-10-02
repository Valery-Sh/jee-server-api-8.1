/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.webmodule;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Valery
 */
public class WebApplicationsManagerTest {
    
    public WebApplicationsManagerTest() {
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
     * Test of getInstance method, of class WebApplicationsManager.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        Project instanceProject = null;
        WebApplicationsManager expResult = null;
        WebApplicationsManager result = WebApplicationsManager.getInstance(instanceProject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getServerInstance method, of class WebApplicationsManager.
     */
    @Test
    public void testGetServerInstance() {
        System.out.println("getServerInstance");
        WebApplicationsManager instance = null;
        Project expResult = null;
        Project result = instance.getServerInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRegistered method, of class WebApplicationsManager.
     */
    @Test
    public void testIsRegistered() {
        System.out.println("isRegistered");
        Project webapp = null;
        WebApplicationsManager instance = null;
        boolean expResult = false;
        boolean result = instance.isRegistered(webapp);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of refreshSuiteInstances method, of class WebApplicationsManager.
     */
    @Test
    public void testRefreshSuiteInstances() {
        System.out.println("refreshSuiteInstances");
        FileObject suiteDir = null;
        WebApplicationsManager.refreshSuiteInstances(suiteDir);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of refresh method, of class WebApplicationsManager.
     */
    @Test
    public void testRefresh() {
        System.out.println("refresh");
        WebApplicationsManager instance = null;
        instance.refresh();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of register method, of class WebApplicationsManager.
     */
    @Test
    public void testRegister() {
        System.out.println("register");
        Project webApp = null;
        WebApplicationsManager instance = WebApplicationsManager.getInstance(webApp);
        instance.register(webApp);
        
        
    }

    /**
     * Test of unregister method, of class WebApplicationsManager.
     */
    @Test
    public void testUnregister_Project() {
        System.out.println("unregister");
        Project webApp = null;
        WebApplicationsManager instance = null;
        instance.unregister(webApp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unregister method, of class WebApplicationsManager.
     */
    @Test
    public void testUnregister_String() {
        System.out.println("unregister");
        String webAppPath = "";
        WebApplicationsManager instance = null;
        instance.unregister(webAppPath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWebAppFileObjects method, of class WebApplicationsManager.
     */
    @Test
    public void testGetWebAppFileObjects() {
        System.out.println("getWebAppFileObjects");
        WebApplicationsManager instance = null;
        List<FileObject> expResult = null;
        List<FileObject> result = instance.getWebAppFileObjects();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    @Test
    public void testTEMPTEMP() {
        System.out.println("TEMPTEMP");
        InstancePreferences ip = SuiteManager.getInstanceProperties("D:/Netbeans_810_Plugins/TestApps/AMEmbServer04");
        String a = ip.getPreferences().absolutePath();
        Path p = Paths.get("d:/Netbeans_810_Plugins/TestApps/AMEmbServer04");
        int b = 0;
        ip.forEach((k,v) -> {
            String p1 = ip.getProperty(k);
            System.out.println("-- key=" + k +"; value="+v);
        } );
    }
    
}
