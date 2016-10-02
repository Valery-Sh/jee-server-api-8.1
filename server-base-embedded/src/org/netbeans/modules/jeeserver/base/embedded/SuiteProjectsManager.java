package org.netbeans.modules.jeeserver.base.embedded;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.DirectoryRegistry;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.NbBasePreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.NbDirectoryPreferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.prefs.NbDirectoryRegistry;
import org.netbeans.modules.jeeserver.base.embedded.project.ServerSuiteProject.Info;
import org.netbeans.modules.jeeserver.base.embedded.project.ServerSuiteProjectFactory;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Valery
 */
@ServiceProvider(service = SuiteProjectsManager.class)
public class SuiteProjectsManager {

    private static final Logger LOG = Logger.getLogger(SuiteProjectsManager.class.getName());
    public static String SUITE_PROPERTIES = "nbconf/suite-properties";
    public static String LOCATION = "location";
    private static final String PREFERENCES = "Preferences";

    private final Map<Path, FileChangeListener> map = new HashMap<>();
    private final Map<Path, FileChangeListener> listeners = Collections.synchronizedMap(map);

    public SuiteProjectsManager() {
        BaseUtil.out("SuiteProjectsManager starting.");
        init();
    }

    /**
     * For test purpose.
     *
     * @param doInit if true then invokes init() method
     *
     */
    public SuiteProjectsManager(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    private void init() {
        refresh();
        TestNbBasePreferences test = new TestNbBasePreferences();
        try {
            test.startTest();
            test.startTest01();
            test.startTest02();
        } catch (BackingStoreException ex) {
            BaseUtil.out("SuiteProjectsManager startTest. EXCEPTION " + ex.getMessage());
        }
    }

    /**
     * @return a map of listeners. Each entry in the map corresponds to a single
     * instance of suite project.
     */
    public Map<Path, FileChangeListener> getListeners() {
        return listeners;
    }

    protected InstancePreferences getProperties(Preferences prefs, String id) {

        InstancePreferences ip = null;
        try {
            synchronized (this) {
                //String[] cn = prefs.childrenNames();
                Preferences child = prefs.node(id);
                child.parent().flush();
                ip = new InstancePreferences(id, child);
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers getProperties() EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
        return ip;

    }

    /**
     * Returns an instance of type null     {@link org.netbeans.modules.jeeserver.base.deployment.utils.prefs.InstancePreferences ) 
     * for a given suite unique identifier.
     * The {@code uid} parameter string may or may not start with a {@code "uid-" }.
     * If it does then the method converts the parameter value by applying
     * {@code uid.substring(4) } and the use the converted result
     *
     * @param uid a string value that specifies a suite unique identifier
     * @return an instance of type {@literal InstancePreferences }.
     */
    public InstancePreferences getProperties(String uid) {
        String suiteUid = uid;
        if (uid.startsWith("uid-")) {
            suiteUid = uid.substring(4);
        }

        return getProperties(suiteNode(suiteUid), SUITE_PROPERTIES);
    }

    /**
     * Returns a preferences node that serves as a root node for all other
     * nodes, Just invokes:
     * <pre>
     *  return NbPreferences.forModule(EmbJ2eePlatformFactory.class);
     * </pre>
     *
     * @return
     */
    protected Preferences rootNode() {
        return NbPreferences.forModule(BaseDeploymentManager.class);
    }

    /**
     * Returns an instance of type {@link Preferences }.for a given suite unique
     * identifier as a {@code mamespace).
     * The {@code uid} parameter string may or may not start with a {@code "uid-" }.
     * If it doesn't then the method  adds a prefix {@code uid- } to the string
     * and then uses the converted result.
     *
     * @param uid a string value that specifies a suite unique identifier
     * @return an instance of type {@code InstancePreferences}.
     */
    public Preferences suiteNode(String uid) {
        String suiteUid = uid;
        if (!uid.startsWith("uid-")) {
            suiteUid = "uid-" + uid;
        }

        return rootNode().node(suiteUid);
    }

    public void register(Project suite) {

        Info info = suite.getLookup().lookup(Info.class);
        if (info == null) {
            BaseUtil.out("SuiteProjectManagers info() = null");
            return;
        }

        try {
            getProperties(info.getUid()).setProperty(LOCATION, suite.getProjectDirectory().getPath());
            Path p = Paths.get(suite.getProjectDirectory().getPath());
            if (listeners.get(p) == null) {
                FileChangeListener l = new FileChangeHandler(this);
                listeners.put(p, l);
                suite.getProjectDirectory().addFileChangeListener(l);
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers register(Project) = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }

    }

    /**
     * Removes a node that corresponds to the given suite project directory.
     *
     * @param suiteFo the suite project directory
     */
    public void suiteDelete(FileObject suiteFo) {

        try {
            synchronized (this) {
                findSuiteNode(suiteFo).removeNode();
                rootNode().flush();
                Path p = Paths.get(suiteFo.getPath());
                getListeners().remove(p);
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers suiteDelete(FileObject) EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }
    
    public void locationChange(String uid, String newLocation) {

        try {
            synchronized (this) {
                InstancePreferences ip = getProperties(uid);
                String oldLocation = ip.getProperty(LOCATION);
                if ( oldLocation != null ) {
                    getListeners().remove(Paths.get(oldLocation));
                    
                }
                ip.setProperty(LOCATION, newLocation);
                rootNode().flush();
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers suiteDelete(FileObject) EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }
    

    /**
     * Searches for a node of type {@code Preferences} for the given suite
     * project directory.
     *
     * @param suiteFo the suite projrct directory
     * @return an object of type Preferences or null if not found.
     */
    protected Preferences findSuiteNode(FileObject suiteFo) {
        Path suitePath = FileUtil.toFile(suiteFo).toPath();
        Preferences result = null;

        try {
            synchronized (this) {
                //String[] uids = rootNode().childrenNames();
                String[] uids = childrenNames();
                for (String uid : uids) {
                    String location = getProperties(uid).getProperty(LOCATION);
                    if (location == null) {
                        continue;
                    }
                    if (Paths.get(location).equals(suitePath)) {
                        result = suiteNode(uid);
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers suiteDelete(FileObject) EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
        return result;
    }

    /**
     * Searches for a node of type {@code Preferences} for the given suite
     * project directory.
     *
     * @param suiteFo the suite projrct directory
     * @return an object of type Preferences or null if not found.
     */
    protected Preferences findSuiteNode(String uid) {
        Preferences result = null;
        String suiteUid = uid.startsWith("uid-") ? uid : "uid-" + uid; 
        try {
            synchronized (this) {
                //String[] uids = rootNode().childrenNames();
                String[] uids = childrenNames();
                for (String childUid : uids) {
                    if ( childUid.equals(suiteUid)) {
                        result = suiteNode(childUid);
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers suiteDelete(FileObject) EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
        return result;
    }
    
    /**
     * Checks whether the specified project has already been registered. The
     * suite project considers to be registers If all of the conditions below
     * are satisfied:
     * <ul>
     * <li>
     * The root node of the given suite project exists. This means that there is
     * a name space which equals to the project's {@code uid}.
     * </li>
     * <li>
     * The property {@code location} is found and it's value is not null.
     * </li>
     * <li>
     * The file for the locations exists
     * </li>
     *
     * </ul>
     *
     * @param suite the suite project to check
     * @return true if the specified project has already been registered
     */
    public boolean isRegistered(Project suite) {

        boolean result = false;

        Info info = suite.getLookup().lookup(Info.class);
        if (info == null) {
            return false;
        }

        String suiteUid = "uid-" + info.getUid();

        try {
            synchronized (this) {
                //String[] uids = rootNode().childrenNames();
                String[] uids = childrenNames();

                if (uids == null || uids.length == 0) {
                    return false;
                }

                for (String uid : uids) {
                    String location = getProperties(uid).getProperty(LOCATION);
                    if (!uid.equals(suiteUid)) {
                        continue;
                    }
                    if (location != null && new File(location).exists()) {
                        result = true;
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers suiteDelete(FileObject) EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
        return result;
    }

    /**
     * Left as an example
     *
     * @param forPref for
     * @return an array
     */
    public String[] childrenNames_OLD(Preferences forPref) {

        FileObject configRoot = FileUtil.getConfigRoot().getFileObject("Preferences");
        String s = configRoot.getPath();

        List<String> names = new ArrayList<>();

        if (configRoot != null) {

            FileObject rootFo = configRoot.getFileObject(rootNode().absolutePath());
            Statistics.StopWatch sw = Statistics.getStopWatch(Statistics.CHILDREN_NAMES, true);
            try {
                Enumeration<? extends FileObject> en = rootFo.getChildren(false); // nut recussive
                while (en.hasMoreElements()) {
                    names.add(en.nextElement().getNameExt());
                }
            } finally {
                sw.stop();
            }
        }
        BaseUtil.out("SuiteProjectManagers childrenNames() length=" + names.size());
        return names.toArray(new String[names.size()]);
    }

    public String[] childrenNames() {
        return childrenNames(rootNode());
    }

    /**
     * Returns the names of the children of this preference node, relative to
     * the specified node. (The returned array will be of size zero if this node
     * has no children.)
     *
     * @param forPreferences the node whose children are checked
     * @return the names of the children of this preference node.
     */
    public String[] childrenNames(Preferences forPreferences) {

        FileObject config = FileUtil.getConfigRoot();
        if (config == null) {
            return new String[0];
        }
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(FileUtil.toFile(config)));
        File file = new File(fo.getPath(), PREFERENCES);

        List<String> names = new ArrayList<>();
        Path rootNodePath = Paths.get(file.getAbsolutePath(), forPreferences.absolutePath());
        File[] files = rootNodePath.toFile().listFiles();
        for (File f : files) {
            names.add(f.getName());
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Walks through children of the root node as specified by the method {@link #rootNode()
     * }
     * and for each child node checks whether it contains the property named
     * {@code location } and the value of the property points to an existing
     * file. If the condition above is not satisfied then the node removes.
     */
    public void refresh() {
        BaseUtil.out("000 SuiteProjectsManager netbeans.user=" + System.getProperty("netbeans.user"));

        try {
            synchronized (this) {
                
                BaseUtil.out("000 SuiteProjectsManager rootNode.childrenNames.length=" + rootNode().childrenNames().length);
                BaseUtil.out("000.01 SuiteProjectsManager rootNode.node(uid).childrenNames.length=" + rootNode().node("uid-99016ddf-8e35-45d1-8b37-2e96cd341268").childrenNames().length);
                
                String[] uids = childrenNames();
                for (String uid : uids) {
                    String location = getProperties(uid).getProperty(LOCATION);
                    if (location != null && new File(location).exists()) {
                        Path p = Paths.get(location);
                        if (listeners.get(p) == null) {
                            FileChangeListener l = new FileChangeHandler(this);
                            listeners.put(p, l);
                            FileUtil.toFileObject(p.toFile()).addFileChangeListener(l);
                        }
                    } else {
                        BaseUtil.out("001 SuiteProjectsManager rootNode.absPath=" + rootNode().absolutePath());
                        
                        BaseUtil.out("002 SuiteProjectsManager suiteUid=" + uid + "; exists=" + rootNode().nodeExists(uid));
                        BaseUtil.out("003 SuiteProjectsManager rootNode.childrenNames.length="  + rootNode().childrenNames().length);
                        BaseUtil.out("004 SuiteProjectsManager rootNode.node(uid).childrenNames.length=" + rootNode().node(uid).childrenNames().length);

                        InstancePreferences ip = getProperties(uid);
                        if (ip != null) {
                            ip.setProperty(LOCATION, "test");
                            ip.getPreferences().parent().flush();
                            suiteNode(uid).removeNode();
                            rootNode().flush();
                        }
                        if (location != null) {
                            Path p = Paths.get(location);
                            if (listeners.get(p) != null) {
                                FileChangeListener l = listeners.get(p);
                                listeners.remove(p);
                                FileUtil.toFileObject(p.toFile()).removeFileChangeListener(l);
                            }
                        }
                    }
                }
                rootNode().flush();
            }
        } catch (Exception ex) {
            BaseUtil.out("SuiteProjectManagers EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }

    }

    /**
     * The implementation of {@code FileChangeListener }. Used to listen the
     * events of suite project changes.
     */
    public static class FileChangeHandler implements FileChangeListener {

        SuiteProjectsManager registries;

        public FileChangeHandler(SuiteProjectsManager registries) {
            this.registries = registries;
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
        }

        @Override
        public void fileChanged(FileEvent fe) {
        }

        @Override
        public synchronized void fileDeleted(FileEvent fe) {
            FileObject suite = (FileObject) fe.getSource();
            if (suite.getFileObject(ServerSuiteProjectFactory.PROJECT_CONF) == null) {
                //nbconf deleted
                suite.removeFileChangeListener(this);
                registries.suiteDelete(suite);
            }
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {

        }

    }

    public static class TestNbBasePreferences {

        public static String ROOT_EXT = "c\\users\\ServersManager";
        public static String ROOT = "c\\root";

        public void startTest() throws BackingStoreException {
/*            NbBasePreferences prefs = new NbBasePreferences(ROOT, ROOT_EXT);
            
            BaseUtil.out("SuiteProjectsManager node nbconf/server-instance exosts=" + prefs.getProperties("nbconf/server-instance"));
            BaseUtil.out("SuiteProjectsManager node MyConf/my-instance-props exosts=" + prefs.getProperties("MyConf/my-instance-props"));

            BaseUtil.out("1 SuiteProjectsManager node nbconf/server-instance exosts=" + prefs.nodeExists("nbconf/server-instance"));
            BaseUtil.out("1 SuiteProjectsManager node MyConf/my-instance-props exosts=" + prefs.nodeExists("MyConf/my-instance-props"));

            BaseUtil.out("2 SuiteProjectsManager node c/users/ServersManager exosts=" + prefs.rootNode().nodeExists("c/root/c/users/ServersManager"));

            String[] s1 = prefs.rootNode().node("c/root/c/users").childrenNames();
            BaseUtil.out("2.1 SuiteProjectsManager node c/users childrenNames=" + s1.length);

            String[] s2 = prefs.rootNode().node("c/root/c/users/ServersManager").childrenNames();
            BaseUtil.out("2.1 SuiteProjectsManager node c/root/c/users/ServersManager=" + s2.length);
*/            
            NbDirectoryPreferences dirPrefs = new NbDirectoryPreferences(Paths.get("d:\\tttttt/mmmmm"));
            InstancePreferences ip = dirPrefs.getProperties("my-dir/my-properies");            
            BaseUtil.out("Nb 01 SuiteProjectsManager NbDirectoryPreferences ip =" + ip);
            
            dirPrefs.createProperties("my-dir/my-properies");
            ip = dirPrefs.getProperties("my-dir/my-properies");
            BaseUtil.out("Nb 01 SuiteProjectsManager NbDirectoryPreferences ip =" + ip);
            
            NbDirectoryRegistry dirReg = new NbDirectoryRegistry(Paths.get("d:\\tttttt/mmmmm"));
            ip = dirReg.getProperties("my-dir/my-properies");
            BaseUtil.out("NbReg 01 SuiteProjectsManager NbDirectoryRegistry ip =" + ip);
            
            DirectoryRegistry child01 = dirReg.children("d:/newDir");
            ip = child01.createProperties();
            BaseUtil.out("NbReg 01 SuiteProjectsManager NbDirectoryRegistry child01 create ip =" + ip);
            ip = child01.getProperties();
            BaseUtil.out("NbReg 01 SuiteProjectsManager NbDirectoryRegistry child01 get ip =" + ip);
            
            
            /*            InstancePreferences ip = prefs.createProperties("nbconf/server-instance");
            BaseUtil.out("SuiteProjectsManager hidden prop=" + ip.getProperty(InstancePreferences.HIDDEN_KEY));
            NbBasePreferences prefs1 = new NbBasePreferences(ROOT,ROOT_EXT);
            InstancePreferences ip1 = prefs.createProperties("MyConf/my-instance-props");
            BaseUtil.out("SuiteProjectsManager hidden prop=" + ip.getProperty(InstancePreferences.HIDDEN_KEY));
             */
//            ip.setProperty("TEST-KEY", "TEST-VALUE");
//            ip.getPreferences().parent().flush();
//            prefs.rootNode().flush();
        }

        public void startTest01() throws BackingStoreException {
/*            String ROOT_EXT1 = "c:\\new_users\\ServersManager";
            String ROOT1 = "c:\\new_root";
            NbBasePreferences instance = new NbBasePreferences(ROOT1, ROOT_EXT1);

            BaseUtil.out("0 startTest01: SuiteProjectsManager rootExtended().absPath=" + instance.rootExtended().absolutePath());
            BaseUtil.out("1 startTest01: SuiteProjectsManager childrenNames.length=" + instance.childrenNames().length);
            
            InstancePreferences ip = instance.createProperties("nbconf/server-properties", false);
            instance.rootExtended().node("nbconf").sync();
            ip.getPreferences().sync();
            
            String hiddenProp = ip.getProperty(InstancePreferences.HIDDEN_KEY);
            
            BaseUtil.out("2 startTest01: SuiteProjectsManager hiddenProp=" + hiddenProp);
            
            String[] result = instance.childrenNames();
            
            String[] expResult = new String[]{"nbconf"};
            
            BaseUtil.out("3 startTest01: SuiteProjectsManager childrenNames.length=" + result.length);
            NbBasePreferences instance1 = new NbBasePreferences(ROOT1, ROOT_EXT1);
            BaseUtil.out("4 startTest01: SuiteProjectsManager childrenNames.length=" + instance1.childrenNames().length);
*/
        }
        
        public void startTest02() throws BackingStoreException {
/*            String ROOT_EXT1 = "d:\\new_users02\\ServersManager";
            String ROOT1 = "d:\\new_root02";
            NbBasePreferences instance = new NbBasePreferences(ROOT1, ROOT_EXT1);
            
            BaseUtil.out("0000000 startTest02: SuiteProjectsManager rootNode('d_').nodeExists=" + instance.rootNode().nodeExists("d_"));

            BaseUtil.out("0 startTest02: SuiteProjectsManager rootExtended().absPath=" + instance.rootExtended().absolutePath());
            
            BaseUtil.out("1 startTest02: SuiteProjectsManager childrenNames.length=" + instance.childrenNames().length);
            
            InstancePreferences ip = instance.createProperties("nbconf/server-properties");
            
            BaseUtil.out("2 startTest02: SuiteProjectsManager ip=" + ip);
            
            String hiddenProp = ip.getProperty(InstancePreferences.HIDDEN_KEY);
            
            BaseUtil.out("3 startTest02: SuiteProjectsManager hiddenProp=" + hiddenProp);
            
            String[] result = instance.childrenNames();
            String[] fileNames = instance.childrenFileNames();
            
            for ( String s : fileNames ) {
                BaseUtil.out("------- startTest02: SuiteProjectsManager childrenFileNames : name=" + s);
            }
            
            String[] expResult = new String[]{"nbconf"};
            
            BaseUtil.out("4 startTest02: SuiteProjectsManager childrenNames.length=" + result.length);
*/
        }

    }
}
