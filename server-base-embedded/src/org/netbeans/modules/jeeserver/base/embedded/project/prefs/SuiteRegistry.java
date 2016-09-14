/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.nio.file.Paths;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;

/**
 *
 * @author Valery
 */
public class SuiteRegistry {

    public static SuiteRegistry getDefault() {
        return new SuiteRegistry();
    }

    public static NbSuiteRegistry getNbSuiteRegistry(Project serverInstance) {
        return NbSuiteRegistry.newInstance(serverInstance);
    }
    public static AvailableModulesManager getAvailableModulesManager(Project serverInstance) {
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        if ( suite == null ) {
            return null;
        }
        return suite.getLookup().lookup(AvailableModulesManager.class);
    }
    public static ServerInstanceAvailableModules getAvailableModules(Project serverInstance) {
        AvailableModulesManager m = getAvailableModulesManager(serverInstance);
        if ( m == null ) {
            return null;
        }
        return m.get(serverInstance);
    }
    

    public static InstancePreferences getNbInstancePreferences(Project serverInstance) {
        return NbSuiteRegistry.newInstance(serverInstance).getProperties();
    }

    public static ServerInstanceRegistry getServerInstanceRegistry(Project serverInstance) {
        return new ServerInstanceRegistry(Paths.get(serverInstance.getProjectDirectory().getPath()));
    }

}
