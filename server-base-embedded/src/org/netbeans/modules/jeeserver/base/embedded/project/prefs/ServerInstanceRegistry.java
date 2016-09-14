package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.nio.file.Path;
import java.util.logging.Logger;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.PathPreferencesRegistry;

/**
 *
 * @author Valery Shyshkin
 */
public class ServerInstanceRegistry extends PathPreferencesRegistry {
    
    private static final Logger LOG = Logger.getLogger(PathPreferencesRegistry.class.getName());
    
    public static final String WEB_APPS_ID = "web-apps";
    //private String serverInstanceDirectory;
    

    public ServerInstanceRegistry(Path path) {
        super(path);
    }
    
    public InstancePreferences getInstanceProperties() {
        return createProperties("properties");
    }
    public WebApplicationsRegistry getWebApplicationsRegistry() {
        //this.setDirectoryPath(webappPath);
        return new WebApplicationsRegistry(getDirectoryPath());
    }
    
}
