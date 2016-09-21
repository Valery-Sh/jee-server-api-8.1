package org.netbeans.modules.jeeserver.base.embedded.specifics;

import java.io.InputStream;
import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;
import org.netbeans.modules.jeeserver.base.deployment.specifics.StartServerPropertiesProvider;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.nodes.SuiteNodesNotifier;
import org.netbeans.modules.jeeserver.base.embedded.utils.SuiteConstants;

/**
 *
 * @author V. Shyshkin
 */
public interface EmbeddedServerSpecifics extends ServerSpecifics {

    //private static final Logger LOG = Logger.getLogger(EmbeddedServerSpecifics.class.getName());
    boolean supportsDistributeAs(SuiteConstants.DistributeAs distributeAs);

    default InputStream getPomFileTemplate() {
        return null;
    }

    default String getDescriptorResourcePath(String actualserverId) {
        return ""; //org.netbeans.modules.jeeserver.jetty.embedded.resources
    }
    @Override
    default void iconChange(String uri, boolean newValue) {
        SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNodesNotifier.class)
                .iconChange(uri, newValue);
    }

    @Override
    default void displayNameChange(String uri, String newValue) {
        SuiteManager.getServerSuiteProject(uri)
                .getLookup()
                .lookup(SuiteNodesNotifier.class)
                .displayNameChange(uri, newValue);
    }


    @Override
    default StartServerPropertiesProvider getStartServerPropertiesProvider(BaseDeploymentManager dm) {
        return new EmbeddedStartServerPropertiesProvider(dm);
    }

}
