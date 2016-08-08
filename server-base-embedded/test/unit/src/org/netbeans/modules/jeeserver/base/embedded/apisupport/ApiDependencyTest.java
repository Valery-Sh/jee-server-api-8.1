/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Valery
 */
public class ApiDependencyTest {
    
    protected String jsonSampleStr;
    
    public ApiDependencyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        jsonSampleStr = createJsonStr();
    }
    
    @After
    public void tearDown() {
    }
    public String createLine() {
        String addons = createJsonStr();
        return "maven:base://org.netbeans.plugin.support.embedded/jetty-9-embedded-command-manager/${command.manager.version}/jetty-9-embedded-command-manager-${command.manager.version}.jar/addons=" + addons;        
    }    
    
    public String createJsonStr() {
        String jsonstr = 
                "{\"exclusions\":" +
                    "{" + 
                        "\"##\": \"comment for maven-2\"," +
                        "\"exclusion\":" +
                        "{\"groupId\":\"*\", " +
                        "\"artifactId\":\"*\" }" +
                    "}" +
                "}";
                
/*                 <!-- comment for maven-2 -->
                <exclusion>
                    <groupId>*</groupId>
                    <artifactId>*</artifactId>
                </exclusion>
                <!-- uncomment for maven-2 
                <exclusion>
                    <groupId>org.eclipse.jetty</groupId>
                    <artifactId>jetty-deploy</artifactId>
                </exclusion>
                -->
            </exclusions>                
  */      
        return jsonstr;
    }

    /**
     * Test of getInstance method, of class ApiDependency.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        String line = createLine();
        ApiDependency dep = ApiDependency.getInstance(line);
        Map map = dep.getOtherTags();
        map.forEach( (k,v) -> {
            String s1 = (String) k;
            String s2 = (String) v;
            String s3 = (String) v;
            
            
        });
        
/*        String mavenLine = "";
        ApiDependency expResult = null;
        ApiDependency result = ApiDependency.getInstance(mavenLine);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of getGroupId method, of class ApiDependency.
     */
    @Test
    public void testGetGroupId() {
        System.out.println("getGroupId");
/*        ApiDependency instance = null;
        String expResult = "";
        String result = instance.getGroupId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of getArtifacId method, of class ApiDependency.
     */
    @Test
    public void testGetArtifacId() {
        System.out.println("getArtifacId");
/*        ApiDependency instance = null;
        String expResult = "";
        String result = instance.getArtifacId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of getVersion method, of class ApiDependency.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
/*        ApiDependency instance = null;
        String expResult = "";
        String result = instance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of getOtherTags method, of class ApiDependency.
     */
    @Test
    public void testGetOtherTags() {
        System.out.println("getOtherTags");
/*        ApiDependency instance = null;
        Map<String, String> expResult = null;
        Map<String, String> result = instance.getOtherTags();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of testJsonToXml method, of class ApiDependency.
     */
    @Test
    public void testJsonToXml() {
        System.out.println("testJsonToXml");
        String jsonString = createJsonStr();
        List list = new ArrayList();
        
        ApiDependency.jsonToXml(jsonString, list);
        //int i = list.get(0).length();
        String a = "";
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAddonTags method, of class ApiDependency.
     */
    @Test
    public void testGetAddonTags() {
        System.out.println("getAddonTags");
/*        ApiDependency instance = null;
        String expResult = "";
        String result = instance.getAddonTags();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of getJarName method, of class ApiDependency.
     */
    @Test
    public void testGetJarName() {
        System.out.println("getJarName");
/*        ApiDependency instance = null;
        String expResult = "";
        String result = instance.getJarName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/        
    }

    /**
     * Test of toStringArray method, of class ApiDependency.
     */
    @Test
    public void testToStringArray() {
        System.out.println("toStringArray");
/*        ApiDependency instance = null;
        String[] expResult = null;
        String[] result = instance.toStringArray();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
*/
    }
    
}
