package org.netbeans.modules.jeeserver.base.embedded.project.webmodule;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;

/**
 *
 * @author Valery Shyshkin
 */
public class AvailableModulesManager implements ModulesManager {
    
    private final Map<Path,ServerInstanceAvailableModules> map = new HashMap<>();
    private final Map<Path,ServerInstanceAvailableModules> store = Collections.synchronizedMap(map);

    @Override
    public Map<Path, ServerInstanceAvailableModules> store() {
        return store;
    }
/*    public static ServerInstanceAvailableModules getAvailableModules(Project serverInstance) {
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
*/    
/*    public static AvailableModulesManager getInstance(Project serverInstance) {
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        if (suite == null) {
            return null;
        }
        return suite.getLookup().lookup(AvailableModulesManager.class);
    }
*/    
    
}
