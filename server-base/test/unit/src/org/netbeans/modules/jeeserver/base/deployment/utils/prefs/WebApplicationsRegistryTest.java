/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

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
import org.openide.util.Exceptions;

/**
 *
 * @author Valery
 */
public class WebApplicationsRegistryTest {
    protected Path serverInstancePath = Paths.get("c:\\MyServers/MyServerInstance01");
    protected Path webappPath01 = Paths.get("C:\\MyWebApps/MyWebApp01");
    protected Path webappPath02 = Paths.get("C:\\MyWebApps/MyWebApp02");
    protected String extNamespace = "WebApplicationsRegistryTest";
    
    protected final String expResultPrefix = "/" + CommonPreferences.COMMON_ROOT + "/WebApplicationsRegistryTest/C:/MyServers/MyServerInstance01";
   
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
    public void testGetAppPropertiesList() {
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
    public void testGetAppPropertiesList_1() {
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
    public void testAddApplication() {
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
    public void testAddApplication_0() {
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
    public void testAddApplication_1() {
        System.out.println("addApplication");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String id = instance.addApplication(webappPath01);
        String result = instance.getProperties("web-apps/" + id).getPreferences().absolutePath();
        String expResult = expResultPrefix + "/web-apps/" + id;
        assertEquals(expResult, result);
        
    }

    /**
     * Test of addApplication method, of class WebApplicationsRegistry.
     * When the web application already exists must return the same node name
     */
    @Test
    public void testAddApplication_2() {
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
    public void testAddApplication_3() {
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
    public void testRemoveApplication_3() {
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
    public void testRemoveApplication_4() {
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
    public void testFindAppNodeName() {
        System.out.println("findWebAppNodeName");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        String result = instance.findAppNodeName(webappPath01);
        assertNull(result);
    }
    /**
     * Test of findAppNodeName method, of class WebApplicationsRegistry.
     */
    @Test
    public void testFindPropertiesByContextPath() {
        System.out.println("findPropertiesByContextPath");
        WebApplicationsRegistry instance = new WebApplicationsRegistry(serverInstancePath, extNamespace);
        instance.addApplication(webappPath01);
        instance.addApplication(webappPath02);
        InstancePreferences ip = instance.findProperties(webappPath01);
        String a = ip.getPreferences().absolutePath();
        ip.setProperty(WebApplicationsRegistry.CONTEXTPATH_PROP, "/WebApp01");
        ip = instance.findProperties(webappPath01);
        String p = ip.getProperty(WebApplicationsRegistry.CONTEXTPATH_PROP);
        //ip.setProperty(WebApplicationsRegistry.CONTEXTPATH_PROP, "/")
        InstancePreferences result = instance.findPropertiesByContextPath("/WebApp01");
        p = result.getProperty(WebApplicationsRegistry.CONTEXTPATH_PROP);        
        //String result = instance.findAppNodeName(webappPath01);
        assertNull(result);
    }
    
}
