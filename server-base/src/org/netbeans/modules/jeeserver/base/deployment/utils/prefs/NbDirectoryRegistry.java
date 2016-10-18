package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Valery
 */
public class NbDirectoryRegistry extends DirectoryRegistry {

    public NbDirectoryRegistry(Path directoryPath) {
        super(directoryPath);
    }

    public NbDirectoryRegistry(Path directoryPath, NbDirectoryRegistry parent) {
        super(directoryPath, parent);
    }

    @Override
    protected CommonPreferences createDelegate(Path path) {
        CommonPreferences result;
        if (parent == null) {
            result = new NbDirectoryPreferences(path);
        } else {
            result = parent.getDelegate().next(path.toString());
        }
        result.directoryRoot();
        return result;
    }

    public static NbDirectoryRegistry getDefault(Path dirPath) {
        return null;
    }

    @Override
    protected DirectoryRegistry newInstance(String namespace) {
        return new NbDirectoryRegistry(Paths.get(namespace), this);
    }

    @Override
    public DirectoryRegistry children(Path childrenPath) {
        return new NbDirectoryRegistry(childrenPath, this);
    }

 
    @Override
    protected DirectoryRegistry filterChildrens(Path path) {
        return new NbDirectoryRegistry(path, this);
    }
}
