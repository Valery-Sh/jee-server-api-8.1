/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;

/**
 * This class acts as a manager of the properties. It manages the set of
 * properties and they persistence.
 * <p>
 * Single InstanceProperties instance created by the manager usually serves to
 * persist properties of single server instance. By definition many
 * InstanceProperties can be created in the same namespace. <i>For common use
 * case client module will use one namespace with several
 * InstanceProperties.</i>
 * <p>
 * The <code>namespace</code> used in both non-static methods is just the
 * symbolic name for the InstanceProperties logically connected (like instances
 * of the same server type for example) and retrievable by calling
 * {@literal getProperties(String)} respectively.
 * <p>
 * Typical use case:
 * <p>
 *
 * <pre>
 *
 *    // we have some instance to persist
 *    InstancePropertiesManager manager = InstancePropertiesManager.getInstance();
 *    InstanceProperties props1 = manager.createProperties("myspace");
 *    props1.put("property", "value");
 *
 *    // we want to persist yet another instance
 *    InstanceProperties props2 = manager.createProperties("myspace");
 *    props2.put("property", "value");
 *
 *    // we want to retrieve all InstanceProperties from "myspace"
 *    // the list will have two elements
 *    List&lt;InstanceProperties&gt; props = manager.getInstanceProperties("myspace");
 * </pre>
 * <p>
 * This class is <i>ThreadSafe</i>.
 *
 * @author V. Shyshkin
 */
public class JavaPreferencesManager extends PreferencesManager {

    private static final Logger LOG = Logger.getLogger(JavaPreferencesManager.class.getName());

    public final static String CONFIG_PREFERENCES = "config/Preferences";

    private static JavaPreferencesManager manager;

    private boolean javaPreferences;

    private JavaPreferencesManager() {
        super();
    }

    public boolean isJavaPreferences() {
        return javaPreferences;
    }

    /**
     * Returns the instance of the default manager.
     *
     * @return the instance of the default manager
     */
    public static synchronized JavaPreferencesManager newInstance() {
        return new JavaPreferencesManager();
    }

    @Override
    protected Preferences getPreferences() {
        Preferences prefs = AbstractPreferences.userRoot();
        prefs = prefs.node("org/netbeans/modules/jeeserver/base/Preferences");
        return prefs;
    }

}
