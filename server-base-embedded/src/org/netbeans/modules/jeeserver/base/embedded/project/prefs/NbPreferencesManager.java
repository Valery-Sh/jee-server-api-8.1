/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.PreferencesManager;
import org.openide.util.NbPreferences;

/**
 *
 * @author V. Shyshkin
 */
public class NbPreferencesManager extends PreferencesManager {
    private static final Logger LOG = Logger.getLogger(NbPreferencesManager.class.getName());

    public final static String CONFIG_PREFERENCES = "config/Preferences";

    private static NbPreferencesManager manager;


    private NbPreferencesManager() {
        super();
    }

    /**
     * Returns the instance of the default manager.
     *
     * @return the instance of the default manager
     */
    public static synchronized NbPreferencesManager newInstance() {
        return new NbPreferencesManager();
    }

    @Override
    protected Preferences getPreferences() {
        return NbPreferences.forModule(NbPreferencesManager.class);

    }
}
