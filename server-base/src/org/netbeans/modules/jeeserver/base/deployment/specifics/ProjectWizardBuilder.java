package org.netbeans.modules.jeeserver.base.deployment.specifics;

import org.openide.filesystems.FileObject;

/**
 *
 * @author V. Shyshkin
 */
public interface ProjectWizardBuilder {
    default FileObject unzipAntProjectTemplate(FileObject targetDir) {
        return null;
    }
    default FileObject unzipMavenProjectTemplate(FileObject targetDir) {
        return null;
    }
}
