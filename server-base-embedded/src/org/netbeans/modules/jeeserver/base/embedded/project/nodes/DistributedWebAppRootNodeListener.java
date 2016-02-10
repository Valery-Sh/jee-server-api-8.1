package org.netbeans.modules.jeeserver.base.embedded.project.nodes;

import java.beans.PropertyChangeEvent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.jeeserver.base.deployment.utils.BaseUtil;
import org.netbeans.modules.jeeserver.base.embedded.project.SuiteManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author V. Shyshkin
 */
public class DistributedWebAppRootNodeListener implements NodeListener {

    private Project instanceProject;

    private String ID = null;

    public DistributedWebAppRootNodeListener(Project instanceProject, String id) {
        this.instanceProject = instanceProject;
        ID = id;
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        BaseUtil.out("*** 1) DistributedWebAppRootNodeListenerchildrenAdded displayName= " + nme.getNode().getDisplayName());
    }

    @Override
    public synchronized void childrenRemoved(NodeMemberEvent nme) {
        String displayName = nme.getNode().getDisplayName();

        BaseUtil.out("ID=" + ID + " " + " ============= DistributedWebAppNodeListener childrenRemoved displayName= " + displayName);//+ "; toString" + nme);        
        Node[] changedNodes = nme.getDelta();
        BaseUtil.out("ID=" + ID + " " + " ====== changed Nodes ====================");
        for (Node n : changedNodes) {
            BaseUtil.out("ID=" + ID + " " + " ---- changedNode.getName() = " + n.getName());
        }
        Node distRootNode = nme.getNode();
        ChildrenNotifier childrenNotifier = distRootNode.getLookup().lookup(ChildrenNotifier.class);
        if (childrenNotifier != null) {
            BaseUtil.out("ID=" + ID + " invoke notifiier childrenChanged()  ");
            childrenNotifier.childrenChanged();
        }
//        BaseUtil.out("ID=" + ID + " " + " ============= DistributedWebAppNodeListener childrenRemoved displayName= " + displayName);//+ "; toString" + nme);
/*
        if (!ProjectManager.getDefault().isProject(webFo)) {
            BaseUtil.out("ID=" + ID + " " + "2.3 !!!!! NOT a PROJECT = " + webFo);
            Project suite = SuiteManager.getServerSuiteProject(instanceProject);

            if (suite != null) {
                //SuiteNotifier suiteNotifier = suite.getLookup().lookup(SuiteNotifier.class);
                BaseUtil.out("ID=" + ID + " " + "2.4 NOT PROJ  suite != null ");
                //suiteNotifier.instancesChanged();
                Node sourceNode = (Node) nme.getSource();
                BaseUtil.out("ID=" + ID + " " + "2.5 childrenRemoved source node.getName() = " + sourceNode.getName());

                Node parentNode = sourceNode.getParentNode();
                BaseUtil.out("ID=" + ID + " " + "2.6 NOT PROJ childrenRemoved sourceNode.getParent() = " + parentNode);
                if (parentNode == null) {
                    BaseUtil.out("ID=" + ID + " " + "2.7 NOT PROJ childrenRemoved sourceNode.getParent() == NULL");
                    return;
                }
                ChildrenNotifier childrenNotifier = parentNode.getLookup().lookup(ChildrenNotifier.class);
                BaseUtil.out("ID=" + ID + " " + "2.8 NOT PROJ childrenRemoved ChildrenNotifier = " + childrenNotifier);
parentNode.getName();
                if (childrenNotifier != null) {
                    BaseUtil.out("ID=" + ID + " " + "2.9 NOT PROJ  ChildrenNotifier  not NULL. childrenRemoved invoke notifiier childrenChanged()  ");
                    sourceNode.removeNodeListener(this);
                    childrenNotifier.childrenChanged();
                }
            }

        }
         */
    }

    @Override
    public void childrenReordered(NodeReorderEvent nre) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ne) {
        BaseUtil.out("ID=" + ID + " " + "*** 4)  nodeDestroyed displayName= " + ne.getNode().getDisplayName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BaseUtil.out("ID=" + ID + " " + " propertyChange name=" + evt.getPropertyName() + "; source=" + evt.getSource() + "; new value=" + evt.getNewValue() + "; oldValue= " + evt.getOldValue());
        BaseUtil.out("ID=" + ID + " " + " --------- newValue" + evt.getNewValue());
        BaseUtil.out("ID=" + ID + " " + " --------- oldValue" + evt.getOldValue());

        /*        BaseUtil.out("ID=" + ID + " " + "*** 5.1) DistributedWebAppNodeListener propertyChange name=" + evt.getPropertyName() + "; new value=" + evt.getNewValue() + "; oldValue= " + evt.getOldValue() + "; displayName=" + displayName);
        if (webFo == null) {
            BaseUtil.out("ID=" + ID + " " + "***  DistributedWebAppNodeListener propertyChange webFo == NULL");
            return;
        }
        if (!ProjectManager.getDefault().isProject(webFo)) {
            BaseUtil.out("ID=" + ID + " " + "********* LISTENER !!!!! NOT a PROJECT = " + webFo);
            Project suite = SuiteManager.getServerSuiteProject(instanceProject);

            if (suite != null) {
                SuiteNotifier suiteNotifier = suite.getLookup().lookup(SuiteNotifier.class);
                BaseUtil.out("ID=" + ID + " " + " ********* !!!!! NOT a PROJECT before notify web app displayName " + displayName);
                suiteNotifier.instancesChanged();
            }

        }
         */
    }

}
