package org.netbeans.modules.jeeserver.base.embedded.project;

import org.netbeans.modules.jeeserver.base.embedded.project.webmodule.DistributeModulesManager;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.ServerUtil;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryPreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.NbDirectoryPreferences;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.InstanceNode;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.ServerInstancesRootNode;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNodesNotifier;
import org.netbeans.modules.jeeserver.base.embedded.project.webmodule.AvailableModulesManager;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author V. Shyshkin
 */
public class SuiteManager {

    private static final Logger LOG = Logger.getLogger(BaseUtil.class.getName());

    public static BaseDeploymentManager getManager(String uri) {
        if (!isEmbeddedServer(uri)) {
            return null;
        }

        BaseDeploymentManager dm = null;
        try {
            dm = (BaseDeploymentManager) DeploymentFactoryManager.getInstance().getDisconnectedDeploymentManager(uri);
        } catch (DeploymentManagerCreationException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return dm;

    }

    public static DeploymentManager getAnyTypeManager(String uri) {
        DeploymentManager dm = null;
        try {
            dm = DeploymentFactoryManager.getInstance().getDisconnectedDeploymentManager(uri);
        } catch (DeploymentManagerCreationException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return dm;

    }

    /*    public static DistributedWebAppRootNode_OLD findDistributedWebAppRootNode(Project serverInstance) {
        InstanceNode node = findInstanceNode(serverInstance);
        if ( node == null ){
            return null;
        }
        return node.findDistributedWebAppRootNode();
    }    
     */
    public static InstanceNode findInstanceNode(Project serverInstance) {
        ServerInstancesRootNode node = findInstancesRootNode(serverInstance);
        if (node == null) {
            return null;
        }
        return node.findInstanceNode(serverInstance);
    }

    public static ServerInstancesRootNode findInstancesRootNode(Project serverInstance) {
        Project suite = getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }
        return ((ServerSuiteProject.Info) suite.getLookup().lookup(ProjectInformation.class))
                .getInstancesRootNode();

    }

    public static boolean isEmbeddedServer(String uri) {
        boolean b = false;
        if (uri == null) {
            return false;
        }
        DeploymentManager dm = getAnyTypeManager(uri);
        if (dm == null || !(dm instanceof BaseDeploymentManager)) {
            b = false;
        } else {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
            if (ip.getProperty(SuiteConstants.SUITE_PROJECT_LOCATION) != null) {
                b = true;
            }
        }
        return b;
    }

    public static BaseDeploymentManager getManager(Project serverInstance) {

        return BaseUtil.managerOf(serverInstance);
    }

    public synchronized static List<String> getLiveServerInstanceIds(FileObject suiteDir) {
        if (suiteDir == null) {
            return null;
        }
        Path suitePath = Paths.get(suiteDir.getPath());
        Deployment d = Deployment.getDefault();

        if (d == null || d.getServerInstanceIDs() == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (String uri : d.getServerInstanceIDs()) {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);

            String foundSuiteLocation = SuiteUtil.getSuiteProjectLocation(ip);
            if (foundSuiteLocation == null || !new File(foundSuiteLocation).exists()) {
                // May be not a native plugin server
                continue;
            }
            Project foundSuite = BaseUtil.getOwnerProject(FileUtil.toFileObject(new File(foundSuiteLocation)));

            if (foundSuite == null) {
                continue;
            }
            Path p = Paths.get(foundSuiteLocation);

            if (suitePath.equals(p)) {
                String instanceLocation = SuiteUtil.getServerLocation(ip);
                FileObject fo = FileUtil.toFileObject(new File(instanceLocation));
                if (fo != null && BaseUtil.getOwnerProject(fo) != null) {
                    result.add(uri);
                }
            }
        }
        return result;
    }

    public synchronized static List<String> getLiveServerInstanceIds(Project serverSuite) {

        //BaseDeploymentManager dm = null;
        if (serverSuite == null) {
            return null;
        }

        return getLiveServerInstanceIds(serverSuite.getProjectDirectory());
    }

    public synchronized static List<String> getServerInstanceIds(Project serverSuite) {
        if (serverSuite == null || serverSuite.getProjectDirectory() == null) {
            return null;
        }
        return getServerInstanceIds(serverSuite.getProjectDirectory());
    }

    public synchronized static List<String> getServerInstanceIds(FileObject suiteDir) {

        if (suiteDir == null) {
            return null;
        }
        Path suitePath = Paths.get(suiteDir.getPath());
        Deployment d = Deployment.getDefault();

        if (d == null || d.getServerInstanceIDs() == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (String uri : d.getServerInstanceIDs()) {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);

            String foundSuiteLocation = SuiteUtil.getSuiteProjectLocation(ip);
            if (foundSuiteLocation == null || !new File(foundSuiteLocation).exists()) {
                // May be not a native plugin server
                continue;
            }
            Project foundSuite = BaseUtil.getOwnerProject(FileUtil.toFileObject(new File(foundSuiteLocation)));

            if (foundSuite == null) {
                continue;
            }
            Path p = Paths.get(foundSuiteLocation);

            if (suitePath.equals(p)) {
                result.add(uri);
            }
        }
        return result;
    }

    public static Project getServerSuiteProject(String uri) {

        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        String suiteLocation;
        if (ip != null) {
            suiteLocation = SuiteUtil.getSuiteProjectLocation(ip);
        } else {
            // extract from url
            String s = SuiteConstants.SUITE_URL_ID; //":server:suite:project:";
            int i = uri.indexOf(s);
            suiteLocation = uri.substring(i + s.length());
            Path path = Paths.get(suiteLocation);
            suiteLocation = path.getParent().toString().replace("\\", "/");
        }
        if (suiteLocation == null || !new File(suiteLocation).exists()) {
            // May be not a native plugin server
            return null;
        }
        return BaseUtil.getOwnerProject(FileUtil.toFileObject(new File(suiteLocation)));
    }

    public static Project getServerSuiteProject(Project serverInstance) {
        return getServerSuiteProject(getManager(serverInstance).getUri());
    }

    public synchronized static void instanceDelete(String uri) {
        BaseUtil.out("SuiteManager.instanceDelete uri=" + uri);
        if (uri == null) {
            BaseUtil.out("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! URI == NULL ");
            BaseUtil.out("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! URI == NULL ");
            return;
        }
        SuiteNodesNotifier suiteNotifier = SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNodesNotifier.class);

        Properties props = ServerUtil.removeInstanceProperties(uri);

        //
        // Delete InstancePreferencies
        //
        String instancePath = SuiteUtil.extractInstancePath(uri);
        if (instancePath != null) {
            DirectoryPreferences registry = new DirectoryPreferences(Paths.get(instancePath));
            registry.removePropertiesRoot();
            Project suite = getServerSuiteProject(uri);
            if (suite != null) {
                suite.getLookup().lookup(DistributeModulesManager.class)
                        .remove(instancePath);
            }

//        DistributeModulesManager m = getInstance(uri);
        }

        suiteNotifier.instancesChanged(); // Can invoke this method too

    }

    /**
     * Invoked when server instance configurations have been changed during
     * customization.
     *
     * @param uri a server instance id as specified by the class
     * {@literal Deployment}.
     */
    public static synchronized void instanceSettingsChange(String uri) {
        //
        // BaseConstants.HTTP_PORT_PROP
        // BaseConstants.SHUTDOWN_PORT_PROP
        // BaseConstants.SHUTDOWN_PORT_PROP (if null then Integer.MAX_VALUE)
        //
        updateInstanceRegistry(ServerUtil.getProperties(uri));

    }

    public synchronized static void removeInstance(String uri) {
        SuiteNodesNotifier suiteNotifier = SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNodesNotifier.class);

        Properties instanceProperties = ServerUtil.removeInstanceProperties(uri);

        suiteNotifier.instancesChanged();

        saveInstanceProperties(instanceProperties);

        String instancePath = SuiteUtil.extractInstancePath(uri);
        if (instancePath == null) {
            return;
        }
        DirectoryPreferences registry = new DirectoryPreferences(Paths.get(instancePath));
        registry.removePropertiesRoot();
        Project suite = getServerSuiteProject(uri);
        if (suite != null) {
            suite.getLookup().lookup(DistributeModulesManager.class)
                    .remove(instancePath);
        }
    }

    public synchronized static void instanceCreate(String uri) {

        SuiteNodesNotifier suiteNotifier = SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNodesNotifier.class);

        suiteNotifier.instancesChanged();

        String displayname = InstanceProperties
                .getInstanceProperties(uri)
                .getProperty(BaseConstants.DISPLAY_NAME_PROP);

        if (displayname != null) {
            suiteNotifier.displayNameChange(uri, displayname);
        }
        Properties props = ServerUtil.getProperties(uri);
        updateInstanceRegistry(props);
    }

    protected static void updateInstanceRegistry(Properties props) {
        Path dirpath = Paths.get("d:\\Netbeans_810_Plugins\\TestApps\\AMEmbServer04");

        NbDirectoryPreferences ppp = new NbDirectoryPreferences(dirpath, "uid-025c0287-3243-4a16-837e-94be30ecfc19");
        int l = ppp.childrenNames().length;
        BaseUtil.out("--- 0000001 SuiteManager : updateInstanceRegistry I test NbDirectoryPreferences lengt=" + l);

        BaseUtil.out("--- 1 SuiteManager : updateInstanceRegistry(Properties)");

        String serverLocation = props.getProperty(BaseConstants.SERVER_LOCATION_PROP);
        BaseUtil.out("--- 2 SuiteManager : updateInstanceRegistry(Properties) serverLocation = " + serverLocation);

        InstancePreferences prefs = getInstanceProperties(serverLocation);

        props.forEach((k, v) -> {
            BaseUtil.out("--- 2 SuiteManager : updateInstanceRegistry(Properties) key = " + k + "; value = " + v);

            prefs.setProperty((String) k, (String) v);
        });

        String shutdownPort = props.getProperty(BaseConstants.SHUTDOWN_PORT_PROP);
        if (shutdownPort == null) { // Cannot be
            shutdownPort = String.valueOf(Integer.MAX_VALUE);
            prefs.setProperty(BaseConstants.SHUTDOWN_PORT_PROP, shutdownPort);
        }

    }

    protected static void saveInstanceProperties(Properties props) {
        BaseUtil.out("--- 1 SuiteManager : saveInstanceProperties()");
        String uri = props.getProperty(BaseConstants.URL_PROP);
        String projDir = props.getProperty(BaseConstants.SERVER_LOCATION_PROP);
        if (uri == null) {
            BaseUtil.out("--- 2 SuiteManager : saveInstanceProperties() uri == null");

            return;
        }
        BaseUtil.out("--- 3 SuiteManager : saveInstanceProperties() uri = " + uri);

        InstancePreferences prefs = getNbInstanceProperties(projDir, uri);
        props.forEach((k, v) -> {
            BaseUtil.out("--- 4 SuiteManager : saveInstanceProperties() key = " + k + "; value = " + v);

            prefs.setProperty((String) k, (String) v);
        });

    }

    public static FileObject getServerInstancesDir(String uri) {

        return SuiteManager.getServerSuiteProject(uri)
                .getProjectDirectory()
                .getFileObject(SuiteConstants.SERVER_INSTANCES_FOLDER);
    }

    public static InstancePreferences getNbInstanceProperties(String instanceDir, String uri) {
        BaseUtil.out("**** 1 SuiteRegistry.getNbInstancePreferences instanceDir=" + instanceDir);
        BaseUtil.out("**** 2 SuiteRegistry.getNbInstancePreferences uri=" + uri);
        Project suite = SuiteManager.getServerSuiteProject(uri);
        FileObject fo = FileUtil.toFileObject(new File(instanceDir));

        if (fo == null) {
            return null;
        }
        BaseUtil.out("**** 3 SuiteRegistry.getNbInstancePreferences fo.getPath()=" + fo.getPath());

        //FileObject suite = SuiteManager.getServerSuiteProject(serverInstance)
        String uid = SuiteUtil.getSuiteUID(suite.getProjectDirectory());

        BaseUtil.out("**** 4 SuiteRegistry.getNbInstancePreferences UID" + uid);

        if (uid == null) {
            return null;
        }
        BaseUtil.out("**** 5 NbSuitePreferences.getNbInstancePreferences UID" + uid);

        return NbSuitePreferences.newInstance(instanceDir, uid).getProperties();

    }

    public static InstancePreferences getInstanceProperties(String serverInstanceDir) {
        return new DirectoryPreferences(Paths.get(serverInstanceDir))
                .createProperties("properties");
    }

    public static ServerInstanceAvailableModules getDistributeModules(Project serverInstance) {

        Project suite = getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }
        DistributeModulesManager m = suite.getLookup().lookup(DistributeModulesManager.class);

        if (m == null) {
            return null;
        }
        return m.get(serverInstance);
    }

    public static ServerInstanceAvailableModules getDistributeModules(String uri) {
        Project suite = getServerSuiteProject(uri);
        if (suite == null) {
            return null;
        }
        DistributeModulesManager m = suite.getLookup().lookup(DistributeModulesManager.class);

//        DistributeModulesManager m = getInstance(uri);
        if (m == null) {
            return null;
        }
        String p = SuiteUtil.extractInstancePath(uri);
        if (p == null) {
            return null;
        }
        return m.get(p);
    }

    public static ServerInstanceAvailableModules getAvailableModules(Project serverInstance) {
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }

        AvailableModulesManager m = suite.getLookup().lookup(AvailableModulesManager.class);

        if (m == null) {
            return null;
        }
        return m.get(serverInstance);
    }
}
