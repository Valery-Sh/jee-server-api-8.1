package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.openide.util.NbPreferences;

/**
 *
 * @author Valery Shyshkin
 */
public class NbDirectoryPreferences extends NbBasePreferences {

    private static final Logger LOG = Logger.getLogger(NbDirectoryPreferences.class.getName());

    //public static String UUID_ROOT = "UUID-ROOT-f4460510-bc81-496d-b584-f4ae5975f04a";
    //protected static String TEST_UUID = "TEST_UUID-ROOT";
    private String DIRECTORY;

    public NbDirectoryPreferences(Path directoryPath, String... registryRootExtentions) {
        this(NbPreferences.forModule(BaseDeploymentManager.class), directoryPath, registryRootExtentions);
    }

    protected NbDirectoryPreferences(Preferences rootNode, Path directoryPath, String... rootExtentions) {
        super(rootNode, rootExtentions);
        init(directoryPath);
    }
    private void init(Path directoryPath){
        this.DIRECTORY = normalize(directoryPath.toString());
    }
    @Override
    protected String normalize(String path) {
        String result = super.normalize(path);
        return result.replace(":", "_");
    }

    /**
     * Returns a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     * 
     * All {@code backslash} characters of the return string are replaced with a 
     * {@code forward} slash. And the {@code colon} characters are replaced with the
     * {@code underline} one.
     * 
     * @return  a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     */
    public String getDirectoryNamespace() {
        return DIRECTORY;
    }
    /**
     * returns an object of type {@code Preferences} that is considered to be a 
     * {@code properties root node }.
     * 
     * The return value is {@code rootExtended().node(getDirectoryNamespace())
     * @return the properties root node.
     * @see #getDirectoryNamespace() 
     */
    @Override
    public Preferences propertiesRoot() {
        return rootExtended().node(DIRECTORY);
    }

    public Path getDirectoryPath() {
        return Paths.get(DIRECTORY);
    }
}//class
