package org.netbeans.modules.jeeserver.base.embedded.project.prefs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.PathPreferencesRegistry;
import org.openide.util.Exceptions;
import sun.util.calendar.BaseCalendar;

/**
 *
 * @author Valery
 */
public class WebApplicationsRegistry extends PathPreferencesRegistry {

    private static final Logger LOG = Logger.getLogger(WebApplicationsRegistry.class.getName());

    public static final String LOCATION = "location";
    public static final String WEB_APPS = "web-apps";
    public static final String CONTEXTPATH_PROP = "contextPath";

    public WebApplicationsRegistry(Path serverInstancePath) {
        super(serverInstancePath);
    }

    public WebApplicationsRegistry(Path serverInstancePath, String... extNamespace) {
        super(serverInstancePath, extNamespace);
    }

    public Preferences webAppsRoot() {
        Preferences prefs = directoryPropertiesRoot();
        try {
            synchronized (this) {
                prefs = prefs.node(WEB_APPS);
                prefs.flush();
                return prefs;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
        //return registryRootExtended().node(WEB_APPS);
    }

    public List<InstancePreferences> getWebAppPropertiesList() {
        List<InstancePreferences> list = new ArrayList<>();
        Preferences rp = webAppsRoot();
        try {
            String[] childs = rp.childrenNames();
            for (String s : childs) {
                list.add(getProperties(WEB_APPS + "/" + s));
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return list;
    }

    public String addWebApplication(Path app) {

        String uid = findWebAppNodeName(app);
        if (uid != null) {
            return uid;
        }
        uid = java.util.UUID.randomUUID().toString();
        InstancePreferences ip = createProperties(WEB_APPS + "/" + uid);
        System.out.println("add web app = " + ip.getPreferences().absolutePath());
        ip.setProperty(LOCATION, app.toString().replace("\\", "/"));
        return uid;
    }

    public String addWebApplication(Path app, Properties props) {

        assert props != null;
        assert props.getProperty("contextPath") != null;

        String uid = findWebAppNodeName(app);
        if (uid != null) {
            return uid;
        }
        uid = java.util.UUID.randomUUID().toString();
        InstancePreferences ip = createProperties(WEB_APPS + "/" + uid);
        System.out.println("add web app = " + ip.getPreferences().absolutePath());
        ip.setProperty(LOCATION, app.toString().replace("\\", "/"));
        props.forEach((k, v) -> {
            ip.setProperty((String) k, (String) v);
        });
        return uid;
    }

    public void removeWebApplication(InstancePreferences webappProps) {
        try {
            webappProps.getPreferences().removeNode();
            webAppsRoot().flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
    }

    public String removeWebApplication(Path app) {

        String uid = findWebAppNodeName(app);
        if (uid == null) {
            return uid;
        }
        Preferences webRoot = webAppsRoot();
        try {
            webRoot.node(uid).removeNode();
            webRoot.flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return uid;
    }

    public InstancePreferences findProperties(String webappDir) {

        String webappNodeName = findWebAppNodeName(Paths.get(webappDir));
        if (webappNodeName == null) {
            return null;
        }
        return getProperties(WEB_APPS + "/" + webappNodeName);
    }

    public InstancePreferences findProperties(Path webappDir) {
        return findProperties(webappDir.toString());
    }

    public InstancePreferences findProperties(File webappDir) {
        return findProperties(webappDir.toPath());
    }

    public InstancePreferences findPropertiesByContextPath(String contextPath) {

        String nodeName = null;
        Preferences rp = webAppsRoot();

        try {
            String[] childs = rp.childrenNames();
            for (String id : childs) {
                String cp = getProperties(WEB_APPS + "/" + id).getProperty(CONTEXTPATH_PROP);
                if (cp != null && contextPath.equals(cp)) {
                    nodeName = id;
                    break;
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return getProperties(WEB_APPS + "/" + nodeName);
    }

    public String findWebAppNodeName(Path app) {
        String nodeName = null;
        Preferences rp = webAppsRoot();
        System.out.println("find web app webAppsRoot = " + rp.absolutePath());
        try {
            String[] childs = rp.childrenNames();
            for (String id : childs) {
                String location = getProperties(WEB_APPS + "/" + id).getProperty(LOCATION);
                if (location != null && app.equals(Paths.get(location))) {
                    nodeName = id;
                    break;
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }

        return nodeName;
    }

}
