package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * DirectoryPreferences reg = DirectoryPreferences.newInstance("my-examples/1", dir);
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
public class DirectoryPreferences extends CommonPreferences {

    //private static final Logger LOG = Logger.getLogger(DirectoryPreferences.class.getName());

    
    private String DIRECTORY;

    public DirectoryPreferences(Path directoryPath, String... registryRootExtentions) {
        super("", registryRootExtentions);
        this.DIRECTORY = directoryPath.toString().replace("\\", "/");
    }

    /**
     * Returns a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     * 
     * All {@code backslash} characters of the return string are replaced with a 
     * {@code forward} slash.
     * 
     * @return  a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     */
    public String getDirectoryNamespace() {
        return DIRECTORY;
    }

    @Override
    public Preferences propertiesRoot() {
        return rootExtended().node(DIRECTORY);
    }
    
    protected void setDirectoryPath(Path directoryPath) {
        this.DIRECTORY = directoryPath.toString().replace("\\", "/");
    }

    public Path getDirectoryPath() {
        return Paths.get(DIRECTORY);
    }

}
