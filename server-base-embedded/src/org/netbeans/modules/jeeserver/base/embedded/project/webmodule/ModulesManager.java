package org.netbeans.modules.jeeserver.base.embedded.project.webmodule;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Valery
 */
public interface ModulesManager {
    
    Map<Path,ServerInstanceAvailableModules> store();
    
    default void put(Path serverInstancePath, ServerInstanceAvailableModules modules ) {
        store().put(serverInstancePath, modules);
    }
    default void put(String serverInstancePath, ServerInstanceAvailableModules modules ) {
        store().put(Paths.get(serverInstancePath),modules);
    }
    default void put(Project serverInstance, ServerInstanceAvailableModules modules ) {
        store().put(Paths.get(serverInstance.getProjectDirectory().getPath()),modules);
    }
    
    default ServerInstanceAvailableModules get(Path serverInstancePath ) {
        ServerInstanceAvailableModules modules = store().get(serverInstancePath);
        if ( modules == null ) {
            Project p = BaseUtil.getOwnerProject(FileUtil.toFileObject(serverInstancePath.toFile()));
            modules = new ServerInstanceAvailableModules(p);
            store().put(serverInstancePath, modules);
        }
        return modules;
    }
    default ServerInstanceAvailableModules get(String serverInstancePath ) {
        return get(Paths.get(serverInstancePath));
    }
    default ServerInstanceAvailableModules get(Project serverInstance ) {
        return get(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
    default ServerInstanceAvailableModules remove(Path serverInstancePath ) {
        return store().remove(serverInstancePath);
    }
    default ServerInstanceAvailableModules remove(String serverInstancePath ) {
        return store().remove(Paths.get(serverInstancePath));
    }
    
    default ServerInstanceAvailableModules remove(Project serverInstance ) {
        return store().remove(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
        
    default int size() {
        return store().size();
    }
    default boolean isEmpty() {
        return store().isEmpty();
    }
    
    default boolean contains(Path serverInstancePath) {
        return store().containsKey(serverInstancePath);
    }
    default boolean contains(String serverInstancePath) {
        return store().containsKey(Paths.get(serverInstancePath));
    }
    default boolean contains(Project serverInstance) {
        return store().containsKey(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }
    
/*    public static Provider getProvider() {
        return new Provider();
    }
    public static class Provider {
        
        private final AvailableModulesManager availableModulesManger;
        
        protected Provider() {
            this.availableModulesManger = new AvailableModulesManager();
        }

    
    }
*/
}
