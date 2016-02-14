package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.stream.Stream;

/**
 *
 * @author V. Shyshkin
 */
public class JavaPreferencesRegistry {

    private static final Logger LOG = Logger.getLogger(JavaPreferencesRegistry.class.getName());

    public static final String SERVER_INSTANCE_ID = "server-instance";

    private final Path serverInstance;

    private final String dir;
    private String suiteUID;

    private JavaPreferencesManager manager;

    public static final String UID = "uid-java-preferences";

    //private InstancePreferences instancePreferences;
    protected JavaPreferencesRegistry(Path serverInstance) {
        this.serverInstance = serverInstance;
        dir = serverInstance.toString().replace("\\", "/");
        suiteUID = "-java-preferences";
        manager = JavaPreferencesManager.newInstance();
    }

    protected JavaPreferencesRegistry(Path serverInstance, String suiteUID) {
        this(serverInstance);
        this.suiteUID = suiteUID;
    }

    protected JavaPreferencesRegistry(String suiteUID, String dir) {
        this.serverInstance = null;
        this.dir = dir;
        this.suiteUID = suiteUID;
        manager = JavaPreferencesManager.newInstance();

    }

    public static JavaPreferencesRegistry newInstance(Path instanceProject) {
        JavaPreferencesRegistry d = new JavaPreferencesRegistry(instanceProject);
        return d;
    }

    public static JavaPreferencesRegistry newInstance(String suiteUID, String dir) {
        return new JavaPreferencesRegistry(suiteUID, dir);
    }

    public static void update(String suiteUID, List<Path> projectPaths) {
        /*        if (projectPaths == null || projectPaths.isEmpty()) {
            return;
        }

        List<String> list = new ArrayList<>();

        JavaPreferencesRegistry r = newInstance(suiteUID, projectPaths.get(0).toString());
        //
        // Create list that contains namespaces which are legal and we keep them all
        //

        BaseUtil.out("================================");
        BaseUtil.out("       JavaPreferencesRegistry  Legal NameSpaces           ");
        BaseUtil.out("================================");

        projectPaths.forEach(p -> {
            list.add(r.getNamespace(p.toString()));
            BaseUtil.out(" --- " + r.getNamespace(p.toString()));
        });


        List<String> toRemove = null;
        try {
            toRemove = manager.getEntries(suiteUID, list);
        } catch (BackingStoreException ex) {
            BaseUtil.out("JavaPreferencesRegistry  EXCEPTION getInstance(String uid, String dir) try getEntries = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);

        }
        BaseUtil.out("================================");
        BaseUtil.out("       JavaPreferencesRegistry  To Remove NameSpaces           ");
        BaseUtil.out("================================");

        for (String c : toRemove) {
            BaseUtil.out(" ---  toRemove  = " + c);
        }

        try {
            for (String ns : toRemove) {
                pm.remove(ns);
            }
        } catch (BackingStoreException ex) {
            BaseUtil.out("JavaPreferencesRegistry  EXCEPTION getInstance(String uid, String dir) try remove = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
        }
         */
    }

    public JavaPreferencesRegistry remove() {
        try {
            manager.remove(this.getNamespace());
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return this;
    }

    /**
     *
     * 1) "uid" 2) uid for ServerSuite 3) "/" separator 3) the project directory
     * with replaced ":" with "_".and "\" with "/" characters
     *
     * @return
     */
    protected String getNamespace() {
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

        String namespace = this.getNamespace();

//        BaseUtil.out("0) JavaPreferencesRegistry id = " + id);
//        BaseUtil.out("0.1) JavaPreferencesRegistry nameSpace = " + namespace);
        InstancePreferences ip = manager.getProperties(namespace, id);
        if (ip == null) {
            instancePreferences = manager.createProperties(namespace, id);
//            BaseUtil.out("1) JavaPreferencesRegistry ip = " + ip);

        } else {
//            BaseUtil.out("2) JavaPreferencesRegistry ip = " + ip);
            instancePreferences = ip;
        }

        return instancePreferences;
    }

    public JavaPreferencesRegistry putProperty(String propName, String value) {
        getProperties("server-instance").putString(propName, value);
        return this;
    }

    public void forEachProperty(BiConsumer<String, String> action) {
        forEachProperty("server-nstance", action);
    }

    public void forEachProperty(String forId, BiConsumer<String, String> action) {
        InstancePreferences ip = getProperties(forId);
        String[] keys = ip.keys();
        for (String key : keys) {
            action.accept(key, ip.getString(key, null));
        }

    }

    public Map<String, String> filter(String forId, BiPredicate<String, String> predicate) {
        InstancePreferences ip = getProperties(forId);
        Map<String, String> map = new HashMap<>();
        String[] keys = ip.keys();
        for (String key : keys) {
            String value = ip.getString(key, null);
            if (predicate.test(key, value)) {
                map.put(key, value);
            }
        }

        return map;
    }

    public JavaPreferencesRegistry putProperties(Properties props, String... forId) {
        if (props == null || props.isEmpty() || forId == null) {
            return this;
        }
        
        String[] ids = forId.length == 0 ? new String[]{SERVER_INSTANCE_ID} : forId;
        for (String id : ids) {
            InstancePreferences ip = getProperties(id);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String nm = (String) en.nextElement();
                ip.putString(nm, props.getProperty(nm));
            }
        }
        return this;
    }

    public JavaPreferencesRegistry putProperties(Map<String, String> map, String... forId) {
        if (map == null || map.isEmpty() || forId == null) {
            return this;
        }
        String[] ids = forId.length == 0 ? new String[]{SERVER_INSTANCE_ID} : forId;
        for (String id : ids) {
            InstancePreferences ip = getProperties(id);
            map.forEach((k, v) -> {
                ip.putString(k, v);
            });
        }
        return this;

    }

    public JavaPreferencesRegistry removeKeys(List<String> keysToRemove, String... forId) {
        String[] ids = forId.length == 0 ? new String[]{SERVER_INSTANCE_ID} : forId;
        for (String id : ids) {
            InstancePreferences ip = getProperties(id);
            String[] keys = ip.keys();
            for (String key : keys) {
                ip.removeKey(key);
            }
        }
        return this;
    }

    public JavaPreferencesRegistry fillProperties(Properties props, String... forId) {
        if (props == null || forId == null) {
            return this;
        }

        String[] ids = forId.length == 0 ? new String[]{SERVER_INSTANCE_ID} : forId;
        for (String id : ids) {
            InstancePreferences ip = getProperties(id);
            String[] keys = ip.keys();

            for (String key : keys) {
                props.put(key, ip.getString(key, null));
            }
        }

        return this;
    }

    public JavaPreferencesRegistry fillProperties(Map<String, String> map, String... forId) {
        if (map == null || forId == null) {
            return this;
        }

        String[] ids = forId.length == 0 ? new String[]{SERVER_INSTANCE_ID} : forId;
        for (String id : ids) {
            InstancePreferences ip = getProperties(id);
            String[] keys = ip.keys();

            for (String key : keys) {
                map.put(key, ip.getString(key, null));
            }
        }
        return this;
    }

    public String getProperty(String propName) {
        return getProperties("server-instance").getString(propName, null);
    }

    public void getEntries(List<String> legalEntries) throws BackingStoreException {
        manager.getEntries(JavaPreferencesRegistry.UID, legalEntries);
    }

    public Stream<String> stream(String... id) {
        InstancePreferences ip = null;
        System.out.println("STREAM length = " + id.length);
        if (id.length == 0) {
            ip = getProperties("server-instance");
        } else {
            ip = getProperties(id[0]);
        }
        List<String> list = Arrays.asList(ip.keys());
        return list.stream();
    }

    public Stream<Entry<String, String>> mapEntryStream() {
        InstancePreferences ip = getProperties("server-instance");
        Map<String, String> map = new HashMap<>();
        fillProperties(map);

        return map.entrySet().stream();
    }

    public Stream<String[]> arrayStream() {
        InstancePreferences ip = getProperties("server-instance");
        Map<String, String> map = new HashMap<>();
        fillProperties(map);
        List<String[]> list = new ArrayList<>();
        map.forEach((k, v) -> {
            list.add(new String[]{k, v});
        });
        return list.stream();
    }

}
