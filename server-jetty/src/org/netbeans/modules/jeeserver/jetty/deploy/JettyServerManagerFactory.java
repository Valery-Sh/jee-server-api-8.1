/**
 * This file is part of Jetty Server support in NetBeans IDE.
 *
 * Jetty Server support in NetBeans IDE is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * Jetty Server support in NetBeans IDE is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should see the GNU General Public License here:
 * <http://www.gnu.org/licenses/>.
 */
package org.netbeans.modules.jeeserver.jetty.deploy;

import java.util.logging.Logger;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecificsProvider;
import org.netbeans.modules.jeeserver.base.deployment.FactoryDelegate;
//import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;


/**
 * Factory to create {@literal DeploymentManager } that can deploy to {@literal Jetty Server}.
 *
 * Jetty Server URI has following format:
 * <PRE><CODE>jettystandalone:deploy:server:project-dir-path</CODE></PRE>
 * for example
 * <PRE><CODE>jettystandalone:deploy:server:c:/myprojects/server01</CODE></PRE> 
 *
 * @author V.Shyshkin
 * @see JettyDeploymentManager
 */
public class JettyServerManagerFactory implements DeploymentFactory, ServerSpecificsProvider {
    private static final Logger LOG = Logger.getLogger(JettyServerManagerFactory.class.getName());

    public static final String SERVER_ID = "jettystandalone";
    
    //private final HashMap<String, DeploymentManager> managers = new HashMap<>();
    
    private static FactoryDelegate delegateInstance = null;
    
    private static JettyServerManagerFactory instance = null;    

    private JettyServerManagerFactory() {
    }
    
    
    public synchronized static JettyServerManagerFactory getInstance() {
        if (null == instance) {
            instance = new JettyServerManagerFactory();
            delegateInstance = new JettyServerFactoryDelegate(SERVER_ID);
            
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }
    
   /**
    * Tests whether the factory can create a manager for the URI.
    * 
    * @param uri the uri
    * @return true when uri is not null and starts with 
    * characters as defined by {@link #URI_PREFIX} , false otherwise
    */
    @Override
    public boolean handlesURI(String uri) {
        return delegateInstance.handlesURI(uri);
    }
  /**
    * Gets a connected deployment manager for the given uri, username and password
    *
    * @param uri the uri of the deployment manager
    * @param username the user name
    * @param password the password 
    * @return the deployment manager
    * @throws DeploymentManagerCreationException
    */
    @Override
    public DeploymentManager getDeploymentManager(String uri, String username, String password) throws DeploymentManagerCreationException {
        return delegateInstance.getDeploymentManager(uri, username, password);
    }

    /**
  /**
    * Gets a disconnected version of the deployment manager
    *
    * Delegates the method call to {@link #getDeploymentManager(java.lang.String, java.lang.String, java.lang.String) }
    * with null values of username and password parameters. 
    * @param uri the uri of the deployment manager
    * @return the deployment manager
    * @throws DeploymentManagerCreationException 
    */
    @Override
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return delegateInstance.getDisconnectedDeploymentManager(uri);
    }
   /**
    * The vendor of the deployment manager
    * 
    * @return the vendor name. By default returns a string value of {@literal Jetty Server}
    */
    @Override
    public String getDisplayName() {
        return "Jetty Server";
    }
   /**
    * The version of the deployment manager
    * @return the version. 
    */
    @Override
    public String getProductVersion() {
        return "Jetty9 Server V.M=1.0";
    }

    @Override
    public String getServerId() {
        return SERVER_ID;
    }

    @Override
    public ServerSpecifics getSpecifics() {
        return new JettyServerSpecifics();
    }

    @Override
    public String[] getSupportedServerIds() {
        return new String[] {"jetty-9-server"};
    }

}