/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jeeserver.base.embedded.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 * This class acts as a manager of the properties. It manages the set of
 * properties and they persistence.
 * <p>
 Single InstanceProperties instance created by the manager usually serves to
 persist properties of single server instance. By definition many
 InstanceProperties can be created in the same namespace. <i>For common use
 * case client module will use one namespace with several
 * InstanceProperties.</i>
 * <p>
 * The <code>namespace</code> used in both non-static methods is just the
 * symbolic name for the InstanceProperties logically connected (like instances
 * of the same server type for example) and retrievable by calling
 * {@link #getProperties(String)} respectively.
 * <p>
 * Typical use case:
 * <p>
 * <
 * pre>
     // we have some instance to persist
     InstancePropertiesManager manager = InstancePropertiesManager.getInstance();
     InstanceProperties props1 = manager.createProperties("myspace");
     props1.put("property", "value");

     // we want to persist yet another instance
     InstanceProperties props2 = manager.createProperties("myspace");
     props2.put("property", "value");

     // we want to retrieve all InstanceProperties from "myspace"
     // the list will have two elements
     List&lt;InstanceProperties&gt; props = manager.getInstanceProperties("myspace");
 </pre>
 * <p>
 * This class is <i>ThreadSafe</i>.
 *
 * @author Petr Hejl
 */
public  class PreferencesManager {

    private static final Logger LOG = Logger.getLogger(PreferencesManager.class.getName());

public final static String CONFIG_PREFERENCES = "config/Preferences";

    private static PreferencesManager manager;


    private PreferencesManager() {
        super();
    }

    /**
     * Returns the instance of the default manager.
     *
     * @return the instance of the default manager
     */
    public static synchronized PreferencesManager getInstance() {
        if (manager == null) {
            manager = new PreferencesManager();
        }

        return manager;
    }

    /**
     * Creates and returns properties in the given namespace. It is perfectly
     * legal to call this method multiple times with the same namespace as a
     * parameter - it will always create new instance of InstanceProperties.
     * Returned properties should serve for persistence of the single server
     * instance.
     *
     * @param namespace string identifying the namespace of created
     * InstanceProperties
     * @return new InstanceProperties logically placed in the given namespace
     */
    public InstancePreferences createProperties(String namespace, String id) {
        Preferences prefs = NbPreferences.forModule(PreferencesManager.class);

        try {
            prefs = prefs.node(namespace);

            synchronized (this) {
                prefs = prefs.node(id);
                prefs.flush();

                InstancePreferences created = new InstancePreferences(id, this, prefs);

                return created;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Returns all existing properties created in the given namespace.
     *
     * @param namespace string identifying the namespace
     * @return list of all existing properties created in the given namespace
     */
    public InstancePreferences getProperties(String namespace, String id) {
        // prefs corresponds to a module code base where dots are replaced 
        // with "/"
        Preferences prefs = NbPreferences.forModule(PreferencesManager.class);
        
        try {
            BaseUtil.out("0) IManager getProperties() prefs.name() = " + prefs.name());
            BaseUtil.out("1) IManager getProperties() prefs.absPath() = " + prefs.absolutePath());

            prefs = prefs.node(namespace);
            BaseUtil.out("2) IManager getProperties() prefs.name() = " + prefs.name());

            prefs.flush();
            BaseUtil.out("3) IManager getProperties() prefs.name() = " + prefs.name());
            InstancePreferences ip = null;
            synchronized (this) {
                
                String[] cn = prefs.childrenNames();
BaseUtil.out("IManager getProperties() childNames.size = " + cn.length);                
                Preferences child = prefs.node(id);
                BaseUtil.out("IManager getProperties() prefs.child = " + child + "; id=" + id);
                BaseUtil.out("IManager getProperties() prefs.child.get(Name) = " + child.get("Name", null) + "; id=" + id);
                
                ip = new InstancePreferences(id, this, child);                

            }
            return ip;
        } catch (Exception ex) {
            BaseUtil.out("IManager getProperties() EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }
    
    protected String[] getChildNames(String namespace) throws BackingStoreException {
        Preferences prefs = NbPreferences.forModule(PreferencesManager.class);
        
        return prefs.node(namespace).childrenNames();
    }
    
    
    protected List<String> getEntries(String namespace, List<String> legalEntries) throws BackingStoreException {
        List<String> entries = new ArrayList<>();
        Preferences prefs = NbPreferences.forModule(PreferencesManager.class);
        
        File prefPath = InstalledFileLocator.getDefault().locate("config/Preferences", prefs.absolutePath(),true);        
        BaseUtil.out("1) PreferencesManager getEntries InstalledFileLocator = " + prefPath);        
        
        
        String dir = prefs.node("uid" + namespace).absolutePath();
        if ( dir.startsWith("/")) {
            dir = dir.substring(1);
        }
        BaseUtil.out("2) PreferencesManager getEntries dir absolutePath = " + dir);
        Path path = Paths.get(prefPath.toPath().toString(),dir);
        BaseUtil.out("3) PreferencesManager getEntries path = " + path);        
        
        
        if ( ! Files.exists(path)) {
            return entries;
        }
        
        try {
            // ----------------------
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException
                {
                    Path modulePath = path.getParent();
                    BaseUtil.out("1.1 PreferencesManager warkTree serverPath = " + modulePath);
                    //entries.add(filePath.toString());
                    Path serverPath = filePath.getParent();
                    //
                    // extract 
                    //
                    Path relserverPath = modulePath.relativize(serverPath);
                    BaseUtil.out("1.2 PreferencesManager warkTree entry = " + filePath.toString());                                        
                    BaseUtil.out("1.2 PreferencesManager warkTree relserverPath = " + relserverPath);                    
                    String relserver = relserverPath.toString().replace("\\", "/");
                    if ( ! legalEntries.contains(relserver)) {
                        entries.add(relserver);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
/*                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException
                {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw e;
                    }
                }        
*/
            });

// ---------------------------------------------        
        } catch (IOException ex) {
            BaseUtil.out("PreferencesManager getEntries EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
        }
        
/*        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            ds.forEach( p  -> {
                if ( Files.isDirectory(p)) {
BaseUtil.out("PreferencesManager getEntries entry = " + p.toString());                    
                    entries.add(p.toString());
                }
            } );
        } catch (IOException ex) {
            BaseUtil.out("PreferencesManager getEntries EXCEPTION = " + ex.getMessage());
            LOG.log(Level.INFO, null, ex);
        }
*/        
        return entries;
    }
    
    protected void remove(Preferences prefs) throws BackingStoreException {
        synchronized (this) {
            prefs.removeNode();
        }
    }
    
  
    protected void remove(String namespace) throws BackingStoreException {
            BaseUtil.out("================================");
            BaseUtil.out("         PreferenceManager Removes");
            BaseUtil.out("================================");
        
        synchronized (this) {
            Preferences prefs = NbPreferences.forModule(PreferencesManager.class);
            prefs = prefs.node(namespace);
            BaseUtil.out(" --- prefs.name() = " + prefs.name());            
            remove(prefs);
        }
        
    }


}
