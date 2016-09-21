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
package org.netbeans.modules.jeeserver.base.deployment.web;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent;
import static org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent.DELETED;
import static org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeEvent.DISPOSE;
import org.netbeans.modules.jeeserver.base.deployment.config.ModulesChangeListener;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.config.WebModuleConfig;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.openide.actions.PropertiesAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.AbstractLookup;

/**
 * Represents the root node of the logical view of the serverProject's folder
 * named {@literal webapps}. Every jetty server serverProject may contain a
 * child folder named {@literal webapps}. Its logical name is
 * {@literal Web Applications}.
 *
 * @author V. Shyshkin
 */
/*@Messages({
    "WebModulesRootNode.shortDescription=Registered applications for this server",
    "WebModulesRootNode.availableWebApps=Available Web Applications"
})
*/
public class WebModulesRootNode extends FilterNode {
    private static final Logger LOG = Logger.getLogger(WebModulesRootNode.class.getName());

    private ModulesChangeListener modulesChangeListener;

    @StaticResource
    private static final String IMAGE = "org/netbeans/modules/jeeserver/base/deployment/resources/web-pages-badge.png";

    /**
     * Creates a new instance of the class for a specified serverProject.
     *
     * @param serverProj a serverProject which is used to create an instance of
     * the class.
     * @param nodeDelegate
     * @throws DataObjectNotFoundException
     */
    public WebModulesRootNode(Project serverProj, Node nodeDelegate) throws DataObjectNotFoundException {
        this(serverProj,nodeDelegate, new RootChildrenKeys(serverProj));
    }
    public WebModulesRootNode(Project serverProj, Node nodeDelegate, RootChildrenKeys keys) throws DataObjectNotFoundException {
        super(nodeDelegate, keys);
        init(serverProj);
    }
    public WebModulesRootNode(Project serverProj, Node nodeDelegate, AbstractLookup lookup) throws DataObjectNotFoundException {
        super(nodeDelegate, new RootChildrenKeys(serverProj), lookup);
        init(serverProj);
    }
    
    /**
     * Creates an instance of class {@link FileChangeHandler} and adds it as a
     * listener of the {@literal FileEvent } to the {@literal FileObject}
     * associated with a {@literal server-instance-config} folder.
     *
     * @param serverProj
     */
    private void init(Project serverProj) {
        ((RootChildrenKeys)getChildren()).setParentNode(this);
        modulesChangeListener = new ModulesChangeHandler(serverProj, this);
        getAvailableModules(serverProj)
                .addModulesChangeListener(modulesChangeListener);
    }
   public ServerInstanceAvailableModules getAvailableModules(Project serverProj) {
         return serverProj.getLookup().lookup(ServerInstanceAvailableModules.class);
   }

    protected ModulesChangeListener getModulesChangeListener() {
        return modulesChangeListener;
    }

    protected void setModulesChangeListener(ModulesChangeListener modulesChangeListener) {
        this.modulesChangeListener = modulesChangeListener;
    }

    /**
     * Returns the logical name of the node.
     *
     * @return the value "Web Applications"
     */
    @Override
    public String getDisplayName() {
        return "Available Web Applications";
    }

    /**
     * Returns an array of actions associated with the node.
     *
     * @param ctx
     * @return an array of the following actions:
     * <ul>
     * <li>
     * Add Existing Web Application
     * </li>
     * <li>
     * Add Archive .war File
     * </li>
     * <li>
     * New Web Application
     * </li>
     * <li>
     * Properties
     * </li>
     * </ul>
     */
    @Override
    public Action[] getActions(boolean ctx) {
        List<Action> actions = new ArrayList<>(2);

        Action propAction = null;

        for (Action a : super.getActions(ctx)) {
            if (a instanceof PropertiesAction) {
                propAction = a;
                break;
            }
        }
        if (propAction != null) {
            actions.add(propAction);
        }

        return actions.toArray(new Action[actions.size()]);

    }

    //Next, we add icons, for the default state, which is
    //closed, and the opened state; we will make them the same. 
    //
    //Icons in serverProj logical views are
    //based on combinations--you can combine the node's own icon
    //with a distinguishing badge that is merged with it. Here we
    //first obtain the icon from a data folder, then we add our
    //badge to it by merging it via a NetBeans API utility method:
    @Override
    public Image getIcon(int type) {
        DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getIcon(type);
        return ImageUtilities.mergeImages(original,
                ImageUtilities.loadImage(IMAGE), 7, 7);
    }

    @Override
    public Image getOpenedIcon(int type) {
        DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getIcon(type);
        return ImageUtilities.mergeImages(original,
                ImageUtilities.loadImage(IMAGE), 7, 7);
    }

    public static class ModulesChangeHandler implements ModulesChangeListener {

        private final Project server;
        private final WebModulesRootNode node;
        private ModulesChangeEvent event;

        public ModulesChangeHandler(Project server, WebModulesRootNode node) {
            this.server = server;
            this.node = node;
        }

        @Override
        public void availableModulesChanged(ModulesChangeEvent event) {
            this.event = event;
            if (event.getEventType() == DISPOSE || event.getEventType() == DELETED) {
                ((RootChildrenKeys) node.getChildren()).removeNotify();
            }
            ((RootChildrenKeys) node.getChildren()).addNotify();

        }
    }

    /**
     * The implementation of the Children.Key of the {@literal Server Libraries}
     * node.
     *
     * @param <T>
     */
    public static class RootChildrenKeys<T> extends FilterNode.Children.Keys<T> {

        private final Project serverProj;
        WebModulesRootNode parentNode;
        /**
         * 
         * Created a new instance of the class for the specified server
         * serverProject.
         *
         * @param serverProj the serverProject which is used to create an
         * instance for.
         */
        public RootChildrenKeys(Project serverProj) {
            this.serverProj = serverProj;
        }

        public void setParentNode(WebModulesRootNode parentNode) {
            this.parentNode = parentNode;
        }
        
        
        /**
         * Creates an array of nodes for a given key.
         *
         * @param key the value used to create nodes.
         * @return an array with a single element. So, there is one node for the
         * specified key
         */
        @Override
        protected Node[] createNodes(T key) {
            return new Node[]{WebModulesNodeFactory.getNode(serverProj, key)};
        }

        /**
         * Called when children of the {@code Web Applications} are first asked
         * for nodes. For each child node of the folder named
         * {@literal "server-instance-config"} gets the name of the child file
         * with extension and adds to a List of Strings. Then invokes the method {@literal setKeys(List)
         * }.
         */
        @Override
        protected void addNotify() {
            //ServerInstanceAvailableModules avm = serverProj.getLookup().lookup(ServerInstanceAvailableModules.class);
            //ServerInstanceAvailableModules avm = parentNode.getAvailableModules(serverProj);
            WebModuleConfig[] list = parentNode.getAvailableModules(serverProj).getModuleList();
            List keyArray = new ArrayList<>(list.length);
            keyArray.addAll(Arrays.asList(list)); //Project webProject = BaseUtil.getOwnerProject(FileUtil.toFileObject(new File(c.getWebProjectPath())));
            this.setKeys(keyArray);
        }
        /**
         * Called when all the children Nodes are freed from memory. The
         * implementation just invokes 
         * {@literal setKey(Collections.EMPTY_LIST) }.
         */
        @Override
        protected void removeNotify() {
            this.setKeys(Collections.EMPTY_LIST);
        }

        /**
         * @return the serverProject
         */
        public Project getServerProject() {
            return serverProj;
        }

        protected FileObject getSourceRoot(Project webProject) {
            Sources sources = ProjectUtils.getSources(webProject);
            SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            FileObject result = null;
            try {
                for (SourceGroup sourceGroup : sourceGroups) {
                    result = sourceGroup.getRootFolder();
                }
            } catch (UnsupportedOperationException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            return result;
        }

    }//class

}//class
