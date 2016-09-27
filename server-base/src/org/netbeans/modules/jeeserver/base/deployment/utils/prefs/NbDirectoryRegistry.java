package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Valery
 */
public class NbDirectoryRegistry extends DirectoryRegistry {
    
    public NbDirectoryRegistry(Path directoryPath) {
        super(directoryPath);
    }
    protected CommonPreferences createDelegate(Path directoryPath) {
        return new NbDirectoryPreferences(directoryPath);
    }
    public static NbDirectoryRegistry getDefault(Path dirPath) {
        return null;
    }

    @Override
    public DirectoryRegistry newInstance(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public DirectoryRegistry newInstance(DirectoryRegistry root,String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public List<? extends DirectoryRegistry> childrens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
