package org.netbeans.modules.jeeserver.base.embedded.webapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNotifier;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author V. Shyshkn
 */
public class DistributedWebAppManager implements ProjectStateRegistry, FileChangeListener {

    private static final Logger LOG = Logger.getLogger(DistributedWebAppManager.class.getName());

    public static final int SUCCESS = 0;

    public static final int ALREADY_EXISTS = 2;

    public static final int CREATE_FOLDER_ERROR = 4;

    public static final int NOT_FOUND = 6;

    public static final int CONTEXTPATH_NOT_FOUND = 8;

    public static final int NOT_A_SUITE = 10;

    private final Project serverInstance;

    protected DistributedWebAppManager(Project serverInstance) {
        this.serverInstance = serverInstance;
    }

    public static DistributedWebAppManager getInstance(Project instanceProject) {
        DistributedWebAppManager d = new DistributedWebAppManager(instanceProject);
        return d;
    }

    @Override
    public Project getProject() {
        return serverInstance;
    }


    @Override
    public Path createRegistry() {

        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());

        String root = serverDir.getRoot().toString().replaceAll(":", "_");
        if (root.startsWith("/")) {
            root = root.substring(1);
        }
        Path targetPath = serverDir.getRoot().relativize(serverDir);
        String tmp = System.getProperty("java.io.tmpdir");

        Path target = Paths.get(tmp, SuiteConstants.TMP_DIST_WEB_APPS, root, targetPath.toString());

        File file = target.toFile();
        if (!file.exists()) {
            try {
                FileUtil.createFolder(file);
            } catch (IOException ex) {
//                result = CREATE_FOLDER_ERROR;
                target = null;
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
        return target;

    }


    public String getServerInstanceProperty(String name) {
        Properties props = new Properties();
        Path target = createRegistry();
        if (target == null) {
            return null;
        }
        FileObject propsFo = FileUtil.toFileObject(target.toFile()).getFileObject("server-instance.properties");

        if (propsFo != null) {
            props = BaseUtil.loadProperties(propsFo);
        }
        return props.getProperty(name);
    }

    public void setServerInstanceProperty(String name, String value) {
        Properties props = new Properties();
        Path target = createRegistry();
        if (target == null) {
            return;
        }
        FileObject propsFo = FileUtil.toFileObject(target.toFile()).getFileObject(SuiteConstants.SERVER_INSTANCE_PROPERTIES_FILE);

        if (propsFo != null) {
            props = BaseUtil.loadProperties(propsFo);
            try {
                propsFo.delete();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
        props.setProperty(name, value);
        BaseUtil.storeProperties(props, FileUtil.toFileObject(target.toFile()), SuiteConstants.SERVER_INSTANCE_PROPERTIES_FILE);

    }

    public void register(Project webApp) {
        int result = SUCCESS;

        Path target = createRegistry();

        if (target == null) {
            return;
        }
        FileObject propsFo = FileUtil.toFileObject(target.toFile()).getFileObject(SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
        Properties props = new Properties();
        if (propsFo != null) {
            props = BaseUtil.loadProperties(propsFo);
            try {
                propsFo.delete();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }

        WebModule wm = WebModule.getWebModule(webApp.getProjectDirectory());

        String cp = wm.getContextPath();

        if (cp != null) {
            props.setProperty(cp, webApp.getProjectDirectory().getPath());
        } else {
            result = CONTEXTPATH_NOT_FOUND;
        }
        if (result == SUCCESS) {
            BaseUtil.storeProperties(props, FileUtil.toFileObject(target.toFile()), SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
            String uri = SuiteManager.getManager(serverInstance).getUri();
            SuiteNotifier sn = SuiteManager.getServerSuiteProject(uri).getLookup().lookup(SuiteNotifier.class);
            sn.childrenChanged(this, webApp);
        }
    }

    public int unregister(Project webApp) {
        int result = SUCCESS;
        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());
        String root = serverDir.getRoot().toString().replaceAll(":", "_");
        if (root.startsWith("/")) {
            root = root.substring(1);
        }
        Path targetPath = serverDir.getRoot().relativize(serverDir);
        String tmp = System.getProperty("java.io.tmpdir");

        Path target = Paths.get(tmp, SuiteConstants.TMP_DIST_WEB_APPS, root, targetPath.toString());

        File file = target.toFile();
        if (!file.exists()) {
            try {
                FileUtil.createFolder(file);
            } catch (IOException ex) {
                result = CREATE_FOLDER_ERROR;
                LOG.log(Level.INFO, ex.getMessage());
            }
        }

        FileObject propsFo = FileUtil.toFileObject(target.toFile()).getFileObject(SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
        Properties props = new Properties();
        if (propsFo != null) {
            props = BaseUtil.loadProperties(propsFo);
            try {
                propsFo.delete();
            } catch (IOException ex) {
                result = CREATE_FOLDER_ERROR;
                LOG.log(Level.INFO, ex.getMessage());
            }
        }

        WebModule wm = WebModule.getWebModule(webApp.getProjectDirectory());

        String cp = wm.getContextPath();

        if (cp != null) {
            props.remove(cp);
            FileObject targetFo = FileUtil.toFileObject(target.toFile());
        } else {
            result = CONTEXTPATH_NOT_FOUND;
        }
        if (result == SUCCESS) {
            BaseUtil.storeProperties(props, FileUtil.toFileObject(target.toFile()), SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
            String uri = SuiteManager.getManager(serverInstance).getUri();
            Project suite = SuiteManager.getServerSuiteProject(uri);
            if (suite == null) {
                result = NOT_A_SUITE;
            } else {
                SuiteNotifier sn = suite.getLookup().lookup(SuiteNotifier.class);
                sn.childrenChanged(this, webApp);
            }
        }
        return result;

    }

    public FileObject getRegistry() {
        Path serverDir = Paths.get(serverInstance.getProjectDirectory().getPath());
        String root = serverDir.getRoot().toString().replaceAll(":", "_");
        if (root.startsWith("/")) {
            root = root.substring(1);
        }
        Path targetPath = serverDir.getRoot().relativize(serverDir);
        String tmp = System.getProperty("java.io.tmpdir");

        Path target = Paths.get(tmp, SuiteConstants.TMP_DIST_WEB_APPS, root, targetPath.toString());
        return FileUtil.toFileObject(target.toFile());
    }


    public Properties getWebAppsProperties() {
        Properties props = new Properties();
        FileObject target = getRegistry();
        if (target == null) {
            return props;
        }
        FileObject propsFo = target.getFileObject(SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
        if (propsFo == null) {
            return props;
        }
        props = BaseUtil.loadProperties(propsFo);
        return props;
    }

    /**
     *
     */
    public void refresh() {
        if (true) return;
        BaseUtil.out("DistributedWebAppManager REFRESH() SERVER INSTANCE  = " + serverInstance);
        Properties props = getWebAppsProperties();
        Properties updates = new Properties();

        final List<Object> toDeleteList = new ArrayList<>();
        Enumeration en = props.propertyNames();
        
        boolean cpUpdated = false;
        
        while (en.hasMoreElements()) {
            Object k = en.nextElement();
            Object v = props.get(k);
            updates.put(k, v);

            final FileObject fo = FileUtil.toFileObject(new File((String) v));

            Project p = BaseUtil.getOwnerProject(fo);
            WebModule wm = getWebModule(p);
            
            if (p == null || wm == null) {
                toDeleteList.add(k);
                continue;
            }

            J2eeModuleProvider provider = p.getLookup().lookup(J2eeModuleProvider.class);
            
            String uri = provider.getServerInstanceID();
            if (uri == null || ! uri.equals(SuiteManager.getManager(serverInstance).getUri())) {
                toDeleteList.add(k);
                continue;
            }

            String cp = wm.getContextPath();
            if (!cp.equals(k)) {
                updates.remove(k);
                updates.setProperty(cp, (String) v);
                cpUpdated = true;
            }
        }

//        BaseUtil.out(" 8) -- DistributedWebAppManager toDeleteList.size = " + toDeleteList.size());

        if (!toDeleteList.isEmpty()) {
            toDeleteList.forEach(d -> {
                updates.remove(d);
            });
        }
        updates.forEach((k,v) -> {
//            BaseUtil.out(" 10) -- DistributedWebAppManager updates k = " + k + "; v="+ v);
            
        });
        if (cpUpdated || !toDeleteList.isEmpty() ) {
//        BaseUtil.out(" 11) -- DistributedWebAppManager store updates");
//        BaseUtil.out(" 12) -- DistributedWebAppManager getRegistry() = " + getRegistry());
            
            BaseUtil.replaceProperties(updates, getRegistry(), SuiteConstants.SERVER_INSTANCE_WEB_APPS_PROPS);
        }
    }


    private WebModule getWebModule(Project p) {
        WebModule wm = null;
        if (p != null && p.getProjectDirectory() != null) {
            wm = WebModule.getWebModule(p.getProjectDirectory());
        }
        return wm;
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
    }

    @Override
    public void fileChanged(FileEvent fe) {
    }

    @Override
    public void fileDeleted(FileEvent fe) {
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
    }
}
