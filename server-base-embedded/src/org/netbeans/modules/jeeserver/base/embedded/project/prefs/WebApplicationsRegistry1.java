package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.nio.file.Path;
import java.util.logging.Logger;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.ApplicationsRegistry;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;

/**
 *
 * @author Valery
 */
public class WebApplicationsRegistry1 extends ApplicationsRegistry {

    private static final Logger LOG = Logger.getLogger(WebApplicationsRegistry1.class.getName());

    public static final String WEB_APPS = "web-apps";
    public static final String CONTEXTPATH_PROP = "contextPath";

    public WebApplicationsRegistry1(Path serverInstancePath, String... rootExtentions) {
        super(serverInstancePath, rootExtentions);
    }


    public InstancePreferences findPropertiesByContextPath(String contextPath) {
        
        return super.findProperties(CONTEXTPATH_PROP, WEB_APPS);
    }


    @Override
    public String applicationsNodeName() {
        return WEB_APPS;
    }

}
