package org.netbeans.modules.jeeserver.base.embedded.project.nodes;

import org.netbeans.api.project.Project;
import org.netbeans.modules.jeeserver.base.deployment.web.WebModulesNodeFactory;
import org.netbeans.modules.jeeserver.base.deployment.web.WebModulesRootNode;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteUtil;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author Valery
 */
@NodeFactory.Registration(projectType = "org-embedded-server-instance-project", position = 0)
public class DistributedWebAppNodeFactory extends WebModulesNodeFactory {

    @Override
    protected boolean isServerSupported(Project serverInstance) {
        return SuiteUtil.isEmbedded(serverInstance);
    }
    @Override
    protected WebModulesRootNode getRootNode(Project serverInstance) throws DataObjectNotFoundException {
        WebModulesRootNode node = new DistributedWebAppRootNode(serverInstance, getNodeDelegate(serverInstance));    
        //node.init(serverInstance);
        return node;
    }
 
}
