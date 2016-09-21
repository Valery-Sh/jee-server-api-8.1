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
package org.netbeans.modules.jeeserver.base.deployment.config;

import org.netbeans.modules.jeeserver.base.deployment.BaseDeploymentManager;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ContextRootConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

import org.openide.filesystems.FileUtil;

/**
 * Implements {@code ModuleConfiguration } interface for
 * {@code Server Plugin}.
 *
 * @author V. Shyshkin
 */
public abstract class AbstractModuleConfiguration implements ModuleConfiguration, ContextRootConfiguration {

    protected static final RequestProcessor RP = new RequestProcessor(AbstractModuleConfiguration.class);

    private Lookup lookup;
    private static final String CONTEXTPATH = "contextPath";
    
    
    private File contextConfigFile;

    protected String serverInstanceId;

    protected Project webProject;
    
    /**
     * Supported names of the file used to configure a web project.
     * For example {@code Jetty Server} allows such names as (@code jetty-web.xml}
     * or {@code web-jetty.xml).
     */
    private final String[] contextFilePaths;

    private static final Logger LOG = Logger.getLogger(AbstractModuleConfiguration.class.getName());

    private final J2eeModule module;

    /**
     * Creates a new instance of the class for the specified module.
     *
     * @param module an object of type {@literal J2eeModule)
     * @param contextFilePaths supported names of files used to configure
     *   a web application. For example {@code Jetty Server} allows such names as (@code jetty-web.xml}
     *   or {@code web-jetty.xml).
     */
    protected AbstractModuleConfiguration(J2eeModule module, String[] contextFilePaths) {
        this.module = module;
        this.contextFilePaths = contextFilePaths;
        init();

    }

    /**
     * Returns supported names of files used to configure
     *   a web application. For example {@code Jetty Server} allows such names as (@code jetty-web.xml}
     *   or {@code web-jetty.xml).
     * 
     * @return supported names of files used to configure
     *   a web application
     */
    public String[] getContextFilePaths() {

        return contextFilePaths;
    }
    /**
     * Returns a {@code Server Project} for the given {@code webProject}.
     * 
     * The method returns {@code null } value if the specified  {@code instanceId} 
     * is not managed by the class {@link BaseDeploymentManager}.
     * 
     * @param webProject a web project used to define a serverInstanceId 
     * of the host server.  
     * 
     * @return an object of type Project that represents the server instance
     */
    protected Project getServerProject(Project webProject) {
        String instanceId = webProject.getLookup().lookup(J2eeModuleProvider.class).getServerInstanceID();

        Project result = null;
        if (instanceId == null) {
            return null;
        }
        try {
            DeploymentManager m = DeploymentFactoryManager.getInstance().getDisconnectedDeploymentManager(instanceId);
            if (m != null && (m instanceof BaseDeploymentManager)) {
                BaseDeploymentManager dm = (BaseDeploymentManager) m;
                result = dm.getServerProject();
            }
        } catch (DeploymentManagerCreationException ex) {
            LOG.log(Level.INFO, "AbstractModuleConfiguration.getProjectPropertiesFileObject. {0}", ex.getMessage()); //NOI18N                        
        }
        return result;
    }
    /**
     * Returns a {@code Server Project} for the given {@code instanceId}.
     * The method returns {@code null } value if the specified 
     * {@code instanceId} is not managed by the class
     * {@link BaseDeploymentManager}.
     * 
     * @param instanceId a server instance id as specified by the class 
     *  org.​netbeans.​modules.​j2ee.​deployment.​devmodules.​spi.​J2eeModuleProviderJ2eeModuleProvider#getServerInstanceId();
     * 
     * @return an object of type Project that represents the server instance
     */
    protected Project getServerProject(String instanceId) {

        Project result = null;
        if (instanceId == null) {
            return null;
        }
        try {
            DeploymentManager m = DeploymentFactoryManager.getInstance().getDisconnectedDeploymentManager(instanceId);
            if (m != null && (m instanceof BaseDeploymentManager)) {
                BaseDeploymentManager dm = (BaseDeploymentManager) m;
                result = dm.getServerProject();
            }
        } catch (DeploymentManagerCreationException ex) {
            LOG.log(Level.INFO, "AbstractModuleConfiguration.getProjectPropertiesFileObject. {0}", ex.getMessage()); //NOI18N                        
        }
        return result;
    }

    protected void notifyCreate() {
        notifyAvailableModule(serverInstanceId, false);
    }

    /**
     * Here is an example for Jetty Server:
     *  <code>
     * <pre>
     *   File jw = module.getDeploymentConfigurationFile("WEB-INF/jetty-web.xml");
     *   File wj = module.getDeploymentConfigurationFile("WEB-INF/web-jetty.xml");
     *   if (jw.exists()) {
     *       return jw;
     *   }
     *   if (!wj.exists()) {
     *       return jw;
     *   }
     *   return wj;
     * </pre>
     * </code>
     *
     * @return
     */
    protected abstract File findContextConfigFile();

    public abstract Properties getContextProperties();

    protected abstract String changeContext(String cp);

    protected abstract void initContextConfigFile();
    
    protected abstract void addListeners();
    
    
    /**
     * @return  a webProject that this class instance represents.
     */
    public Project getWebProject() {
        return webProject;
    }

    private void init() {

        if (getContextConfigFile() == null || !getContextConfigFile().exists()) {
            initContextConfigFile();
        } else {
            Properties props = getContextProperties();
            if (props.getProperty(CONTEXTPATH) == null || props.getProperty(CONTEXTPATH).trim().isEmpty()) {
                if (webProject == null) {
                    webProject = BaseUtil.getOwnerProject(Utilities.toURI(getContextConfigFile()));
                }
                changeContext("/" + webProject.getProjectDirectory().getNameExt());
            }
        }
        if (webProject == null) {
            webProject = BaseUtil.getOwnerProject(FileUtil.toFileObject(getContextConfigFile()));
            serverInstanceId = webProject.getLookup().lookup(J2eeModuleProvider.class).getServerInstanceID();
        }
        addListeners();
    }
    /**
     *
     * Returns lookup associated with the object. This lookup should contain
     * implementations of all the supported configurations. The configuration
     * are: ContextRootConfiguration, DatasourceConfiguration,
     * MappingConfiguration, EjbResourceConfiguration,
     * DeploymentPlanConfiguration, MessageDestinationConfiguration
     * Implementator is advised to use
     * org.openide.util.lookup.Lookups.fixed(java.lang.Object[]) to implement
     * this method.
     *
     * @return (@Lookups.fixed(this)}
     */
    @Override
    public synchronized Lookup getLookup() {

        if (null == lookup) {
            lookup = Lookups.fixed(this);
        }
        if (webProject != null) {
            String id = webProject.getLookup().lookup(J2eeModuleProvider.class).getServerInstanceID();
            if (serverInstanceId != null && !serverInstanceId.equals(id)) {
                String oldid = serverInstanceId;
                serverInstanceId = id;
                notifyServerChange(oldid, id);
                //notifyServerChange(serverInstanceId, id);
            }
        }
        return lookup;
    }

    protected void notifyAvailableModule(String instanceId, final boolean dispose) {

        if (instanceId == null) {
            return;
        }
        Project srv = getServerProject(instanceId);
        if (srv != null) {
            final AvailableModules<AbstractModuleConfiguration> avm = srv.getLookup().lookup(AvailableModules.class);
            if (avm == null) {
                return;
            }
            RP.post(() -> {
                if (dispose) {
                    avm.moduleDispose(this);
                } else {
                    avm.moduleCreate(this);
                }
            }, 0, Thread.NORM_PRIORITY);

        }

    }

    protected void notifyServerChange(String oldInstanceId, String newInstanceId) {
        notifyAvailableModule(oldInstanceId, true);
        notifyAvailableModule(newInstanceId, false);
    }

    public File getContextConfigFile() {
        if (contextConfigFile == null) {
            contextConfigFile = findContextConfigFile();
        }
        return contextConfigFile;
    }

    /**
     * @return an object of type File which specifies a folder where web pages,
     * WEB-INF? META-INF reside.
     */
    public File getWebRoot() {
        File c = getContextConfigFile();
        if (c == null || !c.exists()) {
            return null;
        }
        c = c.getParentFile();
        if (!c.isFile() && ("WEB-INF".equals(c.getName()) || "META-INF".equals(c.getName()))) {
            c = c.getParentFile();
        } else {
            c = null;
        }
        return c;
    }

    /**
     * The server calls this method when it is done using this
     * ModuleConfiguration instance. We must be aware of that
     * if for example we assign a Glassfish server than serverInstanceId 
     * corresponds to Glassfish. 
     */
    @Override
    public void dispose() {
        notifyAvailableModule(serverInstanceId, true);
    }

    public boolean supportsCreateDatasource() {
        return true;
    }

    /**
     *
     *
     * @return @throws ConfigurationException
     */
    @Override
    public String getContextRoot() throws ConfigurationException {
        Properties props = getContextProperties();

        String cp = props.getProperty(CONTEXTPATH);

        if (cp == null || cp.trim().isEmpty()) {
            cp = "/" + webProject.getProjectDirectory().getNameExt();
            changeContext(cp);
        }
        return cp;
    }

    /**
     * @return a J2EE module associated with this ModuleConfiguration instance.
     */
    @Override
    public J2eeModule getJ2eeModule() {
        return module;
    }

    /**
     * Set the web context root.
     *
     * @param contextRoot context root to be set.
     * @throws ConfigurationException reports errors in setting the web context
     * root.
     */
    @Override
    public void setContextRoot(final String contextRoot) throws ConfigurationException {
        changeContext(contextRoot);
    }

}//class
