package org.netbeans.modules.jeeserver.base.embedded.apisupport;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlElement;
import org.netbeans.modules.jeeserver.base.deployment.xml.XmlRoot;
import org.netbeans.modules.jeeserver.base.deployment.xml.pom.PomDocument;

/**
 *
 * @author Valery
 */
public class DefaultSupportedApiProviderTest {
    @StaticResource
    protected static final String  descriptorPath = "org/netbeans/modules/jeeserver/base/embedded/resources/jetty-api-pom.xml";
    @StaticResource
    protected static final String  pomPath = "org/netbeans/modules/jeeserver/base/embedded/resources/jetty-pom.xml";
    
    public DefaultSupportedApiProviderTest() {
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
     * Test of getApiDescriptorPath method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetApiDescriptorPath() {
        System.out.println("getApiDescriptorPath");
        SupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        String expResult = descriptorPath;
        String result = instance.getApiDescriptorPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getApiList method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetApiList() {
        System.out.println("getApiList");
        SupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        XmlRoot root = instance.getXmlDocument().getRoot();
        List<XmlElement> xmlApiList = root.findElementsByPath("api-set/api");

        List<SupportedApi> expResult = new ArrayList<>();
        xmlApiList.forEach(el -> {
            String s = el.getAttributes().get("name");
            DefaultSupportedApi dsa = new DefaultSupportedApi(el.getAttributes().get("name"),instance.getXmlDocument() );
            expResult.add(dsa);
        });
        
        List<SupportedApi> result = instance.getApiList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDownloadPom method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetDownloadPom() {
        System.out.println("getDownloadPom");
        Object[] options = null;
        SupportedApiProvider instance = null;
        InputStream expResult = null;
//        InputStream result = instance.getDownloadPom(options);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getAPI method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetAPI() {
        System.out.println("getAPI");
        String apiName = "standard";
        DefaultSupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        SupportedApi expResult = new DefaultSupportedApi(apiName,instance.getXmlDocument());
        SupportedApi result = instance.getAPI(apiName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getServerVersionProperties method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetServerVersionProperties() {
        System.out.println("getServerVersionProperties");
        DefaultSupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        Map<String, String> expResult = new HashMap<>();
        expResult.put("nb.server.version", "9.3.6.v20151106");
        expResult.put("command.manager.version","[1.3.1,)");
        
        Map<String, String> result = instance.getServerVersionProperties();
        assertEquals(expResult, result);
    }

    /**
     * Test of getServerVersions method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testGetServerVersions() {
        System.out.println("getServerVersions");
        DefaultSupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        
        String[] expResult = new String[] {"9.3.7.v20160115", "9.3.6.v20151106"};
        String[] result = instance.getServerVersions();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testTemp() {
        System.out.println("TEMP");
        // SupportedApiProvider provider = SupportedApiProvider.newInstance(SuiteUtil.getActualServerId(instanceProject));
        Project[] ps = OpenProjects.getDefault().getOpenProjects();
        //AMEmbServer01
        //jetty-9-embedded
        assertTrue(ps.length == 0);
        //DefaultSupportedApiProvider instance = new DefaultSupportedApiProvider(descriptorPath);
        
//        String[] expResult = new String[] {"9.3.7.v20160115", "9.3.6.v20151106"};
//        String[] result = instance.getServerVersions();
//        assertArrayEquals(expResult, result);
    }
    /**
     * Test of updatePom() method, of class AddDependenciesAction.
     */
    @Test
    public void testUpdatePom() {
        System.out.println("updatePom");
        PomDocument pomDocument = new PomDocument(BaseUtil.getResourceAsStream(pomPath));
        DefaultSupportedApiProvider provider = new DefaultSupportedApiProvider(descriptorPath);
        String apiName = "standard";
        String serverVersion = "9.3.13.v2011706";
        DefaultSupportedApi api = new DefaultSupportedApi(apiName, provider.getXmlDocument() );
        provider.updatePom(serverVersion, api, pomDocument, null);
        
        pomDocument.save(Paths.get("d:/0temp"), "updated-pom.xml");
    }
    /**
     * Test of updatePom() method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testUpdatePom_1() {
        System.out.println("updatePom");
        PomDocument pomDocument = new PomDocument(BaseUtil.getResourceAsStream(pomPath));
        DefaultSupportedApiProvider provider = new DefaultSupportedApiProvider(descriptorPath);
        String apiName = "jsp-jndi-annotations";
        String serverVersion = "9.3.13.v2011706";
        DefaultSupportedApi api = new DefaultSupportedApi(apiName, provider.getXmlDocument() );
        provider.updatePom(serverVersion,api, pomDocument,null);
        pomDocument.save(Paths.get("d:/0temp"), "updated-pom-1.xml");
    }

    /**
     * Test of updatePom() method, of class DefaultSupportedApiProvider.
     */
    @Test
    public void testContextAction_updatePom_2() {
        System.out.println("updatePom");
        PomDocument pomDocument = new PomDocument(BaseUtil.getResourceAsStream(pomPath));
        DefaultSupportedApiProvider provider = new DefaultSupportedApiProvider(descriptorPath);
        String apiName = "jersey";
        String serverVersion = "9.3.14.v2011706";
        DefaultSupportedApi api = new DefaultSupportedApi(apiName, provider.getXmlDocument() );
        provider.updatePom(serverVersion,api, pomDocument,null);
        pomDocument.save(Paths.get("d:/0temp"), "updated-pom-2.xml");
    }
    
}
