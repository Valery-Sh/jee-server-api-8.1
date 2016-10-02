package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
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


    private String DIRECTORY;

    public DirectoryPreferences(Path directoryPath, String... registryRootExtentions) {
        super("", registryRootExtentions);
        init(directoryPath);
    }

    private void init(Path directoryPath) {
        this.DIRECTORY = normalize(directoryPath.toString());
        //
        // rootExtentions are normalized in the super constructor
        //
/*        if (rootExtentions != null) {
            this.rootExtentions = new String[rootExtentions.length];
            for (int i = 0; i < rootExtentions.length; i++) {
                this.rootExtentions[i] = normalize(rootExtentions[i]);
            }
        }
*/        
    }
    @Override
    public DirectoryPreferences next(String directory) {
        String[] ext = Arrays.copyOf(rootExtentions, rootExtentions.length + 1);
        ext[ext.length-1] = DIRECTORY;
        return new DirectoryPreferences(Paths.get(directory), normalize(ext));
    }
    @Override
    public String normalize(String path) {
        
        String result = super.normalize(path);
        
        Path root = Paths.get(result).getRoot();
        if ( root == null ) {
            return result;
        }
        
        String p = root.toString();
        
        if (isWindows() && p.indexOf(":") == 1) {
            result = result.substring(0, 2).toUpperCase() + result.substring(2);
        }
        return result;
    }

    /**
     * Returns a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     *
     * All {@code backslash} characters of the return string are replaced with a
     * {@code forward} slash.
     *
     * @return a string value than represents the {@code directoryPath}
     * parameter used to create this instance.
     */
    @Override
    public String directoryNamespace() {
        return DIRECTORY;
    }

    @Override
    public Preferences directoryRoot() {
        return rootExtended().node(DIRECTORY);
    }    
    
    /**
     * Return a string value that represents a full path relative to
     * the {@link #userRoot(). 
     * overridden to assign a new registry root node.
     *
     * @return the value that represents a full path relative to
     * the user root.
     */
/*    public String userRelativePath() {
        String ext = rootExtendedNamespace();
        String dir = directoryNamespace();
        
        String result = dir;
        if ( ! ext.isEmpty() ) {
            result = ext + "/" + dir;
        }
        
        return result;    
    }    
*/
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.DIRECTORY);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DirectoryPreferences other = (DirectoryPreferences) obj;
        if (!Objects.equals(this.DIRECTORY, other.DIRECTORY)) {
            return false;
        }
        String ext = rootExtendedNamespace();
        if (!Objects.equals(ext, other.rootExtendedNamespace())) {
            return false;
        }
        
        return true;
    }

}
