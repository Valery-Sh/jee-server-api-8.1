package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

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
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;

/**
 *
 * @author Valery
 */
public abstract class ApplicationsRegistry extends DirectoryPreferences {

    private static final Logger LOG = Logger.getLogger(ApplicationsRegistry.class.getName());

    public static final String LOCATION = "location";

    protected ApplicationsRegistry(Path directoryPath) {
        super(directoryPath);
    }

    protected ApplicationsRegistry(Path directoryPath, String... registryRootExtentions) {
        super(directoryPath, registryRootExtentions);
    }

    
    public abstract String applicationsNodeName();
    
    public Preferences applicationsRoot() {
        Preferences prefs =  directoryRoot();
        try {
            synchronized (this) {
                prefs = prefs.node(applicationsNodeName());
                prefs.flush();
                return prefs;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public int size() {
        Preferences rp = applicationsRoot();
        int sz = 0;
        try {
            sz = rp.childrenNames().length;
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return sz;

    }

    public boolean isEmpty() {
        return size() == 0;

    }

    public List<InstancePreferences> getAppPropertiesList() {
        List<InstancePreferences> list = new ArrayList<>();
        Preferences rp = applicationsRoot();
        try {
            String[] childs = rp.childrenNames();
            for (String s : childs) {
                list.add(getProperties(applicationsNodeName() + "/" + s));
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return list;
    }

    public String addApplication(Path app) {

        String uid = findAppNodeName(app);
        if (uid != null) {
            return uid;
        }
        uid = java.util.UUID.randomUUID().toString();
        InstancePreferences ip = createProperties(applicationsNodeName() + "/" + uid);
        System.out.println("add web app = " + ip.getPreferences().absolutePath());
        ip.setProperty(LOCATION, app.toString().replace("\\", "/"));
        return uid;
    }

    public String addApplication(Path app, Properties props) {

        assert props != null;

        String uid = findAppNodeName(app);
        if (uid != null) {
            return uid;
        }
        uid = java.util.UUID.randomUUID().toString();
        InstancePreferences ip = createProperties(applicationsNodeName() + "/" + uid);
        System.out.println("add web app = " + ip.getPreferences().absolutePath());
        ip.setProperty(LOCATION, app.toString().replace("\\", "/"));
        props.forEach((k, v) -> {
            ip.setProperty((String) k, (String) v);
        });
        return uid;
    }

    public void removeApplication(InstancePreferences appProps) {
        try {
            appProps.getPreferences().removeNode();
            applicationsRoot().flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
    }

    public String removeApplication(Path app) {
        BaseUtil.out("ApplicationsRegistry removeApplication app=" + app);

        String uid = findAppNodeName(app);
        BaseUtil.out("ApplicationsRegistry removeApplication AFTER findNodeName uid" + uid);
        
        if (uid == null) {
            return uid;
        }
        Preferences webRoot = applicationsRoot();
        try {
            BaseUtil.out("ApplicationsRegistry removeApplication webRoot.nodeExists(uid)" + webRoot.nodeExists(uid));
        } catch (BackingStoreException ex) {
            BaseUtil.out("ApplicationsRegistry removeApplication EXCEPTION webRoot.nodeExists(uid)");
        }
        
        try {
            webRoot.node(uid).removeNode();
            webRoot.flush();
        try {
            BaseUtil.out("1 ApplicationsRegistry removeApplication webRoot.nodeExists(uid)" + webRoot.nodeExists(uid));
        } catch (BackingStoreException ex) {
            BaseUtil.out("1 ApplicationsRegistry removeApplication EXCEPTION webRoot.nodeExists(uid)");
        }
            
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return uid;
    }

    public InstancePreferences findProperties(String appDir) {

        String appNodeName = findAppNodeName(Paths.get(appDir));
        if (appNodeName == null) {
            return null;
        }
        return getProperties(applicationsNodeName() + "/" + appNodeName);
    }

    public InstancePreferences findProperties(Path appDir) {
        return findProperties(appDir.toString());
    }

    public InstancePreferences findProperties(File appDir) {
        return findProperties(appDir.toPath());
    }

    public InstancePreferences findProperties(String key, String value) {

        String nodeName = null;
        Preferences rp = applicationsRoot();

        try {
            String[] childs = rp.childrenNames();
            for (String id : childs) {
                String cp = getProperties(applicationsNodeName() + "/" + id).getProperty(key);
                if (cp != null && value.equals(cp)) {
                    nodeName = id;
                    break;
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return getProperties(applicationsNodeName() + "/" + nodeName);
    }

    public String findAppNodeName(Path app) {
        String nodeName = null;
        Preferences rp = applicationsRoot();
        //System.out.println("find web app webAppsRoot = " + rp.absolutePath());
        try {
            String[] childs = rp.childrenNames();
            for (String id : childs) {
                String location = getProperties(applicationsNodeName() + "/" + id).getProperty(LOCATION);
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
