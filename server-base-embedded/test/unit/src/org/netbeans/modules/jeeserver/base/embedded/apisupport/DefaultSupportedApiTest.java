package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlDocument;

/**
 *
 * @author Valery
 */
public class DefaultSupportedApiTest {
    
    protected XmlDocument document;
    
    public DefaultSupportedApiTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        DefaultSupportedApiProvider provider = new DefaultSupportedApiProvider("org/netbeans/modules/jeeserver/base/embedded/resources/jetty-api-pom.xml");
        //provider.setApiDescriptorPath("org/netbeans/modules/jeeserver/base/embedded/resources/jetty-api-pom.xml");
        document = provider.getXmlDocument();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class DefaultSupportedApi.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String apiName = "standard";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        String expResult = "standard";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class DefaultSupportedApi.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        DefaultSupportedApi instance = null;
//        instance.setName(name);
    }

    /**
     * Test of getDisplayName method, of class DefaultSupportedApi.
     */
    @Test
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        String apiName = "standard";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        String expResult = "Base API";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDisplayName method, of class DefaultSupportedApi.
     */
    @Test
    public void testSetDisplayName() {
        System.out.println("setDisplayName");
        String displayName = "";
        DefaultSupportedApi instance = null;
//        instance.setDisplayName(displayName);
    }

    /**
     * Test of getDescription method, of class DefaultSupportedApi.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        String apiName = "standard";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        String expResult = "The API is required for normal work af any Embedded Server";
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDescription method, of class DefaultSupportedApi.
     */
    @Test
    public void testSetDescription() {
        System.out.println("setDescription");
        String description = "";
        DefaultSupportedApi instance = null;
//        instance.setDescription(description);
    }

    /**
     * Test of getXmlDocument method, of class DefaultSupportedApi.
     */
    @Test
    public void testGetXmlDocument() {
        System.out.println("getXmlDocument");
        DefaultSupportedApi instance = null;
        XmlDocument expResult = null;
//        XmlDocument result = instance.getXmlDocument();
//        assertEquals(expResult, result);
    }

    /**
     * Test of setXmlDocument method, of class DefaultSupportedApi.
     */
    @Test
    public void testSetXmlDocument() {
        System.out.println("setXmlDocument");
        XmlDocument xmlDocument = null;
        DefaultSupportedApi instance = null;
//        instance.setXmlDocument(xmlDocument);
    }

    /**
     * Test of isAlwaysRequired method, of class DefaultSupportedApi.
     */
    @Test
    public void testIsAlwaysRequired() {
        System.out.println("isAlwaysRequired");
        String apiName = "standard";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        
        boolean expResult = true;
        boolean result = instance.isAlwaysRequired();
        assertEquals(expResult, result);
    }
    /**
     * Test of isAlwaysRequired method, of class DefaultSupportedApi.
     * Check when no 'alwaysRequired' tag found. By default must be false
     */
    @Test
    public void testIsAlwaysRequired_1() {
        System.out.println("isAlwaysRequired");
        String apiName = "jsp-jndi-annotations";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        
        boolean expResult = false;
        boolean result = instance.isAlwaysRequired();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAlwaysRequired method, of class DefaultSupportedApi.
     */
    @Test
    public void testSetAlwaysRequired() {
        System.out.println("setAlwaysRequired");
        boolean alwaysRequired = false;
        DefaultSupportedApi instance = null;
//        instance.setAlwaysRequired(alwaysRequired);
    }

    /**
     * Test of getAPIVersions method, of class DefaultSupportedApi.
     * ApiVersions must be empty af the api="jsp-jndi-annotations" doesn't
     * have 'api-versions' tag
     */
    @Test
    public void testGetAPIVersions() {
        System.out.println("getAPIVersions");
        String apiName = "jsp-jndi-annotations";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        assertTrue(instance.getAPIVersions().isEmpty());
    }
    /**
     * Test of getAPIVersions method, of class DefaultSupportedApi.
     * ApiVersions for  apiName="jersey" contains to elements
     */
    @Test
    public void testGetAPIVersions_1() {
        System.out.println("getAPIVersions");
        String apiName = "jersey";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);
        SupportedApi.APIVersions versions = instance.getAPIVersions();
        assertEquals(2,versions.size());
        Map<String,String> displayNames = versions.getDisplayNames();
        assertEquals( "Jersey API Versions",displayNames.get("jersey.version"));
        assertEquals( "API used to create RESTful service resources",displayNames.get("javax.ws.rs.version"));        

        String[] propNames = versions.getPropertyNames();
        assertEquals(2,propNames.length);
        assertEquals( "jersey.version",propNames[0]);
        assertEquals( "javax.ws.rs.version",propNames[1]);
        
        Map<String,String[]> versionMap = versions.getVersions();
        assertEquals(2,versionMap.size());
        String[] v = versionMap.get("jersey.version");
        assertEquals(2,v.length);
        assertEquals("2.22.1",v[0]);
        assertEquals("2.21.1",v[1]);
        
        v = versionMap.get("javax.ws.rs.version");
        assertEquals(1,v.length);
        assertEquals("2.0.1",v[0]);
        //
        // 
        //
        Map<String,String> currentMap = versions.getCurrentVersions();
        String current = currentMap.get("jersey.version");
        assertEquals("2.22.1",current);
        current = currentMap.get("javax.ws.rs.version");
        assertEquals("2.0.1",current);     


    }

    /**
     * Test of getDependencies method, of class DefaultSupportedApi.
     */
    @Test
    public void testGetDependencies() {
        System.out.println("getDependencies");
        String apiName = "standard";
        DefaultSupportedApi instance = new DefaultSupportedApi(apiName, document);        
        List<ApiDependency>  result = instance.getDependencies();
        List<ApiDependency> expResult = new ArrayList<>();
        ApiDependency dep1 = new ApiDependency("org.netbeans.plugin.support.embedded", "jetty-9-embedded-command-manager", "${command.manager.version}", "jar");
        expResult.add(dep1);
        ApiDependency dep2 = new ApiDependency("org.eclipse.jetty.aggregate", "jetty-all", "${nb.server.version}", "pom");
        expResult.add(dep2);
        
        assertEquals(expResult, result);

        
//        List<ApiDependency> result = instance.getDependencies();
//        assertEquals(expResult, result);
    }
    
}
