package org.netbeans.modules.jeeserver.base.embedded.webapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Valery Shyshkin
 */
public interface ProjectStateRegistry {
    
    static final Logger LOG = Logger.getLogger(ProjectStateRegistry.class.getName());
    
    Project getProject();

    default boolean isRegistered(Project app) {
        List<FileObject> list = getWebAppFileObjects();
        if (list.contains(app.getProjectDirectory())) {
            return true;
        } else {
            return false;
        }
    }

    Path createRegistry();

    default FileObject copyFile(FileObject source) {
        FileObject result = null;
        Path target = createRegistry().resolve(source.getNameExt());
        if (Files.exists(target)) {
            result = FileUtil.toFileObject(target.toFile());
        } else {
            Path sourcePath = FileUtil.toFile(source).toPath();
            try {
                Path p = Files.copy(sourcePath, target);
                result = FileUtil.toFileObject(p.toFile());
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
        return result;
    }

    String getServerInstanceProperty(String name);

    void setServerInstanceProperty(String name, String value);
    
    void register(Project app);

    int unregister(Project webApp);
    
    FileObject getRegistry();
    
    Properties getWebAppsProperties();
    
    default List<FileObject> getWebAppFileObjects() {
        List<FileObject> list = new ArrayList<>();
        Properties props = getWebAppsProperties();
        props.forEach((k, v) -> {
            FileObject fo = FileUtil.toFileObject(new File((String) v));
            if (fo != null) {
                Project p = BaseUtil.getOwnerProject(fo);
                if (p != null) {
                    list.add(fo);
                }
            }
        });

        return list;
    }
    
    /**
     *
     */
    void refresh();

    static void refreshSuiteInstances(Project suite) {
        refreshSuiteInstances(suite.getProjectDirectory());
    }

    static void refreshSuiteInstances(FileObject suiteDir) {
        List<String> all = SuiteManager.getLiveServerInstanceIds(suiteDir);
        all.forEach(uri -> {
            DistributedWebAppManager dm = DistributedWebAppManager.getInstance(SuiteManager.getManager(uri).getServerProject());
            dm.refresh();
        });

    }
    
}
