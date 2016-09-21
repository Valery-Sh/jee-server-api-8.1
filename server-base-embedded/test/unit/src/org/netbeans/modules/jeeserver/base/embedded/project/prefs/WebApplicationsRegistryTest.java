/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.WebApplicationsRegistry;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.CommonPreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryPreferences;
import org.openide.util.Exceptions;

/**
 *
 * @author Valery
 */
public class WebApplicationsRegistryTest {
    protected Path serverInstancePath = Paths.get("c:\\MyServers/MyServerInstance01");
    protected Path webappPath01 = Paths.get("c:\\MyWebApps/MyWebApp01");
    protected Path webappPath02 = Paths.get("c:\\MyWebApps/MyWebApp02");
    protected String extNamespace = "WebApplicationsRegistryTest";
    
    protected String expResultPrefix = "/" + CommonPreferences.COMMON_ROOT + "/WebApplicationsRegistryTest/c:/MyServers/MyServerInstance01";
   
    public WebApplicationsRegistryTest() {
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
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath,extNamespace);
        try {
            instance.clearRoot();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
    }

    /**
     * Test of applicationsRoot method, of class WebApplicationsRegistry.
     */
    @Test
    public void testWebAppsRoot() {
        System.out.println("webAppsRoot");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String expResult = expResultPrefix + "/web-apps";
        Preferences webappsRoot = instance.applicationsRoot();
        String result = webappsRoot.absolutePath();
        assertEquals(expResult, result);
        
/*        Path path = Paths.get("D:/Netbeans_810_Plugins/TestApps/AMEmbServer01");
        WebApplicationsRegistry reg = new WebApplicationsRegistry(path);
        InstancePreferences prefs = reg.findProperties("D:\\Netbeans_810_Plugins\\TestApps\\AWebApp01");
        String cp = prefs.getProperty("contextPath");
        int i = 0;
*/        
        //server01.getProperties("web-apps")
        
    }

    /**
     * Test of getAppPropertiesList method, of class WebApplicationsRegistry.
     * No web application found
     */
    @Test
    public void testGetWebAppPropertiesList() {
        System.out.println("getWebAppPropertiesList");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        int expResult = 0;
        
        List<InstancePreferences> list = instance.getAppPropertiesList();
        int result = list.size();
        assertEquals(expResult, result);
        
    }
    /**
     * Test of getAppPropertiesList method, of class WebApplicationsRegistry.
     * One web application entry found
     */
    @Test
    public void testGetWebAppPropertiesList_1() {
        System.out.println("getWebAppPropertiesList");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        InstancePreferences prefs = instance.createProperties("web-apps/123-456");
        int expResult = 1;
        
        List<InstancePreferences> list = instance.getAppPropertiesList();
        int result = list.size();
        assertEquals(expResult, result);
        
    }
    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     */
    @Test
    public void testAddWebApplication() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String result = instance.addApplication(webappPath01);
        assertNotNull(result);
    }
    /**
     * Test of size() method, of class WebApplicationsRegistry.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath);
        String id = instance.addApplication(webappPath01);
        //instance.getProperties("web-apps", node);
        int result = instance.size();
        int expResult = 1;
        assertEquals(expResult,result);
        
    }
    
    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     */
    @Test
    public void testAddWebApplication_0() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath);
        String id = instance.addApplication(webappPath01);
        //instance.getProperties("web-apps", node);
        Path result = Paths.get(instance.getProperties("web-apps/" + id).getProperty("location"));
        assertEquals(webappPath01,result);
        
    }
    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     */
    @Test
    public void testAddWebApplication_1() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String id = instance.addApplication(webappPath01);
        String result = instance.getProperties("web-apps/" + id).getPreferences().absolutePath();
        String expResult = expResultPrefix + "/web-apps/" + id;
        //Path result = Paths.get(instance.getProperties("web-apps/" + id).getProperty("location"));
        assertEquals(expResult, result);
        
    }

    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     * When the web application already exists must return the same node name
     */
    @Test
    public void testAddWebApplication_2() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String node = instance.addApplication(webappPath01);
        String node1 = instance.addApplication(webappPath01);
        assertEquals(node,node1);
        
    }
    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     * Two web applications added
     */
    @Test
    public void testAddWebApplication_3() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String node = instance.addApplication(webappPath01);
        String node1 = instance.addApplication(webappPath02);
        assertNotEquals(node,node1);
        
        String id = instance.findAppNodeName(webappPath01);
        assertEquals(id, node);
        id = instance.findAppNodeName(webappPath02);
        assertEquals(id, node1);        
    }

    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     * Two web applications added
     */
    @Test
    public void testremoveWebApplication_3() {
        System.out.println("removeApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String node = instance.addApplication(webappPath01);
        String node1 = instance.addApplication(webappPath02);
        String id = instance.removeApplication(webappPath01);
        assertEquals(id, node);
        id = instance.findAppNodeName(webappPath01);
        assertNull(id);
        id = instance.findAppNodeName(webappPath02);
        assertNotNull(id);
        
        
    }
    @Test
    public void testremoveWebApplication_4() {
        System.out.println("removeApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String node = instance.addApplication(webappPath01);
        String node1 = instance.addApplication(webappPath02);
        InstancePreferences ip = instance.findProperties(webappPath01);
        try {
            ip.getPreferences().removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        ip = instance.findProperties(webappPath01);
        assertNull(ip);
        ip = instance.findProperties(webappPath02);
        assertNotNull(ip);
        
    }
    
    /**
     * Test of findAppNodeName method, of class WebApplicationsRegistry.
     */
    @Test
    public void testFindWebAppNodeName() {
        System.out.println("findWebAppNodeName");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String result = instance.findAppNodeName(webappPath01);
        assertNull(result);
    }
    
}