package org.netbeans.modules.jeeserver.base.embedded.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryPreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryRegistry;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryRegistry.DirectoryRegistryImpl;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;

/**
 *
 * @author Valery Shyshkin
 */
public class SuiteCommonSupport {

    private static final Logger LOG = Logger.getLogger(SuiteCommonSupport.class.getName());
    public static final String SUITES_ROOT_PREFIX = "suite-set-root";

    public static final String SUITE_PREFIX = "uid-";

    public static final String SUITES_ROOT = SUITES_ROOT_PREFIX + "-" + "6a25f920-092d-42e3-be8b-5ef8a40e96f0";
    //public static final String COMMON_SUITE_EXT = "instance-root-b4019bd9-8602-41c4-ad48-d72ef161cb3e";
    public static final String INSTANCE_PROPERTIES = "server-instance";
    public static final String SUITE_PROPERTIES = "nbconf/suite-properties";

    public static final String WEB_PROPERTIES = "web-context";
    public static final String WEB_APPS_DIR = "web-apps";

    private String DIRECTORY;

    private DirectoryRegistry suitesRoot;

    private SuiteRegistry suiteRegistry;
    private InstanceRegistry instanceRegistry;
    private WebAppsRegistry webappsRegistry;
    //private InstanceRegistry instanceDirectoryRegistry;

    protected SuiteCommonSupport(Path instancePath) {
        this(instancePath, null);
    }

    public SuiteCommonSupport(Path instancePath, String uid) {
        this(instancePath, uid, null);
    }
    private SuiteCommonSupport(Path instancePath, String uid,DirectoryRegistry suitesRoot ) {
        init(instancePath, uid, suitesRoot);
    }

    protected static SuiteCommonSupport getInstance(DirectoryRegistry suitesRoot, Path instancePath, String suitePath) {
        SuiteRegistry sdr = new SuiteRegistry(Paths.get(suitePath), suitesRoot);
        SuiteCommonSupport result = new SuiteCommonSupport(instancePath, sdr.getDelegate().directoryNamespace(), suitesRoot);
        return result;
    }

    public static SuiteCommonSupport getInstance(Path instancePath) {
        DirectoryRegistry suitesRoot = new DirectoryRegistryImpl(Paths.get(SUITES_ROOT));
        SuiteRegistry sdr = findSuite(instancePath, suitesRoot);
        SuiteCommonSupport result = new SuiteCommonSupport(instancePath, sdr.getDelegate().directoryNamespace());
        return result;
    }

    public static SuiteCommonSupport getInstance(Path instancePath, String suitePath) {
        DirectoryRegistry suitesRoot = new DirectoryRegistryImpl(Paths.get(SUITES_ROOT));
        SuiteRegistry sdr = new SuiteRegistry(Paths.get(suitePath), suitesRoot);
        SuiteCommonSupport result = new SuiteCommonSupport(instancePath, sdr.getDelegate().directoryNamespace());
        return result;
    }

    private void init(Path instancePath, String suiteName, DirectoryRegistry suitesRoot ) {
        //DirectoryPreferences d = new DirectoryPreferences(instancePath);
        DIRECTORY = new DirectoryPreferences(instancePath).directoryNamespace();
        this.suitesRoot = suitesRoot;
        if ( suitesRoot == null) {
            this.suitesRoot = new DirectoryRegistryImpl(Paths.get(SUITES_ROOT));
        }
        if (suiteName == null) {
            suiteRegistry = findSuite(instancePath, this.suitesRoot);
        } else {
            suiteRegistry = new SuiteRegistry(Paths.get(suiteName), this.suitesRoot);
        }

        instanceRegistry = suiteRegistry.instanceRegistry(instancePath);
        webappsRegistry = instanceRegistry.webappsRegistry();

    }

    public static SuiteRegistry findSuite(Path instancePath, DirectoryRegistry suitesRoot) {
        SuiteRegistry result = null;
        List<DirectoryRegistry> list = suitesRoot.childrens();
        for (DirectoryRegistry reg : list) {
            if (!filterSuiteName(reg.getDelegate().directoryNamespace())) {
                continue;
            }
            DirectoryRegistry dr = reg.children(instancePath.toString());
            if (dr.nodeExists()) {
                result = new SuiteRegistry(instancePath, suitesRoot);
                break;
            }
        }
        return result;
    }

    protected static boolean filterSuiteName(String source) {
        boolean result = true;
        if (!source.startsWith(SUITE_PREFIX)) {
            return false;
        }

        String str = source.substring(SUITE_PREFIX.length());
        String s = "0123456789abcdef-";
        for (char c : str.toCharArray()) {
            if (!s.contains(Character.toString(c))) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void init(Path instancePath) {
        DirectoryPreferences d = new DirectoryPreferences(instancePath);
        DIRECTORY = d.directoryNamespace();
    }

    public InstancePreferences getInstanceProperties() {
        return instanceRegistry.getProperties(INSTANCE_PROPERTIES);
    }

    public InstancePreferences getSuiteProperties() {
        return suiteRegistry.getProperties(SUITE_PROPERTIES);
    }

    public InstancePreferences createSuiteProperties() {
        return suiteRegistry.createProperties(SUITE_PROPERTIES);
    }

    public SuiteRegistry suiteRegistry() {
        return suiteRegistry;
    }

    public InstanceRegistry instanceRegistry() {
        return instanceRegistry;
    }
    public WebAppsRegistry webAppsRegistry() {
        return webappsRegistry;
    }

    public static class SuiteRegistry extends DirectoryRegistryImpl {

        public SuiteRegistry(Path suitePath) {
            super(suitePath);
        }

        public SuiteRegistry(Path suitePath, DirectoryRegistry suitesRoot) {
            super(suitePath, suitesRoot);
        }

        @Override
        protected DirectoryRegistry filterChildrens(Path path) {
            return new InstanceRegistry(path, this);
        }

        @Override
        protected String getPropertiesNamespace() {
            return SUITE_PROPERTIES;
        }

        public InstanceRegistry instanceRegistry(Path instancePath) {
            return new InstanceRegistry(instancePath, this);
        }
    }

    public static class InstanceRegistry extends DirectoryRegistryImpl {

        public InstanceRegistry(Path instancePath, SuiteRegistry parent) {
            super(instancePath, parent);
        }

        public SuiteRegistry suiteRegistry() {
            return (SuiteRegistry) super.parent();
        }

        public WebAppsRegistry webappsRegistry() {
            return new WebAppsRegistry(Paths.get(WEB_APPS_DIR), this);
        }

        @Override
        protected DirectoryRegistry filterChildrens(Path path) {
            if (!Paths.get(WEB_APPS_DIR).equals(path)) {
                return null;
            }
            return new WebAppsRegistry(path, this);
        }

        public WebApplicationRegistry addWebapp(Path path) {
            return webappsRegistry().addWebapp(path);
        }

        public WebApplicationRegistry removeWebapp(Path path) {
            return webappsRegistry().removeWebapp(path);
        }

        @Override
        protected String getPropertiesNamespace() {
            return INSTANCE_PROPERTIES;
        }

    }//class Instanceregistry

    public static class WebAppsRegistry extends DirectoryRegistryImpl {

        public WebAppsRegistry(Path webappsPath, InstanceRegistry parentInstance) {
            super(webappsPath, parentInstance);
        }

        public SuiteRegistry suiteRegistry() {
            return (SuiteRegistry) super.parent().parent();
        }

        public InstanceRegistry instanceRegistry() {
            return (InstanceRegistry) super.parent();
        }

        @Override
        protected DirectoryRegistry filterChildrens(Path path) {
            return new WebApplicationRegistry(path, this);
        }

        public WebApplicationRegistry findByContextPath(String cp) {
            WebApplicationRegistry result = null;
            List<WebApplicationRegistry> list = childrens();
            for (WebApplicationRegistry reg : list) {
                String value = reg.getAppLocation(cp);
                if (value != null) {
                    result = reg;
                    break;
                }
            }
            return result;
        }

        public WebApplicationRegistry webappsRegistry(Path webapp) {
            return new WebApplicationRegistry(webapp, this);
        }

        public String getAppLocationByContextPath(String cp) {
            WebApplicationRegistry reg = findByContextPath(cp);
            String result = null;
            if (reg != null) {
                result = reg.getAppLocation(cp);
            }
            return result;
        }

        public WebApplicationRegistry removeWebapp(Path webapp) {

            WebApplicationRegistry webReg = webappsRegistry(webapp);

            synchronized (this) {
                webReg.remove();
                return webReg;
            }
        }

        public WebApplicationRegistry addWebapp(Path webapp) {

            WebApplicationRegistry webReg = webappsRegistry(webapp);

            synchronized (this) {
                InstancePreferences ip = webReg.createProperties(LOCATION);
                String l = webReg.getDelegate().normalize(webapp.toString());
                ip.setProperty(LOCATION_PROPERTY_KEY, l);
                flush(ip.getPreferences());
                return webReg;
            }
        }
    }

    public static class WebApplicationRegistry extends DirectoryRegistryImpl {

        public WebApplicationRegistry(Path webappPath, WebAppsRegistry webapps) {
            super(webappPath, webapps);
        }

        public SuiteRegistry suiteRegistry() {
            return (SuiteRegistry) super.parent().parent().parent();
        }

        public InstanceRegistry instanceRegistry() {
            return (InstanceRegistry) parent().parent();
        }

        public String getAppLocation(String contextPath) {
            if (getProperties() == null) {
                return null;
            }
            return getProperties().getProperty("contextPath");
        }

        @Override
        protected String getPropertiesNamespace() {
            return WEB_PROPERTIES;
        }

    }

}
