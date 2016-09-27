package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Internally creates {@link DirectoryPreferences} and delegates to it all the
 * work.
 *
 *
 *
 * @author Valery Shyshkin
 * @param <T>
 */
public abstract class DirectoryRegistry<T extends DirectoryRegistry> {

    private static final Logger LOG = Logger.getLogger(DirectoryRegistry.class.getName());

    public static final String CONFIG_PROPERTIES = "config/instance-properties";
    public static final String DIRECTORY_PROPERTIES = "directory-properties";
    public static final String LOCATION_PROPERTY_KEY = "location";
    public static final String LOCATION = "dir-location";

    private DirectoryPreferences delegate;

    private ApplicationsRegistry applicationsRegistry;

    //private String propertiesNamespace = CONFIG_PROPERTIES;
    private final Path instancePath;

    private DirectoryRegistry parent;

    /**
     * Create a new instance. For example for {@code ServerSuiteProject} the
     * second parameter is suite UID.
     *
     * @param instancePath the path that corresponds to some directory
     * directory.
     */
    protected DirectoryRegistry(Path instancePath) {
        this.instancePath = instancePath;
        init();
    }

    protected DirectoryRegistry(Path instancePath, DirectoryRegistry parent) {
        this.instancePath = instancePath;
        this.parent = parent;
        init();
    }

    private void init() {
        if (parent == null) {
            delegate = new DirectoryPreferences(instancePath);
        } else {
            delegate = parent.getDelegate().next(instancePath.toString());
        }
        delegate.propertiesRoot();
    }

    public static DirectoryRegistry getDefault(String path) {
        return new DirectoryRegistryImpl(Paths.get(path));
    }

    public static DirectoryRegistry getDefault(String path, DirectoryRegistry parent) {
        return new DirectoryRegistryImpl(Paths.get(path), parent);
    }

    protected abstract T newInstance(String path);

    protected abstract T newInstance(DirectoryRegistry root, String path);

    /**
     *
     * @return the parent registry if exists or null.
     */
    public DirectoryRegistry parent() {
        return parent;
    }

    public DirectoryRegistry children(Path childrenPath) {
        DirectoryRegistry dr = new DirectoryRegistryImpl(childrenPath);
        return dr;
    }

    public DirectoryRegistry children(String childrenPath) {
        return children(Paths.get(childrenPath));
    }

    public List<DirectoryRegistry> childrens() {
        List list = new ArrayList<>();
        List result = new ArrayList<>();

        synchronized (this) {
            childrens(this, list);
            System.out.println("## ----------------------------------");
            list.forEach(it -> {
                System.out.println(" ---- " + ((T) it).absolutePath());
            });
            System.out.println("## ----------------------------------");
            //List 
            list.forEach(dr -> {
                T reg = (T) dr;
                String l = reg.getProperties(LOCATION).getProperty(LOCATION_PROPERTY_KEY);
                try {
                    Path dirPath = Paths.get(l);
                    System.out.println(" ---- userRelativePath = " + reg.getDelegate().userRelativePath());
                    //
                    // Create children from this and location
                    //
                    DirectoryRegistry child = children(dirPath);
                    //
                    // Check whether it's an immediate children
                    //
                    if (child.absolutePath().equals(reg.absolutePath())) {
                        result.add(child);
                    }
                } catch (InvalidPathException ex) {
                    LOG.log(Level.INFO, null, ex);
                }
            });
            System.out.println("!! RESULT ----------------------------------");
            result.forEach(it -> {
                System.out.println(" ---- " + ((T) it).absolutePath());
            });

            return result;
        }
    }

    public boolean isRoot() {
        return parent() == null;
    }

    public int size() {
        synchronized (this) {
            try {
                return parent().getDelegate().propertiesRoot().childrenNames().length;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        return 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Preferences directoryRoot() {
        return getDelegate().propertiesRoot();
    }

    protected void childrens(DirectoryRegistry root, List<DirectoryRegistry> list) {

        DirectoryPreferences rootDelegate = root.getDelegate();

        synchronized (this) {
            try {
                String[] names = rootDelegate.directoryRoot().childrenNames();

                for (String name : names) {
                    //String locationPath = DIRECTORY_PROPERTIES + "/" + LOCATION_PROPERTY_KEY;
                    DirectoryRegistry dr = null;
                    if (name.equals(LOCATION) && root != this) { // NOT a ROOT NODE
                        //System.out.println("======== This name=" + name + "; this.abs=" + absolutePath());
                        InstancePreferences ip = rootDelegate.getProperties(LOCATION);
                        if (ip != null && ip.getProperty(LOCATION_PROPERTY_KEY) != null) {
                            dr = root;
                            //System.out.println("======== newInstance name=" + name + "; abs=" + dr.absolutePath());
                        }
                    }
                    if (dr != null) {
                        list.add(dr);
                        //System.out.println("======== added name=" + name + "; dr abs=" + dr.absolutePath());
                        continue;
                    }
                    DirectoryRegistry newReg = newInstance((T) root, name);
                    //System.out.println("======== name=" + name + "; nameDr abs=" + newReg.absolutePath());
                    childrens(newReg, list);

                }
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
    }

    public DirectoryRegistry add(Path path) {
        DirectoryRegistry dr;
        synchronized (this) {
            dr = children(path);
            InstancePreferences ip = dr.createProperties(LOCATION);
            String l = getDelegate().normalize(path.toString());
            ip.setProperty(LOCATION_PROPERTY_KEY, l);
            flush(ip.getPreferences());
            return dr;
        }
    }

    protected List<String> childrenNames() {

        List<String> result = new ArrayList<>();
        synchronized (this) {
            try {
                String[] names = getDelegate().propertiesRoot().childrenNames();
                for (String name : names) {
                        result.add(name);
                }
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        return result;

    }

    protected String getPropertiesNamespace() {
        return CONFIG_PROPERTIES;
    }

    /*    public void setPropertiesNamespace(String propertiesNamespace) {
        this.propertiesNamespace = delegate.normalize(propertiesNamespace);
    }
     */
    protected String getDirectoryPropertiesNamespace() {
        return DIRECTORY_PROPERTIES;
    }

    public DirectoryPreferences getDelegate() {
        return delegate;
    }

    /*    protected CommonPreferences createDelegate(Path instancePath) {
        return (delegate = getDefaultDelegate());
    }
    protected CommonPreferences getDefaultDelegate() {
        if (delegate == null) {
            delegate = new DirectoryPreferences(instancePath);
        }
        return delegate;
    }
     */
    public InstancePreferences getProperties() {
        return getProperties(false);
    }

    public InstancePreferences getProperties(String propertiesPath) {
        return delegate.getProperties(propertiesPath);
    }

    public InstancePreferences getProperties(boolean createIfNotExists) {

        if (!createIfNotExists) {
            return delegate.getProperties(getPropertiesNamespace());
        }
        return createProperties();
    }

    public InstancePreferences createProperties() {
        return createProperties(getPropertiesNamespace());
    }

    public InstancePreferences createProperties(String propPath) {
        InstancePreferences ip = delegate.createProperties(propPath);
        if (ip != null) {
            commit();
        }
        return ip;
    }

    protected InstancePreferences createProperties(String propPath, boolean docommit) {
        InstancePreferences ip = delegate.createProperties(propPath);
        if (ip != null && docommit) {
            commit();
        }
        return ip;
    }

    protected boolean flush(Preferences node) {
        boolean result = false;

        synchronized (this) {
            try {
                node.flush();
                result = true;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
            return result;
        }
    }
    protected void flush() {
        flush(getDelegate().propertiesRoot());
    }

    protected void commit() {
        InstancePreferences ip = createProperties(LOCATION, false);
        String l = getDelegate().directoryNamespace();
        ip.setProperty(LOCATION_PROPERTY_KEY, l);

        flush(ip.getPreferences());

        if (parent() != null) {
            parent().commit();
        }
    }


    public boolean nodeExists() {
        boolean result = true;
        synchronized (this) {
            try {
                String dirPath = getDelegate().directoryNamespace();
                Preferences userRoot = getDelegate().userRoot();
                String ext = getDelegate().rootExtendedNamespace();
                if (parent() == null) {
                    result = userRoot.nodeExists(dirPath);
                } else {
                    if (!ext.trim().isEmpty()) {
                        ext = ext + "/" + dirPath;
                    } else {
                        ext = dirPath;
                    }
                    result = userRoot.nodeExists(ext);
                }
            } catch (BackingStoreException ex) {
                result = false;
                LOG.log(Level.SEVERE, null, ex);
            }
            return result;
        }
    }

    public boolean nodeExists(String relative) {
        if (!nodeExists()) {
            return false;
        }

        boolean result;

        synchronized (this) {
            try {
                String dirPath = getDelegate().directoryNamespace();
                Preferences userRoot = getDelegate().userRoot();
                String ext = getDelegate().rootExtendedNamespace();
                if (parent() == null) {
                    result = userRoot.nodeExists(dirPath + "/" + relative);
                } else {
                    if (!ext.trim().isEmpty()) {
                        ext = ext + "/" + dirPath;
                    } else {
                        ext = dirPath;
                    }
                    ext += "/" + relative;
                    result = userRoot.nodeExists(ext);
                }
            } catch (BackingStoreException ex) {
                result = false;
                LOG.log(Level.SEVERE, null, ex);
            }
            return result;
        }
    }

    public boolean replaceWith(Properties props) {
        boolean result = true;
        synchronized (this) {
            InstancePreferences ip = delegate.createProperties(getPropertiesNamespace());
            try {
                String[] keys = ip.getPreferences().keys();
                for (String key : keys) {
                    removeProperty(key);
                }
                ip.copyFrom(props);
            } catch (BackingStoreException ex) {
                result = false;
                LOG.log(Level.SEVERE, null, ex);
            }
            return result;
        }
    }

    public boolean updateWith(Properties props) {
        boolean result = true;
        synchronized (this) {
            InstancePreferences ip = delegate.createProperties(getPropertiesNamespace());
            ip.copyFrom(props);
            return result;
        }
    }

    public boolean removeProperty(String key) {
        boolean result = true;
        synchronized (this) {
            InstancePreferences ip = getProperties();
            if (key.equals(PreferencesProperties.HIDDEN_KEY)) {
                result = false;
            } else {
                ip.getPreferences().remove(key);
            }
            return result;
        }
    }

    public void removeProperties() {
        synchronized (this) {
            InstancePreferences ip = getProperties();
            if (ip == null) {
                return;
            }
            String[] keys = ip.keys();
            for (String key : keys) {
                removeProperty(key);
            }
        }
    }

    public void remove() {
        if (parent() != null) {
            delegate.removeNode(delegate.propertiesRoot(), parent().getDelegate().propertiesRoot());
        } else {
            delegate.removeNode(delegate.propertiesRoot());
        }
    }

    public String namespace() {
        return getDelegate().propertiesRootNamespace();
    }

    public String absolutePath(String... propNamespaces) {
        if (propNamespaces == null || propNamespaces.length == 0) {
            return delegate.absolutePath(getPropertiesNamespace());
        }
        DirectoryPreferences dprefs = new DirectoryPreferences(Paths.get("a"), "");
        String[] norm = dprefs.normalize(propNamespaces);
        return delegate.absolutePath(String.join("/", norm));
    }

    public static class DirectoryRegistryImpl extends DirectoryRegistry {


        public DirectoryRegistryImpl(Path instancePath) {
            super(instancePath);
        }

        protected DirectoryRegistryImpl(Path instancePath, DirectoryRegistry parent) {
            super(instancePath, parent);
        }

        @Override
        protected DirectoryRegistry newInstance(String namespace) {
            return new DirectoryRegistryImpl(Paths.get(namespace), this);
        }

        @Override
        protected DirectoryRegistry newInstance(DirectoryRegistry root, String namespace) {
            return new DirectoryRegistryImpl(Paths.get(namespace), root);
        }
        /*    protected List<? extends DirectoryRegistryImpl> childrens(boolean style) {
        
        List result = new ArrayList<>();
        synchronized (this) {
            try {
                String[] names = parent().getDelegate().propertiesRoot().childrenNames();
                for (String name : names) {
                    if (filterUID(name)) {
                        result.add(newInstance(name));
                    }
                }
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        return result;
    }
         */
    }//class

}//class
