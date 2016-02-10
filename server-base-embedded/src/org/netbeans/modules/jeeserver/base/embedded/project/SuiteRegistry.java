package org.netbeans.modules.jeeserver.base.embedded.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author V. Shyshkin
 */
public class SuiteRegistry {

    private static final Logger LOG = Logger.getLogger(SuiteRegistry.class.getName());

    private final Project serverInstance;

    private final String dir;
    private final String suiteUID;

    //private InstancePreferences instancePreferences;
    protected SuiteRegistry(Project serverInstance) {
        this.serverInstance = serverInstance;
        dir = serverInstance.getProjectDirectory().getPath();
        Project suite = SuiteManager.getServerSuiteProject(serverInstance);
        suiteUID = SuiteUtil.getSuiteUID(suite.getProjectDirectory());
    }

    protected SuiteRegistry(String suiteUID, String dir) {
        this.serverInstance = null;
        this.dir = dir;
        this.suiteUID = suiteUID;
    }

    public static SuiteRegistry getInstance(Project instanceProject) {
        SuiteRegistry d = new SuiteRegistry(instanceProject);
        //d.createRegistry();
        return d;
    }

    public static SuiteRegistry getInstance(String uid, String dir) {
        return new SuiteRegistry(uid, dir);
    }

    public static void update(String suiteUID, List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return;
        }

        List<String> list = new ArrayList<>();

        SuiteRegistry r = getInstance(suiteUID, projects.get(0).getProjectDirectory().getPath());
        //
        // Create list that contains namespaces which are legal and we keep them all
        //

        BaseUtil.out("================================");
        BaseUtil.out("         Legal NameSpaces           ");
        BaseUtil.out("================================");

        projects.forEach(p -> {
            list.add(r.getNamespace(p.getProjectDirectory().getPath()));
            BaseUtil.out(" --- " + r.getNamespace(p.getProjectDirectory().getPath()));
        });

        PreferencesManager pm = PreferencesManager.getInstance();

        List<String> toRemove = null;
        try {
            toRemove = pm.getEntries(suiteUID, list);
        } catch (BackingStoreException ex) {
            BaseUtil.out("SuiteRegistry  EXCEPTION getInstance(String uid, String dir) try getEntries = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);

        }
        BaseUtil.out("================================");
        BaseUtil.out("         To Remove NameSpaces           ");
        BaseUtil.out("================================");

        for (String c : toRemove) {
            BaseUtil.out(" ---  toRemove  = " + c);
        }

        try {
            for (String ns : toRemove) {
                pm.remove(ns);
            }
        } catch (BackingStoreException ex) {
            BaseUtil.out("SuiteRegistry  EXCEPTION getInstance(String uid, String dir) try remove = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
        }
    }

    public void remove() {
        try {
            PreferencesManager.getInstance().remove(SuiteRegistry.this.getNamespace());
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
    }

    /**
     *
     * 1) "uid" 2) uid for ServerSuite 3) "/" separator 3) the project directory
     * with replaced ":" with "_".and "\" with "/" characters
     *
     * @return
     */
    public String getNamespace() {
        return getNamespace(dir);
    }

    protected String getNamespace(String forDir) {
        Path serverDir = Paths.get(forDir);

        String root = serverDir.getRoot().toString().replaceAll(":", "_");
        if (root.startsWith("/")) {
            root = root.substring(1);
        }
        Path targetPath = serverDir.getRoot().relativize(serverDir);

        Path target = Paths.get(root, targetPath.toString());

        return "uid" + suiteUID + "/" + target.toString().replace("\\", "/");

    }

    protected InstancePreferences getProperties(String id) {
        InstancePreferences instancePreferences;

        String namespace = SuiteRegistry.this.getNamespace();

        PreferencesManager cm = PreferencesManager.getInstance();
        BaseUtil.out("0) SuiteRegistry nameSpace = " + namespace);

        InstancePreferences ip = cm.getProperties(namespace, id);
        if (ip == null) {
            instancePreferences = cm.createProperties(namespace, id);
            BaseUtil.out("1) SuiteRegistry ip = " + ip);

        } else {
            BaseUtil.out("2) SuiteRegistry ip = " + ip);
            instancePreferences = ip;
        }
        return instancePreferences;
    }

    public void putProperty(String propName, String value) {
        getProperties("server-instance").putString(propName, value);
        //instancePreferences.putString(propName, value);
    }

    public String getProperty(String propName) {
        return getProperties("server-instance").getString(propName, null);
        //return instancePreferences.getString(propName, null);
    }

    public String getDefaultPropertiesId() {
        return getProperties("server-instance").getId();
    }
}
