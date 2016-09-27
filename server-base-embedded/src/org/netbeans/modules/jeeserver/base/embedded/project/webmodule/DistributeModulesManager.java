package org.netbeans.modules.jeeserver.base.embedded.project.webmodule;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent;
import static org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent.DELETED;
import static org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent.DISPOSE;
import org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeListener;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class DistributeModulesManager implements ModulesManager {

    private final Map<Path, ServerInstanceAvailableModules> map = new HashMap<>();
    private final Map<Path, ServerInstanceAvailableModules> store = Collections.synchronizedMap(map);

    private final Map<Path, ModulesChangeListener> listenerMap = new HashMap<>();
    private final Map<Path, ModulesChangeListener> listeners = Collections.synchronizedMap(listenerMap);

    
    @Override
    public Map<Path, ServerInstanceAvailableModules> store() {
        return store;
    }

    @Override
    public ServerInstanceAvailableModules get(Path serverInstancePath) {
        ServerInstanceAvailableModules modules = store().get(serverInstancePath);
        if (modules == null) {
            Project p = BaseUtil.getOwnerProject(FileUtil.toFileObject(serverInstancePath.toFile()));
            modules = new ServerInstanceAvailableModules(p);
            listeners.put(serverInstancePath, new ModulesChangeHandler(serverInstancePath));
            store().put(serverInstancePath, modules);
        }
        
        return modules;
    }

    public static class ModulesChangeHandler implements ModulesChangeListener {

        private final Path server;
        private ModulesChangeEvent event;

        public ModulesChangeHandler(Path server) {
            this.server = server;
        }

        @Override
        public void availableModulesChanged(ModulesChangeEvent event) {
            this.event = event;
            if (event.getEventType() == DISPOSE || event.getEventType() == DELETED) {
                Project p = BaseUtil.getOwnerProject(FileUtil.toFileObject(server.toFile()));
                WebApplicationsManager distManager = WebApplicationsManager.getInstance(p);
                distManager.unregister(event.getTarget().getWebProjectPath());
            }
            //((WebModulesRootNode.RootChildrenKeys) node.getChildren()).addNotify();

        }
    }

/*    public static ServerInstanceAvailableModules getAvailableModules(Project serverInstance) {
        //AvailableDistModulesManager m = getInstance(serverInstance);
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }
        DistributeModulesManager m =  suite.getLookup().lookup(DistributeModulesManager.class);
        
        
        if (m == null) {
            return null;
        }
        return m.get(serverInstance);
    }
*/
/*    public static DistributeModulesManager getInstance(String uri) {
        Project suite = SuiteManager.getServerSuiteProject(uri);
        if (suite == null) {
            return null;
        }
        return suite.getLookup().lookup(DistributeModulesManager.class);
    }
*/    
    //private static Project suiteProject;
            
/*    public synchronized  static DistributeModulesManager getInstance(Project suite) {
        if (suite == null) {
            return null;
        }
        DistributeModulesManager mng =  suite.getLookup().lookup(DistributeModulesManager.class);
        if ( mng == null ) {
            mng  = new DistributeModulesManager();
        }
        return mng;
    }
*/    
/*    public static DistributeModulesManager getInstance(Project serverInstance) {
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }
        return suite.getLookup().lookup(DistributeModulesManager.class);
    }
*/
/*    public static ServerInstanceAvailableModules getAvailableModules(String uri) {
        Project suite = SuiteManager.getServerSuiteProject(uri);
        if (suite == null) {
            return null;
        }
        DistributeModulesManager m =  suite.getLookup().lookup(DistributeModulesManager.class);
        
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
*/
}
