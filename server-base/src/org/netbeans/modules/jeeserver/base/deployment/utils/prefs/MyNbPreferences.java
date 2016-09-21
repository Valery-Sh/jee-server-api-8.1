/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences.Provider;

/**
 *
 * @author Valery
 */
public final class MyNbPreferences {

    private static Provider PREFS_IMPL;

    private MyNbPreferences() {
    }

    /**
     * Returns user preference node . {@link Preferences#absolutePath} of such a
     * node depends whether class provided as a parameter was loaded as a part
     * of any module or not. If so, then absolute path corresponds to slashified
     * code name base of module. If not, then absolute path corresponds to
     * class's package.
     *
     * @param cls the class for which a user preference node is desired.
     * @return the user preference node
     */
    public static Preferences forModule(Class cls) {
        if (PREFS_IMPL == null) {
            PREFS_IMPL = getPreferencesProvider();
        }
        return PREFS_IMPL.preferencesForModule(cls);
    }

    /**
     * Returns the root preference node.
     *
     * @return the root preference node.
     */
    public static Preferences root() {
        if (PREFS_IMPL == null) {
            PREFS_IMPL = getPreferencesProvider();
        }
        return PREFS_IMPL.preferencesRoot();
    }

    private static Provider getPreferencesProvider() {
        Provider retval = Lookup.getDefault().lookup(Provider.class);
        if (retval != null) {
            BaseUtil.out("MyNbPreferences getPreferencesProvider retVal = " + retval.getClass().getName());
            System.out.println("MyNbPreferences getPreferencesProvider retVal = " + retval.getClass().getName());
        }

        if (retval == null) {
            BaseUtil.out("MyNbPreferences getPreferencesProvider retVal = NULL");
            System.out.println("MyNbPreferences getPreferencesProvider retVal = NULL");
            retval = new Provider() {
                public Preferences preferencesForModule(Class cls) {
                    return Preferences.userNodeForPackage(cls);
                }

                public Preferences preferencesRoot() {
                    return Preferences.userRoot();
                }
            };
            // Avoided warning in case it is set 
            //(e.g. from NbTestCase - org.netbeans.junit.internal.MemoryPreferencesFactory).
            String prefsFactory = System.getProperty("java.util.prefs.PreferencesFactory");//NOI18N
            BaseUtil.out("MyNbPreferences prefsFactory = " + prefsFactory);
            System.out.println("MyNbPreferences prefsFactory = " + prefsFactory);
        }
        String prefsFactory = System.getProperty("java.util.prefs.PreferencesFactory");//NOI18N
        BaseUtil.out("MyNbPreferences prefsFactory = " + prefsFactory);

        return retval;
    }
}
