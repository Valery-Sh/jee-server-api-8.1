package org.netbeans.modules.jeeserver.base.embedded;

import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.jeeserver.base.deployment.config.AbstractModuleConfiguration;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.prefs.WebApplicationsManager;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.RequestProcessor;

/**
 *
 * @author V. Shyshkin
 */
public abstract class EmbeddedModuleConfiguration1 extends AbstractModuleConfiguration {

    private static final Logger LOG = Logger.getLogger(EmbeddedModuleConfiguration1.class.getName());

    protected FileChangeListener fileChangeListener;
    protected Project serverProject;

    protected EmbeddedModuleConfiguration1(J2eeModule module, String[] contextFilePaths) {
        super(module, contextFilePaths);

    }

    /**
     * The server calls this method when it is done using this
     * ModuleConfiguration instance.
     */
    @Override
    public void dispose() {
        //
        // Embedded servers
        //
        new DisposeNotifier(this, serverInstanceId);
    }

    @Override
    protected void notifyServerChange(String oldInstanceId, String newInstanceId) {

        BaseUtil.out("==== EmbeddedModuleConfiguration notifyServerChange");
        BaseUtil.out(" --- oldInstanceId=" + oldInstanceId);
        BaseUtil.out(" --- newInstanceId=" + newInstanceId);
        BaseUtil.out("========================================================");

        //
        // Embedded servers
        //
        new ServerChangeNotifier(this, oldInstanceId, newInstanceId).post();

    }

    @Override
    protected void addListeners() {
        if (fileChangeListener != null) {
            webProject.getProjectDirectory().removeFileChangeListener(fileChangeListener);
        }
        fileChangeListener = new WebAppDirectoryListener(serverInstanceId);
        webProject.getProjectDirectory().addFileChangeListener(fileChangeListener);
    }

    protected void removeListeners() {
        if (fileChangeListener != null && webProject != null && webProject.getProjectDirectory() != null) {
            webProject.getProjectDirectory().removeFileChangeListener(fileChangeListener);
        }
    }

    public class WebAppDirectoryListener implements FileChangeListener {

        private String serverInstanceId;

        public WebAppDirectoryListener(String serverInstanceId) {
            this.serverInstanceId = serverInstanceId;
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

        protected void deletedOrRenamed(FileObject source) {
            if (ProjectManager.getDefault().isProject(source)) {
                return;
            }
            source.removeFileChangeListener(this);

            Project instance = SuiteManager.getManager(serverInstanceId).getServerProject();

            if (instance == null) {
                return;
            }

/*            Node node = SuiteManager.findDistributedWebAppRootNode(instance);
            if (node == null) {
                return;
            }
            ChildrenNotifier cn = node.getLookup().lookup(ChildrenNotifier.class);

            if (cn == null) {
                return;
            }
*/
            removeListeners();
            WebApplicationsManager distManager = WebApplicationsManager
                    .getInstance(SuiteManager
                            .getManager(serverInstanceId)
                            .getServerProject());
            distManager.unregister(source.getPath());
        }

        @Override
        public synchronized void fileDeleted(FileEvent fe) {
            deletedOrRenamed((FileObject) fe.getSource());
        }

        @Override
        public void fileRenamed(FileRenameEvent fre) {
            deletedOrRenamed((FileObject) fre.getSource());
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fae) {
        }

    }

    public static class ServerChangeNotifier implements Runnable {

        protected static final RequestProcessor RP = new RequestProcessor(ServerChangeNotifier.class);
        //RP.post(this, 0, Thread.NORM_PRIORITY);

        private final EmbeddedModuleConfiguration1 conf;

        private final String newServerInstanceId;
        private final String oldServerInstanceId;

        public ServerChangeNotifier(EmbeddedModuleConfiguration1 conf, String oldServerInstanceId, String newServerInstanceId) {
            this.conf = conf;
            this.newServerInstanceId = newServerInstanceId;
            this.oldServerInstanceId = oldServerInstanceId;
        }

        public void post() {
            RP.post(this, 0, Thread.NORM_PRIORITY);
        }

        @Override
        public void run() {
            if (oldServerInstanceId != null && conf.webProject != null || !SuiteManager.isEmbeddedServer(newServerInstanceId)) {
                WebApplicationsManager distManager = WebApplicationsManager
                        .getInstance(SuiteManager
                                .getManager(oldServerInstanceId)
                                .getServerProject());
                if (distManager.isRegistered(conf.webProject)) {
                    //
                    // new server
                    //
                    distManager.unregister(conf.webProject);
                    conf.removeListeners();
                }
            }
            if (newServerInstanceId != null && conf.webProject != null || !SuiteManager.isEmbeddedServer(newServerInstanceId)) {
                Project server = conf.getServerProject(newServerInstanceId);
                if ( server == null || ! SuiteUtil.isEmbedded(server) ) {
                    //
                    // It's not an embedded server or Jetty Standalone Server
                    //
                    return;
                }
                WebApplicationsManager distManager = WebApplicationsManager
                        .getInstance(SuiteManager
                                .getManager(newServerInstanceId)
                                .getServerProject());
                if (!distManager.isRegistered(conf.webProject)) {
                    //
                    // new server
                    //
                    distManager.register(conf.webProject);
                    conf.addListeners();
                }
            }

        }

    }

    public static class DisposeNotifier implements Runnable {

        protected static final RequestProcessor RP = new RequestProcessor(ServerChangeNotifier.class);
        //RP.post(this, 0, Thread.NORM_PRIORITY);

        private final EmbeddedModuleConfiguration1 conf;

        private final String serverInstanceId;

        public DisposeNotifier(EmbeddedModuleConfiguration1 conf, String serverInstanceId) {
            this.conf = conf;
            this.serverInstanceId = serverInstanceId;
        }

        public void post() {
            RP.post(this, 0, Thread.NORM_PRIORITY);
        }

        @Override
        public void run() {
            if (serverInstanceId == null || conf.webProject == null || !SuiteManager.isEmbeddedServer(serverInstanceId)) {
                return;
            }

            WebApplicationsManager distManager = WebApplicationsManager
                    .getInstance(SuiteManager
                            .getManager(serverInstanceId)
                            .getServerProject());

            if (distManager.isRegistered(conf.webProject)) {
                //
                // Old serverInstanceId
                //
                distManager.unregister(conf.webProject);
                conf.removeListeners();

            }
        }
    }//DisposeNotifier

}
