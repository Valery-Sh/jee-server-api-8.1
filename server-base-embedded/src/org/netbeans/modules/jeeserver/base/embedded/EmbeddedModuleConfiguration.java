package org.netbeans.modules.jeeserver.base.embedded;

import java.io.IOException;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.jeeserver.base.deployment.config.AbstractModuleConfiguration;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.ChildrenNotifier;
import org.netbeans.modules.jeeserver.base.embedded.webapp.DistributedWebAppManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author V. Shyshkin
 */
public abstract class EmbeddedModuleConfiguration extends AbstractModuleConfiguration {

    private static final Logger LOG = Logger.getLogger(EmbeddedModuleConfiguration.class.getName());

    protected FileChangeListener fileChangeListener;

    protected EmbeddedModuleConfiguration(J2eeModule module, String[] contextFilePaths) {
        super(module, contextFilePaths);
    }

    /**
     * The server calls this method when it is done using this
     * ModuleConfiguration instance.
     */
    @Override
    public void dispose() {
        cout("EmbeddedModuleConfiguration DISPOSE ");
        // notifyAvailableModule(serverInstanceId, true);
        //
        // Ebedded servers
        //
        notifyDistributedWebAppChange(serverInstanceId, true); // false means old assigned server                
    }

    /*    @Override
    protected void notifyCreate() {
        BaseUtil.out("EmbeddedModuleConfiguration notifyCreate module=" + this.getJ2eeModule().getUrl());
        
        notifyAvailableModule(serverInstanceId, false); // false means new assigned server
        //
        // Ebedded servers
        //
        notifyDistributedWebAppChange(serverInstanceId, false); // false means new assigned server        
    }
     */
    @Override
    protected void notifyServerChange(String oldInstanceId, String newInstanceId) {
        BaseUtil.out("==== EmbeddedModuleConfiguration notifyServerChange");
        BaseUtil.out(" --- oldInstanceId=" + oldInstanceId);
        BaseUtil.out(" --- newInstanceId=" + newInstanceId);        
        BaseUtil.out("========================================================");        
        
        
        //
        // Ebedded servers
        //
        notifyDistributedWebAppChange(oldInstanceId, true); // true means old assigned server        
        notifyDistributedWebAppChange(newInstanceId, false); // false means new assigned server        

    }

    protected void notifyDistributedWebAppChange(String serverInstanceId, boolean dispose) {
        cout("EmbeddedModuleConfiguration notifyDistributedWebAppChange dispose=" + dispose + "; serverInstanceId = " + serverInstanceId);
        cout(" ---- dispose=" + dispose);        
        cout(" ---- webProject=" + webProject);        
        cout(" ---- isEmbeddedServer=" + SuiteManager.isEmbeddedServer(serverInstanceId));                
        if (serverInstanceId == null || webProject == null || !SuiteManager.isEmbeddedServer(serverInstanceId)) {
            return;
        }
        DistributedWebAppManager distManager = DistributedWebAppManager
                .getInstance(SuiteManager
                        .getManager(serverInstanceId)
                        .getServerProject());
        cout(" ---- distManager.isRegistered=" + distManager.isRegistered(webProject));                
        
        if (dispose && distManager.isRegistered(webProject)) {
            //
            // old server
            //
            cout("EmbeddedModuleConfig notifyDistributedWebAppChange before Unregister");
            distManager.unregister(webProject);
            cout("EmbeddedModuleConfig notifyDistributedWebAppChange INVOKE removeListeners() ");            
            removeListeners();

        } else if (!dispose && !distManager.isRegistered(webProject)) {
            //
            // new server
            //
            distManager.register(webProject);
            addListeners();
            cout("EmbeddedModuleConfig notifyDistributedWebAppChange after register");
        }
        
        
    }

    void cout(String msg) {
        if (webProject == null) {
            BaseUtil.out("webProject = NULL " + msg);
            return;
        }
        String webName = webProject.getProjectDirectory().getNameExt();
        if (!webName.startsWith("WebApp0")) {
            return;
        }
        BaseUtil.out("== " + webName + "== . " + msg);

    }

    private static final String FILECHANGE_LISTENER_EMBEDDED = "WebAppDirectoryListene";

    @Override
    protected void addListeners() {

        cout("ADDLISTENERS webProject = " + webProject);
        cout("    --------  fileChangeListener = " + fileChangeListener);
        if ( fileChangeListener != null ) {
            webProject.getProjectDirectory().removeFileChangeListener(fileChangeListener);                    
        }
        fileChangeListener = new WebAppDirectoryListener(serverInstanceId);
        webProject.getProjectDirectory().addFileChangeListener(fileChangeListener);
    }

    
    
    protected void removeListeners() {
        cout("REMOVELISTENER  before removeListener  webProject = " + webProject);
        cout("    --------  fileChangeListener = " + fileChangeListener);

        if (fileChangeListener != null && webProject != null && webProject.getProjectDirectory() != null) {
            webProject.getProjectDirectory().removeFileChangeListener(fileChangeListener);

            cout(" ------ LISTENER REMOVED webProject = " + webProject);

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
            BaseUtil.out(" DELETE OR RENAME source = " + source.getNameExt()); 
            if (ProjectManager.getDefault().isProject(source)) {
                return;
            }
            
            source.removeFileChangeListener(this);
            
            Project instance = SuiteManager.getManager(serverInstanceId).getServerProject();
            if (instance == null) {
                return;
            }
            Node node = SuiteManager.findDistributedWebAppRootNode(instance);
            if (node == null) {
                return;
            }
            ChildrenNotifier cn = node.getLookup().lookup(ChildrenNotifier.class);
            if (cn == null) {
                return;
            }
            removeListeners();
            cn.childrenChanged();
            
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
}
