package org.netbeans.modules.jeeserver.base.deployment;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
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
    
    public static Properties getProperties(String uri) {
        Properties result = new Properties();
        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        Enumeration en = InstanceProperties.getInstanceProperties(uri).propertyNames();
        while( en.hasMoreElements()) {
            String propName = en.nextElement().toString();
            result.setProperty(propName, ip.getProperty(propName));
        }
        return result;
    }
    public static Properties removeInstanceProperties(String uri) {
        Properties result = getProperties(uri);
        InstanceProperties.removeInstance(uri);
        if ( FactoryDelegate.getManager(uri) != null ) {
            FactoryDelegate.getManagers().remove(uri);
        }
        return result;
    }
    
}
