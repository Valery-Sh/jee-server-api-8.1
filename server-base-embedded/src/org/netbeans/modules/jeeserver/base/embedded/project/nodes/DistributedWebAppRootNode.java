package org.netbeans.modules.jeeserver.base.embedded.project.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.ServerInstanceProperties;
import org.netbeans.modules.jeeserver.base.deployment.config.ServerInstanceAvailableModules;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseConstants;
import org.netbeans.modules.jeeserver.base.deployment.web.WebModulesRootNode;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.netbeans.modules.jeeserver.base.embedded.project.prefs.DistributeModulesManager;
import org.netbeans.modules.jeeserver.base.embedded.webapp.actions.AddDistWebAppAction;
import org.openide.actions.PropertiesAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Represents the root node of the logical view of the serverProject's folder
 * named {@literal webapps}. Every jetty server serverProject may contain a
 * child folder named {@literal webapps}. Its logical name is
 * {@literal Web Applications}.
 *
 * @author V. Shyshkin
 */
public class DistributedWebAppRootNode extends WebModulesRootNode {

    private final InstanceContent lookupContents;

    /**
     * Creates a new instance of the class for a specified serverProject.
     *
     * @param serverInstance a serverProject which is used to create an instance
     * of the class.
     * @param nodeDelegate
     * @throws DataObjectNotFoundException
     */
    public DistributedWebAppRootNode(Project serverInstance, Node nodeDelegate) throws DataObjectNotFoundException {
        this(serverInstance, nodeDelegate, new InstanceContent());
    }

    protected DistributedWebAppRootNode(Project serverInstance, Node nodeDelegate, InstanceContent instanceContent) throws DataObjectNotFoundException {
        super(serverInstance, nodeDelegate, new AbstractLookup(instanceContent));
        this.lookupContents = instanceContent;

        FileObject instanciesDir = nodeDelegate.getLookup().lookup(FileObject.class);
        lookupContents.add(instanciesDir);
        lookupContents.add(serverInstance);

        lookupContents.add((RootChildrenKeys)getChildren());
        
        init();

    }
    private void init() {
        String uri = SuiteManager.getManager(((RootChildrenKeys)getChildren()).getServerProject()).getUri();
        InstanceProperties props = InstanceProperties.getInstanceProperties(uri);
        ServerInstanceProperties sip = new ServerInstanceProperties();
        sip.setServerId(props.getProperty(BaseConstants.SERVER_ID_PROP));
        sip.setUri(props.getProperty(BaseConstants.URL_PROP));

        lookupContents.add(sip);
        lookupContents.add(this);
    }

    @Override
    public ServerInstanceAvailableModules getAvailableModules(Project serverInstance) {
        return DistributeModulesManager.getAvailableModules(serverInstance);
    }

    /**
     * Returns the logical name of the node.
     *
     * @return the value "Web Applications"
     */
    @Override
    public String getDisplayName() {
        return "Web Applications to Distribute";
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
        Action addDistWebAppAction = AddDistWebAppAction.getAddDistWebAppAction(getLookup());
        //Action newAntProjectAction = ServerActions.NewAntProjectAction.getContextAwareInstance(getLookup());
        //Action newMavenProjectAction = ServerActions.NewMavenProjectAction.getContextAwareInstance(getLookup());
        //Action addExistingProject = ServerActions.AddExistingProjectAction.getContextAwareInstance(getLookup());
        Action propAction = null;

        for (Action a : super.getActions(ctx)) {
            if (a instanceof PropertiesAction) {
                propAction = a;
                break;
            }
        }

        actions.add(addDistWebAppAction);

        if (propAction != null) {
            actions.add(propAction);
        }

        return actions.toArray(new Action[actions.size()]);

    }

}
