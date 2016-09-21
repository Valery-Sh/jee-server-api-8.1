package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;

/**
 * This class allows applications to store and retrieve user preference and
 * configuration data. This data is stored persistently in the the directory {@code FileUtil.getConfigRoot()/Preferences
 * } as specified by NetBeans API.
 */
public class NbBasePreferences extends CommonPreferences {

    private static final Logger LOG = Logger.getLogger(NbBasePreferences.class.getName());

    private static final String PREFERENCES = "Preferences";

    public NbBasePreferences(String... rootExtentions) {
        this(BaseDeploymentManager.class, rootExtentions);
    }

    protected NbBasePreferences(Class clazz, String... rootExtentions) {
        this(NbPreferences.forModule(clazz), rootExtentions);
    }

    protected NbBasePreferences(Preferences rootNode, String... rootExtentions) {
        super(rootNode, convert(rootExtentions));
    }
    
    private static String[] convert(String... ext) {
        if ( ext == null || ext.length == 0 ) {
            return ext;
        }
        for ( int i =0; i < ext.length; i++ ) {
            ext[i] = ext[i].replace (":", "_");
        }
        return ext;
    }
    @Override
    protected void initProperties(Preferences prefs) {
        synchronized (this) {
            try {
                prefs.put(PreferencesProperties.HIDDEN_KEY, PreferencesProperties.HIDDEN_VALUE);
                prefs.parent().flush();
            } catch (BackingStoreException ex) {
                BaseUtil.out("StartProjectsManager initProperties(Preferences) EXCEPTION " + ex.getMessage());
                LOG.log(Level.INFO, null, ex);
                throw new IllegalStateException(ex);
            }
        }
    }

    @Override
    public Preferences commonUserRoot() {
        return rootNode().userRoot();
    }

    public String[] childrenFileNames() {
        return childrenFileNames(rootExtended());
    }

    /**
     * Returns the names of the children of this preference node, relative to
     * the specified node. (The returned array will be of size zero if this node
     * has no children).
     *
     * The array includes file names found in the OS File System directory that
     * corresponds to the specified parameter. It may not be the same array as
     * the method {@code Preferences.chuldrenNames()} returns.
     *
     *
     * @param forPreferences the node whose children are checked
     * @return the names of the children of this preference node.
     */
    public String[] childrenFileNames(Preferences forPreferences) {
        
        File file = getFile(forPreferences);
        if ( file == null ) {
            return new String[0];
        }
        Path rootNodePath = Paths.get(file.getAbsolutePath(), forPreferences.absolutePath());
        List<String> names = new ArrayList<>();
        
        File[] files = rootNodePath.toFile().listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                names.add(f.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }
     protected File getFile(Preferences forPrefs) {
        File rootFile = null; 
        FileObject config = FileUtil.getConfigRoot();
        if (config == null) {
            return rootFile;
        }
        
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(FileUtil.toFile(config)));
        
        return new File(fo.getPath(), PREFERENCES);
         
     }
}
