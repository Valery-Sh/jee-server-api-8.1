package org.netbeans.modules.jeeserver.jetty.embedded;

import java.util.List;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.AbstractSupportedApiProvider;
import org.netbeans.modules.jeeserver.base.embedded.apisupport.SupportedApi;

/**
 *
 * @author V. Shyshkin
 */
public class JettySupportedApiProvider extends AbstractSupportedApiProvider{
    
    private final String actualServerId;
    
    
    
    public JettySupportedApiProvider(String actualServerId) {
        this.actualServerId = actualServerId;
    }

    @Override
    protected String[] getMasterLine() {
        return apiMasterLines;
    }

    @Override
    protected String[] getSource() {
        return source;
    }
    /**
     * First part - API name.
     * Second part - version property name
     * Third part - version property display name
     * Forth part = a comma separate list of versions
     */
    

    protected String[] apiVersionLines = new String[]{
        "jersey/jersey.version/Jersey API Versions/" +
        "2.22.1,2.21.1"
        ,
        "jersey/javax.ws.rs.version/API used to create RESTful service resources/" +
        "2.0.1"
        ,
        "angularBeans/angularBeans.version/AngularBeans API Versions/" +
        "1.0.2-RELEASE"
        ,
        
/*        "jersey/v1.version/V1 API Versions/" +
        "1.0.1,1.0.2,1.0.3,1.0.4"
        ,
        "jersey/v2.version/V2 API Versions/" +
        "2.0.1,2.0.2,2.0.3"
        ,
*/        
    };
    
    protected String[] apiMasterLines = new String[]{
        "base/Base API/The API is required for normal work af any Embedded Server",
        "jsp/Annotations, JSP, JNDI API/The API enables the use of annototionas and jsp pages in Web applications",
        "jsf-mojarra/Java ServerFaces/The API enables the use of jsf facelets or (and) jsp pages in Web applications",
        "cdi-weld/Context and Dependency Injection/The API enables the use of the Weld CDI for servlets",
        "jstl/Apache JSTL/Apache JSTL API",
        "jersey/JAX-RS Jersey API/The implementation of the JAX-RS API",
        "angularBeans/AngularBeans API/Enables the usage of AngularJS in Java EE through CDI",
    };

    protected String[] source = new String[]{
     
        //"maven:base://org.eclipse.jetty.aggregate/jetty-all/${nb.server.version}/type=jar,classifier=uber,scope=provided/jetty-all-${nb.server.version}.jar",
        "maven:base://org.eclipse.jetty.aggregate/jetty-all/${nb.server.version}/type=pom/jetty-all-${nb.server.version}.jar",        
        "maven:base://org.netbeans.plugin.support.embedded/jetty-9-embedded-command-manager/${command.manager.version}/jetty-9-embedded-command-manager-${command.manager.version}.jar",
        "maven:jsp://org.ow2.asm/asm-commons/5.0.1/asm-5.0.1.jar",
        "maven:jsp://javax.annotation/javax.annotation-api/1.2/javax.annotation-api-1.2.jar",
        "maven:jsp://org.eclipse.jetty.orbit/javax.mail.glassfish/1.4.1.v201005082020/javax.mail.glassfish-1.4.1.v201005082020.jar",
        "maven:jsp://javax.transaction/javax.transaction-api/1.2/javax.transaction-api-1.2.jar",
        "maven:jsp://org.eclipse.jetty/apache-jsp/${nb.server.version}/apache-jsp-${nb.server.version}",
        "maven:jsp://org.eclipse.jetty.orbit/org.eclipse.jdt.core/3.8.2.v20130121/org.eclipse.jdt.core-3.8.2.v20130121.jar",
        "maven:jsp://org.mortbay.jasper/apache-el/8.0.27/apache-el-8.0.27.jar",
        "maven:jsp://org.mortbay.jasper/apache-jsp/8.0.27/apache-jsp-8.0.27.jar",
        "maven:jsp://org.glassfish.web/el-impl/2.2/el-impl-2.2.jar",
        "maven:jsp://com.google.guava/guava/13.0.1/guava-13.0.1.jar",
        // -- ============== Apache JSTL =============== -->

        "maven:jstl://org.apache.taglibs/taglibs-standard-impl/1.2.5/taglibs-standard-impl-1.2.5.jar",
        "maven:jstl://org.apache.taglibs/taglibs-standard-spec/1.2.5/taglibs-standard-spec-1.2.5.jar",
        //-- ================== JSF-MOJARRA Support =================== -->

        "maven:jsf-mojarra://org.glassfish/javax.faces/2.2.11/javax.faces-2.2.11.jar",
        
        // -- ============== CDI-WELD Support =============== -->

        "maven:cdi-weld://com.google.guava/guava/13.0.1/guava-13.0.1.jar",
        "maven:cdi-weld://javax.enterprise/cdi-api/1.2/cdi-api-1.2.jar",
        "maven:cdi-weld://javax.inject/javax.inject/1/javax.inject-1.jar",
        "maven:cdi-weld://javax.interceptor/javax.interceptor-api/1.2/javax.interceptor-api-1.2.jar",
        "maven:cdi-weld://org.jboss.classfilewriter/jboss-classfilewriter/1.0.5.Final/jboss-classfilewriter-1.0.5.Final.jar",
        "maven:cdi-weld://org.jboss.logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar",
        "maven:cdi-weld://org.jboss.weld/weld-api/2.2.SP3/weld-api-2.2.SP3.jar",
        "maven:cdi-weld://org.jboss.weld/weld-core-impl/2.2.9.Final/weld-core-impl-2.2.9.Final.jar",
        "maven:cdi-weld://org.jboss.weld/weld-core-jsf/2.2.14.Final/weld-core-jsf-2.2.14.Final.jar",
        "maven:cdi-weld://org.jboss.weld.environment/weld-environment-common/2.2.9.Final/weld-environment-common-2.2.9.Final.jar",
        "maven:cdi-weld://org.jboss.weld.servlet/weld-servlet-core/2.2.9.Final/weld-servlet-core-2.2.9.Final.jar",
        "maven:cdi-weld://org.jboss.weld/weld-spi/2.2.SP3/weld-spi-2.2.SP3.jar",

        // -- ============== JAX-RS Jersey Support =============== -->

        "maven:jersey://javax.ws.rs/javax.ws.rs-api/${javax.ws.rs.version}/javax.ws.rs-api-${javax.ws.rs.version}.jar",
        "maven:jersey://org.glassfish.jersey.core/jersey-client/${jersey.version}/jersey-client-${jersey.version}.jar",
        "maven:jersey://org.glassfish.jersey.core/jersey-common/${jersey.version}/jersey-common-${jersey.version}.jar",
        "maven:jersey://org.glassfish.jersey.core/jersey-server/${jersey.version}/jersey-server-${jersey.version}.jar",
        "maven:jersey://org.glassfish.jersey.bundles.repackaged/jersey-guava/${jersey.version}/jersey-guava-${jersey.version}.jar",
        "maven:jersey://org.glassfish.jersey.containers/jersey-container-servlet/${jersey.version}/jersey-container-servlet-${jersey.version}.jar",
        "maven:jersey://org.glassfish.jersey.ext.cdi/jersey-cdi1x/${jersey.version}/jersey-cdi1x-${jersey.version}.jar",

        "maven:angularBeans://com.github.bessemHmidi/angularBeans/${angularBeans.version}/angularBeans-all-${angularBeans.version}",
        
/*        
        "maven:jersey://a1.b2.c3/jersey-v1/${v1.version}/jersey-v1-${v1.version}.jar",
        "maven:jersey://a1.b2.c3/jersey-v2/${v2.version}/jersey-v2-${v2.version}.jar",
*/
        
    };
    
    public String[] getServerVersions() {
        return new String[] {
            "9.3.7.v20160115",
            "9.3.6.v20151106",
            "9.3.5.v20151012",
            "9.3.3.v20150827",
            "9.3.2.v20150730",
            "9.3.1.v20150827",
            "9.3.0.v20150612",
        };
    }

    @Override
    protected String getCommandManagerVersion() {
        return "[1.3.1-SNAPSHOT,)";
    }

    @Override
    protected SupportedApi newApiInstance(String masterLine, List<String> apiLines, List<String> apiVersionLines) {
        JettySupportedApi api = new JettySupportedApi(masterLine,apiLines, apiVersionLines);
        
        return api;
    }

    @Override
    protected String[] getAPIVersionLines() {
        return apiVersionLines;
    }


}//class JettySupportedApiProvider
