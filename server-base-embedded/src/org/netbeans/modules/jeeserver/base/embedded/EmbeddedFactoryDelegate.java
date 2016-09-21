/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded;

import java.io.File;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.FactoryDelegate;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Valery
 */
public abstract class EmbeddedFactoryDelegate extends FactoryDelegate {

  
    public EmbeddedFactoryDelegate(String serverId) {
        super(serverId);
        //
        // Force instantiate the SuiteProjectsManager instance
        //
        Lookup.getDefault().lookup(SuiteProjectsManager.class);        
    }

    /**
     * Determine whether a server exists under the specified location.
     *
     * @param instanceFO an absolute path of the server project directory
     * @return {@literal true } if the server project exists. {@literal false}
     * otherwise
     */
    @Override
    protected boolean existsServer(FileObject instanceFO) {
        String serverLocation = (String) instanceFO.getAttribute(BaseConstants.SERVER_LOCATION_PROP);
        if (serverLocation == null) {
            return false;
        }

        File f = new File(serverLocation);
        if (!f.exists()) {
            return false;
        }

        FileObject fo = FileUtil.toFileObject(f);

        Project p = BaseUtil.getOwnerProject(fo);
        if (p == null) {
            return false;
        }
        return true;
    }

    @Override
    protected void register(final BaseDeploymentManager dm) {

        FileObject fo = dm.getServerProjectDirectory();
        FileChangeListener listener = new FileChangeListener() {

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
            public synchronized void fileDeleted(FileEvent fe) {
                FileObject source = (FileObject) fe.getSource();

                if (!ProjectManager.getDefault().isProject(source)) {
                    String uri = dm.getUri();
                    Project suite = SuiteManager.getServerSuiteProject(dm.getUri());

                    source.removeFileChangeListener(this);
                    dm.getServerProjectDirectoryListeners().remove(this);
                    if (suite != null) {
                        SuiteManager.instanceDelete(uri);
                    }

                }

            }

            @Override
            public void fileRenamed(FileRenameEvent fe) {
            }

            @Override
            public void fileAttributeChanged(FileAttributeEvent fe) {

            }
        };

        fo.addFileChangeListener(listener);
        dm.getServerProjectDirectoryListeners().add(listener);
        //saveFileChangeListener(listener);
    }

}
