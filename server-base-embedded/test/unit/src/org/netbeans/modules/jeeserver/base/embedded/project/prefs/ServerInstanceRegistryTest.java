/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.PathPreferencesRegistry;
import org.openide.util.Exceptions;

/**
 *
 * @author Valery
 */
public class ServerInstanceRegistryTest {
    
    protected Path serverInstancePath = Paths.get("c:\\MyServers/MyServerInstance01");
    
    protected String expResultPrefix = "/UUID-ROOT-f4460510-bc81-496d-b584-f4ae5975f04a/DEFAULT-DIRECTORIES/c:/MyServers/MyServerInstance01";
    
    public ServerInstanceRegistryTest() {
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
        ServerInstanceRegistry instance = new ServerInstanceRegistry(serverInstancePath);
        try {
            instance.clearRegistryRoot();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
    }

    /**
     * Test of getInstanceProperties method, of class ServerInstanceRegistry.
     */
    @Test
    public void testGetInstanceProperties() {
        System.out.println("getInstanceProperties");
        ServerInstanceRegistry instance = new ServerInstanceRegistry(serverInstancePath);
        //InstancePreferences expResult = null;
        InstancePreferences result = instance.getInstanceProperties();
        assertEquals(expResultPrefix + "/properties", result.getPreferences().absolutePath());
        
        System.out.println("getLocation before = " + result.getProperty("location"));
        System.out.println("abs = " + result.getPreferences().absolutePath());
        result.setProperty("location", "c:\\MyWebApps\\WebApp01");
        System.out.println("getLocation after = " + result.getProperty("location"));
        
    }

    /**
     * Test of getWebApplicationsRegistry method, of class ServerInstanceRegistry.
     */
    @Test
    public void testGetWebAppRegistry() {
        System.out.println("getWebAppRegistry");
        ServerInstanceRegistry instance = null;
        WebApplicationsRegistry expResult = null;
        WebApplicationsRegistry result = instance.getWebApplicationsRegistry();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
