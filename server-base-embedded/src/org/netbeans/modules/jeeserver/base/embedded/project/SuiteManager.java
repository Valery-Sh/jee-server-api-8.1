package org.netbeans.modules.jeeserver.base.embedded.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.ServerUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.DistributedWebAppRootNode;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.InstanceNode;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.ServerInstancesRootNode;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNotifier;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author V. Shyshkin
 */
public class SuiteManager {

    private static final Logger LOG = Logger.getLogger(BaseUtil.class.getName());

    public static BaseDeploymentManager getManager(String uri) {
        if ( ! isEmbeddedServer(uri) ) {
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
    public static DistributedWebAppRootNode findDistributedWebAppRootNode(Project serverInstance) {
        InstanceNode node = findInstanceNode(serverInstance);
        if ( node == null ){
            return null;
        }
        return node.findDistributedWebAppRootNode();
    }    
    public static InstanceNode findInstanceNode(Project serverInstance) {
        ServerInstancesRootNode node = findInstancesRootNode(serverInstance);
        if ( node == null ) {
            return null;
        }
        return node.findInstanceNode(serverInstance);
    } 
    
    public static ServerInstancesRootNode findInstancesRootNode(Project serverInstance) {
        Project suite = getServerSuiteProject(serverInstance);
        if ( suite == null) {
            return null;
        }
        return ((ServerSuiteProject.Info)suite.getLookup().lookup(ProjectInformation.class))
                    .getInstancesRootNode();        
        
    }
    public static boolean isEmbeddedServer(String uri) {
        boolean b = false;
        if (uri == null) {
            return false;
        }
        DeploymentManager dm = getAnyTypeManager(uri);
        if (dm == null || ! (dm instanceof BaseDeploymentManager)) {
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
                if ( fo != null && BaseUtil.getOwnerProject(fo) != null ) {
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
    public synchronized static void removeInstance(String uri) {

        SuiteNotifier notif = SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNotifier.class);

        ServerUtil.removeInstanceProperties(uri);
        notif.instancesChanged();
    }

    public static FileObject getServerInstancesDir(String uri) {

        return SuiteManager.getServerSuiteProject(uri)
                .getProjectDirectory()
                .getFileObject(SuiteConstants.SERVER_INSTANCES_FOLDER);
    }

}
