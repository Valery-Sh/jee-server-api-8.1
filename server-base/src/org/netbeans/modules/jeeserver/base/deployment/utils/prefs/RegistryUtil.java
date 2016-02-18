package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author V. Shishkin
 */
public class RegistryUtil {
    public static void removeRegistry(PathPreferencesRegistry registry) throws BackingStoreException {
        registry.removeRegistryDirectory(registry.directoryNode());
    }
    
/*    
    public void getEntries(List<String> legalEntries) throws BackingStoreException {
        //getEntries(PathPreferencesRegistry.UID, legalEntries);
    }

    public void getEntries(int level, Preferences prefs, List<String> legalEntries) throws BackingStoreException {

        String[] cnames = prefs.childrenNames();

        //System.out.println("*GET ENTRY. prefs.name() = " + prefs.name());
        //System.out.println("*GET ENTRY. prefs.absolutePath() = " + prefs.absolutePath());
        //System.out.println("*GET ENTRY. cnildren lingth = " + cnames.length);
        String line = "*";
        if (level > 0) {
            char[] chars = new char[level];
            Arrays.fill(chars, '-');
            line = new String(chars);
        }
        for (String nm : cnames) {
            System.out.println(line + " " + nm);
            //prefs.node(nm);
            getEntries(level + 1, prefs.node(nm), legalEntries);

        }
    }

    public void getEntries(String namespace, List<String> legalEntries) throws BackingStoreException {
        Preferences prefs = rootNode();
        String[] cnames = prefs.childrenNames();
        System.out.println("GET ENTRY. prefs.name() = " + prefs.name());
        System.out.println("GET ENTRY. prefs.absolutePath() = " + prefs.absolutePath());
        System.out.println("GET ENTRY. cnildren lingth = " + cnames.length);
        getEntries(0, prefs, legalEntries);
    }
*/    
}
