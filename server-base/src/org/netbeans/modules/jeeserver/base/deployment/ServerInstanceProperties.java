/**
 * This file is part of Base JEE Server support in NetBeans IDE.
 *
 * Base JEE Server support in NetBeans IDE is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * Base JEE Server support in NetBeans IDE is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should see the GNU General Public License here:
 * <http://www.gnu.org/licenses/>.
 */
package org.netbeans.modules.jeeserver.base.deployment;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;

import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;

/**
 * Each Server project contains a instance of this class in its Lookup.
 * The presence of such an object in a project lookup means that the project is 
 * a Server. In addition it contains a set of configuration properties.
 * 
 * @author V. Shyshkin
 */
public class ServerInstanceProperties {
    
    private static final Logger LOG = Logger.getLogger(ServerInstanceProperties.class.getName());
    //private Deployment.Mode currentDeploymentMode;
    private String uri;
    private String serverId;
    //private String actualServerId;
//    private String layerProjectFolderPath;
    private boolean valid = true;
  /*  
    public String getLayerProjectFolderPath() {
        return layerProjectFolderPath;
    }
*/
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Returns the server identifier.
     * @return serverIdentifier. For example embedded jetty server identifier
     * is a string value "jetty9".
     */
    public String getServerId() {
        return serverId;
    }
    
    
    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    /**
     * Return a string prefix that is used to create a unique server URI.
     * @return server URI prefix. For example: "jetty9:deploy:server"
     */
    public String getUriPrefix() {
        return getServerId() + BaseConstants.URIPREFIX_NO_ID;
    }
    /**
     * Returns a string URI that uniquely identifies a server.
     * @return a string URI of the server
     */
    public String getUri() {
        return uri;
    }
    /**
     * Returns a deployment manager that manages the server project it
     * represents.
     * @return an object of type {@literal BaseDeploymentManager}
     */
    public BaseDeploymentManager getManager()  {
        BaseDeploymentManager dm = null;
        try {
            dm = (BaseDeploymentManager) DeploymentFactoryManager.getInstance().getDisconnectedDeploymentManager(getUri());
        } catch (DeploymentManagerCreationException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return dm;
    }
    
    /**
     * Returns a string representation of the server's {@literal http port number}.
     * @return an {@literal http port}
     */
    public String getHttpPort() {
        return getManager().getInstanceProperties().getProperty(BaseConstants.HTTP_PORT_PROP);
    }
    
    public String getInstanceProperty(String propName) {
        return getManager().getInstanceProperties().getProperty(propName);
    }
    
    /**
     * Returns the server project's directory.
     * @return an absolute path of the project's directory
     */
    public String getHomeDir() {
        
       return getManager().getInstanceProperties().getProperty(BaseConstants.HOME_DIR_PROP);
    }
}
