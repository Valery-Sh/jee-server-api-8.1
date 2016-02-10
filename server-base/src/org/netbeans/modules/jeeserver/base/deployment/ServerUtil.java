package org.netbeans.modules.jeeserver.base.deployment;

import java.util.Map;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;

/**
 *
 * @author V. Shyshkin
 */
public interface ServerUtil {
    
    public static InstanceProperties createInstanceProperties(String uri, String displayName) throws InstanceCreationException {
        return createInstanceProperties(uri, displayName, null);
    }
    public static InstanceProperties createInstanceProperties(String uri, String displayName, Map<String,String> initialProperties) throws InstanceCreationException {
        if ( FactoryDelegate.getManager(uri) != null ) {
            FactoryDelegate.getManagers().remove(uri);
        }
        return InstanceProperties.createInstanceProperties(uri, null, null, displayName, initialProperties);
    }
    public static void removeInstanceProperties(String uri) {
        InstanceProperties.removeInstance(uri);
        if ( FactoryDelegate.getManager(uri) != null ) {
            FactoryDelegate.getManagers().remove(uri);
        }
        
    }
    
}
