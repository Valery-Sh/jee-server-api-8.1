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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.specifics.ServerSpecifics;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Factory to create {@literal DeploymentManager } that can deploy to
 * {@literal Server}.
 *
 * The Server URI has the following format:
 * <PRE><CODE><i>server-id</i>:deploy:server:<i>project-dir-path</i></CODE></PRE>
 * for example
 *
 * @author V.Shyshkin
 * @see ProjectDeploymentManager
 */
public abstract class FactoryDelegate {

    private static final Logger LOG = Logger.getLogger(FactoryDelegate.class.getName());

    private final static Map<String, BaseDeploymentManager> managers = new ConcurrentHashMap<>();

    private final String serverId;
    protected String uriPrefix;
    protected List<String> toDelete;

    public FactoryDelegate(String serverId) {
        //this.specifics = specifics;
        this.serverId = serverId;
        uriPrefix = serverId + ":" + BaseConstants.URIPREFIX_NO_ID + ":";
        toDelete = new ArrayList<>();
        registerUnusedInstances();
    }

    public abstract ServerSpecifics newServerSpecifics();

    public synchronized void removeUnusedManagers() {
        List<String> toRemove = new ArrayList<>();
        managers.forEach((u, m) -> {
            if (InstanceProperties.getInstanceProperties(u) == null) {
                toRemove.add(u);
            }
        });
        toRemove.forEach(u -> {
            managers.remove(u);
        });
    }

    public void deleteUnusedInstances() {

        if (toDelete.isEmpty()) {
            return;
        }
        String[] ar = new String[toDelete.size()];
        ar = toDelete.toArray(ar);

        for (String uri : ar) {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
            if (ip != null) {
                ServerUtil.removeInstanceProperties(uri);
                toDelete.remove(uri);
            }
            InstanceProperties.getInstanceProperties(uri);
        }

    }

    /**
     * Determine whether a server exists under the specified location.
     *
     * @param instanceFO an absolute path of the server project directory
     * @return {@literal true } if the server project exists. {@literal false}
     * otherwise
     */
    protected boolean existsServer(FileObject instanceFO) {

        String serverLocation = (String) instanceFO.getAttribute(BaseConstants.SERVER_LOCATION_PROP);

        if (serverLocation == null) {
            return false;
        }

        File f = new File(serverLocation);
        if (!f.exists()) {
            return false;
        }

        FileObject fo = FileUtil.toFileObject(f);

        //Project p = FileOwnerQuery.getOwner(fo);
        Project p = BaseUtil.getOwnerProject(fo);
        if (p == null) {
            return false;
        }

        if (p.getLookup().lookup(ServerInstanceProperties.class) == null) {
            return false;
        }
        return true;
    }

    public synchronized void registerUnusedInstances() {

        FileObject dir = FileUtil.getConfigFile("/J2EE/InstalledServers");
        FileObject instanceFOs[] = dir.getChildren();
        for (FileObject instanceFO : instanceFOs) {

            String url = (String) instanceFO.getAttribute(InstanceProperties.URL_ATTR);
            if (!url.startsWith(uriPrefix)) {
                continue;
            }
            if (existsServer(instanceFO)) {
                continue;
            }
            toDelete.add(url);
        }
    }

    /**
     * Tests whether the factory can create a manager for the URI.
     *
     * @param uri the uri
     * @return true when uri is not null and starts with characters as defined
     * by {@link #URI_PREFIX} , false otherwise
     */
    public boolean handlesURI(String uri) {
        if (uri == null) {
            return false;
        }
        return uri.startsWith(uriPrefix);
    }

    public synchronized void removeManager(String uri) {
        managers.remove(uri);
    }

    /**
     * Gets a connected deployment manager for the given uri, username and
     * password
     *
     * @param uri the uri of the deployment manager
     * @param username the user name
     * @param password the password
     * @return the deployment manager
     * @throws DeploymentManagerCreationException
     */
    public synchronized BaseDeploymentManager getDeploymentManager(String uri, String username, String password) throws DeploymentManagerCreationException {
        //BaseUtil.out("+++ FactoryDelegate getDeploymentManager uri = " + uri);

        deleteUnusedInstances();
        removeUnusedManagers();

        if (InstanceProperties.getInstanceProperties(uri) == null) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri);
        }
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri);
        }
        BaseDeploymentManager manager = managers.get(uri);

        if (null == manager) {
            manager = new BaseDeploymentManager(serverId, uri,
                    newServerSpecifics());
            managers.put(uri, manager);
            register(manager);
        }

        return manager;
    }
    /**
     * Does nothing here.
     * @param manager an instance of the deployment manager
     */
    protected void register(BaseDeploymentManager manager) {
        
    }
    
    /**
     *
     * Gets a disconnected version of the deployment manager
     *
     * Delegates the method call to {@link #getDeploymentManager(java.lang.String, java.lang.String, java.lang.String)
     * }
     * with null values of username and password parameters.
     *
     * @param uri the uri of the deployment manager
     * @return the deployment manager
     * @throws DeploymentManagerCreationException
     */
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return getDeploymentManager(uri, null, null);
    }

    /**
     * @return a display name. The displayName is a concatenation of the {@link #serverId ) and " Server" string constant.
     * {@literal Server}
     */
    public String getDisplayName() {
        return serverId + " Server";
    }

    /**
     * The version of the deployment manager.
     *
     * @return the version.
     */
    public String getProductVersion() {
        return "Server 1.0";
    }

    public static Map<String, BaseDeploymentManager> getManagers() {
        return managers;
    }

    public static BaseDeploymentManager getManager(String uri) {
        return managers.get(uri);
    }

}
