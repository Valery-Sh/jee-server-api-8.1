/**
 * This file is part of Jetty Server support in NetBeans IDE.
 *
 * Jetty Server support in NetBeans IDE is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or (at your option) any later version.
 *
 * Jetty Server support in NetBeans IDE is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should see the GNU General Public License here:
 * <http://www.gnu.org/licenses/>.
 */
package org.netbeans.modules.jeeserver.jetty.embedded.project.nodes;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.jeeserver.base.deployment.config.WebModuleConfig;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtils;
import org.netbeans.modules.jeeserver.base.embedded.utils.EmbConstants;
import org.netbeans.modules.jeeserver.base.embedded.utils.EmbUtils;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

/**
 * Factory class for distributed creation of project node's children. The
 * instance of the class are assumed to be registered in layer at a location
 * specific for {@literal j2se } project type.
 *
 * @author V. Shyshkin
 */
@NodeFactory.Registration(projectType = "org-jetty-embedded-instance-project", position = 1)
public class ServerProjectNodeFactory implements NodeFactory {

    private static final Logger LOG = Logger.getLogger(ServerProjectNodeFactory.class.getName());

    /**
     * Creates a new instance of {@literal WebApplicationsNode} for the
     * specified project and returns it an a @{code NodeList) item. The project
     * must be recognized as an embedded server.
     *
     * @param project
     * @return {@literal NodeFactorySupport.fixedNodeList() ) if the given project
     * is not an embedded server. {@code NodeFactorySupport.fixedNodeList(none) )
     * where the {@code node } is of type {@code WebApplicationsNode}
     * }@see WebApplicationsNode
     */
    @Override
    public NodeList createNodes(Project project) {
        PackageView p;
        Node node = null;
        if (!EmbUtils.isEmbedded(project)) {
            return NodeFactorySupport.fixedNodeList();
        }
        FileObject mvnFolder = project.getProjectDirectory().getFileObject(EmbConstants.SERVER_PROJECT_FOLDER);
        if (mvnFolder != null) {
            try {
                Project mvnProject = FileOwnerQuery.getOwner(mvnFolder);
                if ( mvnProject == null || project.equals(mvnProject)) {
                    return NodeFactorySupport.fixedNodeList();
                }
                LogicalViewProvider lvp = mvnProject.getLookup().lookup(LogicalViewProvider.class);
                node = lvp.createLogicalView();
                OpenProjects.getDefault().close(new Project[] {mvnProject});                
                return NodeFactorySupport.fixedNodeList(node);
            } catch (Exception ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
        //If the above try/catch fails, e.g.,
        //our item isn't in the lookup,
        //then return an empty list of nodes:
        return NodeFactorySupport.fixedNodeList();

    }
}//class