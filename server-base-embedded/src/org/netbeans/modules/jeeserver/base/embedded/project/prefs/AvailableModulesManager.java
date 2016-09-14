package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;

/**
 *
 * @author Valery Shyshkin
 */
public class AvailableModulesManager {
    
    private final Map<Path,ServerInstanceAvailableModules> map = new HashMap<>();
    private final Map<Path,ServerInstanceAvailableModules> store = Collections.synchronizedMap(map);

    public void put(Path serverInstancePath, ServerInstanceAvailableModules modules ) {
        store.put(serverInstancePath, modules);
    }
    public void put(String serverInstancePath, ServerInstanceAvailableModules modules ) {
        store.put(Paths.get(serverInstancePath),modules);
    }
    public void put(Project serverInstance, ServerInstanceAvailableModules modules ) {
        store.put(Paths.get(serverInstance.getProjectDirectory().getPath()),modules);
    }
    
    public ServerInstanceAvailableModules get(Path serverInstancePath ) {
        return store.get(serverInstancePath);
    }
    public ServerInstanceAvailableModules get(String serverInstancePath ) {
        return store.get(Paths.get(serverInstancePath));
    }
    public ServerInstanceAvailableModules get(Project serverInstance ) {
        return store.get(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
    public ServerInstanceAvailableModules remove(Path serverInstancePath ) {
        return store.remove(serverInstancePath);
    }
    public ServerInstanceAvailableModules remove(String serverInstancePath ) {
        return store.remove(Paths.get(serverInstancePath));
    }
    
    public ServerInstanceAvailableModules remove(Project serverInstance ) {
        return store.remove(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
        
    public int size() {
        return store.size();
    }
    public boolean isEmpty() {
        return store.isEmpty();
    }
    
    public boolean contains(Path serverInstancePath) {
        return store.containsKey(serverInstancePath);
    }
    public boolean contains(String serverInstancePath) {
        return store.containsKey(Paths.get(serverInstancePath));
    }
    public boolean contains(Project serverInstance) {
        return store.containsKey(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
    
}
