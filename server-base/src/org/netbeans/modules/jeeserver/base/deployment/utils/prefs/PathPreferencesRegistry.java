package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The class is a specialized wrapper around the class
 * {@literal AbstractPreferences} from the package {@literal java.util.prefs}.
 * The main objective of the class is to provide access to various settings
 * (properties,preferences etc.) specific to an application or module. The
 * method {@link #getProperties(java.lang.String) } return an instance of type
 * {@link PreferencesProperties}. This instance allows to store data and extract
 * them in a manner like we do when use {@literal java.util.Properties} but
 * without worrying about persistence.
 * <p>
 * For example, we can execute the following code:
 * <pre>
 * Path forDirPath = Paths.get("c:/my-tests/first-test");
 * PathPreferencesRegistry reg = PathPreferencesRegistry.newInstance("my-examples/1", dir);
 * PreferencesProperties props = reg.getProperties{"example-properties");
 * </pre> As a result, somewhere in the name space defined by the class
 * {@literal AbstractPreferences} the following structure will be created:
 * <pre>
 *   UUID_ROOT/my-examples/1/c_/my-tests/first-test/<i>example-properties</i>
 * </pre> The full path, shown above, has the following structure:
 * <ul>
 * <li> {@literal UUID_ROOT } is a string value of the static constant defined
 * in the class.
 * </li>
 * <li> {@literal  my-examples/1 } is a string value passed as a first parameter
 * value in the {@literal newInstance } method call.
 * </li>
 * <li> {@literal c_/my-tests/first-test } is a string value representation of
 * the second parameter of type {@literal Path } passed in the {@literal newInstance
 * } method call. Pay attention that the original value contains a colon
 * character which is replaced with an underline symbol. The backslash is also
 * replaced by a forward slash.
 * </li>
 * </ul>
 * We call the first part plus second part as a {@literal "registry root"}. And
 * "registry root" + third part - "directory node". The last part defines a root
 * for properties whose value is used as a parameter for the method 
 * {@literal getProperties() } call.
 *
 * <p>
 * Here {@link #UUID_ROOT } is a string value of the static constant defined in
 * the class.
 * <p>
 * We can create just another properties store:
 * <pre>
 *     props2 = reg.getProperties{"example-properties-2");
 * </pre> and receive as a result:
 * <pre>
 *   UUID_ROOT/my-examples/1/c:/my-tests/first-test/<i>example-properties-1</i>
 * </pre> Now that we have an object of type {@link PreferencesProperties} , we
 * can read or write various properties, for example:
 * <pre>
 *  props.setProperty("myProp1","My first property");
 *  String value = props.getProperty("myProp1");
 * </pre> There are many useful methods in the class
 * {@link PreferencesProperties} that we can use to work with the properties.
 * <p>
 * We can create an instance of the class applying one of two static methods:
 *
 * <ul>
 * <li>{@link #newInstance(java.lang.String, java.nio.file.Path) }</li>
 * <li>{@link #newInstance(java.nio.file.Path) }</li>
 * </ul>
 *
 * @author V. Shyshkin
 */
public class PathPreferencesRegistry {

    private static final Logger LOG = Logger.getLogger(PathPreferencesRegistry.class.getName());

    public static String UUID_ROOT = "UUID-ROOT-f4460510-bc81-496d-b584-f4ae5975f04a";

    protected static String TEST_UUID = "TEST_UUID-ROOT";

    private String DIRECTORY;

    private String[] registryRootExtentions;

    public PathPreferencesRegistry(Path directoryPath, String... registryRootExtentions) {
        this.DIRECTORY = directoryPath.toString().replace("\\", "/");
        if (registryRootExtentions != null) {
            this.registryRootExtentions = new String[registryRootExtentions.length];
            for (int i = 0; i < registryRootExtentions.length; i++) {
                this.registryRootExtentions[i] = registryRootExtentions[i].replace("\\", "/");
            }
        }
    }

    /*    public PathPreferencesRegistry(String directoriesRootNamespace, Path DIRECTORY) {
        this.DIRECTORY = DIRECTORY;
        if (directoriesRootNamespace == null) {
            this.directoriesRootNamespace = DEFAULT_DIRECTORIES_ROOT;
        } else {
            this.directoriesRootNamespace = directoriesRootNamespace.replace("\\", "/");
        }
//        this.factory = PreferencesPropertiesFactory.getDefault();        
    }
     */
    public void setDirectoryPath(Path directoryPath) {
        this.DIRECTORY = directoryPath.toString().replace("\\", "/");
    }

    public Path getDirectoryPath() {
        return Paths.get(DIRECTORY);
    }

    /**
     * @return Returns a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     */
    public String getDirectoryNamespace() {
        return DIRECTORY;
    }


    /**
     * Return a string value that is used to create a root node of the registry.
     * The implementation returns {@link #UUID_ROOT} constant value and may be
     * overridden to assign a new registry root node.
     *
     * @return Return a string value which is used to create a root node of the
     * registry
     */
    protected String registryRootNamespace() {
        return UUID_ROOT;
    }

    /**
     * Returns the root preference node for the calling user. Just calls:
     * <pre>
     *   AbstractPreferences.userRoot();
     * </pre>
     *
     * @return the root preference node for the calling user.
     * @throws java.lang.SecurityException - If a security manager is present
     * and it denies RuntimePermission("preferences").
     * @see java.lang.RuntimePermission
     */
    public Preferences userRoot() {
        return AbstractPreferences.userRoot();
    }
    public Preferences registryRoot() {
        return userRoot().node(registryRootNamespace());
    }
    public Preferences registryRootExtended() {
        Preferences p = userRoot();

        if (registryRootExtentions == null || registryRootExtentions.length == 0) {
            p = p.node(registryRootNamespace());
        } else {
            p = p.node(registryRootNamespace());
            for (int i = 0; i < registryRootExtentions.length; i++) {
                p = p.node(registryRootExtentions[i]);
            }
        }
        return p;
    }


    /**
     * Returns a preferences node that represents a directory name space. Just
     * returns a value:
     * <pre>
    registryRootExtended().node(getNamespace());
 </pre>
     *
     * @return Returns a preferences node that represents a directory name
     * space.
     */
    public Preferences directoryPropertiesRoot() {
        return registryRootExtended().node(DIRECTORY);

    }

    public Preferences clearRegistryRoot() throws BackingStoreException {
        synchronized (this) {
            String[] childs = registryRootExtended().childrenNames();
            for (String c : childs) {
                registryRootExtended().node(c).removeNode();
            }
            return registryRootExtended();
        }
    }

    protected Preferences clearRoot() throws BackingStoreException {
        synchronized (this) {
            String[] childs = userRoot().childrenNames();
            for (String c : childs) {
                userRoot().node(c).removeNode();
            }
            return userRoot();
        }
    }

    /**
     * Checks whether a node specified by the parameter exists.
     *
     * @param namespace a string that specifies a path relative to the node as
     * defined by the method {@link #registryRootExtended() }.
     * @return {@literal  true} if the node exists, {@literal false} - otherwise
     */
    public boolean nodeExists(String namespace) {
        boolean b = false;
        try {
            b = registryRootExtended().nodeExists(convertPath(namespace));
        } catch (BackingStoreException ex) {
            Logger.getLogger(PathPreferencesRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }

    /**
     * Removes directory nodes starting from the given node. Be careful
     * regardless whether the child node exists the method always tries to
     * remove the given node. Once the node is deleted, the method recursively
     * removes all parent nodes as long as one of the conditions is satisfied
     * <ul>
     * <li>The parent node is a root registry node as specified by the method
     * {@link #removeRegistryDirectory(java.util.prefs.Preferences)}
     * </li>
     * <li>The parent node has children nodes
     * </li>
     * </ul>
     *
     * The above-mentioned node is not removed.
     *
     * @param prefs initial node to delete
     * @throws BackingStoreException Thrown to indicate that a preferences
     * operation could not complete because of a failure in the backing store,
     * or a failure to contact the backing store.
     */
    protected void removeRegistryDirectory(Preferences prefs) throws BackingStoreException {
        Preferences parent = prefs.parent();
        prefs.removeNode();
        String rootAbs = registryRootExtended().absolutePath();
        if (parent.absolutePath().equals(registryRootExtended().absolutePath())) {
            return;
        }
        if (parent.childrenNames().length > 0) {
            return;
        }
        removeRegistryDirectory(parent);
    }

    /**
     * Removes directory nodes starting from the given node. Be careful
     * regardless whether the child node exists the method always tries to
     * remove the given node. Once the node is deleted, the method recursively
     * removes all parent nodes as long as one of the conditions is satisfied
     * <ul>
     * <li>The parent node is a root registry node as specified by the method
     * {@link #removeRegistryDirectory(java.util.prefs.Preferences)}
     * </li>
     * <li>The parent node has children nodes
     * </li>
     * </ul>
     *
     * The above-mentioned node is not removed.
     *
     */
    public void removeRegistryDirectory() {
        try {
            removeRegistryDirectory(directoryPropertiesRoot());
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
    }


    protected String convertPath(String path) {
        String result = path.replace("\\", "/");
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }

    /**
     * The method returns an instance of {@link  PreferencesProperties} class.
     * The {@literal id} is used to create a node relatively to a directory node
     * as the method {@link #directoryPropertiesRoot() } specifies.
     *
     * @param id the value that specifies a name for a node where properties are
     * written and read.
     *
     * @return an object of type {@link PreferencesProperties}
     */
    public InstancePreferences getProperties(String id) {
        if (DIRECTORY == null) {
            return null;
        }
        Preferences prefs = directoryPropertiesRoot();
        try {

            if (!prefs.nodeExists(id)) {
                return null;
            }
            synchronized (this) {
                prefs = prefs.node(id);
                PreferencesProperties properties = new InstancePreferences(id, prefs);//factory.create(id, prefs);                
                return (InstancePreferences) properties;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public InstancePreferences createProperties(String id) {
        if (DIRECTORY == null) {
            return null;
        }

        Preferences prefs = directoryPropertiesRoot();
        try {
            synchronized (this) {
                prefs = prefs.node(id);
                prefs.flush();
                PreferencesProperties created = new InstancePreferences(id, prefs);//factory.create(id, prefs);                
                return (InstancePreferences) created;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }
}
