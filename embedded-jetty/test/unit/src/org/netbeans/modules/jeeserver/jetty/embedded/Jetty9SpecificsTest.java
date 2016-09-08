/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.jetty.embedded;

import java.awt.Image;
import java.io.InputStream;
import java.util.Properties;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.specifics.InstanceBuilder;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApiProvider;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.xml.sax.InputSource;

/**
 *
 * @author Valery
 */
public class Jetty9SpecificsTest {
    
    public Jetty9SpecificsTest() {
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
     * Test of shutdownCommand method, of class Jetty9Specifics.
     */
    @Test
    public void testShutdownCommand() {
        System.out.println("shutdownCommand");
        BaseDeploymentManager dm = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        boolean expResult = false;
//        boolean result = instance.shutdownCommand(dm);
//        assertEquals(expResult, result);
    }

    /**
     * Test of execCommand method, of class Jetty9Specifics.
     */
    @Test
    public void testExecCommand() {
        System.out.println("execCommand");
        BaseDeploymentManager dm = null;
        String cmd = "";
        Jetty9Specifics instance = new Jetty9Specifics();
        String expResult = "";
        String result = instance.execCommand(dm, cmd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFindJSPServlet method, of class Jetty9Specifics.
     */
    @Test
    public void testGetFindJSPServlet() {
        System.out.println("getFindJSPServlet");
        DeploymentManager dm = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        FindJSPServlet expResult = null;
        FindJSPServlet result = instance.getFindJSPServlet(dm);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getServerImage method, of class Jetty9Specifics.
     */
    @Test
    public void testGetServerImage() {
        System.out.println("getServerImage");
        Project serverProject = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        Image expResult = null;
        Image result = instance.getServerImage(serverProject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of needsShutdownPort method, of class Jetty9Specifics.
     */
    @Test
    public void testNeedsShutdownPort() {
        System.out.println("needsShutdownPort");
        Jetty9Specifics instance = new Jetty9Specifics();
        boolean expResult = false;
        boolean result = instance.needsShutdownPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDefaultPort method, of class Jetty9Specifics.
     */
    @Test
    public void testGetDefaultPort() {
        System.out.println("getDefaultPort");
        Jetty9Specifics instance = new Jetty9Specifics();
        int expResult = 0;
        int result = instance.getDefaultPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDefaultDebugPort method, of class Jetty9Specifics.
     */
    @Test
    public void testGetDefaultDebugPort() {
        System.out.println("getDefaultDebugPort");
        Jetty9Specifics instance = new Jetty9Specifics();
        int expResult = 0;
        int result = instance.getDefaultDebugPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDefaultShutdownPort method, of class Jetty9Specifics.
     */
    @Test
    public void testGetDefaultShutdownPort() {
        System.out.println("getDefaultShutdownPort");
        Jetty9Specifics instance = new Jetty9Specifics();
        int expResult = 0;
        int result = instance.getDefaultShutdownPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of supportsDistributeAs method, of class Jetty9Specifics.
     */
    @Test
    public void testSupportsDistributeAs() {
        System.out.println("supportsDistributeAs");
        SuiteConstants.DistributeAs distributeAs = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        boolean expResult = false;
        boolean result = instance.supportsDistributeAs(distributeAs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContextProperties method, of class Jetty9Specifics.
     */
    @Test
    public void testGetContextProperties_FileObject() {
        System.out.println("getContextProperties");
        FileObject config = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        Properties expResult = null;
        Properties result = instance.getContextProperties(config);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContextProperties method, of class Jetty9Specifics.
     */
    @Test
    public void testGetContextProperties_InputSource() {
        System.out.println("getContextProperties");
        InputSource source = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        Properties expResult = null;
        Properties result = instance.getContextProperties(source);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceBuilder method, of class Jetty9Specifics.
     */
    @Test
    public void testGetInstanceBuilder() {
        System.out.println("getInstanceBuilder");
        Properties props = null;
        InstanceBuilder.Options options = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        InstanceBuilder expResult = null;
        InstanceBuilder result = instance.getInstanceBuilder(props, options);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


    /**
     * Test of getDescriptorResourcePath method, of class Jetty9Specifics.
     */
    @Test
    public void testGetDescriptorResourcePath() {
        System.out.println("getDescriptorResourcePath");
        Jetty9Specifics instance = new Jetty9Specifics();
        String expResult = "org/netbeans/modules/jeeserver/jetty/embedded/resources/jetty-api-pom.xml";
        String result = instance.getDescriptorResourcePath("jetty-9-embedded");
        assertEquals(expResult, result);
        InputStream is = BaseUtil.getResourceAsStream(result);
        assertNotNull(is);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of saveFileChangeListener method, of class Jetty9Specifics.
     */
    @Test
    public void testSaveFileChangeListener() {
        System.out.println("saveFileChangeListener");
        FileChangeListener l = null;
        Jetty9Specifics instance = new Jetty9Specifics();
    }

    /**
     * Test of deleteFileChangeListener method, of class Jetty9Specifics.
     */
    @Test
    public void testDeleteFileChangeListener() {
        System.out.println("deleteFileChangeListener");
        FileChangeListener l = null;
        Jetty9Specifics instance = new Jetty9Specifics();
    }

    /**
     * Test of hashCode method, of class Jetty9Specifics.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Jetty9Specifics instance = new Jetty9Specifics();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Jetty9Specifics.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        Jetty9Specifics instance = new Jetty9Specifics();
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
